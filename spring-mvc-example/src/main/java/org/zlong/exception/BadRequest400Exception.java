package org.zlong.exception;

/**
 * Created with IntelliJ IDEA.
 * User: zhanglong
 * Date: 12-7-27
 * Time: 上午10:57
 * To change this template use File | Settings | File Templates.
 */
public class BadRequest400Exception extends RuntimeException{

    public BadRequest400Exception() {
    }

    public BadRequest400Exception(String message) {
        super(message);
    }

    public BadRequest400Exception(String message, Throwable cause) {
        super(message, cause);
    }
}
