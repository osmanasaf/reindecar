package com.reindecar.dto.leasing;

import com.reindecar.entity.leasing.ContractSignature;
import jakarta.validation.constraints.NotBlank;

public record SignContractRequest(
    @NotBlank
    String signedBy,

    String signatoryRole,

    ContractSignature.SignatureType signatureType
) {}
