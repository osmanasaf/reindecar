package com.reindecar.entity.pricing;

import com.reindecar.common.entity.BaseEntity;
import com.reindecar.common.valueobject.Money;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Vade bazlı iskonto tanımları.
 * Örn: 24 ay için %15 indirim veya 40.000 TL sabit indirim.
 */
@Entity
@Table(
    name = "term_discount",
    uniqueConstraints = @UniqueConstraint(
        columnNames = {"category_id", "term_months"},
        name = "uk_term_discount_category_term"
    )
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TermDiscount extends BaseEntity {

    @Column(name = "category_id")
    private Long categoryId;

    @NotNull
    @Min(12)
    @Column(name = "term_months", nullable = false)
    private Integer termMonths;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "discount_type", nullable = false, length = 20)
    private DiscountType discountType;

    @NotNull
    @Column(name = "discount_value", nullable = false, precision = 19, scale = 4)
    private BigDecimal discountValue;

    @Column(nullable = false)
    private boolean active = true;

    public static TermDiscount createPercentage(Long categoryId, int termMonths, BigDecimal percentage) {
        var discount = new TermDiscount();
        discount.categoryId = categoryId;
        discount.termMonths = termMonths;
        discount.discountType = DiscountType.PERCENTAGE;
        discount.discountValue = percentage;
        discount.active = true;
        return discount;
    }

    public static TermDiscount createFixedAmount(Long categoryId, int termMonths, BigDecimal amount) {
        var discount = new TermDiscount();
        discount.categoryId = categoryId;
        discount.termMonths = termMonths;
        discount.discountType = DiscountType.FIXED_AMOUNT;
        discount.discountValue = amount;
        discount.active = true;
        return discount;
    }

    public static TermDiscount createGlobalPercentage(int termMonths, BigDecimal percentage) {
        return createPercentage(null, termMonths, percentage);
    }

    public static TermDiscount createGlobalFixedAmount(int termMonths, BigDecimal amount) {
        return createFixedAmount(null, termMonths, amount);
    }

    public Money applyDiscount(Money basePrice) {
        if (!active) return basePrice;

        return switch (discountType) {
            case PERCENTAGE -> {
                var discountAmount = basePrice.getAmount()
                    .multiply(discountValue)
                    .divide(BigDecimal.valueOf(100), 2, java.math.RoundingMode.HALF_UP);
                yield basePrice.subtract(Money.of(discountAmount, basePrice.getCurrency()));
            }
            case FIXED_AMOUNT -> basePrice.subtract(Money.of(discountValue, basePrice.getCurrency()));
        };
    }

    public Money calculateDiscountAmount(Money basePrice) {
        if (!active) return Money.zero(basePrice.getCurrency());

        return switch (discountType) {
            case PERCENTAGE -> {
                var discountAmount = basePrice.getAmount()
                    .multiply(discountValue)
                    .divide(BigDecimal.valueOf(100), 2, java.math.RoundingMode.HALF_UP);
                yield Money.of(discountAmount, basePrice.getCurrency());
            }
            case FIXED_AMOUNT -> Money.of(discountValue, basePrice.getCurrency());
        };
    }

    public boolean isApplicable(Long categoryId, int termMonths) {
        if (!active) return false;
        if (!this.termMonths.equals(termMonths)) return false;
        return this.categoryId == null || this.categoryId.equals(categoryId);
    }

    public void update(DiscountType discountType, BigDecimal discountValue) {
        this.discountType = discountType;
        this.discountValue = discountValue;
    }

    public void activate() {
        this.active = true;
    }

    public void deactivate() {
        this.active = false;
    }
}
