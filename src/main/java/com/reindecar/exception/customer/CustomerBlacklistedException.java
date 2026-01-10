package com.reindecar.exception.customer;

import com.reindecar.common.exception.BusinessException;
import com.reindecar.common.exception.ErrorCode;

public class CustomerBlacklistedException extends BusinessException {

    public CustomerBlacklistedException(String customerName) {
        super(ErrorCode.CUSTOMER_BLACKLISTED, customerName);
    }
}
