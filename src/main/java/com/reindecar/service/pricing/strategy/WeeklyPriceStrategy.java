package com.reindecar.service.pricing.strategy;

import com.reindecar.common.valueobject.Money;
import com.reindecar.entity.pricing.RentalType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Haftalık kiralama fiyat hesaplama stratejisi.
 * Hesaplama: (haftalık_fiyat × tam_hafta) + (günlük_fiyat × kalan_gün)
 * Örnek: 30 gün = (4 hafta × haftalık) + (2 gün × günlük)
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class WeeklyPriceStrategy implements PriceCalculationStrategy {

    private static final int DAYS_PER_WEEK = 7;

    @Override
    public Money calculatePrice(PriceCalculationContext context) {
        if (!isApplicable(context)) {
            return null;
        }

        Money weeklyPrice = context.getWeeklyPrice();
        Money dailyPrice = context.getDailyPrice();

        if (weeklyPrice == null) {
            log.warn("Haftalık fiyat bulunamadı, vehicleId: {}", context.getVehicleId());
            return null;
        }

        int totalDays = context.getTotalDays();
        int fullWeeks = totalDays / DAYS_PER_WEEK;
        int remainingDays = totalDays % DAYS_PER_WEEK;

        Money weeksTotal = weeklyPrice.multiply(fullWeeks);
        Money totalPrice = weeksTotal;

        if (remainingDays > 0 && dailyPrice != null) {
            Money daysTotal = dailyPrice.multiply(remainingDays);
            totalPrice = weeksTotal.add(daysTotal);
            log.debug("Haftalık fiyat hesaplandı: {} hafta x {} + {} gün x {} = {}",
                fullWeeks, weeklyPrice, remainingDays, dailyPrice, totalPrice);
        } else {
            log.debug("Haftalık fiyat hesaplandı: {} hafta x {} = {}",
                fullWeeks, weeklyPrice, totalPrice);
        }

        return totalPrice;
    }

    private boolean isApplicable(PriceCalculationContext context) {
        return context.getRentalType() == RentalType.WEEKLY;
    }

    @Override
    public int getPriority() {
        return 100;
    }

    @Override
    public String getStrategyName() {
        return "Weekly Price";
    }
}
