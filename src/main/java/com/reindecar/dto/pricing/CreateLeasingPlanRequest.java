package com.reindecar.dto.pricing;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(description = "Request to create a leasing plan")
public record CreateLeasingPlanRequest(
    
    @Schema(description = "Vehicle category ID", example = "1")
    @NotNull
    Long categoryId,
    
    @Schema(description = "Contract term in months", example = "24")
    @NotNull
    @Min(12)
    int termMonths,
    
    @Schema(description = "Monthly base price amount", example = "15000.00")
    @NotNull
    BigDecimal monthlyBasePrice,
    
    @Schema(description = "Currency", example = "TRY")
    String currency,
    
    @Schema(description = "Included km per month", example = "2500")
    @NotNull
    @Min(0)
    int includedKmPerMonth,
    
    @Schema(description = "Valid from date")
    LocalDate validFrom,
    
    @Schema(description = "Valid to date")
    LocalDate validTo
) {}
