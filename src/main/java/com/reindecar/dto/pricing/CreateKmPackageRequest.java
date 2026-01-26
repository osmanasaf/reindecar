package com.reindecar.dto.pricing;

import com.reindecar.entity.pricing.RentalType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;

@Schema(description = "Request to create a new KM package")
public record CreateKmPackageRequest(
    @NotBlank(message = "Package name is required")
    @Schema(description = "Name of the KM package", example = "Standart 5000 KM")
    String name,

    @NotNull(message = "Included KM is required")
    @Min(value = 0, message = "Included KM cannot be negative")
    @Schema(description = "Included kilometers in the package", example = "5000")
    Integer includedKm,

    @NotNull(message = "Extra KM price is required")
    @Min(value = 0, message = "Extra KM price cannot be negative")
    @Schema(description = "Price per extra kilometer", example = "0.50")
    BigDecimal extraKmPrice,

    @NotEmpty(message = "At least one applicable rental type is required")
    @Schema(description = "Applicable rental types for this package")
    List<RentalType> applicableTypes,

    @Schema(description = "Whether this package offers unlimited kilometers", example = "false")
    Boolean unlimited,

    @Schema(description = "Category ID (null for global packages)", example = "1")
    Long categoryId
) {}
