package com.reindecar.dto.contract;

import com.reindecar.entity.pricing.RentalType;

import java.time.Instant;
import java.util.List;

public record ContractTemplateResponse(
    Long id,
    String code,
    String name,
    RentalType rentalType,
    int version,
    boolean active,
    List<ContractTermResponse> terms,
    Instant createdAt
) {}
