package com.reindecar.exception.customer;

import com.reindecar.common.exception.EntityNotFoundException;

public class DriverNotFoundException extends EntityNotFoundException {

    public DriverNotFoundException(Long id) {
        super("Driver", id);
    }
}
