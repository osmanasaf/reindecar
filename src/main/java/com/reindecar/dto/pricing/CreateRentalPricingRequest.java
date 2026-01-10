package com.reindecar.dto.pricing;

import com.reindecar.entity.pricing.RentalType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateRentalPricingRequest(
    Long vehicleId,
    Long customerId,
    Long categoryId,

    @NotNull(message = "Rental type is required")
    RentalType rentalType,

    @NotNull(message = "Monthly price is required")
    BigDecimal monthlyPrice,

    @Min(value = 0, message = "Km limit cannot be negative")
    int kmLimit,

    @NotNull(message = "Extra km price is required")
    BigDecimal extraKmPrice,

    LocalDate validFrom,
    LocalDate validTo,
    String notes
) {}
