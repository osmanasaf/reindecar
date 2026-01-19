package com.reindecar.dto.leasing;

public record KmSummaryResponse(
    Long rentalId,
    int totalUsedKm,
    int totalExcessKm,
    int currentRollover,
    int monthlyAllowance,
    int recordCount,
    String lastRecordDate,
    int lastRecordKm
) {}
