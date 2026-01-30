package com.reindecar.service.rental.validation;

import com.reindecar.dto.rental.CreateRentalRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Kiralama validasyon orchestrator servisi.
 * 
 * Bu servis tüm RentalValidationRule implementasyonlarını toplar ve 
 * sırayla çalıştırır (Strategy Pattern + Composite Pattern).
 * 
 * Yeni bir validasyon kuralı eklemek için sadece RentalValidationRule interface'ini
 * implement eden yeni bir @Component oluşturmak yeterlidir (Open/Closed Principle).
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RentalValidationService {
    
    private final List<RentalValidationRule> rules;
    
    /**
     * Tüm validasyon kurallarını çalıştırır.
     * 
     * @param request Kiralama oluşturma isteği
     * @throws com.reindecar.common.exception.BusinessException Herhangi bir kural başarısız olursa
     */
    public void validate(CreateRentalRequest request) {
        log.debug("Running {} validation rules for rental creation", rules.size());
        
        for (RentalValidationRule rule : rules) {
            log.trace("Executing validation rule: {}", rule.getClass().getSimpleName());
            rule.validate(request);
        }
        
        log.debug("All validation rules passed");
    }
}
