package com.reindecar.entity.leasing;

import com.reindecar.common.entity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "maintenance_schedules", indexes = {
    @Index(name = "idx_schedule_rental", columnList = "rental_id"),
    @Index(name = "idx_schedule_vehicle", columnList = "vehicle_id"),
    @Index(name = "idx_schedule_status", columnList = "status")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MaintenanceSchedule extends BaseEntity {

    @NotNull
    @Column(name = "rental_id", nullable = false)
    private Long rentalId;

    @NotNull
    @Column(name = "vehicle_id", nullable = false)
    private Long vehicleId;

    @Enumerated(EnumType.STRING)
    @Column(name = "schedule_type", nullable = false, length = 20)
    private ScheduleType scheduleType;

    @Column(name = "next_maintenance_km")
    private Integer nextMaintenanceKm;

    @Column(name = "next_maintenance_date")
    private LocalDate nextMaintenanceDate;

    @Column(name = "last_maintenance_km")
    private Integer lastMaintenanceKm;

    @Column(name = "last_maintenance_date")
    private LocalDate lastMaintenanceDate;

    @Column(name = "maintenance_interval_km")
    private Integer maintenanceIntervalKm;

    @Column(name = "maintenance_interval_days")
    private Integer maintenanceIntervalDays;

    @Column(name = "reminder_sent", nullable = false)
    private boolean reminderSent = false;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ScheduleStatus status = ScheduleStatus.SCHEDULED;

    @Column(length = 500)
    private String notes;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    public enum ScheduleType {
        KM_BASED, TIME_BASED, HYBRID
    }

    public enum ScheduleStatus {
        SCHEDULED, DUE, COMPLETED, OVERDUE, CANCELLED
    }

    public static MaintenanceSchedule createKmBased(
            Long rentalId,
            Long vehicleId,
            int currentKm,
            int intervalKm) {

        MaintenanceSchedule schedule = new MaintenanceSchedule();
        schedule.rentalId = rentalId;
        schedule.vehicleId = vehicleId;
        schedule.scheduleType = ScheduleType.KM_BASED;
        schedule.lastMaintenanceKm = currentKm;
        schedule.maintenanceIntervalKm = intervalKm;
        schedule.nextMaintenanceKm = currentKm + intervalKm;
        schedule.status = ScheduleStatus.SCHEDULED;
        schedule.createdAt = Instant.now();
        return schedule;
    }

    public static MaintenanceSchedule createTimeBased(
            Long rentalId,
            Long vehicleId,
            int intervalDays) {

        MaintenanceSchedule schedule = new MaintenanceSchedule();
        schedule.rentalId = rentalId;
        schedule.vehicleId = vehicleId;
        schedule.scheduleType = ScheduleType.TIME_BASED;
        schedule.lastMaintenanceDate = LocalDate.now();
        schedule.maintenanceIntervalDays = intervalDays;
        schedule.nextMaintenanceDate = LocalDate.now().plusDays(intervalDays);
        schedule.status = ScheduleStatus.SCHEDULED;
        schedule.createdAt = Instant.now();
        return schedule;
    }

    public static MaintenanceSchedule createHybrid(
            Long rentalId,
            Long vehicleId,
            int currentKm,
            int intervalKm,
            int intervalDays) {

        MaintenanceSchedule schedule = new MaintenanceSchedule();
        schedule.rentalId = rentalId;
        schedule.vehicleId = vehicleId;
        schedule.scheduleType = ScheduleType.HYBRID;
        schedule.lastMaintenanceKm = currentKm;
        schedule.lastMaintenanceDate = LocalDate.now();
        schedule.maintenanceIntervalKm = intervalKm;
        schedule.maintenanceIntervalDays = intervalDays;
        schedule.nextMaintenanceKm = currentKm + intervalKm;
        schedule.nextMaintenanceDate = LocalDate.now().plusDays(intervalDays);
        schedule.status = ScheduleStatus.SCHEDULED;
        schedule.createdAt = Instant.now();
        return schedule;
    }

    public void checkDueStatus(int currentKm, LocalDate today) {
        boolean isDue = false;
        
        if (this.nextMaintenanceKm != null && currentKm >= this.nextMaintenanceKm) {
            isDue = true;
        }
        
        if (this.nextMaintenanceDate != null && !today.isBefore(this.nextMaintenanceDate)) {
            isDue = true;
        }
        
        if (isDue && this.status == ScheduleStatus.SCHEDULED) {
            this.status = ScheduleStatus.DUE;
            this.updatedAt = Instant.now();
        }
    }

    public void checkOverdueStatus(int currentKm, LocalDate today) {
        boolean isOverdue = false;
        
        if (this.nextMaintenanceKm != null && currentKm > this.nextMaintenanceKm + 1000) {
            isOverdue = true;
        }
        
        if (this.nextMaintenanceDate != null && today.isAfter(this.nextMaintenanceDate.plusDays(7))) {
            isOverdue = true;
        }
        
        if (isOverdue && (this.status == ScheduleStatus.SCHEDULED || this.status == ScheduleStatus.DUE)) {
            this.status = ScheduleStatus.OVERDUE;
            this.updatedAt = Instant.now();
        }
    }

    public void complete(int completedAtKm) {
        this.lastMaintenanceKm = completedAtKm;
        this.lastMaintenanceDate = LocalDate.now();
        
        if (this.maintenanceIntervalKm != null) {
            this.nextMaintenanceKm = completedAtKm + this.maintenanceIntervalKm;
        }
        if (this.maintenanceIntervalDays != null) {
            this.nextMaintenanceDate = LocalDate.now().plusDays(this.maintenanceIntervalDays);
        }
        
        this.status = ScheduleStatus.SCHEDULED;
        this.reminderSent = false;
        this.updatedAt = Instant.now();
    }

    public void markReminderSent() {
        this.reminderSent = true;
        this.updatedAt = Instant.now();
    }

    public void cancel() {
        this.status = ScheduleStatus.CANCELLED;
        this.updatedAt = Instant.now();
    }

    public boolean isDue() {
        return this.status == ScheduleStatus.DUE;
    }

    public boolean isOverdue() {
        return this.status == ScheduleStatus.OVERDUE;
    }
}
