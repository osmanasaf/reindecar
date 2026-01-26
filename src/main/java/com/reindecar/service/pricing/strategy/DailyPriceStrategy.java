package com.reindecar.service.pricing.strategy;

import com.reindecar.common.valueobject.Money;
import com.reindecar.entity.pricing.RentalType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Günlük kiralama fiyat hesaplama stratejisi.
 * Hesaplama: günlük_fiyat × gün_sayısı
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DailyPriceStrategy implements PriceCalculationStrategy {

    @Override
    public Money calculatePrice(PriceCalculationContext context) {
        if (!isApplicable(context)) {
            return null;
        }

        Money dailyPrice = context.getDailyPrice();
        if (dailyPrice == null) {
            log.warn("Günlük fiyat bulunamadı, vehicleId: {}", context.getVehicleId());
            return null;
        }

        Money totalPrice = dailyPrice.multiply(context.getTotalDays());
        log.debug("Günlük fiyat hesaplandı: {} gün x {} = {}",
            context.getTotalDays(), dailyPrice, totalPrice);

        return totalPrice;
    }

    private boolean isApplicable(PriceCalculationContext context) {
        return context.getRentalType() == RentalType.DAILY;
    }

    @Override
    public int getPriority() {
        return 100;
    }

    @Override
    public String getStrategyName() {
        return "Daily Price";
    }
}
