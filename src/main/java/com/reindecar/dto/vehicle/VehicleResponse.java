package com.reindecar.dto.vehicle;

import com.reindecar.entity.vehicle.FuelType;
import com.reindecar.entity.vehicle.Transmission;
import com.reindecar.entity.vehicle.VehicleStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

public record VehicleResponse(
    Long id,
    String plateNumber,
    String vinNumber,
    String brand,
    String model,
    int year,
    String color,
    FuelType fuelType,
    Transmission transmission,
    int engineCapacity,
    int seatCount,
    Long categoryId,
    String categoryName,
    Long branchId,
    String branchName,
    VehicleStatus status,
    int currentKm,
    LocalDate insuranceExpiryDate,
    LocalDate inspectionExpiryDate,
    LocalDate registrationDate,
    BigDecimal dailyPrice,
    boolean isInsuranceExpiringSoon,
    boolean isInspectionExpiringSoon,
    String notes,
    Instant createdAt
) {}
