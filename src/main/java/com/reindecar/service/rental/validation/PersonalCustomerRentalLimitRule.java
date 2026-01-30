package com.reindecar.service.rental.validation;

import com.reindecar.dto.rental.CreateRentalRequest;
import com.reindecar.entity.customer.CustomerType;
import com.reindecar.exception.rental.CustomerRentalLimitExceededException;
import com.reindecar.repository.rental.RentalRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Kişisel müşteri kiralama limit validasyon kuralı.
 * 
 * İş Kuralı: Bireysel (PERSONAL) müşteri aynı anda sadece 1 aktif (RESERVED, ACTIVE, OVERDUE) kiralama yapabilir.
 * Şirket (COMPANY) müşteriler için bu kural geçerli değildir - birden fazla kiralama yapabilirler.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class PersonalCustomerRentalLimitRule implements RentalValidationRule {
    
    private final RentalRepository rentalRepository;
    
    @Override
    public void validate(CreateRentalRequest request) {
        if (request.customerType() != CustomerType.PERSONAL) {
            log.debug("Customer type is COMPANY, skipping rental limit check");
            return;
        }
        
        long count = rentalRepository.countBlockingRentalsByPersonalCustomer(request.customerId());
        
        if (count > 0) {
            log.warn("Personal customer {} already has {} active rental(s)", request.customerId(), count);
            throw new CustomerRentalLimitExceededException(request.customerId());
        }
        
        log.debug("Personal customer {} has no active rentals", request.customerId());
    }
}
