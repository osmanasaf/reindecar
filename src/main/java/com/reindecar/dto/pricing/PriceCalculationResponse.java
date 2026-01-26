package com.reindecar.dto.pricing;

import com.reindecar.entity.pricing.RentalType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record PriceCalculationResponse(
    Long vehicleId,
    String vehicleName,
    Long customerId,
    RentalType rentalType,
    LocalDate startDate,
    LocalDate endDate,
    int totalDays,
    BigDecimal dailyPrice,
    BigDecimal weeklyPrice,
    BigDecimal monthlyPrice,
    BigDecimal unitPrice,
    BigDecimal baseTotal,
    BigDecimal finalTotal,
    String currency,
    KmPackageResponse kmPackage,
    List<PriceBreakdownItem> breakdown
) {}
