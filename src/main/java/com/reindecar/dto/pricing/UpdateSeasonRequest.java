package com.reindecar.dto.pricing;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public record UpdateSeasonRequest(
    @Size(max = 100)
    String name,

    LocalDate startDate,

    LocalDate endDate,

    @DecimalMin("0.01")
    BigDecimal priceMultiplier,

    Boolean active
) {}
