package com.reindecar.service.maintenance;

import com.reindecar.common.dto.PageResponse;
import com.reindecar.common.valueobject.Money;
import com.reindecar.dto.maintenance.CreateMaintenanceRecordRequest;
import com.reindecar.dto.maintenance.MaintenanceRecordResponse;
import com.reindecar.dto.maintenance.UpdateMaintenanceRecordRequest;
import com.reindecar.dto.maintenance.VehicleMaintenanceMapResponse;
import com.reindecar.entity.maintenance.MaintenanceRecord;
import com.reindecar.entity.vehicle.Vehicle;
import com.reindecar.exception.maintenance.MaintenanceRecordNotFoundException;
import com.reindecar.exception.vehicle.VehicleNotFoundException;
import com.reindecar.mapper.maintenance.MaintenanceMapper;
import com.reindecar.repository.maintenance.MaintenanceRecordRepository;
import com.reindecar.repository.vehicle.VehicleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class MaintenanceService {

    private final MaintenanceRecordRepository maintenanceRepository;
    private final VehicleRepository vehicleRepository;
    private final MaintenanceMapper maintenanceMapper;

    @Transactional
    public MaintenanceRecordResponse createMaintenanceRecord(CreateMaintenanceRecordRequest request) {
        log.info("Creating maintenance record for vehicle: {}", request.vehicleId());

        Vehicle vehicle = vehicleRepository.findById(request.vehicleId())
            .orElseThrow(() -> new VehicleNotFoundException(request.vehicleId()));

        MaintenanceRecord record = maintenanceMapper.toEntity(request);
        record = maintenanceRepository.save(record);

        return maintenanceMapper.toResponseWithPlate(record, vehicle.getPlateNumber());
    }

    public MaintenanceRecordResponse getMaintenanceById(Long id) {
        MaintenanceRecord record = findById(id);
        String plate = getVehiclePlate(record.getVehicleId());
        return maintenanceMapper.toResponseWithPlate(record, plate);
    }

    @Transactional
    public MaintenanceRecordResponse updateMaintenanceRecord(Long id, UpdateMaintenanceRecordRequest request) {
        log.info("Updating maintenance record: {}", id);

        MaintenanceRecord record = findById(id);

        Money cost = request.costAmount() != null
            ? Money.of(request.costAmount(), 
                request.costCurrency() != null ? request.costCurrency() : Money.DEFAULT_CURRENCY)
            : null;

        record.update(
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

        record = maintenanceRepository.save(record);
        String plate = getVehiclePlate(record.getVehicleId());
        return maintenanceMapper.toResponseWithPlate(record, plate);
    }

    public PageResponse<MaintenanceRecordResponse> getMaintenancesByVehicle(Long vehicleId, Pageable pageable) {
        String plate = getVehiclePlate(vehicleId);
        Page<MaintenanceRecord> records = maintenanceRepository.findByVehicleIdOrderByMaintenanceDateDesc(vehicleId, pageable);
        Page<MaintenanceRecordResponse> responsePage = records.map(r -> maintenanceMapper.toResponseWithPlate(r, plate));
        return PageResponse.of(responsePage);
    }

    public VehicleMaintenanceMapResponse getVehicleMaintenanceMap(Long vehicleId) {
        String plate = getVehiclePlate(vehicleId);
        List<MaintenanceRecord> maintenances = maintenanceRepository.findAllByVehicleId(vehicleId);
        return maintenanceMapper.toMapResponse(vehicleId, plate, maintenances);
    }

    public List<MaintenanceRecord> getAllMaintenancesByVehicle(Long vehicleId) {
        return maintenanceRepository.findAllByVehicleId(vehicleId);
    }

    private MaintenanceRecord findById(Long id) {
        return maintenanceRepository.findById(id)
            .orElseThrow(() -> new MaintenanceRecordNotFoundException(id));
    }

    private String getVehiclePlate(Long vehicleId) {
        return vehicleRepository.findById(vehicleId)
            .map(Vehicle::getPlateNumber)
            .orElse("N/A");
    }
}
