package com.reindecar.exception.rental;

import com.reindecar.common.constant.ValidationMessages;
import com.reindecar.common.exception.BusinessException;
import com.reindecar.common.exception.ErrorCode;

public class DriverAlreadyInActiveRentalException extends BusinessException {
    
    public DriverAlreadyInActiveRentalException(Long driverId) {
        super(ErrorCode.INVALID_OPERATION, 
              String.format(ValidationMessages.DRIVER_ALREADY_IN_ACTIVE_RENTAL, driverId));
    }
}
