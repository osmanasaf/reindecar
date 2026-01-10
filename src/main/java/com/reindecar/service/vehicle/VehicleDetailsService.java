package com.reindecar.service.vehicle;

import com.reindecar.common.valueobject.Money;
import com.reindecar.dto.vehicle.UpdateVehicleDetailsRequest;
import com.reindecar.dto.vehicle.VehicleDetailsResponse;
import com.reindecar.entity.vehicle.Vehicle;
import com.reindecar.entity.vehicle.VehicleDetails;
import com.reindecar.repository.vehicle.VehicleDetailsRepository;
import com.reindecar.repository.vehicle.VehicleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class VehicleDetailsService {

    private final VehicleDetailsRepository detailsRepository;
    private final VehicleRepository vehicleRepository;

    private static final Money HGS_LOW_THRESHOLD = Money.of(BigDecimal.valueOf(100), Money.DEFAULT_CURRENCY);
    private static final int SERVICE_KM_THRESHOLD = 500;
    private static final int DAYS_THRESHOLD = 7;

    public VehicleDetailsResponse getByVehicleId(Long vehicleId) {
        VehicleDetails details = findOrCreate(vehicleId);
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
            .orElseThrow(() -> new IllegalArgumentException("Vehicle not found"));
        return toResponse(details, vehicle.getCurrentKm());
    }

    @Transactional
    public VehicleDetailsResponse updateDetails(Long vehicleId, UpdateVehicleDetailsRequest request) {
        log.info("Updating vehicle details for vehicleId: {}", vehicleId);
        
        VehicleDetails details = findOrCreate(vehicleId);
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
            .orElseThrow(() -> new IllegalArgumentException("Vehicle not found"));

        updateFromRequest(details, request);
        detailsRepository.save(details);
        
        return toResponse(details, vehicle.getCurrentKm());
    }

    @Transactional
    public VehicleDetailsResponse updateHgsBalance(Long vehicleId, Money newBalance) {
        log.info("Updating HGS balance for vehicleId: {}", vehicleId);
        
        VehicleDetails details = findOrCreate(vehicleId);
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
            .orElseThrow(() -> new IllegalArgumentException("Vehicle not found"));

        details.updateHgsBalance(newBalance);
        detailsRepository.save(details);
        
        return toResponse(details, vehicle.getCurrentKm());
    }

    private VehicleDetails findOrCreate(Long vehicleId) {
        return detailsRepository.findByVehicleId(vehicleId)
            .orElseGet(() -> {
                VehicleDetails newDetails = VehicleDetails.createFor(vehicleId);
                return detailsRepository.save(newDetails);
            });
    }

    private void updateFromRequest(VehicleDetails details, UpdateVehicleDetailsRequest request) {
        if (request.hgsNumber() != null || request.hgsBalance() != null) {
            Money balance = request.hgsBalance() != null 
                ? Money.of(request.hgsBalance(), Money.DEFAULT_CURRENCY) 
                : details.getHgsBalance();
            details.updateHgsInfo(request.hgsNumber(), balance);
        }
        
        if (request.kabisNumber() != null) {
            details.updateKabisNumber(request.kabisNumber());
        }
        
        if (request.mtvDate() != null) {
            details.updateMtvDate(request.mtvDate());
        }
        
        if (request.nextServiceDate() != null || request.nextServiceKm() != null) {
            details.updateServiceInfo(request.nextServiceDate(), request.nextServiceKm());
        }
        
        if (request.nextTireChangeDate() != null) {
            details.updateTireChangeDate(request.nextTireChangeDate());
        }
        
        if (request.creditEndDate() != null || request.remainingCreditAmount() != null) {
            Money remaining = request.remainingCreditAmount() != null 
                ? Money.of(request.remainingCreditAmount(), Money.DEFAULT_CURRENCY) 
                : details.getRemainingCreditAmount();
            details.updateFinanceInfo(request.creditEndDate(), remaining);
        }
        
        if (request.purchaseDate() != null || request.purchasePrice() != null) {
            Money price = request.purchasePrice() != null 
                ? Money.of(request.purchasePrice(), Money.DEFAULT_CURRENCY) 
                : details.getPurchasePrice();
            details.updatePurchaseInfo(request.purchaseDate(), price);
        }
    }

    private VehicleDetailsResponse toResponse(VehicleDetails details, int currentKm) {
        return new VehicleDetailsResponse(
            details.getId(),
            details.getVehicleId(),
            details.getHgsNumber(),
            details.getHgsBalance() != null ? details.getHgsBalance().getAmount() : null,
            details.getHgsBalance() != null ? details.getHgsBalance().getCurrency() : null,
            details.getHgsLastUpdated(),
            details.getKabisNumber(),
            details.getMtvDate(),
            details.getRegistrationDate(),
            details.getNextServiceDate(),
            details.getNextServiceKm(),
            details.getLastServiceDate(),
            details.getNextTireChangeDate(),
            details.getCreditEndDate(),
            details.getRemainingCreditAmount() != null ? details.getRemainingCreditAmount().getAmount() : null,
            details.getRemainingCreditAmount() != null ? details.getRemainingCreditAmount().getCurrency() : null,
            details.getPurchaseDate(),
            details.getPurchasePrice() != null ? details.getPurchasePrice().getAmount() : null,
            details.getPurchasePrice() != null ? details.getPurchasePrice().getCurrency() : null,
            details.isHgsBalanceLow(HGS_LOW_THRESHOLD),
            details.isServiceDueSoon(currentKm, SERVICE_KM_THRESHOLD),
            details.isMtvDueSoon(DAYS_THRESHOLD),
            details.isTireChangeDueSoon(DAYS_THRESHOLD)
        );
    }
}
