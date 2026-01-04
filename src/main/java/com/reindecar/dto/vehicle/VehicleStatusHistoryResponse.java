package com.reindecar.dto.vehicle;

import com.reindecar.entity.vehicle.VehicleStatus;

import java.time.Instant;

public record VehicleStatusHistoryResponse(
    Long id,
    VehicleStatus oldStatus,
    VehicleStatus newStatus,
    String referenceType,
    Long referenceId,
    String reason,
    String changedBy,
    Instant changedAt
) {}
