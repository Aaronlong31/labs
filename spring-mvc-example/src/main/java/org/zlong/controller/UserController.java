/**
 * 
 */
package org.zlong.controller;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.zlong.annotation.ExceptionResolver;
import org.zlong.annotation.MapEntry;
import org.zlong.handler.AOPValidated;
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
	@ExceptionResolver({ @MapEntry(exceptions = IllegalArgumentException.class, httpStatus = HttpStatus.BAD_REQUEST) })
	public int add(@AOPValidated @ModelAttribute @Validated User user, BindingResult bindingResult) {
		return 1;
	}

}
