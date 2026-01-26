package com.reindecar.service.vehicle;

import com.reindecar.dto.vehicle.VehicleCombinedMapResponse;
import com.reindecar.dto.vehicle.VehicleCombinedMapResponse.*;
import com.reindecar.entity.damage.DamageReport;
import com.reindecar.entity.damage.DamageSeverity;
import com.reindecar.entity.maintenance.MaintenanceRecord;
import com.reindecar.entity.maintenance.MaintenanceType;
import com.reindecar.entity.vehicle.Vehicle;
import com.reindecar.exception.vehicle.VehicleNotFoundException;
import com.reindecar.repository.damage.DamageReportRepository;
import com.reindecar.repository.maintenance.MaintenanceRecordRepository;
import com.reindecar.repository.vehicle.VehicleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class VehicleCombinedMapService {

    private final VehicleRepository vehicleRepository;
    private final DamageReportRepository damageRepository;
    private final MaintenanceRecordRepository maintenanceRepository;

    public VehicleCombinedMapResponse getCombinedMap(Long vehicleId) {
        log.info("Fetching combined damage + maintenance map for vehicle: {}", vehicleId);

        Vehicle vehicle = vehicleRepository.findById(vehicleId)
            .orElseThrow(() -> new VehicleNotFoundException(vehicleId));

        List<DamageReport> activeDamages = damageRepository.findActiveByVehicleId(vehicleId);
        List<MaintenanceRecord> maintenances = maintenanceRepository.findAllByVehicleId(vehicleId);

        Map<Integer, ZoneCombinedInfo> zones = buildCombinedZones(activeDamages, maintenances);

        ColorLegend legend = buildColorLegend();

        return new VehicleCombinedMapResponse(
            vehicleId,
            vehicle.getPlateNumber(),
            activeDamages.size(),
            maintenances.size(),
            zones,
            legend
        );
    }

    private Map<Integer, ZoneCombinedInfo> buildCombinedZones(
            List<DamageReport> damages, 
            List<MaintenanceRecord> maintenances) {
        
        Map<Integer, List<DamageReport>> damagesByZone = damages.stream()
            .collect(Collectors.groupingBy(d -> d.getLocation().getZoneId()));

        Map<Integer, List<MaintenanceRecord>> maintenancesByZone = new HashMap<>();
        for (MaintenanceRecord record : maintenances) {
            if (record.getAffectedZones() != null) {
                for (Integer zoneId : record.getAffectedZones()) {
                    maintenancesByZone.computeIfAbsent(zoneId, k -> new ArrayList<>()).add(record);
                }
            }
        }

        Set<Integer> allZones = new HashSet<>();
        allZones.addAll(damagesByZone.keySet());
        allZones.addAll(maintenancesByZone.keySet());

        Map<Integer, ZoneCombinedInfo> result = new HashMap<>();

        for (Integer zoneId : allZones) {
            List<DamageReport> zoneDamages = damagesByZone.getOrDefault(zoneId, List.of());
            List<MaintenanceRecord> zoneMaintenances = maintenancesByZone.getOrDefault(zoneId, List.of());

            DamageSeverity maxSeverity = zoneDamages.stream()
                .map(DamageReport::getSeverity)
                .max(Comparator.comparingInt(DamageSeverity::getPriority))
                .orElse(null);

            MaintenanceRecord lastMaintenance = zoneMaintenances.stream()
                .max(Comparator.comparing(MaintenanceRecord::getMaintenanceDate))
                .orElse(null);

            List<Long> damageIds = zoneDamages.stream()
                .map(DamageReport::getId)
                .toList();

            List<Long> maintenanceIds = zoneMaintenances.stream()
                .map(MaintenanceRecord::getId)
                .toList();

            result.put(zoneId, new ZoneCombinedInfo(
                zoneId,
                zoneDamages.size(),
                zoneMaintenances.size(),
                maxSeverity,
                maxSeverity != null ? maxSeverity.getColorCode() : null,
                lastMaintenance != null ? lastMaintenance.getMaintenanceType() : null,
                lastMaintenance != null ? lastMaintenance.getMaintenanceDate() : null,
                lastMaintenance != null ? lastMaintenance.getMaintenanceType().getColorCode() : null,
                !zoneDamages.isEmpty(),
                !zoneMaintenances.isEmpty(),
                damageIds,
                maintenanceIds
            ));
        }

        return result;
    }

    private ColorLegend buildColorLegend() {
        Map<String, String> damageSeverityColors = new LinkedHashMap<>();
        for (DamageSeverity severity : DamageSeverity.values()) {
            damageSeverityColors.put(severity.name(), severity.getColorCode());
        }

        Map<String, String> maintenanceTypeColors = new LinkedHashMap<>();
        for (MaintenanceType type : MaintenanceType.values()) {
            maintenanceTypeColors.put(type.name(), type.getColorCode());
        }

        return new ColorLegend(damageSeverityColors, maintenanceTypeColors);
    }
}
