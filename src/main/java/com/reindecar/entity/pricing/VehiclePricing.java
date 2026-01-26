package com.reindecar.entity.pricing;

import com.reindecar.common.entity.BaseEntity;
import com.reindecar.common.valueobject.Money;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Araç bazlı özel fiyatlandırma.
 * Kategori fiyatlarını override eder.
 */
@Entity
@Table(name = "vehicle_pricing")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class VehiclePricing extends BaseEntity {

    @NotNull
    @Column(name = "vehicle_id", nullable = false, unique = true)
    private Long vehicleId;

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

    @Column(nullable = false)
    private boolean active = true;

    public static VehiclePricing create(Long vehicleId) {
        var pricing = new VehiclePricing();
        pricing.vehicleId = vehicleId;
        pricing.active = true;
        return pricing;
    }

    public void setDailyPrice(Money price) {
        this.dailyPrice = price;
    }

    public void setWeeklyPrice(Money price) {
        this.weeklyPrice = price;
    }

    public void setMonthlyPrice(Money price) {
        this.monthlyPrice = price;
    }

    public void setYearlyPrice(Money price) {
        this.yearlyPrice = price;
    }

    public boolean hasDailyPrice() {
        return dailyPrice != null;
    }

    public boolean hasWeeklyPrice() {
        return weeklyPrice != null;
    }

    public boolean hasMonthlyPrice() {
        return monthlyPrice != null;
    }

    public boolean hasYearlyPrice() {
        return yearlyPrice != null;
    }

    public void activate() {
        this.active = true;
    }

    public void deactivate() {
        this.active = false;
    }
}
