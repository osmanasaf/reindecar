package com.reindecar.service.pricing.strategy;

import com.reindecar.common.valueobject.Money;
import com.reindecar.entity.pricing.RentalType;
import com.reindecar.entity.pricing.TermDiscount;
import com.reindecar.repository.pricing.TermDiscountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Yıllık/Leasing fiyat hesaplama stratejisi.
 * 12+ ay kiralamalar için kullanılır.
 * Hesaplama: (aylık_fiyat × vade_ay) - vade_iskontosu
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class YearlyLeasingStrategy implements PriceCalculationStrategy {

    private static final int MIN_LEASING_MONTHS = 12;

    private final TermDiscountRepository termDiscountRepository;

    @Override
    public Money calculatePrice(PriceCalculationContext context) {
        if (!isApplicable(context)) {
            return null;
        }

        Money monthlyPrice = context.getMonthlyPrice();
        if (monthlyPrice == null) {
            log.warn("Aylık fiyat bulunamadı, vehicleId: {}", context.getVehicleId());
            return null;
        }

        int termMonths = context.getTermMonths();
        if (termMonths < MIN_LEASING_MONTHS) {
            termMonths = MIN_LEASING_MONTHS;
        }

        Money basePrice = monthlyPrice.multiply(termMonths);
        Money finalPrice = applyTermDiscount(basePrice, context.getCategoryId(), termMonths);

        log.debug("Yıllık/Leasing fiyat hesaplandı: {} ay x {} = {}, iskonto sonrası: {}",
            termMonths, monthlyPrice, basePrice, finalPrice);

        return finalPrice;
    }

    private Money applyTermDiscount(Money basePrice, Long categoryId, int termMonths) {
        return termDiscountRepository.findBestDiscount(categoryId, termMonths)
            .map(discount -> {
                Money discountedPrice = discount.applyDiscount(basePrice);
                log.debug("Vade iskontosu uygulandı: {} ay, tip: {}, değer: {}",
                    termMonths, discount.getDiscountType(), discount.getDiscountValue());
                return discountedPrice;
            })
            .orElse(basePrice);
    }

    private boolean isApplicable(PriceCalculationContext context) {
        return context.getRentalType() == RentalType.LEASING;
    }

    @Override
    public int getPriority() {
        return 100;
    }

    @Override
    public String getStrategyName() {
        return "Yearly/Leasing Price";
    }

    public Money calculateWithCustomDiscount(PriceCalculationContext context, TermDiscount customDiscount) {
        Money monthlyPrice = context.getMonthlyPrice();
        if (monthlyPrice == null) {
            return null;
        }

        int termMonths = context.getTermMonths();
        Money basePrice = monthlyPrice.multiply(termMonths);

        if (customDiscount != null) {
            return customDiscount.applyDiscount(basePrice);
        }

        return basePrice;
    }
}
