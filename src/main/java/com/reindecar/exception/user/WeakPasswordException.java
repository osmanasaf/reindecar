package com.reindecar.exception.user;

import com.reindecar.common.exception.BusinessException;
import com.reindecar.common.exception.ErrorCode;

public class WeakPasswordException extends BusinessException {

    public WeakPasswordException(String message) {
        super(ErrorCode.WEAK_PASSWORD, message);
    }

    public WeakPasswordException() {
        super(ErrorCode.WEAK_PASSWORD, "password requirements not met");
    }
}
