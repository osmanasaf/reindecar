package com.reindecar.dto.rental;

import com.reindecar.entity.pricing.RentalType;
import com.reindecar.entity.rental.RentalStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

public record RentalResponse(
    Long id,
    String rentalNumber,
    RentalType rentalType,
    RentalStatus status,
    Long vehicleId,
    Long customerId,
    Long driverId,
    Long branchId,
    Long returnBranchId,
    LocalDate startDate,
    LocalDate endDate,
    LocalDate actualReturnDate,
    int totalDays,
    int startKm,
    int endKm,
    int totalKm,
    Long kmPackageId,
    BigDecimal dailyPrice,
    BigDecimal totalPrice,
    BigDecimal discountAmount,
    BigDecimal extraKmCharge,
    BigDecimal grandTotal,
    String currency,
    boolean isOverdue,
    int overdueDays,
    String notes,
    Instant createdAt
) {}
