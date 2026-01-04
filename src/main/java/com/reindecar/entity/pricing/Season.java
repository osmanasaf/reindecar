package com.reindecar.entity.pricing;

import com.reindecar.common.entity.BaseEntity;
import com.reindecar.common.valueobject.Money;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "seasons")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Season extends BaseEntity {

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal priceMultiplier;

    @Column(nullable = false)
    private boolean active = true;

    public static Season create(
            String name,
            LocalDate startDate,
            LocalDate endDate,
            BigDecimal priceMultiplier) {
        
        Season season = new Season();
        season.name = name;
        season.startDate = startDate;
        season.endDate = endDate;
        season.priceMultiplier = priceMultiplier;
        season.active = true;
        return season;
    }

    public boolean isApplicable(LocalDate date) {
        if (!active) {
            return false;
        }
        return !date.isBefore(startDate) && !date.isAfter(endDate);
    }

    public Money applyMultiplier(Money basePrice) {
        return basePrice.multiply(priceMultiplier);
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
