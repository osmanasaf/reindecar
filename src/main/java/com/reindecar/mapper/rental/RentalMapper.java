package com.reindecar.mapper.rental;

import com.reindecar.dto.rental.RentalResponse;
import com.reindecar.entity.rental.Rental;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RentalMapper {

    @Mapping(target = "totalDays", expression = "java(rental.getTotalDays())")
    @Mapping(target = "totalKm", expression = "java(rental.getTotalKm())")
    @Mapping(target = "dailyPrice", expression = "java(toAmount(rental.getDailyPrice()))")
    @Mapping(target = "totalPrice", expression = "java(toAmount(rental.getTotalPrice()))")
    @Mapping(target = "discountAmount", expression = "java(toAmount(rental.getDiscountAmount()))")
    @Mapping(target = "extraKmCharge", expression = "java(toAmount(rental.getExtraKmCharge()))")
    @Mapping(target = "grandTotal", expression = "java(toAmount(rental.getGrandTotal()))")
    @Mapping(target = "currency", expression = "java(rental.getDailyPrice().getCurrency())")
    @Mapping(target = "isOverdue", expression = "java(rental.isOverdue())")
    @Mapping(target = "overdueDays", expression = "java(rental.getOverdueDays())")
    @Mapping(target = "driverId", ignore = true)
    RentalResponse toResponse(Rental rental);

    default java.math.BigDecimal toAmount(com.reindecar.common.valueobject.Money money) {
        return money != null ? money.getAmount() : null;
    }
}
