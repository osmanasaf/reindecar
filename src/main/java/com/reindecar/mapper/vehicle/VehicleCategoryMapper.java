package com.reindecar.mapper.vehicle;

import com.reindecar.common.valueobject.Money;
import com.reindecar.dto.vehicle.CreateCategoryRequest;
import com.reindecar.dto.vehicle.VehicleCategoryResponse;
import com.reindecar.entity.vehicle.VehicleCategory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface VehicleCategoryMapper {

    @Mapping(target = "defaultDailyPrice", expression = "java(toAmount(category.getDefaultDailyPrice()))")
    VehicleCategoryResponse toResponse(VehicleCategory category);

    default VehicleCategory toEntity(CreateCategoryRequest request) {
        return VehicleCategory.create(
            request.code(),
            request.name(),
            request.description(),
            Money.of(request.defaultDailyPrice(), Money.DEFAULT_CURRENCY),
            request.sortOrder()
        );
    }

    default java.math.BigDecimal toAmount(Money money) {
        return money != null ? money.getAmount() : null;
    }
}
