package com.reindecar.dto.leasing;

import java.time.Instant;
import java.time.LocalDate;

public record KmRecordResponse(
    Long id,
    Long rentalId,
    LocalDate recordDate,
    String periodYearMonth,
    int currentKm,
    int previousKm,
    int usedKm,
    int monthlyAllowance,
    int excessKm,
    int rolloverFromPrevious,
    int rolloverToNext,
    String recordedBy,
    Instant createdAt
) {}
