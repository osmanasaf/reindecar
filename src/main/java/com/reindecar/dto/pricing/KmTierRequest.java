package com.reindecar.dto.pricing;

import java.math.BigDecimal;

public record KmTierRequest(
    int fromKm,
    Integer toKm,
    BigDecimal pricePerKm
) {}
