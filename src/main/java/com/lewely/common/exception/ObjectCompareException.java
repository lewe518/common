package com.lewely.common.exception;

/**
 * ObjectCompareException
 *
 * @author yiliua
 * Create at: 2020/10/26
 */
public class ObjectCompareException extends RuntimeException {
    public ObjectCompareException() {
    }

    public ObjectCompareException(String message) {
        super(message);
    }

    public ObjectCompareException(Throwable cause) {
        super(cause);
    }
}
