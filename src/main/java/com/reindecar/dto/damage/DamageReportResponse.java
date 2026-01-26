package com.reindecar.dto.damage;

import com.reindecar.entity.damage.DamageLocation;
import com.reindecar.entity.damage.DamageSeverity;
import com.reindecar.entity.damage.DamageType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Schema(description = "Hasar raporu yanıtı")
public record DamageReportResponse(
    Long id,
    Long vehicleId,
    String vehiclePlate,
    Long rentalId,
    LocalDate reportDate,
    DamageType damageType,
    String damageTypeDisplayName,
    DamageLocation location,
    String locationDisplayName,
    int zoneId,
    DamageSeverity severity,
    String severityDisplayName,
    String severityColorCode,
    String description,
    BigDecimal estimatedCostAmount,
    String estimatedCostCurrency,
    String reportedBy,
    boolean repaired,
    LocalDate repairedDate,
    BigDecimal repairCostAmount,
    String repairCostCurrency,
    Instant createdAt,
    Instant updatedAt
) {}
