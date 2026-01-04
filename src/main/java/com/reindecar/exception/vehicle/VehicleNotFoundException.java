package com.reindecar.exception.vehicle;

import com.reindecar.common.exception.EntityNotFoundException;

public class VehicleNotFoundException extends EntityNotFoundException {

    public VehicleNotFoundException(Long id) {
        super("Vehicle", id);
    }
}
