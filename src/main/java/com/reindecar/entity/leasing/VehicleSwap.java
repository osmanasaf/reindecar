package com.reindecar.entity.leasing;

import com.reindecar.common.entity.BaseEntity;
import com.reindecar.common.valueobject.Money;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "vehicle_swaps", indexes = {
    @Index(name = "idx_swap_rental", columnList = "rental_id"),
    @Index(name = "idx_swap_date", columnList = "swap_date")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class VehicleSwap extends BaseEntity {

    @NotNull
    @Column(name = "rental_id", nullable = false)
    private Long rentalId;

    @NotNull
    @Column(name = "old_vehicle_id", nullable = false)
    private Long oldVehicleId;

    @NotNull
    @Column(name = "new_vehicle_id", nullable = false)
    private Long newVehicleId;

    @NotNull
    @Column(name = "swap_date", nullable = false)
    private LocalDate swapDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private SwapReason reason;

    @Column(name = "old_vehicle_km", nullable = false)
    private int oldVehicleKm;

    @Column(name = "new_vehicle_km", nullable = false)
    private int newVehicleKm;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "price_difference_amount")),
        @AttributeOverride(name = "currency", column = @Column(name = "price_difference_currency", length = 3))
    })
    private Money priceDifference;

    @Column(length = 500)
    private String notes;

    @Column(name = "processed_by", length = 100)
    private String processedBy;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    public enum SwapReason {
        CUSTOMER_REQUEST,
        VEHICLE_ISSUE,
        UPGRADE,
        DOWNGRADE,
        ACCIDENT,
        MAINTENANCE
    }

    public static VehicleSwap create(
            Long rentalId,
            Long oldVehicleId,
            Long newVehicleId,
            LocalDate swapDate,
            SwapReason reason,
            int oldVehicleKm,
            int newVehicleKm,
            Money priceDifference,
            String notes,
            String processedBy) {

        VehicleSwap swap = new VehicleSwap();
        swap.rentalId = rentalId;
        swap.oldVehicleId = oldVehicleId;
        swap.newVehicleId = newVehicleId;
        swap.swapDate = swapDate;
        swap.reason = reason;
        swap.oldVehicleKm = oldVehicleKm;
        swap.newVehicleKm = newVehicleKm;
        swap.priceDifference = priceDifference;
        swap.notes = notes;
        swap.processedBy = processedBy;
        swap.createdAt = Instant.now();
        return swap;
    }

    public boolean hasUpgrade() {
        return this.priceDifference != null && this.priceDifference.getAmount().signum() > 0;
    }

    public boolean hasDowngrade() {
        return this.priceDifference != null && this.priceDifference.getAmount().signum() < 0;
    }
}
