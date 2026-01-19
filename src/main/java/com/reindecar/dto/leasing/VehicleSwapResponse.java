package com.reindecar.dto.leasing;

import com.reindecar.entity.leasing.VehicleSwap;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

public record VehicleSwapResponse(
    Long id,
    Long rentalId,
    Long oldVehicleId,
    Long newVehicleId,
    LocalDate swapDate,
    VehicleSwap.SwapReason reason,
    int oldVehicleKm,
    int newVehicleKm,
    BigDecimal priceDifference,
    String currency,
    String notes,
    String processedBy,
    Instant createdAt
) {}
