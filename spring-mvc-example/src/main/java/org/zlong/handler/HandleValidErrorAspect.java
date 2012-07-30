package org.zlong.handler;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.zlong.exception.BadRequest400Exception;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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
        Object[] args = joinPoint.getArgs();
        Method invocationMethod = ((MethodSignature) joinPoint.getSignature()).getMethod();
        handlerError(invocationMethod, args);
    }

    private void handlerError(Method method, Object[] args) {
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();

        List<String> allErrorMessages = new ArrayList<String>();
        for (int i = 0; i < parameterAnnotations.length; i++) {
            for (Annotation annotation : parameterAnnotations[i]) {
                if (annotation instanceof AOPValidated) {
                    AOPValidated aopValidated = (AOPValidated) annotation;
                    Class<?>[] groups = aopValidated.value();
                    allErrorMessages.addAll(getErrorMessages(args[i], groups));
                    break;
                }
            }
        }
        if (!allErrorMessages.isEmpty()) {
            throw new BadRequest400Exception(allErrorMessages.toString());
        }
        return;
    }

    private List<String> getErrorMessages(Object form, Class<?>[] groups) {

        List<String> errorMessages = new ArrayList<String>();
        if (form == null) {
            return errorMessages;
        }

        Set<ConstraintViolation<Object>> validate = validator.validate(form, groups);
        if (CollectionUtils.isEmpty(validate)) {
            return errorMessages;
        }

        for (ConstraintViolation<Object> constraintViolation : validate) {
            errorMessages.add(constraintViolation.getMessage());
        }
        return errorMessages;
    }

	@Autowired
	public void setValidator(Validator validator) {
		this.validator = validator;
	}

}
