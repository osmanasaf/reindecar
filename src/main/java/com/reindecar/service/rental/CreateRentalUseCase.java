package com.reindecar.service.rental;

import com.reindecar.common.valueobject.Money;
import com.reindecar.dto.pricing.CalculatePriceRequest;
import com.reindecar.dto.pricing.PriceCalculationResponse;
import com.reindecar.dto.rental.CreateRentalRequest;
import com.reindecar.entity.customer.Customer;
import com.reindecar.entity.pricing.RentalType;
import com.reindecar.entity.rental.Rental;
import com.reindecar.entity.vehicle.Vehicle;
import com.reindecar.entity.vehicle.VehicleStatus;
import com.reindecar.exception.customer.CustomerBlacklistedException;
import com.reindecar.exception.rental.RentalOverlapException;
import com.reindecar.repository.customer.CustomerRepository;
import com.reindecar.repository.rental.RentalRepository;
import com.reindecar.repository.vehicle.VehicleRepository;
import com.reindecar.service.pricing.PriceCalculationService;
import com.reindecar.service.vehicle.VehicleStatusService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Year;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreateRentalUseCase {

    private final RentalRepository rentalRepository;
    private final VehicleRepository vehicleRepository;
    private final CustomerRepository customerRepository;
    private final PriceCalculationService priceCalculationService;
    private final VehicleStatusService vehicleStatusService;

    @Transactional
    public Rental execute(CreateRentalRequest request, String createdBy) {
        log.info("Creating rental for vehicle: {}, customer: {}", request.vehicleId(), request.customerId());

        validateCustomerNotBlacklisted(request.customerId());
        
        Vehicle vehicle = validateVehicleAvailable(request.vehicleId());
        
        validateNoOverlap(request.vehicleId(), request.startDate(), request.endDate());

        PriceCalculationResponse priceCalc = calculatePrice(request);

        String rentalNumber = generateRentalNumber();

        Money dailyPrice = Money.of(priceCalc.dailyPrice(), priceCalc.currency());
        Money totalPrice = Money.of(priceCalc.finalTotal(), priceCalc.currency());
        Money discountAmount = request.discountAmount() != null 
            ? Money.of(request.discountAmount(), priceCalc.currency())
            : Money.zero(priceCalc.currency());

        Rental rental = Rental.create(
            rentalNumber,
            request.rentalType(),
            request.vehicleId(),
            request.customerId(),
            request.driverId(),
            request.branchId(),
            request.returnBranchId(),
            request.startDate(),
            request.endDate(),
            request.kmPackageId(),
            dailyPrice,
            totalPrice,
            discountAmount,
            request.notes(),
            createdBy
        );

        Rental savedRental = rentalRepository.save(rental);

        vehicle.changeStatus(VehicleStatus.RESERVED);
        vehicleRepository.save(vehicle);
        vehicleStatusService.recordStatusChange(
            vehicle.getId(),
            VehicleStatus.AVAILABLE,
            VehicleStatus.RESERVED,
            "RENTAL",
            savedRental.getId(),
            "Reserved for rental " + rentalNumber,
            createdBy
        );

        log.info("Rental created: {}", rentalNumber);
        return savedRental;
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
            throw new IllegalStateException("Vehicle is not available for rental");
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
