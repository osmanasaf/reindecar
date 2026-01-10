package com.reindecar.dto.pricing;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record LeasingPriceCalculationResponse(
    Long vehicleId,
    String vehicleName,
    Long customerId,
    int termMonths,
    LocalDate startDate,
    LocalDate endDate,
    
    // Monthly pricing
    BigDecimal monthlyBasePrice,
    BigDecimal monthlyDiscount,
    BigDecimal monthlyNetPrice,
    
    // Total contract pricing
    BigDecimal totalBasePrice,
    BigDecimal totalDiscount,
    BigDecimal totalNetPrice,
    
    // Km package
    int includedKmPerMonth,
    int totalIncludedKm,
    KmPackageResponse kmPackage,
    
    // Currency
    String currency,
    
    // Breakdown
    List<PriceBreakdownItem> breakdown,
    
    // Applied discounts
    List<AppliedDiscount> appliedDiscounts,
    
    // Source
    String pricingSource
) {
    public record AppliedDiscount(
        String name,
        String type,
        BigDecimal value,
        BigDecimal savedAmount
    ) {}
}
