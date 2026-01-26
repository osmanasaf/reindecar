package com.reindecar.service.pricing.strategy;

import com.reindecar.common.valueobject.Money;
import com.reindecar.entity.pricing.RentalType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.Period;

/**
 * Aylık kiralama fiyat hesaplama stratejisi.
 * Takvim ayına göre hesaplama yapar.
 * Hesaplama: (aylık_fiyat × tam_ay) + (günlük_fiyat × kalan_gün)
 * Örnek: 1 Ocak - 10 Haziran = 5 ay + 9 gün
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class MonthlyPriceStrategy implements PriceCalculationStrategy {

    @Override
    public Money calculatePrice(PriceCalculationContext context) {
        if (!isApplicable(context)) {
            return null;
        }

        Money monthlyPrice = context.getMonthlyPrice();
        Money dailyPrice = context.getDailyPrice();

        if (monthlyPrice == null) {
            log.warn("Aylık fiyat bulunamadı, vehicleId: {}", context.getVehicleId());
            return null;
        }

        LocalDate startDate = context.getStartDate();
        LocalDate endDate = context.getEndDate();

        // Takvim ayına göre hesapla
        Period period = Period.between(startDate, endDate);
        int fullMonths = period.getYears() * 12 + period.getMonths();
        int remainingDays = period.getDays();

        Money monthsTotal = monthlyPrice.multiply(fullMonths);
        Money totalPrice = monthsTotal;

        if (remainingDays > 0 && dailyPrice != null) {
            Money daysTotal = dailyPrice.multiply(remainingDays);
            totalPrice = monthsTotal.add(daysTotal);
            log.debug("Aylık fiyat hesaplandı: {} ay x {} + {} gün x {} = {}",
                fullMonths, monthlyPrice, remainingDays, dailyPrice, totalPrice);
        } else {
            log.debug("Aylık fiyat hesaplandı: {} ay x {} = {}",
                fullMonths, monthlyPrice, totalPrice);
        }

        return totalPrice;
    }

    private boolean isApplicable(PriceCalculationContext context) {
        return context.getRentalType() == RentalType.MONTHLY;
    }

    @Override
    public int getPriority() {
        return 100;
    }

    @Override
    public String getStrategyName() {
        return "Monthly Price";
    }
}
