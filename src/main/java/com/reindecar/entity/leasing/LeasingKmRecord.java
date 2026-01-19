package com.reindecar.entity.leasing;

import com.reindecar.common.entity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDate;
import java.time.YearMonth;

@Entity
@Table(name = "leasing_km_records", indexes = {
    @Index(name = "idx_km_record_rental", columnList = "rental_id"),
    @Index(name = "idx_km_record_date", columnList = "record_date")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LeasingKmRecord extends BaseEntity {

    @NotNull
    @Column(name = "rental_id", nullable = false)
    private Long rentalId;

    @NotNull
    @Column(name = "record_date", nullable = false)
    private LocalDate recordDate;

    @Column(name = "period_year_month", nullable = false, length = 7)
    private String periodYearMonth;

    @Min(0)
    @Column(name = "current_km", nullable = false)
    private int currentKm;

    @Min(0)
    @Column(name = "previous_km", nullable = false)
    private int previousKm;

    @Min(0)
    @Column(name = "used_km", nullable = false)
    private int usedKm;

    @Min(0)
    @Column(name = "monthly_allowance", nullable = false)
    private int monthlyAllowance;

    @Column(name = "excess_km", nullable = false)
    private int excessKm;

    @Column(name = "rollover_from_previous", nullable = false)
    private int rolloverFromPrevious;

    @Column(name = "rollover_to_next", nullable = false)
    private int rolloverToNext;

    @Column(name = "recorded_by", length = 100)
    private String recordedBy;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    public static LeasingKmRecord create(
            Long rentalId,
            LocalDate recordDate,
            int currentKm,
            int previousKm,
            int monthlyAllowance,
            int rolloverFromPrevious,
            String recordedBy) {

        LeasingKmRecord record = new LeasingKmRecord();
        record.rentalId = rentalId;
        record.recordDate = recordDate;
        record.periodYearMonth = YearMonth.from(recordDate).toString();
        record.currentKm = currentKm;
        record.previousKm = previousKm;
        record.monthlyAllowance = monthlyAllowance;
        record.rolloverFromPrevious = rolloverFromPrevious;
        record.recordedBy = recordedBy;
        record.createdAt = Instant.now();

        record.calculateUsage();
        return record;
    }

    private void calculateUsage() {
        this.usedKm = this.currentKm - this.previousKm;
        
        int effectiveAllowance = this.monthlyAllowance + this.rolloverFromPrevious;
        
        if (this.usedKm > effectiveAllowance) {
            this.excessKm = this.usedKm - effectiveAllowance;
            this.rolloverToNext = 0;
        } else {
            this.excessKm = 0;
            this.rolloverToNext = effectiveAllowance - this.usedKm;
        }
    }

    public boolean hasExcess() {
        return this.excessKm > 0;
    }

    public boolean hasRollover() {
        return this.rolloverToNext > 0;
    }
}
