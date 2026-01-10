package com.reindecar.entity.pricing;

import com.reindecar.common.entity.BaseEntity;
import com.reindecar.common.valueobject.Money;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "campaigns")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Campaign extends BaseEntity {

    @NotBlank
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String name;

    @Size(max = 500)
    @Column(length = 500)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "discount_type", nullable = false, length = 20)
    private DiscountType discountType;

    @Column(name = "discount_value", nullable = false, precision = 10, scale = 2)
    private BigDecimal discountValue;

    @ElementCollection(targetClass = RentalType.class)
    @CollectionTable(name = "campaign_applicable_types", joinColumns = @JoinColumn(name = "campaign_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "rental_type")
    private List<RentalType> applicableRentalTypes = new ArrayList<>();

    @Column(name = "min_term_months")
    private Integer minTermMonths;

    @Column(name = "valid_from", nullable = false)
    private LocalDate validFrom;

    @Column(name = "valid_to", nullable = false)
    private LocalDate validTo;

    @Column(nullable = false)
    private boolean active = true;

    @Column(name = "category_id")
    private Long categoryId;

    public static Campaign create(
            String name,
            String description,
            DiscountType discountType,
            BigDecimal discountValue,
            List<RentalType> applicableRentalTypes,
            LocalDate validFrom,
            LocalDate validTo) {

        Campaign campaign = new Campaign();
        campaign.name = name;
        campaign.description = description;
        campaign.discountType = discountType;
        campaign.discountValue = discountValue;
        campaign.applicableRentalTypes = new ArrayList<>(applicableRentalTypes);
        campaign.validFrom = validFrom;
        campaign.validTo = validTo;
        campaign.active = true;
        return campaign;
    }

    public void setMinTermMonths(Integer minTermMonths) {
        this.minTermMonths = minTermMonths;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public boolean isApplicable(RentalType rentalType, LocalDate date, int termMonths) {
        if (!active) return false;
        if (date.isBefore(validFrom) || date.isAfter(validTo)) return false;
        if (!applicableRentalTypes.contains(rentalType)) return false;
        if (minTermMonths != null && termMonths < minTermMonths) return false;
        return true;
    }

    public boolean isApplicableForCategory(Long categoryId) {
        return this.categoryId == null || this.categoryId.equals(categoryId);
    }

    public Money applyDiscount(Money originalPrice) {
        return switch (discountType) {
            case PERCENTAGE -> {
                BigDecimal discountMultiplier = BigDecimal.ONE.subtract(
                    discountValue.divide(BigDecimal.valueOf(100))
                );
                yield originalPrice.multiply(discountMultiplier);
            }
            case FIXED_AMOUNT -> originalPrice.subtract(Money.of(discountValue, originalPrice.getCurrency()));
        };
    }

    public Money getDiscountAmount(Money originalPrice) {
        return switch (discountType) {
            case PERCENTAGE -> {
                BigDecimal discountMultiplier = discountValue.divide(BigDecimal.valueOf(100));
                yield originalPrice.multiply(discountMultiplier);
            }
            case FIXED_AMOUNT -> Money.of(discountValue, originalPrice.getCurrency());
        };
    }

    public void activate() {
        this.active = true;
    }

    public void deactivate() {
        this.active = false;
    }

    public enum DiscountType {
        PERCENTAGE,
        FIXED_AMOUNT
    }
}
