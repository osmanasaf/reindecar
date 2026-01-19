package com.reindecar.dto.leasing;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record RecordKmRequest(
    @NotNull
    @Min(0)
    Integer currentKm,

    LocalDate recordDate
) {}
