package com.reindecar.dto.contract;

import jakarta.validation.constraints.NotBlank;

public record SignContractRequest(
    @NotBlank(message = "Signed by is required")
    String signedBy,

    @NotBlank(message = "Signature method is required")
    String signatureMethod
) {}
