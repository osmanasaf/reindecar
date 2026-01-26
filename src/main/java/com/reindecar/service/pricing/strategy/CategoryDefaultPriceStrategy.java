package com.reindecar.service.pricing.strategy;

import com.reindecar.common.valueobject.Money;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Varsayılan fiyat stratejisi.
 * Araç veya kategori fiyatı kullanılarak hesaplama yapar.
 * En düşük önceliğe sahiptir (fallback).
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class CategoryDefaultPriceStrategy implements PriceCalculationStrategy {

    @Override
    public Money calculatePrice(PriceCalculationContext context) {
        log.debug("Applying category default price strategy");
        
        // Önce araç günlük fiyatını dene
        Money dailyPrice = context.getDailyPrice();
        if (dailyPrice != null) {
            return dailyPrice.multiply(context.getTotalDays());
        }
        
        // Sonra kategori varsayılan fiyatını dene
        Money categoryPrice = context.getCategoryDefaultPrice();
        if (categoryPrice != null) {
            return categoryPrice.multiply(context.getTotalDays());
        }
        
        return null;
    }

    @Override
    public int getPriority() {
        return 1;
    }

    @Override
    public String getStrategyName() {
        return "Category Default Price";
    }
}
