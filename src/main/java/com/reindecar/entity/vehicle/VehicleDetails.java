package com.reindecar.entity.vehicle;

import com.reindecar.common.entity.BaseEntity;
import com.reindecar.common.valueobject.Money;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "vehicle_details")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class VehicleDetails extends BaseEntity {

    @Column(nullable = false, unique = true, name = "vehicle_id")
    private Long vehicleId;

    @Size(max = 50)
    @Column(length = 50)
    private String hgsNumber;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "hgs_balance_amount")),
        @AttributeOverride(name = "currency", column = @Column(name = "hgs_balance_currency"))
    })
    private Money hgsBalance;

    @Column
    private Instant hgsLastUpdated;

    @Size(max = 50)
    @Column(length = 50)
    private String kabisNumber;

    @Column
    private LocalDate mtvDate;

    @Column
    private LocalDate registrationDate;

    @Column
    private LocalDate nextServiceDate;

    @Min(value = 0)
    @Column
    private Integer nextServiceKm;

    @Column
    private LocalDate lastServiceDate;

    @Column
    private LocalDate nextTireChangeDate;

    @Column
    private LocalDate creditEndDate;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "remaining_credit_amount")),
        @AttributeOverride(name = "currency", column = @Column(name = "remaining_credit_currency"))
    })
    private Money remainingCreditAmount;

    @Column
    private LocalDate purchaseDate;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "purchase_price_amount")),
        @AttributeOverride(name = "currency", column = @Column(name = "purchase_price_currency"))
    })
    private Money purchasePrice;

    public static VehicleDetails createFor(Long vehicleId) {
        VehicleDetails details = new VehicleDetails();
        details.vehicleId = vehicleId;
        return details;
    }

    public void updateHgsInfo(String hgsNumber, Money balance) {
        this.hgsNumber = hgsNumber;
        this.hgsBalance = balance;
        this.hgsLastUpdated = Instant.now();
    }

    public void updateHgsBalance(Money newBalance) {
        this.hgsBalance = newBalance;
        this.hgsLastUpdated = Instant.now();
    }

    public void updateKabisNumber(String kabisNumber) {
        this.kabisNumber = kabisNumber;
    }

    public void updateMtvDate(LocalDate mtvDate) {
        this.mtvDate = mtvDate;
    }

    public void updateServiceInfo(LocalDate nextServiceDate, Integer nextServiceKm) {
        this.lastServiceDate = this.nextServiceDate;
        this.nextServiceDate = nextServiceDate;
        this.nextServiceKm = nextServiceKm;
    }

    public void updateTireChangeDate(LocalDate nextTireChangeDate) {
        this.nextTireChangeDate = nextTireChangeDate;
    }

    public void updateFinanceInfo(LocalDate creditEndDate, Money remainingAmount) {
        this.creditEndDate = creditEndDate;
        this.remainingCreditAmount = remainingAmount;
    }

    public void updatePurchaseInfo(LocalDate purchaseDate, Money purchasePrice) {
        this.purchaseDate = purchaseDate;
        this.purchasePrice = purchasePrice;
    }

    public boolean isHgsBalanceLow(Money threshold) {
        if (hgsBalance == null || threshold == null) {
            return false;
        }
        return hgsBalance.isLessThan(threshold);
    }

    public boolean isServiceDueSoon(int currentKm, int kmThreshold) {
        if (nextServiceKm == null) {
            return false;
        }
        return (nextServiceKm - currentKm) <= kmThreshold;
    }

    public boolean isServiceDateDueSoon(int daysThreshold) {
        if (nextServiceDate == null) {
            return false;
        }
        return nextServiceDate.isBefore(LocalDate.now().plusDays(daysThreshold));
    }

    public boolean isMtvDueSoon(int daysThreshold) {
        if (mtvDate == null) {
            return false;
        }
        return mtvDate.isBefore(LocalDate.now().plusDays(daysThreshold));
    }

    public boolean isTireChangeDueSoon(int daysThreshold) {
        if (nextTireChangeDate == null) {
            return false;
        }
        return nextTireChangeDate.isBefore(LocalDate.now().plusDays(daysThreshold));
    }
}
