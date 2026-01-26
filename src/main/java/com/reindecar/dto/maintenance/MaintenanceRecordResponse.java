package com.reindecar.dto.maintenance;

import com.reindecar.entity.maintenance.MaintenanceType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Schema(description = "Bakım kaydı yanıtı")
public record MaintenanceRecordResponse(
    Long id,
    Long vehicleId,
    String vehiclePlate,
    MaintenanceType maintenanceType,
    String maintenanceTypeDisplayName,
    String maintenanceTypeColorCode,
    LocalDate maintenanceDate,
    int currentKm,
    BigDecimal costAmount,
    String costCurrency,
    String serviceProvider,
    String description,
    List<Integer> affectedZones,
    List<String> partsReplaced,
    String paintColor,
    Instant createdAt,
    Instant updatedAt
) {}
