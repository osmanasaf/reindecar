package com.reindecar.dto.rental;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Request to add a driver to a rental")
public record AddDriverRequest(
    @NotNull(message = "Driver ID is required")
    @Schema(description = "ID of the driver to add", example = "1")
    Long driverId,

    @Schema(description = "Whether this driver should be the primary driver", example = "false")
    Boolean primary,

    @Schema(description = "Additional notes about this driver assignment")
    String notes
) {}
