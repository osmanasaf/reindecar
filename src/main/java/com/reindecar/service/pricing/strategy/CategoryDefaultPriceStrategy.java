package com.reindecar.service.pricing.strategy;

import com.reindecar.common.valueobject.Money;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CategoryDefaultPriceStrategy implements PriceCalculationStrategy {

    @Override
    public Money calculatePrice(PriceCalculationContext context) {
        log.debug("Applying category default price strategy");
        return context.getCategoryDefaultPrice().multiply(context.getTotalDays());
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
