package com.reindecar.dto.vehicle;

import com.reindecar.entity.vehicle.FuelType;
import com.reindecar.entity.vehicle.Transmission;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
@Schema(description = "Request to create a new vehicle")
public record CreateVehicleRequest(
    @NotBlank(message = "Plate number is required")
    @Size(max = 20)
    @Schema(description = "Vehicle plate number", example = "34ABC123")
    String plateNumber,

    @NotBlank(message = "VIN number is required")
    @Size(max = 50)
    @Schema(description = "Vehicle Identification Number (17 characters)", example = "1HGBH41JXMN109186")
    String vinNumber,

    @NotBlank(message = "Brand is required")
    @Size(max = 50)
    @Schema(description = "Vehicle brand", example = "Toyota")
    String brand,

    @NotBlank(message = "Model is required")
    @Size(max = 50)
    @Schema(description = "Vehicle model", example = "Corolla")
    String model,

    @Min(value = 2000, message = "Year must be at least 2000")
    @Schema(description = "Model year", example = "2023")
    int year,

    @Size(max = 30)
    @Schema(description = "Vehicle color", example = "Beyaz")
    String color,

    @NotNull(message = "Fuel type is required")
    @Schema(description = "Fuel type")
    FuelType fuelType,

    @NotNull(message = "Transmission is required")
    @Schema(description = "Transmission type")
    Transmission transmission,

    @Schema(description = "Engine capacity in cc", example = "1600")
    int engineCapacity,

    @Schema(description = "Number of seats", example = "5")
    int seatCount,

    @NotNull(message = "Category ID is required")
    @Schema(description = "Vehicle category ID", example = "1")
    Long categoryId,

    @NotNull(message = "Branch ID is required")
    @Schema(description = "Branch ID where vehicle is located", example = "1")
    Long branchId,

    @Schema(description = "Current kilometers", example = "50000")
    int currentKm,

    @Schema(description = "Insurance expiry date", example = "2025-12-31")
    LocalDate insuranceExpiryDate,

    @Schema(description = "Inspection expiry date", example = "2025-06-30")
    LocalDate inspectionExpiryDate,

    @Schema(description = "Registration date", example = "2023-01-15")
    LocalDate registrationDate,

    @Schema(description = "Daily rental price", example = "500.00")
    BigDecimal dailyPrice,

    @Schema(description = "Weekly rental price", example = "3000.00")
    BigDecimal weeklyPrice,

    @Schema(description = "Monthly rental price", example = "10000.00")
    BigDecimal monthlyPrice,

    @Size(max = 1000)
    @Schema(description = "Additional notes")
    String notes
) {}
