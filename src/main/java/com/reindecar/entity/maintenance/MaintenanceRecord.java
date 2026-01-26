package com.reindecar.entity.maintenance;

import com.reindecar.common.entity.BaseEntity;
import com.reindecar.common.valueobject.Money;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "maintenance_records")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MaintenanceRecord extends BaseEntity {

    @Column(nullable = false, name = "vehicle_id")
    private Long vehicleId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private MaintenanceType maintenanceType;

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

    @ElementCollection
    @CollectionTable(
        name = "maintenance_record_affected_zones",
        joinColumns = @JoinColumn(name = "maintenance_record_id")
    )
    @Column(name = "zone_id")
    private List<Integer> affectedZones = new ArrayList<>();

    @ElementCollection
    @CollectionTable(
        name = "maintenance_record_parts_replaced",
        joinColumns = @JoinColumn(name = "maintenance_record_id")
    )
    @Column(name = "part_name", length = 200)
    private List<String> partsReplaced = new ArrayList<>();

    @Column(length = 50)
    private String paintColor;

    public static MaintenanceRecord create(
            Long vehicleId,
            MaintenanceType maintenanceType,
            LocalDate maintenanceDate,
            int currentKm,
            Money cost,
            String serviceProvider,
            String description,
            List<Integer> affectedZones,
            List<String> partsReplaced,
            String paintColor) {
        
        MaintenanceRecord record = new MaintenanceRecord();
        record.vehicleId = vehicleId;
        record.maintenanceType = maintenanceType;
        record.maintenanceDate = maintenanceDate;
        record.currentKm = currentKm;
        record.cost = cost;
        record.serviceProvider = serviceProvider;
        record.description = description;
        record.affectedZones = affectedZones != null ? new ArrayList<>(affectedZones) : new ArrayList<>();
        record.partsReplaced = partsReplaced != null ? new ArrayList<>(partsReplaced) : new ArrayList<>();
        record.paintColor = paintColor;
        return record;
    }

    public void update(
            MaintenanceType maintenanceType,
            LocalDate maintenanceDate,
            int currentKm,
            Money cost,
            String serviceProvider,
            String description,
            List<Integer> affectedZones,
            List<String> partsReplaced,
            String paintColor) {
        
        this.maintenanceType = maintenanceType;
        this.maintenanceDate = maintenanceDate;
        this.currentKm = currentKm;
        this.cost = cost;
        this.serviceProvider = serviceProvider;
        this.description = description;
        this.affectedZones = affectedZones != null ? new ArrayList<>(affectedZones) : new ArrayList<>();
        this.partsReplaced = partsReplaced != null ? new ArrayList<>(partsReplaced) : new ArrayList<>();
        this.paintColor = paintColor;
    }
}
