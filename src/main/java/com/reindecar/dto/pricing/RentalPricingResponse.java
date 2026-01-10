package com.reindecar.dto.pricing;

import com.reindecar.entity.pricing.RentalType;

import java.math.BigDecimal;
import java.time.LocalDate;

public record RentalPricingResponse(
    Long id,
    Long vehicleId,
    Long customerId,
    Long categoryId,
    RentalType rentalType,
    BigDecimal monthlyPrice,
    String currency,
    int kmLimit,
    BigDecimal extraKmPrice,
    LocalDate validFrom,
    LocalDate validTo,
    boolean active,
    String notes,
    String pricingLevel
) {}
