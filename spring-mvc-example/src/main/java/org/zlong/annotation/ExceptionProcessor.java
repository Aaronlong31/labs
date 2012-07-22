/**
 * 
 */
package org.zlong.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.http.HttpStatus;

/**
 * @author zhanglong
 *
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ExceptionProcessor {

	public Class<? extends Exception>[] exceptions();
	
	public HttpStatus httpStatus();
	
	public String message() default "";
}
