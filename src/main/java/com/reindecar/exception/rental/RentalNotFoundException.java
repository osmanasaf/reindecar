package com.reindecar.exception.rental;

import com.reindecar.common.exception.EntityNotFoundException;

public class RentalNotFoundException extends EntityNotFoundException {

    public RentalNotFoundException(Long id) {
        super("Rental", id);
    }
}
