package com.reindecar.entity.maintenance;

import com.reindecar.common.entity.BaseEntity;
import com.reindecar.common.valueobject.Money;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "maintenance_records")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MaintenanceRecord extends BaseEntity {

    @Column(nullable = false, name = "vehicle_id")
    private Long vehicleId;

    @Column(nullable = false, length = 100)
    private String maintenanceType;

    @Column(nullable = false)
    private LocalDate maintenanceDate;

    @Column
    private int currentKm;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "cost_amount")),
        @AttributeOverride(name = "currency", column = @Column(name = "cost_currency"))
    })
    private Money cost;

    @Column(length = 200)
    private String serviceProvider;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false)
    private Instant createdAt;

    public static MaintenanceRecord create(
            Long vehicleId,
            String maintenanceType,
            LocalDate maintenanceDate,
            int currentKm,
            Money cost,
            String serviceProvider,
            String description) {
        
        MaintenanceRecord record = new MaintenanceRecord();
        record.vehicleId = vehicleId;
        record.maintenanceType = maintenanceType;
        record.maintenanceDate = maintenanceDate;
        record.currentKm = currentKm;
        record.cost = cost;
        record.serviceProvider = serviceProvider;
        record.description = description;
        record.createdAt = Instant.now();
        return record;
    }
}
