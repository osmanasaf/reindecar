package com.reindecar.mapper.vehicle;

import com.reindecar.common.valueobject.Money;
import com.reindecar.dto.vehicle.CreateVehicleRequest;
import com.reindecar.dto.vehicle.VehicleResponse;
import com.reindecar.entity.vehicle.Vehicle;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface VehicleMapper {

    @Mapping(target = "categoryName", ignore = true)
    @Mapping(target = "branchName", ignore = true)
    @Mapping(target = "dailyPrice", expression = "java(toAmount(vehicle.getDailyPrice()))")
    @Mapping(target = "isInsuranceExpiringSoon", expression = "java(vehicle.isInsuranceExpiringSoon())")
    @Mapping(target = "isInspectionExpiringSoon", expression = "java(vehicle.isInspectionExpiringSoon())")
    VehicleResponse toResponse(Vehicle vehicle);

    default Vehicle toEntity(CreateVehicleRequest request) {
        return Vehicle.create(
            request.plateNumber(),
            request.vinNumber(),
            request.brand(),
            request.model(),
            request.year(),
            request.color(),
            request.fuelType(),
            request.transmission(),
            request.engineCapacity(),
            request.seatCount(),
            request.categoryId(),
            request.branchId(),
            request.currentKm(),
            request.insuranceExpiryDate(),
            request.inspectionExpiryDate(),
            request.registrationDate(),
            toMoney(request.dailyPrice()),
            request.notes()
        );
    }

    default Money toMoney(java.math.BigDecimal amount) {
        return amount != null ? Money.of(amount, Money.DEFAULT_CURRENCY) : null;
    }

    default java.math.BigDecimal toAmount(Money money) {
        return money != null ? money.getAmount() : null;
    }
}
