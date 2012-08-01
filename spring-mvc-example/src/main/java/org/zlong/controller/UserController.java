/**
 *
 */
package org.zlong.controller;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.zlong.exception.BadRequest400Exception;
import org.zlong.handler.AOPValidated;
import org.zlong.handler.ExceptionResolver;
import org.zlong.handler.MapEntry;
import org.zlong.model.User;

/**
 * @author zhanglong
 */
@Controller
@RequestMapping(value = "/user")
public class UserController {

    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    @ExceptionResolver({@MapEntry(exceptions = BadRequest400Exception.class, httpStatus = HttpStatus.BAD_REQUEST)})
    public int add(@AOPValidated @ModelAttribute User user) {
        return 1;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseBody
    public User get(@PathVariable int id) {
        User user = new User();
        user.setAge(123);
        user.setId(id);
        user.setName("zhangshan");
        user.setPassword("123456");
        return user;
    }

}
