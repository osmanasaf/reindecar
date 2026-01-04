package com.reindecar.dto.rental;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record ActivateRentalRequest(
    @NotNull(message = "Start km is required")
    @Min(value = 0, message = "Start km must be positive")
    int startKm
) {}
