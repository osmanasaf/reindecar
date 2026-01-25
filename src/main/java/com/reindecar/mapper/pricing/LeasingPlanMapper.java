package com.reindecar.mapper.pricing;

import com.reindecar.common.valueobject.Money;
import com.reindecar.dto.pricing.CreateLeasingPlanRequest;
import com.reindecar.dto.pricing.LeasingPlanResponse;
import com.reindecar.dto.pricing.UpdateLeasingPlanRequest;
import com.reindecar.entity.pricing.LeasingPlan;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = org.mapstruct.NullValuePropertyMappingStrategy.IGNORE)
public interface LeasingPlanMapper {

    @Mapping(target = "categoryName", ignore = true)
    @Mapping(target = "currency", ignore = true)
    @Mapping(target = "totalContractPrice", ignore = true)
    @Mapping(target = "monthlyBasePrice", expression = "java(toAmount(plan.getMonthlyBasePrice()))")
    LeasingPlanResponse toResponse(LeasingPlan plan);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "categoryId", source = "categoryId")
    @Mapping(target = "termMonths", source = "termMonths")
    @Mapping(target = "monthlyBasePrice", expression = "java(toMoney(request.monthlyBasePrice(), request.currency()))")
    @Mapping(target = "includedKmPerMonth", source = "includedKmPerMonth")
    @Mapping(target = "validFrom", source = "validFrom")
    @Mapping(target = "validTo", source = "validTo")
    @Mapping(target = "active", source = "active")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    void updateEntity(@MappingTarget LeasingPlan plan, UpdateLeasingPlanRequest request);

    default Money toMoney(java.math.BigDecimal amount, String currency) {
        if (amount == null) {
            return null;
        }
        String currencyCode = currency != null ? currency : Money.DEFAULT_CURRENCY;
        return Money.of(amount, currencyCode);
    }

    default java.math.BigDecimal toAmount(Money money) {
        return money != null ? money.getAmount() : null;
    }
}
