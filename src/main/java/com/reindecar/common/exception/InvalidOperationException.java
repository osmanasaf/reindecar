package com.reindecar.common.exception;

public class InvalidOperationException extends BusinessException {

    private static final String CODE = "INVALID_OPERATION";

    public InvalidOperationException(String message) {
        super(CODE, message);
    }
}

