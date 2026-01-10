package com.reindecar.service.vehicle;

import com.reindecar.common.dto.PageResponse;
import com.reindecar.common.exception.DuplicateEntityException;
import com.reindecar.common.exception.EntityNotFoundException;
import com.reindecar.common.exception.BusinessException;
import com.reindecar.common.exception.ErrorCode;
import com.reindecar.common.service.BaseService;
import com.reindecar.dto.vehicle.CreateVehicleRequest;
import com.reindecar.dto.vehicle.UpdateVehicleStatusRequest;
import com.reindecar.dto.vehicle.VehicleResponse;
import com.reindecar.dto.vehicle.VehicleStatusHistoryResponse;
import com.reindecar.entity.vehicle.Vehicle;
import com.reindecar.entity.vehicle.VehicleStatus;
import com.reindecar.exception.vehicle.VehicleNotFoundException;
import com.reindecar.mapper.vehicle.VehicleMapper;
import com.reindecar.repository.vehicle.VehicleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@Transactional(readOnly = true)
public class VehicleService extends BaseService<Vehicle, Long, VehicleRepository> {

    private final VehicleMapper vehicleMapper;
    private final VehicleStatusService statusService;

    public VehicleService(
            VehicleRepository repository,
            VehicleMapper vehicleMapper,
            VehicleStatusService statusService) {
        super(repository, "Vehicle");
        this.vehicleMapper = vehicleMapper;
        this.statusService = statusService;
    }

    public PageResponse<VehicleResponse> getAllVehicles(Pageable pageable) {
        log.info("Fetching all vehicles with pagination: {}", pageable);
        Page<Vehicle> vehicles = repository.findAllActive(pageable);
        return PageResponse.of(vehicles.map(vehicleMapper::toResponse));
    }

    public PageResponse<VehicleResponse> getAvailableVehicles(Pageable pageable) {
        log.info("Fetching available vehicles");
        Page<Vehicle> vehicles = repository.findByStatusAndDeletedFalse(VehicleStatus.AVAILABLE, pageable);
        return PageResponse.of(vehicles.map(vehicleMapper::toResponse));
    }

    public PageResponse<VehicleResponse> getVehiclesByBranch(Long branchId, Pageable pageable) {
        log.info("Fetching vehicles by branch: {}", branchId);
        Page<Vehicle> vehicles = repository.findByBranchIdAndDeletedFalse(branchId, pageable);
        return PageResponse.of(vehicles.map(vehicleMapper::toResponse));
    }

    public VehicleResponse getVehicleById(Long id) {
        log.info("Fetching vehicle by id: {}", id);
        return findById(id, vehicleMapper::toResponse);
    }

    @Transactional
    public VehicleResponse createVehicle(CreateVehicleRequest request) {
        log.info("Creating vehicle with plate: {}", request.plateNumber());

        validateUniquePlateNumber(request.plateNumber());
        validateUniqueVinNumber(request.vinNumber());

        Vehicle vehicle = vehicleMapper.toEntity(request);
        return create(vehicle, vehicleMapper::toResponse);
    }

    @Transactional
    public VehicleResponse changeStatus(Long id, UpdateVehicleStatusRequest request, String changedBy) {
        log.info("Changing vehicle {} status to {}", id, request.newStatus());

        return update(id, vehicle -> {
            VehicleStatus oldStatus = vehicle.getStatus();
            vehicle.changeStatus(request.newStatus());
            
            statusService.recordStatusChange(
                id,
                oldStatus,
                request.newStatus(),
                null,
                null,
                request.reason(),
                changedBy
            );
            
            return vehicle;
        }, vehicleMapper::toResponse);
    }

    @Transactional
    public void changeBranch(Long id, Long newBranchId) {
        log.info("Changing vehicle {} branch to {}", id, newBranchId);

        update(id, vehicle -> {
            vehicle.changeBranch(newBranchId);
            return vehicle;
        }, vehicleMapper::toResponse);
    }

    @Transactional
    public void updateKilometers(Long id, int newKm) {
        log.info("Updating vehicle {} kilometers to {}", id, newKm);

        update(id, vehicle -> {
            vehicle.updateKilometers(newKm);
            return vehicle;
        }, vehicleMapper::toResponse);
    }

    @Transactional
    public void deleteVehicle(Long id) {
        log.info("Deleting vehicle with id: {}", id);

        update(id, vehicle -> {
            if (vehicle.getStatus().isRented()) {
                throw new BusinessException(ErrorCode.INVALID_OPERATION, "Cannot delete rented vehicle");
            }
            vehicle.markAsDeleted();
            return vehicle;
        }, vehicleMapper::toResponse);
    }

    public PageResponse<VehicleStatusHistoryResponse> getVehicleHistory(Long id, Pageable pageable) {
        log.info("Fetching status history for vehicle: {}", id);
        return statusService.getVehicleHistory(id, pageable);
    }

    @Override
    protected EntityNotFoundException createNotFoundException(Long id) {
        return new VehicleNotFoundException(id);
    }

    @Override
    protected Long extractId(Vehicle entity) {
        return entity.getId();
    }

    private void validateUniquePlateNumber(String plateNumber) {
        if (repository.existsByPlateNumberAndDeletedFalse(plateNumber)) {
            throw new DuplicateEntityException("Vehicle", "plateNumber", plateNumber);
        }
    }

    private void validateUniqueVinNumber(String vinNumber) {
        if (repository.existsByVinNumberAndDeletedFalse(vinNumber)) {
            throw new DuplicateEntityException("Vehicle", "vinNumber", vinNumber);
        }
    }
}
