/**
 * 
 */
package org.zlong.handler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
import org.zlong.annotation.ExceptionProcessor;
import org.zlong.annotation.ExceptionResolver;

/**
 * @author zhanglong
 * 
 */
public class AbstractHandlerExceptionResolver implements
		HandlerExceptionResolver {

	@Override
	public ModelAndView resolveException(HttpServletRequest request,
			HttpServletResponse response, Object handler, Exception ex) {

		HandlerMethod handlerMethod = (HandlerMethod) handler;
		ExceptionResolver exceptionResolver = handlerMethod
				.getMethodAnnotation(ExceptionResolver.class);
		ExceptionProcessor exceptionProcessor = getExceptionProcessor(
				exceptionResolver, ex);

		if (exceptionProcessor == null) {
			return null;
		}

		return null;
	}

	private ExceptionProcessor getExceptionProcessor(
			ExceptionResolver exceptionResolver, Exception ex) {

		if (exceptionResolver == null) {
			return null;
		}
		ExceptionProcessor[] exceptionProcessors = exceptionResolver.value();

		for (ExceptionProcessor exceptionProcessor : exceptionProcessors) {
			Class<? extends Exception>[] handlerExceptions = exceptionProcessor
					.exceptions();
			for (Class<? extends Exception> handlerException : handlerExceptions) {
				if (handlerException.isInstance(ex)) {
					return exceptionProcessor;
				}
			}
		}
		return null;
	}
}
