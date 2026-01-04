package com.reindecar.exception.user;

import com.reindecar.common.exception.BusinessException;

public class WeakPasswordException extends BusinessException {

    public WeakPasswordException(String message) {
        super("AUTH_002", message);
    }

    public WeakPasswordException() {
        super("AUTH_002", "Password must be at least 8 characters long and contain at least one uppercase letter and one digit");
    }
}
