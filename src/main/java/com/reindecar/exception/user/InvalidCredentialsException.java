package com.reindecar.exception.user;

import com.reindecar.common.exception.BusinessException;

public class InvalidCredentialsException extends BusinessException {

    public InvalidCredentialsException() {
        super("AUTH_001", "Invalid username or password");
    }
}
