package com.reindecar.dto.pricing;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;

import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(description = "Request to update a leasing plan")
public record UpdateLeasingPlanRequest(
    
    @Schema(description = "Vehicle category ID", example = "1")
    Long categoryId,
    
    @Schema(description = "Contract term in months", example = "24")
    @Min(12)
    Integer termMonths,
    
    @Schema(description = "Monthly base price amount", example = "15000.00")
    BigDecimal monthlyBasePrice,
    
    @Schema(description = "Currency", example = "TRY")
    String currency,
    
    @Schema(description = "Included km per month", example = "2500")
    @Min(0)
    Integer includedKmPerMonth,
    
    @Schema(description = "Valid from date")
    LocalDate validFrom,
    
    @Schema(description = "Valid to date")
    LocalDate validTo,
    
    @Schema(description = "Active status")
    Boolean active
) {}
