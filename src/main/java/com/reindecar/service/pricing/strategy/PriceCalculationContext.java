package com.reindecar.service.pricing.strategy;

import com.reindecar.common.valueobject.Money;
import com.reindecar.entity.pricing.RentalType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class PriceCalculationContext {
    
    private Long vehicleId;
    private Long categoryId;
    private Long customerId;
    private RentalType rentalType;
    private LocalDate startDate;
    private LocalDate endDate;
    private int totalDays;
    private Money categoryDefaultPrice;
}
