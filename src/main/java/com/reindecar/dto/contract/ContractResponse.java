package com.reindecar.dto.contract;

import com.reindecar.entity.contract.ContractStatus;

import java.time.Instant;
import java.time.LocalDate;

public record ContractResponse(
    Long id,
    String contractNumber,
    Long rentalId,
    Long templateId,
    int contractVersion,
    ContractStatus status,
    LocalDate validFrom,
    LocalDate validTo,
    Instant signedAt,
    String signedBy,
    String signatureMethod,
    boolean isExpired,
    boolean isSigned,
    Instant createdAt
) {}
