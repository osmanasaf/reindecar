package com.reindecar.dto.pricing;

import java.math.BigDecimal;
import java.time.LocalDate;

public record SeasonResponse(
    Long id,
    String name,
    LocalDate startDate,
    LocalDate endDate,
    BigDecimal priceMultiplier,
    boolean active
) {}
