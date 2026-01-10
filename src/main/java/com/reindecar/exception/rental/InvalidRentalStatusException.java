package com.reindecar.exception.rental;

import com.reindecar.common.exception.BusinessException;
import com.reindecar.common.exception.ErrorCode;
import com.reindecar.entity.rental.RentalStatus;

public class InvalidRentalStatusException extends BusinessException {

    public InvalidRentalStatusException(RentalStatus from, RentalStatus to) {
        super(ErrorCode.RENTAL_INVALID_STATE, String.format("%s -> %s", from, to));
    }
}
