/**
 * 
 */
package org.zlong.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.zlong.annotation.ExceptionProcessor;
import org.zlong.annotation.ExceptionResolver;
import org.zlong.model.User;

/**
 * @author zhanglong
 * 
 */
@Controller
@RequestMapping(value = "/user")
public class UserController {

	@RequestMapping(method = RequestMethod.POST)
	@ResponseBody
	@ExceptionResolver({ @ExceptionProcessor(exceptions = IllegalArgumentException.class, httpStatus = HttpStatus.BAD_REQUEST, message = "") })
	public int add(@ModelAttribute @Validated() User user,
			BindingResult bindingResult) {

		String errorMessage = getErrorMessage(bindingResult);
		if (errorMessage != null) {
			throw new IllegalArgumentException(errorMessage);
		}
		return 1;
	}

	private String getErrorMessage(BindingResult bindingResult) {
		if (!bindingResult.hasErrors()) {
			return null;
		}
		List<ObjectError> allErrors = bindingResult.getAllErrors();
		List<String> errorMessages = new ArrayList<>();

		for (ObjectError error : allErrors) {
			errorMessages.add(error.getObjectName() + ":"
					+ error.getDefaultMessage());
		}
		return errorMessages.toString();
	}

}
