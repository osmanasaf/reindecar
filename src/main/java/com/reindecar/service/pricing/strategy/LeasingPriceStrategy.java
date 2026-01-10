package com.reindecar.service.pricing.strategy;

import com.reindecar.common.valueobject.Money;
import com.reindecar.repository.pricing.LeasingPlanRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
@Slf4j
public class LeasingPriceStrategy implements PriceCalculationStrategy {

    private final LeasingPlanRepository leasingPlanRepository;

    @Override
    public Money calculatePrice(PriceCalculationContext context) {
        if (!context.isLeasingCalculation()) {
            return null;
        }

        LocalDate calculationDate = context.getStartDate() != null 
            ? context.getStartDate() 
            : LocalDate.now();

        return leasingPlanRepository.findApplicablePlan(
                context.getCategoryId(),
                context.getEffectiveTermMonths(),
                calculationDate
            )
            .map(plan -> {
                Money monthlyPrice = plan.getMonthlyBasePrice();
                int months = context.getEffectiveTermMonths();
                log.info("Leasing plan found: {} months @ {} / month", months, monthlyPrice);
                return monthlyPrice.multiply(months);
            })
            .orElse(null);
    }

    @Override
    public int getPriority() {
        return 200;
    }

    @Override
    public String getStrategyName() {
        return "LeasingPlanStrategy";
    }
}
