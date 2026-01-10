package com.reindecar.dto.pricing;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

@Schema(description = "Km kademesi fiyatı")
public record KmTierRequest(
    @Min(value = 0, message = "From km cannot be negative")
    @Schema(description = "Kademe başlangıç km", example = "0")
    int fromKm,

    @Schema(description = "Kademe bitiş km (null = sınırsız)", example = "500")
    Integer toKm,

    @NotNull(message = "Price per km is required")
    @Schema(description = "Km başına ücret (TRY)", example = "5.00")
    BigDecimal pricePerKm
) {}
