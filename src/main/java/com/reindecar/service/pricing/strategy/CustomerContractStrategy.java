package com.reindecar.service.pricing.strategy;

import com.reindecar.common.valueobject.Money;
import com.reindecar.repository.pricing.CustomerContractRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
@Slf4j
public class CustomerContractStrategy implements PriceCalculationStrategy {

    private final CustomerContractRepository customerContractRepository;

    @Override
    public Money calculatePrice(PriceCalculationContext context) {
        if (context.getCustomerId() == null) {
            return null;
        }

        if (!context.isLeasingCalculation() && !context.isMonthlyRental()) {
            return null;
        }

        LocalDate calculationDate = context.getStartDate() != null 
            ? context.getStartDate() 
            : LocalDate.now();

        return customerContractRepository.findActiveContract(
                context.getCustomerId(),
                context.getCategoryId(),
                calculationDate
            )
            .map(contract -> {
                Money monthlyPrice = contract.getNegotiatedMonthlyPrice();
                int months = context.getEffectiveTermMonths();
                log.info("Customer contract found: {} @ {} / month for {} months", 
                    contract.getContractNumber(), monthlyPrice, months);
                return monthlyPrice.multiply(months);
            })
            .orElse(null);
    }

    @Override
    public int getPriority() {
        return 300;
    }

    @Override
    public String getStrategyName() {
        return "CustomerContractStrategy";
    }
}
