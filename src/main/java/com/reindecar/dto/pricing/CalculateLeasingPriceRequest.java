package com.reindecar.dto.pricing;

import com.reindecar.entity.pricing.RentalType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

@Schema(description = "Request for calculating leasing price")
public record CalculateLeasingPriceRequest(
    
    @Schema(description = "Vehicle ID", example = "1")
    @NotNull
    Long vehicleId,
    
    @Schema(description = "Customer ID (required for corporate contracts)", example = "10")
    Long customerId,
    
    @Schema(description = "Contract term in months", example = "24")
    @NotNull
    @Min(12)
    int termMonths,
    
    @Schema(description = "Contract start date", example = "2026-02-01")
    @NotNull
    LocalDate startDate,
    
    @Schema(description = "Km package ID (optional)")
    Long kmPackageId
) {}
