package com.reindecar.dto.rental;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record CompleteRentalRequest(
    @NotNull(message = "Actual return date is required")
    LocalDate actualReturnDate,

    @NotNull(message = "End km is required")
    @Min(value = 0, message = "End km must be positive")
    int endKm,

    String notes
) {}
