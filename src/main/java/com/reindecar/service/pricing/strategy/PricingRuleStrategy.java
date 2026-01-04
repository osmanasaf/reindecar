package com.reindecar.service.pricing.strategy;

import com.reindecar.common.valueobject.Money;
import com.reindecar.entity.pricing.PricingRule;
import com.reindecar.repository.pricing.PricingRuleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class PricingRuleStrategy implements PriceCalculationStrategy {

    private final PricingRuleRepository pricingRuleRepository;

    @Override
    public Money calculatePrice(PriceCalculationContext context) {
        log.debug("Applying pricing rule strategy");

        Optional<PricingRule> applicableRule = pricingRuleRepository
            .findApplicableRules(
                context.getCategoryId(),
                context.getRentalType(),
                context.getTotalDays(),
                context.getStartDate()
            )
            .stream()
            .findFirst();

        if (applicableRule.isPresent()) {
            Money price = applicableRule.get().calculatePrice(context.getTotalDays());
            log.info("Applied pricing rule: {} days at {}", context.getTotalDays(), price);
            return price;
        }

        return null;
    }

    @Override
    public int getPriority() {
        return 2;
    }

    @Override
    public String getStrategyName() {
        return "Pricing Rule";
    }
}
