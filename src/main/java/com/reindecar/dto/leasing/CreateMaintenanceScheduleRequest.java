package com.reindecar.dto.leasing;

import com.reindecar.entity.leasing.MaintenanceSchedule;
import jakarta.validation.constraints.NotNull;

public record CreateMaintenanceScheduleRequest(
    @NotNull
    MaintenanceSchedule.ScheduleType scheduleType,

    Integer intervalKm,

    Integer intervalDays,

    Integer currentKm
) {}
