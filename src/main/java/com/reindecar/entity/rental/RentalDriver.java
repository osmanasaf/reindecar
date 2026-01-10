package com.reindecar.entity.rental;

import com.reindecar.common.entity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "rental_drivers", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"rental_id", "driver_id"})
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RentalDriver extends BaseEntity {

    @Column(name = "rental_id", nullable = false)
    private Long rentalId;

    @Column(name = "driver_id", nullable = false)
    private Long driverId;

    @Column(name = "is_primary", nullable = false)
    private boolean primary;

    @Column(name = "added_at", nullable = false)
    private Instant addedAt;

    @Size(max = 100)
    @Column(name = "added_by", length = 100)
    private String addedBy;

    @Column(length = 500)
    private String notes;

    public static RentalDriver create(Long rentalId, Long driverId, boolean isPrimary, String addedBy) {
        RentalDriver rd = new RentalDriver();
        rd.rentalId = rentalId;
        rd.driverId = driverId;
        rd.primary = isPrimary;
        rd.addedAt = Instant.now();
        rd.addedBy = addedBy;
        return rd;
    }

    public void setPrimary(boolean primary) {
        this.primary = primary;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
