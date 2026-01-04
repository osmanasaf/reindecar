package com.reindecar.entity.vehicle;

import com.reindecar.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "vehicle_status_history")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class VehicleStatusHistory extends BaseEntity {

    @Column(nullable = false, name = "vehicle_id")
    private Long vehicleId;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private VehicleStatus oldStatus;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private VehicleStatus newStatus;

    @Column(length = 50)
    private String referenceType;

    @Column
    private Long referenceId;

    @Column(length = 500)
    private String reason;

    @Column(nullable = false, length = 100)
    private String changedBy;

    @Column(nullable = false)
    private Instant changedAt;

    public static VehicleStatusHistory create(
            Long vehicleId,
            VehicleStatus oldStatus,
            VehicleStatus newStatus,
            String referenceType,
            Long referenceId,
            String reason,
            String changedBy) {
        
        VehicleStatusHistory history = new VehicleStatusHistory();
        history.vehicleId = vehicleId;
        history.oldStatus = oldStatus;
        history.newStatus = newStatus;
        history.referenceType = referenceType;
        history.referenceId = referenceId;
        history.reason = reason;
        history.changedBy = changedBy;
        history.changedAt = Instant.now();
        return history;
    }
}
