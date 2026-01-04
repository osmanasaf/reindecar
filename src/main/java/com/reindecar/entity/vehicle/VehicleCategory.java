package com.reindecar.entity.vehicle;

import com.reindecar.common.entity.BaseEntity;
import com.reindecar.common.valueobject.Money;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "vehicle_categories")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class VehicleCategory extends BaseEntity {

    @Column(nullable = false, unique = true, length = 10)
    private String code;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 500)
    private String description;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "default_daily_price_amount")),
        @AttributeOverride(name = "currency", column = @Column(name = "default_daily_price_currency"))
    })
    private Money defaultDailyPrice;

    @Column(nullable = false)
    private int sortOrder;

    @Column(nullable = false)
    private boolean active = true;

    public static VehicleCategory create(
            String code,
            String name,
            String description,
            Money defaultDailyPrice,
            int sortOrder) {
        
        VehicleCategory category = new VehicleCategory();
        category.code = code.toUpperCase();
        category.name = name;
        category.description = description;
        category.defaultDailyPrice = defaultDailyPrice;
        category.sortOrder = sortOrder;
        category.active = true;
        return category;
    }

    public void updateInfo(String name, String description, int sortOrder) {
        this.name = name;
        this.description = description;
        this.sortOrder = sortOrder;
    }

    public void updatePricing(Money newPrice) {
        this.defaultDailyPrice = newPrice;
    }

    public void activate() {
        this.active = true;
    }

    public void deactivate() {
        this.active = false;
    }

    public boolean isActive() {
        return active;
    }
}
