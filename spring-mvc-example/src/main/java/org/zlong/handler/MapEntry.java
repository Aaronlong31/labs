/**
 * 
 */
package org.zlong.handler;

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
public @interface MapEntry {

	public Class<? extends Exception>[] exceptions();
	
	public HttpStatus httpStatus();
	
	public String message() default "";
}
