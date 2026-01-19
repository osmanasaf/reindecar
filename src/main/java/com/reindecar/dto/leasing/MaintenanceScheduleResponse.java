package com.reindecar.dto.leasing;

import com.reindecar.entity.leasing.MaintenanceSchedule;

import java.time.Instant;
import java.time.LocalDate;

public record MaintenanceScheduleResponse(
    Long id,
    Long rentalId,
    Long vehicleId,
    MaintenanceSchedule.ScheduleType scheduleType,
    Integer nextMaintenanceKm,
    LocalDate nextMaintenanceDate,
    Integer lastMaintenanceKm,
    LocalDate lastMaintenanceDate,
    Integer maintenanceIntervalKm,
    Integer maintenanceIntervalDays,
    boolean reminderSent,
    MaintenanceSchedule.ScheduleStatus status,
    String notes,
    Instant createdAt
) {}
