package org.zlong.annotation;

import java.util.ArrayList;
import java.util.List;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

@Aspect
@Component
public class HandleValidErrorAspect {

	@Before("execution(* org.zlong.controller..*.*(..,org.springframework.validation.BindingResult,..))")
	public void catchBindingResult(JoinPoint joinPoint) {
		BindingResult bindingResult = (BindingResult) getBindingResult(joinPoint);
		handleError(bindingResult);
	}

	private BindingResult getBindingResult(JoinPoint joinPoint) {
		Object[] args = joinPoint.getArgs();

		for (Object object : args) {
			if (object instanceof BindingResult) {
				return (BindingResult) object;
			}
		}
		return null;
	}

	private void handleError(BindingResult bindingResult) {

		if (bindingResult == null || !bindingResult.hasErrors()) {
			return;
		}
		List<ObjectError> allErrors = bindingResult.getAllErrors();
		List<String> errorMessages = new ArrayList<>();

		for (ObjectError error : allErrors) {
			errorMessages.add(error.getObjectName() + ":" + error.getDefaultMessage());
		}
		throw new IllegalArgumentException(errorMessages.toString());
	}
}
