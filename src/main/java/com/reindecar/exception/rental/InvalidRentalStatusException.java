package com.reindecar.exception.rental;

import com.reindecar.common.exception.BusinessException;
import com.reindecar.entity.rental.RentalStatus;

public class InvalidRentalStatusException extends BusinessException {

    public InvalidRentalStatusException(RentalStatus from, RentalStatus to) {
        super("RENTAL_001", 
              String.format("Invalid rental status transition from %s to %s", from, to));
    }
}
