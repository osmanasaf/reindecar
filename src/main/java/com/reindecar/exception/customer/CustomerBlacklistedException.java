package com.reindecar.exception.customer;

import com.reindecar.common.exception.BusinessException;

public class CustomerBlacklistedException extends BusinessException {

    public CustomerBlacklistedException(String customerName) {
        super("CUSTOMER_001", "Customer '" + customerName + "' is blacklisted and cannot perform rentals");
    }
}
