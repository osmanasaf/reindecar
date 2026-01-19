package com.reindecar.dto.pricing;

import com.reindecar.entity.pricing.CustomerContract;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

public record CustomerContractResponse(
    Long id,
    Long customerId,
    Long categoryId,
    String contractNumber,
    int termMonths,
    BigDecimal negotiatedMonthlyPrice,
    String currency,
    int includedKmPerMonth,
    BigDecimal extraKmPrice,
    LocalDate startDate,
    LocalDate endDate,
    CustomerContract.ContractStatus status,
    String notes,
    BigDecimal totalContractPrice,
    int totalIncludedKm,
    Instant createdAt
) {}
