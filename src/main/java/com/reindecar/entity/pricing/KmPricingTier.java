package com.reindecar.entity.pricing;

import com.reindecar.common.entity.BaseEntity;
import com.reindecar.common.valueobject.Money;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "km_pricing_tiers")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class KmPricingTier extends BaseEntity {

    @Column(name = "km_bundle_id", nullable = false)
    private Long kmBundleId;

    @Min(0)
    @Column(nullable = false)
    private int fromKm;

    @Column
    private Integer toKm;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "price_per_km_amount")),
        @AttributeOverride(name = "currency", column = @Column(name = "price_per_km_currency"))
    })
    private Money pricePerKm;

    @Column(nullable = false)
    private int sortOrder;

    public static KmPricingTier create(
            Long kmBundleId,
            int fromKm,
            Integer toKm,
            Money pricePerKm,
            int sortOrder) {

        KmPricingTier tier = new KmPricingTier();
        tier.kmBundleId = kmBundleId;
        tier.fromKm = fromKm;
        tier.toKm = toKm;
        tier.pricePerKm = pricePerKm;
        tier.sortOrder = sortOrder;
        return tier;
    }

    public boolean isApplicable(int km) {
        if (km < fromKm) return false;
        if (toKm != null && km > toKm) return false;
        return true;
    }

    public boolean isUnlimited() {
        return toKm == null;
    }

    public int getKmRange() {
        if (toKm == null) return Integer.MAX_VALUE;
        return toKm - fromKm;
    }

    public Money calculateCost(int kmInThisTier) {
        return pricePerKm.multiply(kmInThisTier);
    }
}
