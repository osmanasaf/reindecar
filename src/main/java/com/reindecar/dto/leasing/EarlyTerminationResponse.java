package com.reindecar.dto.leasing;

import com.reindecar.entity.leasing.EarlyTermination;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

public record EarlyTerminationResponse(
    Long id,
    Long rentalId,
    Long contractId,
    LocalDate terminationDate,
    LocalDate contractEndDate,
    int remainingMonths,
    BigDecimal penaltyRate,
    BigDecimal monthlyRent,
    BigDecimal penaltyAmount,
    BigDecimal excessKmCharge,
    BigDecimal totalAmount,
    String currency,
    String reason,
    EarlyTermination.TerminationStatus status,
    String approvedBy,
    Instant approvedAt,
    Instant createdAt
) {}
