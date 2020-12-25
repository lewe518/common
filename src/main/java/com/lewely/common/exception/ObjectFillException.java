package com.lewely.common.exception;

/**
 * ObjectFillException
 *
 * @author yiliua
 * Create at: 2020/10/26
 */
public class ObjectFillException extends RuntimeException {
    public ObjectFillException() {
    }

    public ObjectFillException(String message) {
        super(message);
    }

    public ObjectFillException(Throwable cause) {
        super(cause);
    }
}
