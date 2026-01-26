package com.reindecar.service.vehicle;

import com.reindecar.common.valueobject.Money;
import com.reindecar.dto.vehicle.VehicleHistoryResponse;
import com.reindecar.dto.vehicle.VehicleHistoryResponse.*;
import com.reindecar.entity.damage.DamageReport;
import com.reindecar.entity.customer.Customer;
import com.reindecar.entity.maintenance.MaintenanceRecord;
import com.reindecar.entity.rental.Rental;
import com.reindecar.entity.vehicle.Vehicle;
import com.reindecar.entity.vehicle.VehicleStatusHistory;
import com.reindecar.exception.vehicle.VehicleNotFoundException;
import com.reindecar.repository.customer.CustomerRepository;
import com.reindecar.repository.vehicle.VehicleRepository;
import com.reindecar.repository.vehicle.VehicleStatusHistoryRepository;
import com.reindecar.service.damage.DamageService;
import com.reindecar.service.maintenance.MaintenanceService;
import com.reindecar.service.rental.RentalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class VehicleHistoryService {

    private final VehicleRepository vehicleRepository;
    private final VehicleStatusHistoryRepository statusHistoryRepository;
    private final RentalService rentalService;
    private final DamageService damageService;
    private final MaintenanceService maintenanceService;
    private final CustomerRepository customerRepository;

    public VehicleHistoryResponse getVehicleHistory(Long vehicleId) {
        log.info("Fetching complete history for vehicle: {}", vehicleId);

        Vehicle vehicle = vehicleRepository.findById(vehicleId)
            .orElseThrow(() -> new VehicleNotFoundException(vehicleId));

        String vehicleName = vehicle.getBrand() + " " + vehicle.getModel();

        List<Rental> rentals = rentalService.getRentalsByVehicleId(vehicleId);
        List<DamageReport> damages = damageService.getAllDamagesByVehicle(vehicleId);
        List<MaintenanceRecord> maintenances = maintenanceService.getAllMaintenancesByVehicle(vehicleId);
        List<VehicleStatusHistory> statusChanges = statusHistoryRepository.findAllByVehicleId(vehicleId);
        Map<Long, String> customerNames = loadCustomerNames(rentals);

        return new VehicleHistoryResponse(
            vehicleId,
            vehicle.getPlateNumber(),
            vehicleName,
            mapRentals(rentals, customerNames),
            mapMaintenances(maintenances),
            mapDamages(damages),
            mapStatusChanges(statusChanges)
        );
    }

    private Map<Long, String> loadCustomerNames(List<Rental> rentals) {
        Set<Long> customerIds = rentals.stream()
            .map(Rental::getCustomerId)
            .filter(id -> id != null)
            .collect(Collectors.toSet());
        if (customerIds.isEmpty()) {
            return Map.of();
        }
        return customerRepository.findAllById(customerIds).stream()
            .collect(Collectors.toMap(Customer::getId, Customer::getDisplayName));
    }

    private List<RentalHistoryItem> mapRentals(List<Rental> rentals, Map<Long, String> customerNames) {
        return rentals.stream()
            .map(r -> new RentalHistoryItem(
                r.getId(),
                r.getRentalNumber(),
                r.getCustomerId(),
                customerNames.get(r.getCustomerId()),
                r.getStartDate(),
                r.getEndDate(),
                r.getActualReturnDate(),
                r.getStatus().name(),
                r.getStartKm(),
                r.getEndKm(),
                r.getGrandTotal() != null ? r.getGrandTotal().getAmount() : null,
                r.getGrandTotal() != null ? r.getGrandTotal().getCurrency() : null,
                r.getCreatedAt()
            ))
            .toList();
    }

    private List<MaintenanceHistoryItem> mapMaintenances(List<MaintenanceRecord> maintenances) {
        return maintenances.stream()
            .map(m -> new MaintenanceHistoryItem(
                m.getId(),
                m.getMaintenanceType().name(),
                m.getMaintenanceType().getDisplayName(),
                m.getMaintenanceDate(),
                m.getCurrentKm(),
                m.getCost() != null ? m.getCost().getAmount() : null,
                m.getCost() != null ? m.getCost().getCurrency() : null,
                m.getServiceProvider(),
                m.getDescription(),
                m.getAffectedZones() != null ? new java.util.ArrayList<>(m.getAffectedZones()) : java.util.List.of(),
                m.getCreatedAt()
            ))
            .toList();
    }

    private List<DamageHistoryItem> mapDamages(List<DamageReport> damages) {
        return damages.stream()
            .map(d -> new DamageHistoryItem(
                d.getId(),
                d.getDamageType().name(),
                d.getDamageType().getDisplayName(),
                d.getLocation().name(),
                d.getLocation().getDisplayName(),
                d.getLocation().getZoneId(),
                d.getSeverity().name(),
                d.getSeverity().getDisplayName(),
                d.getReportDate(),
                d.getDescription(),
                d.getEstimatedCost() != null ? d.getEstimatedCost().getAmount() : null,
                d.getEstimatedCost() != null ? d.getEstimatedCost().getCurrency() : null,
                d.isRepaired(),
                d.getRepairedDate(),
                d.getRepairCost() != null ? d.getRepairCost().getAmount() : null,
                d.getRepairCost() != null ? d.getRepairCost().getCurrency() : null,
                d.getCreatedAt()
            ))
            .toList();
    }

    private List<StatusChangeItem> mapStatusChanges(List<VehicleStatusHistory> statusChanges) {
        return statusChanges.stream()
            .map(s -> new StatusChangeItem(
                s.getId(),
                s.getOldStatus() != null ? s.getOldStatus().name() : null,
                s.getNewStatus().name(),
                s.getReferenceType(),
                s.getReferenceId(),
                s.getReason(),
                s.getChangedBy(),
                s.getChangedAt()
            ))
            .toList();
    }
}
