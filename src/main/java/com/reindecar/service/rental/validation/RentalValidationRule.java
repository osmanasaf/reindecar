package com.reindecar.service.rental.validation;

import com.reindecar.dto.rental.CreateRentalRequest;

/**
 * Kiralama validasyon kuralı arayüzü.
 * Her yeni kural bu interface'i implement eder (Open/Closed Principle).
 */
public interface RentalValidationRule {
    
    /**
     * Kiralama oluşturma isteğini validate eder.
     * 
     * @param request Kiralama oluşturma isteği
     * @throws com.reindecar.common.exception.BusinessException Validasyon başarısız olursa
     */
    void validate(CreateRentalRequest request);
}
