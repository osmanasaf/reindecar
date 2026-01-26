package com.reindecar.dto.pricing;

import com.reindecar.entity.pricing.RentalType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;

import java.math.BigDecimal;
import java.util.List;

@Schema(description = "Request to update an existing KM package")
public record UpdateKmPackageRequest(
    @Schema(description = "Name of the KM package", example = "Premium 10000 KM")
    String name,

    @Min(value = 0, message = "Included KM cannot be negative")
    @Schema(description = "Included kilometers in the package", example = "10000")
    Integer includedKm,

    @Min(value = 0, message = "Extra KM price cannot be negative")
    @Schema(description = "Price per extra kilometer", example = "0.75")
    BigDecimal extraKmPrice,

    @Schema(description = "Applicable rental types for this package")
    List<RentalType> applicableTypes,

    @Schema(description = "Whether this package offers unlimited kilometers")
    Boolean unlimited,

    @Schema(description = "Whether this package is active")
    Boolean active,

    @Schema(description = "Category ID (null for global packages)")
    Long categoryId
) {}
