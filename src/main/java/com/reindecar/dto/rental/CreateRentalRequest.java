package com.reindecar.dto.rental;

import com.reindecar.entity.pricing.RentalType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
@Schema(description = "Request to create a new rental")
public record CreateRentalRequest(
    @NotNull(message = "Rental type is required")
    @Schema(description = "Type of rental (DAILY, WEEKLY, MONTHLY)")
    RentalType rentalType,

    @NotNull(message = "Vehicle ID is required")
    @Schema(description = "ID of the vehicle to rent", example = "1")
    Long vehicleId,

    @NotNull(message = "Customer ID is required")
    @Schema(description = "ID of the customer", example = "1")
    Long customerId,

    @Schema(description = "ID of additional driver (optional)", example = "1")
    Long driverId,

    @NotNull(message = "Branch ID is required")
    @Schema(description = "Pickup branch ID", example = "1")
    Long branchId,

    @Schema(description = "Return branch ID (optional, same as pickup if not specified)", example = "2")
    Long returnBranchId,

    @NotNull(message = "Start date is required")
    @FutureOrPresent(message = "Start date cannot be in the past")
    @Schema(description = "Rental start date", example = "2026-01-10")
    LocalDate startDate,

    @NotNull(message = "End date is required")
    @Future(message = "End date must be in the future")
    @Schema(description = "Rental end date", example = "2026-01-15")
    LocalDate endDate,

    @Schema(description = "Kilometer package ID (optional)", example = "1")
    Long kmPackageId,

    @Schema(description = "Discount amount (optional)", example = "50.00")
    BigDecimal discountAmount,

    @Schema(description = "Additional notes")
    String notes
) {}
