package com.reindecar.dto.vehicle;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public record UpdateVehicleDetailsRequest(
    @Size(max = 50) String hgsNumber,
    BigDecimal hgsBalance,
    @Size(max = 50) String kabisNumber,
    LocalDate mtvDate,
    LocalDate nextServiceDate,
    @Min(0) Integer nextServiceKm,
    LocalDate nextTireChangeDate,
    LocalDate creditEndDate,
    BigDecimal remainingCreditAmount,
    LocalDate purchaseDate,
    BigDecimal purchasePrice
) {}
