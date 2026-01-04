package com.reindecar.dto.pricing;

import com.reindecar.entity.pricing.RentalType;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record CalculatePriceRequest(
    @NotNull(message = "Vehicle ID is required")
    Long vehicleId,

    Long customerId,

    @NotNull(message = "Rental type is required")
    RentalType rentalType,

    @NotNull(message = "Start date is required")
    LocalDate startDate,

    @NotNull(message = "End date is required")
    LocalDate endDate,

    Long kmPackageId
) {}
