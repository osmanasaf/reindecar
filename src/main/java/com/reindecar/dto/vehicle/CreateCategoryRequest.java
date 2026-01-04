package com.reindecar.dto.vehicle;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record CreateCategoryRequest(
    @NotBlank(message = "Code is required")
    @Size(max = 10)
    String code,

    @NotBlank(message = "Name is required")
    @Size(max = 100)
    String name,

    @Size(max = 500)
    String description,

    @NotNull(message = "Default daily price is required")
    BigDecimal defaultDailyPrice,

    int sortOrder
) {}
