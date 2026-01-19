package com.reindecar.dto.pricing;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateSeasonRequest(
    @NotBlank
    @Size(max = 100)
    String name,

    @NotNull
    LocalDate startDate,

    @NotNull
    LocalDate endDate,

    @NotNull
    @DecimalMin("0.01")
    BigDecimal priceMultiplier
) {}
