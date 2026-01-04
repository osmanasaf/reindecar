package com.reindecar.service.maintenance;

import com.reindecar.common.dto.PageResponse;
import com.reindecar.common.valueobject.Money;
import com.reindecar.entity.maintenance.MaintenanceRecord;
import com.reindecar.repository.maintenance.MaintenanceRecordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class MaintenanceService {

    private final MaintenanceRecordRepository maintenanceRepository;

    @Transactional
    public MaintenanceRecord recordMaintenance(
            Long vehicleId,
            String maintenanceType,
            LocalDate maintenanceDate,
            int currentKm,
            BigDecimal cost,
            String serviceProvider,
            String description) {
        
        log.info("Recording maintenance for vehicle: {}", vehicleId);

        Money costMoney = Money.of(cost, "TRY");

        MaintenanceRecord record = MaintenanceRecord.create(
            vehicleId,
            maintenanceType,
            maintenanceDate,
            currentKm,
            costMoney,
            serviceProvider,
            description
        );

        return maintenanceRepository.save(record);
    }

    public PageResponse<MaintenanceRecord> getMaintenanceByVehicle(Long vehicleId, Pageable pageable) {
        Page<MaintenanceRecord> records = maintenanceRepository.findByVehicleIdOrderByMaintenanceDateDesc(vehicleId, pageable);
        return PageResponse.of(records);
    }
}
