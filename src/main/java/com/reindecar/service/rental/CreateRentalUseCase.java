package com.reindecar.service.rental;

import com.reindecar.common.constant.ValidationMessages;
import com.reindecar.common.exception.BusinessException;
import com.reindecar.common.exception.ErrorCode;
import com.reindecar.common.valueobject.Money;
import com.reindecar.dto.pricing.CalculatePriceRequest;
import com.reindecar.dto.pricing.PriceCalculationResponse;
import com.reindecar.dto.rental.CreateRentalRequest;
import com.reindecar.entity.customer.Customer;
import com.reindecar.entity.customer.Driver;
import com.reindecar.entity.rental.Rental;
import com.reindecar.entity.rental.RentalDriver;
import com.reindecar.entity.vehicle.Vehicle;
import com.reindecar.exception.customer.DriverNotFoundException;
import com.reindecar.exception.customer.CustomerBlacklistedException;
import com.reindecar.exception.rental.RentalOverlapException;
import com.reindecar.repository.customer.CustomerRepository;
import com.reindecar.repository.customer.DriverRepository;
import com.reindecar.repository.rental.RentalDriverRepository;
import com.reindecar.repository.rental.RentalRepository;
import com.reindecar.repository.vehicle.VehicleRepository;
import com.reindecar.service.pricing.PriceCalculationService;
import com.reindecar.service.rental.validation.RentalValidationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Year;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreateRentalUseCase {

    private final RentalRepository rentalRepository;
    private final VehicleRepository vehicleRepository;
    private final CustomerRepository customerRepository;
    private final DriverRepository driverRepository;
    private final RentalDriverRepository rentalDriverRepository;
    private final PriceCalculationService priceCalculationService;
    private final RentalValidationService rentalValidationService;

    @Transactional
    public Rental execute(CreateRentalRequest request, String createdBy) {
        log.info("Creating rental for vehicle: {}, customer: {}", request.vehicleId(), request.customerId());

        // Önce yeni validasyon servisini çalıştır (sürücü müsaitlik, müşteri limiti)
        rentalValidationService.validate(request);

        validateCustomerNotBlacklisted(request.customerId());
        validateVehicleAvailable(request.vehicleId());
        validateNoOverlap(request.vehicleId(), request.startDate(), request.endDate());
        validateDrivers(request.driverIds(), request.primaryDriverId());

        PriceCalculationResponse priceCalc = calculatePrice(request);

        String rentalNumber = generateRentalNumber();

        Money dailyPrice = Money.of(priceCalc.dailyPrice(), priceCalc.currency());
        Money totalPrice = Money.of(priceCalc.finalTotal(), priceCalc.currency());
        Money discountAmount = request.discountAmount() != null 
            ? Money.of(request.discountAmount(), priceCalc.currency())
            : Money.zero(priceCalc.currency());
        Money customExtraKmPrice = request.customExtraKmPrice() != null
            ? Money.of(request.customExtraKmPrice(), priceCalc.currency())
            : null;

        Rental rental = Rental.create(
            rentalNumber,
            request.rentalType(),
            request.vehicleId(),
            request.customerId(),
            request.customerType(),
            request.contractSignerId(),
            request.contractSignerName(),
            request.branchId(),
            request.returnBranchId(),
            request.startDate(),
            request.endDate(),
            request.kmPackageId(),
            request.customIncludedKm(),
            customExtraKmPrice,
            dailyPrice,
            totalPrice,
            discountAmount,
            request.notes(),
            createdBy
        );

        Rental savedRental = rentalRepository.save(rental);

        addDriversToRental(savedRental.getId(), request.driverIds(), request.primaryDriverId(), createdBy);

        // Not: Araç durumu DRAFT aşamasında değiştirilmez
        // Araç rezervasyonu reserve() çağrıldığında yapılır

        log.info("Rental created as DRAFT: {}", rentalNumber);
        return savedRental;
    }

    private void addDriversToRental(Long rentalId, List<Long> driverIds, Long primaryDriverId, String createdBy) {
        if (driverIds == null || driverIds.isEmpty()) {
            return;
        }

        for (Long driverId : driverIds) {
            boolean isPrimary = driverId.equals(primaryDriverId);
            RentalDriver rentalDriver = RentalDriver.create(rentalId, driverId, isPrimary, createdBy);
            rentalDriverRepository.save(rentalDriver);
        }
    }

    private void validateDrivers(List<Long> driverIds, Long primaryDriverId) {
        if (driverIds == null || driverIds.isEmpty()) {
            throw new BusinessException(ErrorCode.INVALID_PARAMETER, ValidationMessages.RENTAL_DRIVER_REQUIRED);
        }

        if (primaryDriverId == null) {
            throw new BusinessException(ErrorCode.INVALID_PARAMETER, ValidationMessages.RENTAL_PRIMARY_DRIVER_REQUIRED);
        }

        Set<Long> uniqueDriverIds = new HashSet<>(driverIds);
        if (uniqueDriverIds.size() != driverIds.size()) {
            throw new BusinessException(ErrorCode.DUPLICATE_ENTITY, ValidationMessages.DRIVER_DUPLICATE_IN_REQUEST);
        }

        if (!uniqueDriverIds.contains(primaryDriverId)) {
            throw new BusinessException(ErrorCode.INVALID_PARAMETER, ValidationMessages.RENTAL_PRIMARY_DRIVER_NOT_IN_LIST);
        }

        for (Long driverId : uniqueDriverIds) {
            Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new DriverNotFoundException(driverId));
            validateDriverEligible(driver);
        }
    }

    private void validateDriverEligible(Driver driver) {
        if (!driver.isActive()) {
            throw new BusinessException(ErrorCode.INVALID_OPERATION, ValidationMessages.DRIVER_NOT_ACTIVE);
        }
        if (driver.isLicenseExpired()) {
            throw new BusinessException(ErrorCode.INVALID_OPERATION, ValidationMessages.DRIVER_LICENSE_EXPIRED);
        }
    }

    private void validateCustomerNotBlacklisted(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
            .orElseThrow(() -> new IllegalArgumentException("Customer not found"));
        
        if (customer.isBlacklisted()) {
            throw new CustomerBlacklistedException(customer.getDisplayName());
        }
    }

    private Vehicle validateVehicleAvailable(Long vehicleId) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
            .orElseThrow(() -> new IllegalArgumentException("Vehicle not found"));
        
        if (!vehicle.getStatus().isAvailableForRental()) {
            throw new BusinessException(ErrorCode.VEHICLE_NOT_AVAILABLE, "Vehicle is not available for rental: " + vehicleId);
        }
        
        return vehicle;
    }

    private void validateNoOverlap(Long vehicleId, LocalDate startDate, LocalDate endDate) {
        List<Rental> overlapping = rentalRepository.findOverlappingRentals(vehicleId, startDate, endDate);
        
        if (!overlapping.isEmpty()) {
            throw new RentalOverlapException(vehicleId, startDate, endDate);
        }
    }

    private PriceCalculationResponse calculatePrice(CreateRentalRequest request) {
        CalculatePriceRequest priceRequest = new CalculatePriceRequest(
            request.vehicleId(),
            request.customerId(),
            request.rentalType(),
            request.startDate(),
            request.endDate(),
            request.termMonths(),
            request.kmPackageId()
        );
        
        return priceCalculationService.calculatePrice(priceRequest);
    }

    private String generateRentalNumber() {
        String prefix = "RNT-" + Year.now().getValue() + "-";
        long count = rentalRepository.countByRentalNumberPrefix(prefix);
        return String.format("%s%05d", prefix, count + 1);
    }
}
