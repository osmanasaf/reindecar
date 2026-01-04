package com.reindecar.dto.vehicle;

import java.math.BigDecimal;

public record VehicleCategoryResponse(
    Long id,
    String code,
    String name,
    String description,
    BigDecimal defaultDailyPrice,
    int sortOrder,
    boolean active
) {}
