package com.reindecar.dto.pricing;

import java.math.BigDecimal;

public record KmPackageResponse(
    Long id,
    String name,
    int includedKm,
    BigDecimal extraKmPrice,
    boolean unlimited
) {}
