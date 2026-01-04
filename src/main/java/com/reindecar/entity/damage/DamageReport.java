package com.reindecar.entity.damage;

import com.reindecar.common.entity.BaseEntity;
import com.reindecar.common.valueobject.Money;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "damage_reports")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DamageReport extends BaseEntity {

    @Column(nullable = false, name = "vehicle_id")
    private Long vehicleId;

    @Column(name = "rental_id")
    private Long rentalId;

    @Column(nullable = false)
    private LocalDate reportDate;

    @Column(nullable = false, length = 200)
    private String damageType;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "estimated_cost_amount")),
        @AttributeOverride(name = "currency", column = @Column(name = "estimated_cost_currency"))
    })
    private Money estimatedCost;

    @Column(length = 100)
    private String reportedBy;

    @Column(nullable = false)
    private Instant createdAt;

    public static DamageReport create(
            Long vehicleId,
            Long rentalId,
            LocalDate reportDate,
            String damageType,
            String description,
            Money estimatedCost,
            String reportedBy) {
        
        DamageReport report = new DamageReport();
        report.vehicleId = vehicleId;
        report.rentalId = rentalId;
        report.reportDate = reportDate;
        report.damageType = damageType;
        report.description = description;
        report.estimatedCost = estimatedCost;
        report.reportedBy = reportedBy;
        report.createdAt = Instant.now();
        return report;
    }
}
