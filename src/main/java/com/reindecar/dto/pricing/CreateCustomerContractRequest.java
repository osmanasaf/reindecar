package com.reindecar.dto.pricing;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateCustomerContractRequest(
    @NotNull
    Long customerId,

    @NotNull
    Long categoryId,

    @Min(12)
    int termMonths,

    @NotNull
    BigDecimal negotiatedMonthlyPrice,

    @Min(0)
    int includedKmPerMonth,

    BigDecimal extraKmPrice,

    @NotNull
    LocalDate startDate,

    @Size(max = 500)
    String notes
) {}
