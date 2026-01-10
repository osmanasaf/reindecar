package com.reindecar.common.exception;

public class InvalidOperationException extends BusinessException {

    public InvalidOperationException(String message) {
        super(ErrorCode.INVALID_OPERATION, message);
    }
}
