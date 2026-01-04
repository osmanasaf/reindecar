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
@Table(name = "pricing_rules")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PricingRule extends BaseEntity {

    @Column(nullable = false, name = "category_id")
    private Long categoryId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RentalType rentalType;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "base_price_amount")),
        @AttributeOverride(name = "currency", column = @Column(name = "base_price_currency"))
    })
    private Money basePrice;

    @Column
    private int minDays;

    @Column
    private int maxDays;

    @Column(precision = 5, scale = 2)
    private BigDecimal discountPercent;

    @Column
    private LocalDate validFrom;

    @Column
    private LocalDate validTo;

    @Column(nullable = false)
    private boolean active = true;

    public static PricingRule create(
            Long categoryId,
            RentalType rentalType,
            Money basePrice,
            int minDays,
            int maxDays,
            BigDecimal discountPercent,
            LocalDate validFrom,
            LocalDate validTo) {
        
        PricingRule rule = new PricingRule();
        rule.categoryId = categoryId;
        rule.rentalType = rentalType;
        rule.basePrice = basePrice;
        rule.minDays = minDays;
        rule.maxDays = maxDays;
        rule.discountPercent = discountPercent;
        rule.validFrom = validFrom;
        rule.validTo = validTo;
        rule.active = true;
        return rule;
    }

    public boolean isApplicable(RentalType type, int days, LocalDate date) {
        if (!active || rentalType != type) {
            return false;
        }

        if (days < minDays || (maxDays > 0 && days > maxDays)) {
            return false;
        }

        if (validFrom != null && date.isBefore(validFrom)) {
            return false;
        }

        if (validTo != null && date.isAfter(validTo)) {
            return false;
        }

        return true;
    }

    public Money calculatePrice(int days) {
        Money total = basePrice.multiply(days);
        
        if (discountPercent != null && discountPercent.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal discountMultiplier = BigDecimal.ONE.subtract(
                discountPercent.divide(BigDecimal.valueOf(100))
            );
            total = total.multiply(discountMultiplier);
        }
        
        return total;
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
