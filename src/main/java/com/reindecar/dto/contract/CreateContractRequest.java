package com.reindecar.dto.contract;

import jakarta.validation.constraints.NotNull;

public record CreateContractRequest(
    @NotNull(message = "Rental ID is required")
    Long rentalId,

    Long templateId
) {}
