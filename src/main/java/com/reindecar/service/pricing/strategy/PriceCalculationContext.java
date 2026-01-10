package com.reindecar.service.pricing.strategy;

import com.reindecar.common.valueobject.Money;
import com.reindecar.entity.pricing.RentalType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class PriceCalculationContext {
    
    private Long vehicleId;
    private Long categoryId;
    private Long customerId;
    private RentalType rentalType;
    private LocalDate startDate;
    private LocalDate endDate;
    private int totalDays;
    private Money categoryDefaultPrice;
    
    // Leasing-specific fields
    private int termMonths;
    private Long kmPackageId;
    private boolean isLeasing;
    
    public boolean isLeasingCalculation() {
        return isLeasing || rentalType == RentalType.LEASING;
    }
    
    public boolean isMonthlyRental() {
        return rentalType == RentalType.MONTHLY;
    }
    
    public int getEffectiveTermMonths() {
        if (termMonths > 0) return termMonths;
        // Calculate months from date range for monthly rentals
        if (isMonthlyRental() && startDate != null && endDate != null) {
            return (int) java.time.Period.between(startDate, endDate).toTotalMonths() + 1;
        }
        return 1;
    }
}
