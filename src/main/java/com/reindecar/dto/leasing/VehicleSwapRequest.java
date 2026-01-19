package com.reindecar.dto.leasing;

import com.reindecar.entity.leasing.VehicleSwap;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record VehicleSwapRequest(
    @NotNull
    Long newVehicleId,

    LocalDate swapDate,

    @NotNull
    VehicleSwap.SwapReason reason,

    int currentKm,

    BigDecimal priceDifference,

    String notes
) {}
