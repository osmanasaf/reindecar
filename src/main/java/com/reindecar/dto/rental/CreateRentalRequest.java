package com.reindecar.dto.rental;

import com.reindecar.entity.customer.CustomerType;
import com.reindecar.entity.pricing.RentalType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Builder
@Schema(description = "Request to create a new rental")
public record CreateRentalRequest(
    @NotNull(message = "Rental type is required")
    @Schema(description = "Type of rental (DAILY, WEEKLY, MONTHLY, LEASING)")
    RentalType rentalType,

    @NotNull(message = "Vehicle ID is required")
    @Schema(description = "ID of the vehicle to rent", example = "1")
    Long vehicleId,

    @NotNull(message = "Customer ID is required")
    @Schema(description = "ID of the customer (Person or Company)", example = "1")
    Long customerId,

    @NotNull(message = "Customer type is required")
    @Schema(description = "Type of customer (PERSONAL or COMPANY)")
    CustomerType customerType,

    @Schema(description = "ID of contract signer (required for COMPANY)", example = "1")
    Long contractSignerId,

    @Schema(description = "Name of contract signer")
    String contractSignerName,

    @Schema(description = "List of driver IDs")
    List<Long> driverIds,

    @Schema(description = "Primary driver ID (must be in driverIds list)")
    Long primaryDriverId,

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
