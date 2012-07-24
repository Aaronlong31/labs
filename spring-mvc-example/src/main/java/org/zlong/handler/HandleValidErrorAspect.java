package org.zlong.handler;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

/**
 * 
 * @author zhanglong 2012-7-24 下午4:06:39
 */
@Component
@Aspect
public class HandleValidErrorAspect {

	private Validator validator;

	@Before("execution(* org.zlong..*Controller.*(..,@org.zlong.handler.AOPValidated (*),..))")
	public void catchError(JoinPoint joinPoint) {
		Object form = getValidatedForm(joinPoint);
		handlerError(form);
	}

	private Object getValidatedForm(JoinPoint joinPoint) {
		Object[] args = joinPoint.getArgs();

		Method invocaMethod = ((MethodSignature) joinPoint.getSignature()).getMethod();
		Annotation[][] parameterAnnotations = invocaMethod.getParameterAnnotations();

		int i = 0;
		for (Annotation[] annotations : parameterAnnotations) {
			for (Annotation annotation : annotations) {
				if (AOPValidated.class.isInstance(annotation)) {
					return args[i];
				}
			}
			i++;
		}
		return null;
	}

	private void handlerError(Object form) {

		if (form == null) {
			return;
		}

		Set<ConstraintViolation<Object>> validate = validator.validate(form);
		if (CollectionUtils.isEmpty(validate)) {
			return;
		}
		List<String> errorMessages = new ArrayList<String>();
		for (ConstraintViolation<Object> constraintViolation : validate) {
			errorMessages.add(constraintViolation.getMessage());
		}
		throw new IllegalArgumentException(errorMessages.toString());
	}

	@Autowired
	public void setValidator(Validator validator) {
		this.validator = validator;
	}

}
