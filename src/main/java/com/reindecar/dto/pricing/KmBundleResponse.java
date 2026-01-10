package com.reindecar.dto.pricing;

import java.math.BigDecimal;
import java.util.List;

public record KmBundleResponse(
    Long id,
    String name,
    String description,
    int includedKm,
    List<KmTierResponse> tiers,
    boolean active
) {}
