package com.reindecar.exception.rental;

import com.reindecar.common.exception.BusinessException;

import java.time.LocalDate;

public class RentalOverlapException extends BusinessException {

    public RentalOverlapException(Long vehicleId, LocalDate startDate, LocalDate endDate) {
        super("RENTAL_002", 
              String.format("Vehicle %d is not available for dates %s to %s", 
                          vehicleId, startDate, endDate));
    }
}
