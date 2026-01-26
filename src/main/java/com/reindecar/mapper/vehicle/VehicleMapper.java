package com.reindecar.mapper.vehicle;

import com.reindecar.common.valueobject.Money;
import com.reindecar.dto.vehicle.CreateVehicleRequest;
import com.reindecar.dto.vehicle.VehicleResponse;
import com.reindecar.entity.vehicle.Vehicle;
import com.reindecar.dto.vehicle.UpdateVehicleDetailsRequest;
import com.reindecar.dto.vehicle.VehicleDetailsResponse;
import com.reindecar.entity.vehicle.VehicleDetails;
import org.mapstruct.*;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface VehicleMapper {

    @Mapping(target = "categoryName", ignore = true)
    @Mapping(target = "branchName", ignore = true)
    @Mapping(target = "dailyPrice", expression = "java(toAmount(vehicle.getDailyPrice()))")
    @Mapping(target = "weeklyPrice", expression = "java(toAmount(vehicle.getWeeklyPrice()))")
    @Mapping(target = "monthlyPrice", expression = "java(toAmount(vehicle.getMonthlyPrice()))")
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
            toMoney(request.weeklyPrice()),
            toMoney(request.monthlyPrice()),
            request.notes()
        );
    }

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "vehicleId", ignore = true)
    @Mapping(target = "hgsLastUpdated", ignore = true) // Updated manually or via entity listener if changed
    @Mapping(target = "hgsBalance", source = "hgsBalance")
    @Mapping(target = "remainingCreditAmount", source = "remainingCreditAmount")
    @Mapping(target = "purchasePrice", source = "purchasePrice")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "registrationDate", ignore = true)
    @Mapping(target = "lastServiceDate", ignore = true)
    void updateDetails(@MappingTarget VehicleDetails details, UpdateVehicleDetailsRequest request);

    default Money toMoney(java.math.BigDecimal amount) {
        return amount != null ? Money.of(amount, Money.DEFAULT_CURRENCY) : null;
    }

    default java.math.BigDecimal toAmount(Money money) {
        return money != null ? money.getAmount() : null;
    }
}
