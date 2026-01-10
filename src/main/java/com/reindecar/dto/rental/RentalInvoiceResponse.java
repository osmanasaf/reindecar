package com.reindecar.dto.rental;

import com.reindecar.entity.rental.InvoiceStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record RentalInvoiceResponse(
    Long id,
    Long rentalId,
    String invoiceNumber,
    LocalDate invoiceDate,
    InvoiceStatus status,
    BigDecimal baseRentalAmount,
    BigDecimal extraKmAmount,
    BigDecimal discountAmount,
    BigDecimal totalAmount,
    BigDecimal taxRate,
    BigDecimal taxAmount,
    BigDecimal grandTotal,
    String currency,
    List<InvoiceItemResponse> items,
    String notes
) {}
