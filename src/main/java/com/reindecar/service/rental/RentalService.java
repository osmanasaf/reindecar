package com.reindecar.service.rental;

import com.reindecar.common.constant.DomainConstants;
import com.reindecar.common.dto.PageResponse;
import com.reindecar.common.exception.EntityNotFoundException;
import com.reindecar.dto.rental.*;
import com.reindecar.entity.rental.Rental;
import com.reindecar.entity.rental.RentalStatus;
import com.reindecar.entity.vehicle.Vehicle;
import com.reindecar.entity.vehicle.VehicleStatus;
import com.reindecar.exception.rental.RentalNotFoundException;
import com.reindecar.mapper.rental.RentalMapper;
import com.reindecar.repository.rental.RentalRepository;
import com.reindecar.repository.vehicle.VehicleRepository;
import com.reindecar.service.vehicle.VehicleStatusService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class RentalService {

    private final RentalRepository rentalRepository;
    private final RentalMapper rentalMapper;
    private final CreateRentalUseCase createRentalUseCase;
    private final VehicleRepository vehicleRepository;
    private final VehicleStatusService vehicleStatusService;

    private static final String ENTITY_VEHICLE = "Vehicle";

    public PageResponse<RentalResponse> getAllRentals(Pageable pageable) {
        log.info("Fetching all rentals");
        Page<Rental> rentals = rentalRepository.findAll(pageable);
        return PageResponse.of(rentals.map(rentalMapper::toResponse));
    }

    public PageResponse<RentalResponse> getActiveRentals(Pageable pageable) {
        log.info("Fetching active rentals");
        Page<Rental> rentals = rentalRepository.findActiveRentals(pageable);
        return PageResponse.of(rentals.map(rentalMapper::toResponse));
    }

    public PageResponse<RentalResponse> getOverdueRentals(Pageable pageable) {
        log.info("Fetching overdue rentals");
        Page<Rental> rentals = rentalRepository.findOverdueRentals(pageable);
        return PageResponse.of(rentals.map(rentalMapper::toResponse));
    }

    public RentalResponse getRentalById(Long id) {
        log.info("Fetching rental by id: {}", id);
        Rental rental = findRentalByIdOrThrow(id);
        return rentalMapper.toResponse(rental);
    }

    @Transactional
    public RentalResponse createRental(CreateRentalRequest request, String createdBy) {
        log.info("Creating rental");
        Rental rental = createRentalUseCase.execute(request, createdBy);
        return rentalMapper.toResponse(rental);
    }

    @Transactional
    public RentalResponse reserveRental(Long id) {
        log.info("Reserving rental: {}", id);
        Rental rental = findRentalByIdOrThrow(id);

        updateVehicleStatus(rental, VehicleStatus.RESERVED, "Reserved");
        rental.reserve();
        rentalRepository.save(rental);

        log.info("Rental {} reserved", rental.getRentalNumber());
        return rentalMapper.toResponse(rental);
    }

    @Transactional
    public RentalResponse activateRental(Long id, ActivateRentalRequest request) {
        log.info("Activating rental: {}", id);
        Rental rental = findRentalByIdOrThrow(id);

        updateVehicleStatus(rental, VehicleStatus.RENTED, "Activated");
        rental.activate(request.startKm());
        rentalRepository.save(rental);

        log.info("Rental {} activated", rental.getRentalNumber());
        return rentalMapper.toResponse(rental);
    }

    @Transactional
    public RentalResponse startReturn(Long id) {
        log.info("Starting return for rental: {}", id);
        Rental rental = findRentalByIdOrThrow(id);
        rental.startReturn();
        rentalRepository.save(rental);
        return rentalMapper.toResponse(rental);
    }

    @Transactional
    public RentalResponse completeRental(Long id, CompleteRentalRequest request) {
        log.info("Completing rental: {}", id);
        Rental rental = findRentalByIdOrThrow(id);

        updateVehicleStatus(rental, VehicleStatus.AVAILABLE, "Returned");
        rental.complete(request.actualReturnDate(), request.endKm(), rental.getExtraKmCharge());
        rentalRepository.save(rental);

        log.info("Rental {} completed", rental.getRentalNumber());
        return rentalMapper.toResponse(rental);
    }

    @Transactional
    public void cancelRental(Long id) {
        log.info("Cancelling rental: {}", id);
        Rental rental = findRentalByIdOrThrow(id);

        releaseVehicleIfReserved(rental);
        rental.cancel();
        rentalRepository.save(rental);
    }

    private void updateVehicleStatus(Rental rental, VehicleStatus newStatus, String action) {
        Vehicle vehicle = findVehicleByIdOrThrow(rental.getVehicleId());
        VehicleStatus previousStatus = vehicle.getStatus();

        vehicle.changeStatus(newStatus);
        vehicleRepository.save(vehicle);

        String reason = action + " for rental " + rental.getRentalNumber();
        recordVehicleStatusChange(vehicle.getId(), previousStatus, newStatus, rental.getId(), reason);
    }

    private void releaseVehicleIfReserved(Rental rental) {
        if (rental.getStatus() != RentalStatus.RESERVED) {
            return;
        }

        Vehicle vehicle = findVehicleByIdOrThrow(rental.getVehicleId());
        if (vehicle.getStatus() != VehicleStatus.RESERVED) {
            return;
        }

        vehicle.changeStatus(VehicleStatus.AVAILABLE);
        vehicleRepository.save(vehicle);

        String reason = "Reservation cancelled for rental " + rental.getRentalNumber();
        recordVehicleStatusChange(vehicle.getId(), VehicleStatus.RESERVED, VehicleStatus.AVAILABLE, rental.getId(), reason);
        log.info("Vehicle {} released due to rental cancellation", vehicle.getId());
    }

    private void recordVehicleStatusChange(Long vehicleId, VehicleStatus from, VehicleStatus to, Long rentalId, String reason) {
        vehicleStatusService.recordStatusChange(
            vehicleId, from, to,
            DomainConstants.STATUS_CHANGE_SOURCE_RENTAL,
            rentalId, reason,
            DomainConstants.SYSTEM_USER
        );
    }

    private Rental findRentalByIdOrThrow(Long id) {
        return rentalRepository.findById(id)
            .orElseThrow(() -> new RentalNotFoundException(id));
    }

    private Vehicle findVehicleByIdOrThrow(Long vehicleId) {
        return vehicleRepository.findById(vehicleId)
            .orElseThrow(() -> new EntityNotFoundException(ENTITY_VEHICLE, vehicleId));
    }
}
