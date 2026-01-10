package com.reindecar.dto.pricing;

import java.math.BigDecimal;

public record KmTierResponse(
    int fromKm,
    Integer toKm,
    BigDecimal pricePerKm,
    String currency
) {}
