package com.reindecar.dto.pricing;

import java.math.BigDecimal;
import java.time.LocalDate;

public record LeasingPlanResponse(
    Long id,
    Long categoryId,
    String categoryName,
    int termMonths,
    BigDecimal monthlyBasePrice,
    String currency,
    int includedKmPerMonth,
    BigDecimal totalContractPrice,
    int totalIncludedKm,
    LocalDate validFrom,
    LocalDate validTo,
    boolean active
) {}
