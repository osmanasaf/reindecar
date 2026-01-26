package com.reindecar.dto.rental;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.time.LocalDate;

@Schema(description = "Rental driver information")
public record RentalDriverResponse(
    @Schema(description = "Rental driver record ID")
    Long id,
    
    @Schema(description = "Rental ID")
    Long rentalId,
    
    @Schema(description = "Driver ID")
    Long driverId,
    
    @Schema(description = "Driver's full name")
    String driverName,
    
    @Schema(description = "Driver's national ID (TC)")
    String nationalId,
    
    @Schema(description = "Driver's phone number")
    String phone,
    
    @Schema(description = "Driver's license number")
    String licenseNumber,
    
    @Schema(description = "Driver's license class")
    String licenseClass,
    
    @Schema(description = "License expiry date")
    LocalDate licenseExpiryDate,
    
    @Schema(description = "Whether the license is expired")
    boolean licenseExpired,
    
    @Schema(description = "Whether this is the primary driver")
    boolean primary,
    
    @Schema(description = "When the driver was added to rental")
    Instant addedAt,
    
    @Schema(description = "Who added the driver")
    String addedBy,
    
    @Schema(description = "Additional notes")
    String notes
) {}
