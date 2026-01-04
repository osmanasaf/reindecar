package com.reindecar.dto.payment;

import com.reindecar.entity.payment.PaymentMethod;
import com.reindecar.entity.payment.PaymentStatus;

import java.math.BigDecimal;
import java.time.Instant;

public record PaymentResponse(
    Long id,
    Long rentalId,
    BigDecimal amount,
    String currency,
    PaymentMethod method,
    PaymentStatus status,
    String transactionRef,
    String invoiceRef,
    Instant paidAt,
    String createdBy
) {}
