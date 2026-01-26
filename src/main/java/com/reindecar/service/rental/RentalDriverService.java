package com.reindecar.service.rental;

import com.reindecar.common.constant.ValidationMessages;
import com.reindecar.common.exception.BusinessException;
import com.reindecar.common.exception.ErrorCode;
import com.reindecar.common.exception.EntityNotFoundException;
import com.reindecar.dto.rental.AddDriverRequest;
import com.reindecar.dto.rental.RentalDriverResponse;
import com.reindecar.entity.customer.Driver;
import com.reindecar.entity.rental.Rental;
import com.reindecar.entity.rental.RentalDriver;
import com.reindecar.entity.rental.RentalStatus;
import com.reindecar.repository.customer.DriverRepository;
import com.reindecar.repository.rental.RentalDriverRepository;
import com.reindecar.repository.rental.RentalRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class RentalDriverService {

    private static final String ENTITY_RENTAL = "Rental";
    private static final String ENTITY_DRIVER = "Driver";
    private static final String ENTITY_RENTAL_DRIVER = "RentalDriver";

    private static final Set<RentalStatus> MODIFIABLE_STATUSES = Set.of(
        RentalStatus.DRAFT,
        RentalStatus.RESERVED,
        RentalStatus.ACTIVE
    );

    private final RentalDriverRepository rentalDriverRepository;
    private final RentalRepository rentalRepository;
    private final DriverRepository driverRepository;

    @Transactional(readOnly = true)
    public List<RentalDriverResponse> getDriversByRentalId(Long rentalId) {
        validateRentalExists(rentalId);
        
        List<RentalDriver> rentalDrivers = rentalDriverRepository.findByRentalIdOrdered(rentalId);
        
        return rentalDrivers.stream()
            .map(this::toResponse)
            .toList();
    }

    @Transactional
    public RentalDriverResponse addDriverToRental(Long rentalId, AddDriverRequest request, String addedBy) {
        log.info("Adding driver {} to rental {}", request.driverId(), rentalId);

        Rental rental = findRentalOrThrow(rentalId);
        validateRentalModifiable(rental);

        Driver driver = findDriverOrThrow(request.driverId());
        validateDriverEligible(driver);
        validateDriverNotAlreadyAdded(rentalId, request.driverId());

        RentalDriver rentalDriver = createRentalDriver(rentalId, request, addedBy);
        RentalDriver saved = rentalDriverRepository.save(rentalDriver);

        log.info("Driver {} added to rental {}", request.driverId(), rentalId);
        return toResponse(saved);
    }

    private void validateDriverEligible(Driver driver) {
        if (!driver.isActive()) {
            throw new BusinessException(ErrorCode.INVALID_OPERATION, ValidationMessages.DRIVER_NOT_ACTIVE);
        }
        if (driver.isLicenseExpired()) {
            throw new BusinessException(ErrorCode.INVALID_OPERATION, ValidationMessages.DRIVER_LICENSE_EXPIRED);
        }
    }

    private void validateDriverNotAlreadyAdded(Long rentalId, Long driverId) {
        if (rentalDriverRepository.findByRentalIdAndDriverId(rentalId, driverId).isPresent()) {
            throw new BusinessException(ErrorCode.DUPLICATE_ENTITY, ValidationMessages.DRIVER_ALREADY_ADDED);
        }
    }

    private RentalDriver createRentalDriver(Long rentalId, AddDriverRequest request, String addedBy) {
        boolean isPrimary = request.primary() != null && request.primary();
        if (isPrimary) {
            clearPrimaryDriver(rentalId);
        }

        RentalDriver rentalDriver = RentalDriver.create(rentalId, request.driverId(), isPrimary, addedBy);
        if (request.notes() != null) {
            rentalDriver.setNotes(request.notes());
        }
        return rentalDriver;
    }

    @Transactional
    public void removeDriverFromRental(Long rentalId, Long driverId) {
        log.info("Removing driver {} from rental {}", driverId, rentalId);

        Rental rental = findRentalOrThrow(rentalId);
        validateRentalModifiable(rental);

        RentalDriver rentalDriver = findRentalDriverOrThrow(rentalId, driverId);
        rentalDriverRepository.delete(rentalDriver);

        log.info("Driver {} removed from rental {}", driverId, rentalId);
    }

    @Transactional
    public RentalDriverResponse setPrimaryDriver(Long rentalId, Long driverId) {
        log.info("Setting driver {} as primary for rental {}", driverId, rentalId);

        Rental rental = findRentalOrThrow(rentalId);
        validateRentalModifiable(rental);

        RentalDriver rentalDriver = findRentalDriverOrThrow(rentalId, driverId);
        clearPrimaryDriver(rentalId);
        rentalDriver.setPrimary(true);

        RentalDriver saved = rentalDriverRepository.save(rentalDriver);
        log.info("Driver {} set as primary for rental {}", driverId, rentalId);
        return toResponse(saved);
    }

    private void clearPrimaryDriver(Long rentalId) {
        rentalDriverRepository.findByRentalIdAndPrimaryTrue(rentalId)
            .ifPresent(existingPrimary -> {
                existingPrimary.setPrimary(false);
                rentalDriverRepository.save(existingPrimary);
            });
    }

    private Rental findRentalOrThrow(Long rentalId) {
        return rentalRepository.findById(rentalId)
            .orElseThrow(() -> new EntityNotFoundException(ENTITY_RENTAL, rentalId));
    }

    private Driver findDriverOrThrow(Long driverId) {
        return driverRepository.findById(driverId)
            .orElseThrow(() -> new EntityNotFoundException(ENTITY_DRIVER, driverId));
    }

    private RentalDriver findRentalDriverOrThrow(Long rentalId, Long driverId) {
        return rentalDriverRepository.findByRentalIdAndDriverId(rentalId, driverId)
            .orElseThrow(() -> new EntityNotFoundException(ENTITY_RENTAL_DRIVER, 
                "rental=" + rentalId + ", driver=" + driverId));
    }

    private void validateRentalExists(Long rentalId) {
        if (!rentalRepository.existsById(rentalId)) {
            throw new EntityNotFoundException(ENTITY_RENTAL, rentalId);
        }
    }

    private void validateRentalModifiable(Rental rental) {
        if (!MODIFIABLE_STATUSES.contains(rental.getStatus())) {
            throw new BusinessException(ErrorCode.INVALID_OPERATION,
                String.format(ValidationMessages.RENTAL_CANNOT_MODIFY_STATUS, rental.getStatus()));
        }
    }

    private RentalDriverResponse toResponse(RentalDriver rentalDriver) {
        Driver driver = driverRepository.findById(rentalDriver.getDriverId()).orElse(null);
        return buildDriverResponse(rentalDriver, driver);
    }

    private RentalDriverResponse buildDriverResponse(RentalDriver rentalDriver, Driver driver) {
        return new RentalDriverResponse(
            rentalDriver.getId(),
            rentalDriver.getRentalId(),
            rentalDriver.getDriverId(),
            driver != null ? driver.getFullName() : ValidationMessages.DRIVER_UNKNOWN,
            driver != null ? driver.getNationalId() : null,
            driver != null ? driver.getPhone() : null,
            driver != null ? driver.getLicenseNumber() : null,
            driver != null ? driver.getLicenseClass() : null,
            driver != null ? driver.getLicenseExpiryDate() : null,
            driver != null && driver.isLicenseExpired(),
            rentalDriver.isPrimary(),
            rentalDriver.getAddedAt(),
            rentalDriver.getAddedBy(),
            rentalDriver.getNotes()
        );
    }
}
