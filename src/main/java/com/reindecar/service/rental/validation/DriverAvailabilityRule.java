package com.reindecar.service.rental.validation;

import com.reindecar.dto.rental.CreateRentalRequest;
import com.reindecar.exception.rental.DriverAlreadyInActiveRentalException;
import com.reindecar.repository.rental.RentalDriverRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Sürücü müsaitlik validasyon kuralı.
 * 
 * İş Kuralı: Bir sürücü aynı anda sadece 1 aktif (RESERVED, ACTIVE, OVERDUE) kiralamada olabilir.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DriverAvailabilityRule implements RentalValidationRule {
    
    private final RentalDriverRepository rentalDriverRepository;
    
    @Override
    public void validate(CreateRentalRequest request) {
        if (request.driverIds() == null || request.driverIds().isEmpty()) {
            return;
        }
        
        for (Long driverId : request.driverIds()) {
            if (rentalDriverRepository.hasBlockingRental(driverId)) {
                log.warn("Driver {} already has an active rental", driverId);
                throw new DriverAlreadyInActiveRentalException(driverId);
            }
        }
        
        log.debug("All drivers are available for rental");
    }
}
