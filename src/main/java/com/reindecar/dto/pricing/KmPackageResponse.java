package com.reindecar.dto.pricing;

import com.reindecar.entity.pricing.RentalType;

import java.math.BigDecimal;
import java.util.List;

public record KmPackageResponse(
    Long id,
    String name,
    int includedKm,
    BigDecimal extraKmPrice,
    String currency,
    List<RentalType> applicableTypes,
    boolean unlimited,
    boolean active,
    Long categoryId,
    String categoryName,
    boolean global
) {}
