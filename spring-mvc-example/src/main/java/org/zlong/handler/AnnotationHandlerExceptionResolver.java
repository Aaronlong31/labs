/**
 * 
 */
package org.zlong.handler;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.http.converter.xml.XmlAwareFormHttpMessageConverter;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
import org.zlong.annotation.ExceptionResolver;
import org.zlong.annotation.MapEntry;

/**
 * @author zhanglong
 * 
 */
public class AnnotationHandlerExceptionResolver implements HandlerExceptionResolver {

	private static Logger logger = LoggerFactory
			.getLogger(AnnotationHandlerExceptionResolver.class);

	private List<HttpMessageConverter<?>> messageConverters;

	/**
	 * Default constructor.
	 */
	public AnnotationHandlerExceptionResolver() {

		StringHttpMessageConverter stringHttpMessageConverter = new StringHttpMessageConverter();
		stringHttpMessageConverter.setWriteAcceptCharset(false); // See SPR-7316
		this.messageConverters = new ArrayList<HttpMessageConverter<?>>();
		this.messageConverters.add(new MappingJacksonHttpMessageConverter());
		this.messageConverters.add(new XmlAwareFormHttpMessageConverter());
	}

	@Override
	public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response,
			Object handler, Exception ex) {

		HandlerMethod handlerMethod = (HandlerMethod) handler;
		ExceptionResolver exceptionResolver = handlerMethod
				.getMethodAnnotation(ExceptionResolver.class);
		MapEntry exceptionProcessor = getExceptionProcessor(exceptionResolver, ex);

		if (exceptionProcessor == null) {
			return null;
		}

		ServletWebRequest webRequest = new ServletWebRequest(request, response);
		try {
			return getModelAndView(handlerMethod.getMethod(), webRequest, exceptionProcessor, ex);
		} catch (Exception e) {
			logger.error("Invoking request method resulted in exception : " + handlerMethod, e);
		}

		return null;
	}

	private MapEntry getExceptionProcessor(ExceptionResolver exceptionResolver, Exception ex) {

		if (exceptionResolver == null) {
			return null;
		}
		MapEntry[] exceptionProcessors = exceptionResolver.value();

		for (MapEntry exceptionProcessor : exceptionProcessors) {
			Class<? extends Exception>[] handlerExceptions = exceptionProcessor.exceptions();
			for (Class<? extends Exception> handlerException : handlerExceptions) {
				if (handlerException.isInstance(ex)) {
					return exceptionProcessor;
				}
			}
		}
		return null;
	}

	private ModelAndView getModelAndView(Method handlerMethod, ServletWebRequest webRequest,
			MapEntry exceptionProcessor, Exception ex) throws Exception {

		HttpStatus responseStatus = exceptionProcessor.httpStatus();
		webRequest.getResponse().setStatus(responseStatus.value());

		ErrorBody returnValue = new ErrorBody();
		returnValue.setErrorCode(responseStatus.value());

		String message = StringUtils.hasText(exceptionProcessor.message()) ? exceptionProcessor
				.message() : ex.getMessage();
		returnValue.setMessage(message);

		if (returnValue != null
				&& AnnotationUtils.findAnnotation(handlerMethod, ResponseBody.class) != null) {
			return handleResponseBody(returnValue, webRequest);
		}

		throw new IllegalArgumentException("Invalid handler method return value: " + returnValue);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private ModelAndView handleResponseBody(Object returnValue, ServletWebRequest webRequest)
			throws ServletException, IOException {

		HttpInputMessage inputMessage = new ServletServerHttpRequest(webRequest.getRequest());
		List<MediaType> acceptedMediaTypes = inputMessage.getHeaders().getAccept();
		if (acceptedMediaTypes.isEmpty()) {
			acceptedMediaTypes = Collections.singletonList(MediaType.ALL);
		}
		MediaType.sortByQualityValue(acceptedMediaTypes);
		HttpOutputMessage outputMessage = new ServletServerHttpResponse(webRequest.getResponse());
		Class<?> returnValueType = returnValue.getClass();
		if (this.messageConverters != null) {
			for (MediaType acceptedMediaType : acceptedMediaTypes) {
				for (HttpMessageConverter messageConverter : this.messageConverters) {
					if (messageConverter.canWrite(returnValueType, acceptedMediaType)) {
						messageConverter.write(returnValue, acceptedMediaType, outputMessage);
						return new ModelAndView();
					}
				}
			}
		}
		if (logger.isWarnEnabled()) {
			logger.warn("Could not find HttpMessageConverter that supports return type ["
					+ returnValueType + "] and " + acceptedMediaTypes);
		}
		return null;
	}
}
