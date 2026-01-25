package com.reindecar.dto.reporting;

import com.reindecar.entity.rental.RentalStatus;

import java.time.LocalDate;

public record UpcomingReturnResponse(
        Long rentalId,
        String rentalNumber,
        Long vehicleId,
        Long customerId,
        LocalDate endDate,
        RentalStatus status,
        long daysUntilReturn
) {
}
