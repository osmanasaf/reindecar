package com.reindecar.exception.user;

import com.reindecar.common.exception.EntityNotFoundException;

public class UserNotFoundException extends EntityNotFoundException {

    public UserNotFoundException(Long id) {
        super("User", id);
    }

    public UserNotFoundException(String username) {
        super("User with username: " + username + " not found");
    }
}
