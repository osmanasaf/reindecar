package com.reindecar.dto.pricing;

import java.math.BigDecimal;

public record PriceBreakdownItem(
    String description,
    BigDecimal amount
) {}
