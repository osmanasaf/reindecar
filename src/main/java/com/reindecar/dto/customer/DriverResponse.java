package com.reindecar.dto.customer;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

@Schema(description = "Driver information")
public record DriverResponse(
    @Schema(description = "Driver ID")
    Long id,

    @Schema(description = "Customer ID (optional)")
    Long customerId,

    @Schema(description = "National ID")
    String nationalId,

    @Schema(description = "First name")
    String firstName,

    @Schema(description = "Last name")
    String lastName,

    @Schema(description = "Phone number")
    String phone,

    @Schema(description = "License number")
    String licenseNumber,

    @Schema(description = "License class")
    String licenseClass,

    @Schema(description = "License expiry date")
    LocalDate licenseExpiryDate,

    @Schema(description = "Primary driver flag")
    boolean primary,

    @Schema(description = "Active flag")
    boolean active
) {}
