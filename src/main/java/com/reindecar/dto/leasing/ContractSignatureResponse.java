package com.reindecar.dto.leasing;

import com.reindecar.entity.leasing.ContractSignature;

import java.time.Instant;

public record ContractSignatureResponse(
    Long id,
    Long contractId,
    Long rentalId,
    ContractSignature.SignatureType signatureType,
    Instant signedAt,
    String signedBy,
    String signatoryRole,
    String ipAddress,
    String deviceInfo,
    boolean verified,
    String verificationCode,
    Instant createdAt
) {}
