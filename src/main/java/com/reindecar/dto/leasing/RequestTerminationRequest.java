package com.reindecar.dto.leasing;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record RequestTerminationRequest(
    @NotNull
    LocalDate terminationDate,

    String reason
) {}
