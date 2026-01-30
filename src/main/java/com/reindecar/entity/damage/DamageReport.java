package com.reindecar.entity.damage;

import com.reindecar.common.entity.BaseEntity;
import com.reindecar.common.valueobject.Money;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private DamageType damageType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private DamageLocation location;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private DamageSeverity severity;

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
    private boolean repaired;

    @Column
    private LocalDate repairedDate;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "repair_cost_amount")),
        @AttributeOverride(name = "currency", column = @Column(name = "repair_cost_currency"))
    })
    private Money repairCost;

    public static DamageReport create(
            Long vehicleId,
            Long rentalId,
            LocalDate reportDate,
            DamageType damageType,
            DamageLocation location,
            DamageSeverity severity,
            String description,
            Money estimatedCost,
            String reportedBy) {
        
        DamageReport report = new DamageReport();
        report.vehicleId = vehicleId;
        report.rentalId = rentalId;
        report.reportDate = reportDate;
        report.damageType = damageType;
        report.location = location;
        report.severity = severity;
        report.description = description;
        report.estimatedCost = estimatedCost;
        report.reportedBy = reportedBy;
        report.repaired = false;
        return report;
    }

    public void update(
            DamageType damageType,
            DamageLocation location,
            DamageSeverity severity,
            String description,
            Money estimatedCost) {
        
        if (damageType != null) this.damageType = damageType;
        if (location != null) this.location = location;
        if (severity != null) this.severity = severity;
        if (description != null) this.description = description;
        if (estimatedCost != null) this.estimatedCost = estimatedCost;
    }

    public void markAsRepaired(LocalDate repairedDate, Money repairCost) {
        this.repaired = true;
        this.repairedDate = repairedDate;
        this.repairCost = repairCost;
    }

    public void undoRepair() {
        this.repaired = false;
        this.repairedDate = null;
        this.repairCost = null;
    }
}
