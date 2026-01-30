package com.reindecar.exception.rental;

import com.reindecar.common.constant.ValidationMessages;
import com.reindecar.common.exception.BusinessException;
import com.reindecar.common.exception.ErrorCode;

public class CustomerRentalLimitExceededException extends BusinessException {
    
    public CustomerRentalLimitExceededException(Long customerId) {
        super(ErrorCode.INVALID_OPERATION, ValidationMessages.PERSONAL_CUSTOMER_RENTAL_LIMIT_EXCEEDED);
    }
}
