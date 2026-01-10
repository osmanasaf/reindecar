package com.reindecar.entity.vehicle;

import com.reindecar.common.entity.BaseEntity;
import com.reindecar.common.valueobject.Money;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "vehicle_insurances")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class VehicleInsurance extends BaseEntity {

    @NotNull
    @Column(nullable = false, name = "vehicle_id")
    private Long vehicleId;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private InsuranceType insuranceType;

    @Size(max = 50)
    @Column(length = 50)
    private String policyNumber;

    @Size(max = 100)
    @Column(length = 100)
    private String company;

    @NotNull
    @Column(nullable = false)
    private LocalDate startDate;

    @NotNull
    @Column(nullable = false)
    private LocalDate endDate;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "premium_amount")),
        @AttributeOverride(name = "currency", column = @Column(name = "premium_currency"))
    })
    private Money premium;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "coverage_amount")),
        @AttributeOverride(name = "currency", column = @Column(name = "coverage_currency"))
    })
    private Money coverage;

    @Size(max = 20)
    @Column(length = 20)
    private String contactPhone;

    @Size(max = 500)
    @Column(length = 500)
    private String notes;

    @Column(nullable = false)
    private boolean active = true;

    @Column(nullable = false)
    private Instant createdAt;

    public static VehicleInsurance create(
            Long vehicleId,
            InsuranceType insuranceType,
            String policyNumber,
            String company,
            LocalDate startDate,
            LocalDate endDate,
            Money premium,
            Money coverage,
            String contactPhone,
            String notes) {
        
        VehicleInsurance insurance = new VehicleInsurance();
        insurance.vehicleId = vehicleId;
        insurance.insuranceType = insuranceType;
        insurance.policyNumber = policyNumber;
        insurance.company = company;
        insurance.startDate = startDate;
        insurance.endDate = endDate;
        insurance.premium = premium;
        insurance.coverage = coverage;
        insurance.contactPhone = contactPhone;
        insurance.notes = notes;
        insurance.active = true;
        insurance.createdAt = Instant.now();
        return insurance;
    }

    public void updateInfo(
            String policyNumber,
            String company,
            LocalDate startDate,
            LocalDate endDate,
            Money premium,
            Money coverage,
            String contactPhone,
            String notes) {
        
        this.policyNumber = policyNumber;
        this.company = company;
        this.startDate = startDate;
        this.endDate = endDate;
        this.premium = premium;
        this.coverage = coverage;
        this.contactPhone = contactPhone;
        this.notes = notes;
    }

    public void deactivate() {
        this.active = false;
    }

    public void activate() {
        this.active = true;
    }

    public boolean isExpired() {
        return endDate.isBefore(LocalDate.now());
    }

    public boolean isExpiringSoon(int daysThreshold) {
        return endDate.isBefore(LocalDate.now().plusDays(daysThreshold));
    }

    public boolean isValid() {
        LocalDate now = LocalDate.now();
        return active && !now.isBefore(startDate) && !now.isAfter(endDate);
    }
}
