package com.reindecar.dto.leasing;

import java.math.BigDecimal;

public record GenerateInvoiceRequest(
    Long rentalId,
    int year,
    int month,
    BigDecimal additionalCharges,
    String additionalChargesNote
) {}
