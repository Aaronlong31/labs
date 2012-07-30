package org.zlong.handler;

import java.lang.annotation.*;

/**
 * 
 * @author zhanglong 2012-7-24 下午5:58:17
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AOPValidated {

	Class<?>[] value() default {};
}
