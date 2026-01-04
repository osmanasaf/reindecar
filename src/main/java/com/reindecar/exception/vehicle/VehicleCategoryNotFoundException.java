package com.reindecar.exception.vehicle;

import com.reindecar.common.exception.EntityNotFoundException;

public class VehicleCategoryNotFoundException extends EntityNotFoundException {

    public VehicleCategoryNotFoundException(Long id) {
        super("VehicleCategory", id);
    }
}
