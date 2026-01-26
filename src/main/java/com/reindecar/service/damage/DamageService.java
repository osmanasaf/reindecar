package com.reindecar.service.damage;

import com.reindecar.common.dto.PageResponse;
import com.reindecar.common.valueobject.Money;
import com.reindecar.dto.damage.CreateDamageReportRequest;
import com.reindecar.dto.damage.DamageReportResponse;
import com.reindecar.dto.damage.MarkDamageRepairedRequest;
import com.reindecar.dto.damage.UpdateDamageReportRequest;
import com.reindecar.dto.damage.VehicleDamageMapResponse;
import com.reindecar.entity.damage.DamageReport;
import com.reindecar.entity.vehicle.Vehicle;
import com.reindecar.exception.damage.DamageReportNotFoundException;
import com.reindecar.exception.vehicle.VehicleNotFoundException;
import com.reindecar.mapper.damage.DamageMapper;
import com.reindecar.repository.damage.DamageReportRepository;
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
public class DamageService {

    private final DamageReportRepository damageRepository;
    private final VehicleRepository vehicleRepository;
    private final DamageMapper damageMapper;

    @Transactional
    public DamageReportResponse createDamageReport(CreateDamageReportRequest request) {
        log.info("Creating damage report for vehicle: {}", request.vehicleId());

        Vehicle vehicle = vehicleRepository.findById(request.vehicleId())
            .orElseThrow(() -> new VehicleNotFoundException(request.vehicleId()));

        DamageReport report = damageMapper.toEntity(request);
        report = damageRepository.save(report);

        return damageMapper.toResponseWithPlate(report, vehicle.getPlateNumber());
    }

    public DamageReportResponse getDamageById(Long id) {
        DamageReport report = findById(id);
        String plate = getVehiclePlate(report.getVehicleId());
        return damageMapper.toResponseWithPlate(report, plate);
    }

    @Transactional
    public DamageReportResponse updateDamageReport(Long id, UpdateDamageReportRequest request) {
        log.info("Updating damage report: {}", id);

        DamageReport report = findById(id);

        Money estimatedCost = request.estimatedCostAmount() != null
            ? Money.of(request.estimatedCostAmount(), 
                request.estimatedCostCurrency() != null ? request.estimatedCostCurrency() : Money.DEFAULT_CURRENCY)
            : null;

        report.update(
            request.damageType(),
            request.location(),
            request.severity(),
            request.description(),
            estimatedCost
        );

        report = damageRepository.save(report);
        String plate = getVehiclePlate(report.getVehicleId());
        return damageMapper.toResponseWithPlate(report, plate);
    }

    @Transactional
    public DamageReportResponse markAsRepaired(Long id, MarkDamageRepairedRequest request) {
        log.info("Marking damage as repaired: {}", id);

        DamageReport report = findById(id);

        Money repairCost = request.repairCostAmount() != null
            ? Money.of(request.repairCostAmount(), 
                request.repairCostCurrency() != null ? request.repairCostCurrency() : Money.DEFAULT_CURRENCY)
            : null;

        report.markAsRepaired(request.repairedDate(), repairCost);
        report = damageRepository.save(report);

        String plate = getVehiclePlate(report.getVehicleId());
        return damageMapper.toResponseWithPlate(report, plate);
    }

    public PageResponse<DamageReportResponse> getDamagesByVehicle(Long vehicleId, Pageable pageable) {
        String plate = getVehiclePlate(vehicleId);
        Page<DamageReport> reports = damageRepository.findByVehicleIdOrderByReportDateDesc(vehicleId, pageable);
        Page<DamageReportResponse> responsePage = reports.map(r -> damageMapper.toResponseWithPlate(r, plate));
        return PageResponse.of(responsePage);
    }

    public PageResponse<DamageReportResponse> getDamagesByRental(Long rentalId, Pageable pageable) {
        Page<DamageReport> reports = damageRepository.findByRentalIdOrderByReportDateDesc(rentalId, pageable);
        Page<DamageReportResponse> responsePage = reports.map(r -> {
            String plate = getVehiclePlate(r.getVehicleId());
            return damageMapper.toResponseWithPlate(r, plate);
        });
        return PageResponse.of(responsePage);
    }

    public VehicleDamageMapResponse getVehicleDamageMap(Long vehicleId) {
        String plate = getVehiclePlate(vehicleId);
        List<DamageReport> activeDamages = damageRepository.findActiveByVehicleId(vehicleId);
        return damageMapper.toMapResponse(vehicleId, plate, activeDamages);
    }

    public List<DamageReport> getAllDamagesByVehicle(Long vehicleId) {
        return damageRepository.findAllByVehicleId(vehicleId);
    }

    private DamageReport findById(Long id) {
        return damageRepository.findById(id)
            .orElseThrow(() -> new DamageReportNotFoundException(id));
    }

    private String getVehiclePlate(Long vehicleId) {
        return vehicleRepository.findById(vehicleId)
            .map(Vehicle::getPlateNumber)
            .orElse("N/A");
    }
}
