package com.reindecar.dto.rental;

import java.math.BigDecimal;

public record InvoiceItemResponse(
    String description,
    int quantity,
    BigDecimal unitPrice,
    BigDecimal totalPrice
) {}
