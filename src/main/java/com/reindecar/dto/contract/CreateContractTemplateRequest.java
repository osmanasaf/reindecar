package com.reindecar.dto.contract;

import com.reindecar.entity.pricing.RentalType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateContractTemplateRequest(
    @NotBlank
    @Size(max = 50)
    String code,

    @NotBlank
    @Size(max = 200)
    String name,

    @NotNull
    RentalType rentalType,

    @NotBlank
    String content
) {}
