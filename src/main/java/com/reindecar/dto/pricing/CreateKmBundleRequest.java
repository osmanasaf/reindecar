package com.reindecar.dto.pricing;

import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record CreateKmBundleRequest(
    @NotBlank(message = "Name is required")
    String name,

    String description,

    int includedKm,

    List<KmTierRequest> tiers
) {}
