package com.reindecar.dto.contract;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateContractTermRequest(
    @NotBlank
    @Size(max = 200)
    String title,

    @NotBlank
    String content,

    boolean required,

    int sortOrder
) {}
