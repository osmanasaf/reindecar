package com.reindecar.dto.vehicle;

import com.reindecar.entity.vehicle.InsuranceType;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

public record VehicleInsuranceResponse(
    Long id,
    Long vehicleId,
    InsuranceType insuranceType,
    String policyNumber,
    String company,
    LocalDate startDate,
    LocalDate endDate,
    BigDecimal premium,
    String premiumCurrency,
    BigDecimal coverage,
    String coverageCurrency,
    String contactPhone,
    String notes,
    boolean active,
    boolean isExpired,
    boolean isExpiringSoon,
    boolean isValid,
    Instant createdAt
) {}
