package com.reindecar.exception.customer;

import com.reindecar.common.exception.EntityNotFoundException;

public class CustomerNotFoundException extends EntityNotFoundException {

    public CustomerNotFoundException(Long id) {
        super("Customer", id);
    }

    public CustomerNotFoundException(String identifier) {
        super("Customer with identifier: " + identifier + " not found");
    }
}
