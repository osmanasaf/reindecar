package com.reindecar.mapper.damage;

import com.reindecar.common.valueobject.Money;
import com.reindecar.dto.damage.CreateDamageReportRequest;
import com.reindecar.dto.damage.DamageReportResponse;
import com.reindecar.dto.damage.VehicleDamageMapResponse;
import com.reindecar.entity.damage.DamageReport;
import com.reindecar.entity.damage.DamageSeverity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface DamageMapper {

    @Mapping(target = "vehiclePlate", ignore = true)
    @Mapping(target = "damageTypeDisplayName", expression = "java(report.getDamageType().getDisplayName())")
    @Mapping(target = "locationDisplayName", expression = "java(report.getLocation().getDisplayName())")
    @Mapping(target = "zoneId", expression = "java(report.getLocation().getZoneId())")
    @Mapping(target = "severityDisplayName", expression = "java(report.getSeverity().getDisplayName())")
    @Mapping(target = "severityColorCode", expression = "java(report.getSeverity().getColorCode())")
    @Mapping(target = "estimatedCostAmount", expression = "java(toAmount(report.getEstimatedCost()))")
    @Mapping(target = "estimatedCostCurrency", expression = "java(toCurrency(report.getEstimatedCost()))")
    @Mapping(target = "repairCostAmount", expression = "java(toAmount(report.getRepairCost()))")
    @Mapping(target = "repairCostCurrency", expression = "java(toCurrency(report.getRepairCost()))")
    DamageReportResponse toResponse(DamageReport report);

    default DamageReportResponse toResponseWithPlate(DamageReport report, String vehiclePlate) {
        var response = toResponse(report);
        return new DamageReportResponse(
            response.id(),
            response.vehicleId(),
            vehiclePlate,
            response.rentalId(),
            response.reportDate(),
            response.damageType(),
            response.damageTypeDisplayName(),
            response.location(),
            response.locationDisplayName(),
            response.zoneId(),
            response.severity(),
            response.severityDisplayName(),
            response.severityColorCode(),
            response.description(),
            response.estimatedCostAmount(),
            response.estimatedCostCurrency(),
            response.reportedBy(),
            response.repaired(),
            response.repairedDate(),
            response.repairCostAmount(),
            response.repairCostCurrency(),
            response.createdAt(),
            response.updatedAt()
        );
    }

    default VehicleDamageMapResponse toMapResponse(Long vehicleId, String vehiclePlate, List<DamageReport> damages) {
        List<DamageReportResponse> damageResponses = damages.stream()
            .map(d -> toResponseWithPlate(d, vehiclePlate))
            .toList();

        Map<Integer, VehicleDamageMapResponse.ZoneDamageInfo> zones = new HashMap<>();
        
        Map<Integer, List<DamageReport>> damagesByZone = damages.stream()
            .collect(Collectors.groupingBy(d -> d.getLocation().getZoneId()));

        for (Map.Entry<Integer, List<DamageReport>> entry : damagesByZone.entrySet()) {
            int zoneId = entry.getKey();
            List<DamageReport> zoneDamages = entry.getValue();
            
            DamageSeverity maxSeverity = zoneDamages.stream()
                .map(DamageReport::getSeverity)
                .max(Comparator.comparingInt(DamageSeverity::getPriority))
                .orElse(DamageSeverity.MINOR);

            List<Long> damageIds = zoneDamages.stream()
                .map(DamageReport::getId)
                .toList();

            zones.put(zoneId, new VehicleDamageMapResponse.ZoneDamageInfo(
                zoneId,
                zoneDamages.size(),
                maxSeverity,
                maxSeverity.getColorCode(),
                damageIds
            ));
        }

        return new VehicleDamageMapResponse(
            vehicleId,
            vehiclePlate,
            damages.size(),
            zones,
            damageResponses
        );
    }

    default DamageReport toEntity(CreateDamageReportRequest request) {
        Money estimatedCost = request.estimatedCostAmount() != null
            ? Money.of(request.estimatedCostAmount(), 
                request.estimatedCostCurrency() != null ? request.estimatedCostCurrency() : Money.DEFAULT_CURRENCY)
            : null;

        return DamageReport.create(
            request.vehicleId(),
            request.rentalId(),
            request.reportDate(),
            request.damageType(),
            request.location(),
            request.severity(),
            request.description(),
            estimatedCost,
            request.reportedBy()
        );
    }

    default BigDecimal toAmount(Money money) {
        return money != null ? money.getAmount() : null;
    }

    default String toCurrency(Money money) {
        return money != null ? money.getCurrency() : null;
    }
}
