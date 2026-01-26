package com.reindecar.entity.pricing;

import com.reindecar.common.entity.BaseEntity;
import com.reindecar.common.valueobject.Money;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(
    name = "vehicle_category_pricing",
    uniqueConstraints = @UniqueConstraint(
        columnNames = {"category_id", "valid_from"},
        name = "uk_category_pricing_validity"
    )
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class VehicleCategoryPricing extends BaseEntity {

    @NotNull
    @Column(name = "category_id", nullable = false)
    private Long categoryId;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "daily_price_amount")),
        @AttributeOverride(name = "currency", column = @Column(name = "daily_price_currency", length = 3))
    })
    private Money dailyPrice;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "weekly_price_amount")),
        @AttributeOverride(name = "currency", column = @Column(name = "weekly_price_currency", length = 3))
    })
    private Money weeklyPrice;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "monthly_price_amount")),
        @AttributeOverride(name = "currency", column = @Column(name = "monthly_price_currency", length = 3))
    })
    private Money monthlyPrice;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "yearly_price_amount")),
        @AttributeOverride(name = "currency", column = @Column(name = "yearly_price_currency", length = 3))
    })
    private Money yearlyPrice;

    @Column(name = "valid_from")
    private LocalDate validFrom;

    @Column(name = "valid_to")
    private LocalDate validTo;

    @Column(nullable = false)
    private boolean active = true;

    public static VehicleCategoryPricing create(
            Long categoryId,
            Money dailyPrice,
            Money weeklyPrice,
            Money monthlyPrice,
            Money yearlyPrice,
            LocalDate validFrom,
            LocalDate validTo) {
        var pricing = new VehicleCategoryPricing();
        pricing.categoryId = categoryId;
        pricing.dailyPrice = dailyPrice;
        pricing.weeklyPrice = weeklyPrice;
        pricing.monthlyPrice = monthlyPrice;
        pricing.yearlyPrice = yearlyPrice;
        pricing.validFrom = validFrom;
        pricing.validTo = validTo;
        pricing.active = true;
        return pricing;
    }

    public boolean isApplicable(LocalDate date) {
        if (!active) return false;
        if (validFrom != null && date.isBefore(validFrom)) return false;
        if (validTo != null && date.isAfter(validTo)) return false;
        return true;
    }

    public void updatePrices(
            Money dailyPrice,
            Money weeklyPrice,
            Money monthlyPrice,
            Money yearlyPrice) {
        this.dailyPrice = dailyPrice;
        this.weeklyPrice = weeklyPrice;
        this.monthlyPrice = monthlyPrice;
        this.yearlyPrice = yearlyPrice;
    }

    public void updateValidity(LocalDate validFrom, LocalDate validTo) {
        this.validFrom = validFrom;
        this.validTo = validTo;
    }

    public void activate() {
        this.active = true;
    }

    public void deactivate() {
        this.active = false;
    }
}
