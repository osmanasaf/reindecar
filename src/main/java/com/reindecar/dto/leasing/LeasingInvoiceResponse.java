package com.reindecar.dto.leasing;

import com.reindecar.entity.leasing.LeasingInvoice;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

public record LeasingInvoiceResponse(
    Long id,
    String invoiceNumber,
    Long rentalId,
    Long contractId,
    Long customerId,
    LocalDate periodStart,
    LocalDate periodEnd,
    BigDecimal monthlyRent,
    int excessKm,
    BigDecimal excessKmCharge,
    BigDecimal additionalCharges,
    String additionalChargesNote,
    BigDecimal totalAmount,
    String currency,
    LeasingInvoice.InvoiceStatus status,
    LocalDate dueDate,
    Instant paidAt,
    Instant createdAt
) {}
