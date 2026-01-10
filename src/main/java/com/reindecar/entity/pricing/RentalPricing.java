package com.reindecar.entity.pricing;

import com.reindecar.common.entity.BaseEntity;
import com.reindecar.common.valueobject.Money;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "rental_pricing")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RentalPricing extends BaseEntity {

    @Column(name = "vehicle_id")
    private Long vehicleId;

    @Column(name = "customer_id")
    private Long customerId;

    @Column(name = "category_id")
    private Long categoryId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RentalType rentalType;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "monthly_price_amount")),
        @AttributeOverride(name = "currency", column = @Column(name = "monthly_price_currency"))
    })
    private Money monthlyPrice;

    @Min(0)
    @Column(nullable = false)
    private int kmLimit;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "extra_km_price_amount")),
        @AttributeOverride(name = "currency", column = @Column(name = "extra_km_price_currency"))
    })
    private Money extraKmPrice;

    @Column
    private LocalDate validFrom;

    @Column
    private LocalDate validTo;

    @Column(nullable = false)
    private boolean active = true;

    @Size(max = 500)
    @Column(length = 500)
    private String notes;

    public static RentalPricing createForVehicle(
            Long vehicleId,
            RentalType rentalType,
            Money monthlyPrice,
            int kmLimit,
            Money extraKmPrice) {

        RentalPricing pricing = new RentalPricing();
        pricing.vehicleId = vehicleId;
        pricing.rentalType = rentalType;
        pricing.monthlyPrice = monthlyPrice;
        pricing.kmLimit = kmLimit;
        pricing.extraKmPrice = extraKmPrice;
        pricing.active = true;
        return pricing;
    }

    public static RentalPricing createForCustomer(
            Long customerId,
            Long categoryId,
            RentalType rentalType,
            Money monthlyPrice,
            int kmLimit,
            Money extraKmPrice) {

        RentalPricing pricing = new RentalPricing();
        pricing.customerId = customerId;
        pricing.categoryId = categoryId;
        pricing.rentalType = rentalType;
        pricing.monthlyPrice = monthlyPrice;
        pricing.kmLimit = kmLimit;
        pricing.extraKmPrice = extraKmPrice;
        pricing.active = true;
        return pricing;
    }

    public static RentalPricing createForCategory(
            Long categoryId,
            RentalType rentalType,
            Money monthlyPrice,
            int kmLimit,
            Money extraKmPrice) {

        RentalPricing pricing = new RentalPricing();
        pricing.categoryId = categoryId;
        pricing.rentalType = rentalType;
        pricing.monthlyPrice = monthlyPrice;
        pricing.kmLimit = kmLimit;
        pricing.extraKmPrice = extraKmPrice;
        pricing.active = true;
        return pricing;
    }

    public void updatePricing(Money monthlyPrice, int kmLimit, Money extraKmPrice) {
        this.monthlyPrice = monthlyPrice;
        this.kmLimit = kmLimit;
        this.extraKmPrice = extraKmPrice;
    }

    public void setValidityPeriod(LocalDate from, LocalDate to) {
        this.validFrom = from;
        this.validTo = to;
    }

    public Money calculateExtraKmCost(int actualKm) {
        if (kmLimit <= 0 || actualKm <= kmLimit) {
            return Money.zero(extraKmPrice.getCurrency());
        }
        int extraKm = actualKm - kmLimit;
        return extraKmPrice.multiply(extraKm);
    }

    public boolean isKmLimitExceeded(int currentKm, int startKm) {
        int usedKm = currentKm - startKm;
        return kmLimit > 0 && usedKm > kmLimit;
    }

    public int getRemainingKm(int currentKm, int startKm) {
        int usedKm = currentKm - startKm;
        return Math.max(0, kmLimit - usedKm);
    }

    public boolean isApplicable(LocalDate date) {
        if (!active) return false;
        if (validFrom != null && date.isBefore(validFrom)) return false;
        if (validTo != null && date.isAfter(validTo)) return false;
        return true;
    }

    public void activate() {
        this.active = true;
    }

    public void deactivate() {
        this.active = false;
    }

    public boolean isVehicleSpecific() {
        return vehicleId != null;
    }

    public boolean isCustomerSpecific() {
        return customerId != null;
    }

    public boolean isCategoryLevel() {
        return vehicleId == null && customerId == null && categoryId != null;
    }
}
