package com.reindecar.exception.rental;

import com.reindecar.common.exception.BusinessException;
import com.reindecar.common.exception.ErrorCode;

import java.time.LocalDate;

public class RentalOverlapException extends BusinessException {

    public RentalOverlapException(Long vehicleId, LocalDate startDate, LocalDate endDate) {
        super(ErrorCode.RENTAL_OVERLAP, String.format("Vehicle ID %d (%s - %s)", vehicleId, startDate, endDate));
    }
}
