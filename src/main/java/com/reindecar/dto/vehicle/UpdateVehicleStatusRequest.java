package com.reindecar.dto.vehicle;

import com.reindecar.entity.vehicle.VehicleStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateVehicleStatusRequest(
    @NotNull(message = "New status is required")
    VehicleStatus newStatus,

    @Size(max = 500)
    String reason
) {}
