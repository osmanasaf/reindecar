package com.reindecar.service.pricing.strategy;

import com.reindecar.common.valueobject.Money;
import com.reindecar.entity.pricing.RentalType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.Period;

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

    // Araç fiyatları (doğrudan Vehicle'dan)
    private Money dailyPrice;
    private Money weeklyPrice;
    private Money monthlyPrice;

    // Kategori varsayılan fiyatı (geriye uyumluluk için)
    private Money categoryDefaultPrice;

    // Leasing alanları
    private int termMonths;
    private Long kmPackageId;
    private boolean isLeasing;

    public boolean isDailyRental() {
        return rentalType == RentalType.DAILY;
    }

    public boolean isWeeklyRental() {
        return rentalType == RentalType.WEEKLY;
    }

    public boolean isMonthlyRental() {
        return rentalType == RentalType.MONTHLY;
    }

    public boolean isLeasingRental() {
        return rentalType == RentalType.LEASING;
    }

    public boolean isLeasingCalculation() {
        return isLeasing || rentalType == RentalType.LEASING;
    }

    public int getEffectiveTermMonths() {
        if (termMonths > 0) {
            return termMonths;
        }
        if ((isMonthlyRental() || isLeasingCalculation()) && startDate != null && endDate != null) {
            Period period = Period.between(startDate, endDate);
            return period.getYears() * 12 + period.getMonths() + (period.getDays() > 0 ? 1 : 0);
        }
        return 1;
    }
}
