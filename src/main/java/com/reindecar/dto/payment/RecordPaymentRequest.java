package com.reindecar.dto.payment;

import com.reindecar.entity.payment.PaymentMethod;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record RecordPaymentRequest(
    @NotNull(message = "Amount is required")
    BigDecimal amount,

    @NotNull(message = "Payment method is required")
    PaymentMethod method,

    String transactionRef,
    String invoiceRef,
    String notes
) {}
