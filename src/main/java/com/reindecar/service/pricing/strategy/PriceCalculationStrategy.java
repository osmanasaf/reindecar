package com.reindecar.service.pricing.strategy;

import com.reindecar.common.valueobject.Money;

public interface PriceCalculationStrategy {
    
    Money calculatePrice(PriceCalculationContext context);
    
    int getPriority();
    
    String getStrategyName();
}
