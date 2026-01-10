package com.reindecar.dto.vehicle;

import com.reindecar.entity.vehicle.InsuranceType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateVehicleInsuranceRequest(
    @NotNull(message = "Vehicle ID is required")
    Long vehicleId,

    @NotNull(message = "Insurance type is required")
    InsuranceType insuranceType,

    @Size(max = 50)
    String policyNumber,

    @Size(max = 100)
    String company,

    @NotNull(message = "Start date is required")
    LocalDate startDate,

    @NotNull(message = "End date is required")
    LocalDate endDate,

    BigDecimal premium,
    BigDecimal coverage,

    @Size(max = 20)
    String contactPhone,

    @Size(max = 500)
    String notes
) {}
