package com.reindecar.mapper.maintenance;

import com.reindecar.common.valueobject.Money;
import com.reindecar.dto.maintenance.CreateMaintenanceRecordRequest;
import com.reindecar.dto.maintenance.MaintenanceRecordResponse;
import com.reindecar.dto.maintenance.VehicleMaintenanceMapResponse;
import com.reindecar.entity.maintenance.MaintenanceRecord;
import com.reindecar.entity.maintenance.MaintenanceType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface MaintenanceMapper {

    @Mapping(target = "vehiclePlate", ignore = true)
    @Mapping(target = "maintenanceTypeDisplayName", expression = "java(record.getMaintenanceType().getDisplayName())")
    @Mapping(target = "maintenanceTypeColorCode", expression = "java(record.getMaintenanceType().getColorCode())")
    @Mapping(target = "costAmount", expression = "java(toAmount(record.getCost()))")
    @Mapping(target = "costCurrency", expression = "java(toCurrency(record.getCost()))")
    MaintenanceRecordResponse toResponse(MaintenanceRecord record);

    default MaintenanceRecordResponse toResponseWithPlate(MaintenanceRecord record, String vehiclePlate) {
        var response = toResponse(record);
        return new MaintenanceRecordResponse(
            response.id(),
            response.vehicleId(),
            vehiclePlate,
            response.maintenanceType(),
            response.maintenanceTypeDisplayName(),
            response.maintenanceTypeColorCode(),
            response.maintenanceDate(),
            response.currentKm(),
            response.costAmount(),
            response.costCurrency(),
            response.serviceProvider(),
            response.description(),
            response.affectedZones(),
            response.partsReplaced(),
            response.paintColor(),
            response.createdAt(),
            response.updatedAt()
        );
    }

    default VehicleMaintenanceMapResponse toMapResponse(Long vehicleId, String vehiclePlate, List<MaintenanceRecord> maintenances) {
        List<MaintenanceRecordResponse> maintenanceResponses = maintenances.stream()
            .map(m -> toResponseWithPlate(m, vehiclePlate))
            .toList();

        Map<Integer, VehicleMaintenanceMapResponse.ZoneMaintenanceInfo> zones = new HashMap<>();
        
        Map<Integer, List<MaintenanceRecord>> maintenancesByZone = new HashMap<>();
        
        for (MaintenanceRecord record : maintenances) {
            if (record.getAffectedZones() != null) {
                for (Integer zoneId : record.getAffectedZones()) {
                    maintenancesByZone.computeIfAbsent(zoneId, k -> new ArrayList<>()).add(record);
                }
            }
        }

        for (Map.Entry<Integer, List<MaintenanceRecord>> entry : maintenancesByZone.entrySet()) {
            int zoneId = entry.getKey();
            List<MaintenanceRecord> zoneMaintenances = entry.getValue();
            
            MaintenanceRecord lastMaintenance = zoneMaintenances.stream()
                .max(Comparator.comparing(MaintenanceRecord::getMaintenanceDate))
                .orElse(null);

            if (lastMaintenance != null) {
                List<Long> maintenanceIds = zoneMaintenances.stream()
                    .map(MaintenanceRecord::getId)
                    .toList();

                zones.put(zoneId, new VehicleMaintenanceMapResponse.ZoneMaintenanceInfo(
                    zoneId,
                    zoneMaintenances.size(),
                    lastMaintenance.getMaintenanceType(),
                    lastMaintenance.getMaintenanceDate(),
                    lastMaintenance.getMaintenanceType().getColorCode(),
                    maintenanceIds
                ));
            }
        }

        return new VehicleMaintenanceMapResponse(
            vehicleId,
            vehiclePlate,
            maintenances.size(),
            zones,
            maintenanceResponses
        );
    }

    default MaintenanceRecord toEntity(CreateMaintenanceRecordRequest request) {
        Money cost = request.costAmount() != null
            ? Money.of(request.costAmount(), 
                request.costCurrency() != null ? request.costCurrency() : Money.DEFAULT_CURRENCY)
            : null;

        return MaintenanceRecord.create(
            request.vehicleId(),
            request.maintenanceType(),
            request.maintenanceDate(),
            request.currentKm(),
            cost,
            request.serviceProvider(),
            request.description(),
            request.affectedZones(),
            request.partsReplaced(),
            request.paintColor()
        );
    }

    default BigDecimal toAmount(Money money) {
        return money != null ? money.getAmount() : null;
    }

    default String toCurrency(Money money) {
        return money != null ? money.getCurrency() : null;
    }
}
