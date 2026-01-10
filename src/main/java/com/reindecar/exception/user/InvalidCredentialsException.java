package com.reindecar.exception.user;

import com.reindecar.common.exception.BusinessException;
import com.reindecar.common.exception.ErrorCode;

public class InvalidCredentialsException extends BusinessException {

    public InvalidCredentialsException() {
        super(ErrorCode.UNAUTHORIZED, "Invalid username or password");
    }
}
