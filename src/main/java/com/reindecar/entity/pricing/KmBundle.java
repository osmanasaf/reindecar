package com.reindecar.entity.pricing;

import com.reindecar.common.entity.BaseEntity;
import com.reindecar.common.valueobject.Money;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Entity
@Table(name = "km_bundles")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class KmBundle extends BaseEntity {

    @NotBlank
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String name;

    @Size(max = 500)
    @Column(length = 500)
    private String description;

    @Column(nullable = false)
    private int includedKm;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "km_bundle_id")
    @OrderBy("sortOrder ASC")
    private List<KmPricingTier> pricingTiers = new ArrayList<>();

    @Column(nullable = false)
    private boolean active = true;

    public static KmBundle create(String name, String description, int includedKm) {
        KmBundle bundle = new KmBundle();
        bundle.name = name;
        bundle.description = description;
        bundle.includedKm = includedKm;
        bundle.active = true;
        return bundle;
    }

    public void addTier(int fromKm, Integer toKm, Money pricePerKm) {
        int nextOrder = pricingTiers.size() + 1;
        KmPricingTier tier = KmPricingTier.create(this.getId(), fromKm, toKm, pricePerKm, nextOrder);
        pricingTiers.add(tier);
    }

    public Money calculateExtraKmCost(int totalKmUsed) {
        if (totalKmUsed <= includedKm) {
            return Money.zero(Money.DEFAULT_CURRENCY);
        }

        int extraKm = totalKmUsed - includedKm;
        Money totalCost = Money.zero(Money.DEFAULT_CURRENCY);
        int remainingKm = extraKm;

        List<KmPricingTier> sortedTiers = pricingTiers.stream()
            .sorted(Comparator.comparingInt(KmPricingTier::getSortOrder))
            .toList();

        for (KmPricingTier tier : sortedTiers) {
            if (remainingKm <= 0) break;

            int tierRange = tier.isUnlimited() ? remainingKm : tier.getKmRange();
            int kmInThisTier = Math.min(remainingKm, tierRange);

            Money tierCost = tier.calculateCost(kmInThisTier);
            totalCost = totalCost.add(tierCost);
            remainingKm -= kmInThisTier;
        }

        return totalCost;
    }

    public List<KmCostBreakdown> getExtraKmBreakdown(int totalKmUsed) {
        List<KmCostBreakdown> breakdown = new ArrayList<>();

        if (totalKmUsed <= includedKm) {
            return breakdown;
        }

        int extraKm = totalKmUsed - includedKm;
        int remainingKm = extraKm;
        int processedKm = 0;

        List<KmPricingTier> sortedTiers = pricingTiers.stream()
            .sorted(Comparator.comparingInt(KmPricingTier::getSortOrder))
            .toList();

        for (KmPricingTier tier : sortedTiers) {
            if (remainingKm <= 0) break;

            int tierRange = tier.isUnlimited() ? remainingKm : tier.getKmRange();
            int kmInThisTier = Math.min(remainingKm, tierRange);

            Money tierCost = tier.calculateCost(kmInThisTier);

            String description = String.format("%d-%d km arası (%s/km)",
                includedKm + processedKm,
                includedKm + processedKm + kmInThisTier,
                tier.getPricePerKm().getAmount().toPlainString());

            breakdown.add(new KmCostBreakdown(
                description,
                kmInThisTier,
                tier.getPricePerKm().getAmount(),
                tierCost.getAmount()
            ));

            remainingKm -= kmInThisTier;
            processedKm += kmInThisTier;
        }

        return breakdown;
    }

    public void activate() {
        this.active = true;
    }

    public void deactivate() {
        this.active = false;
    }

    public record KmCostBreakdown(
        String description,
        int km,
        java.math.BigDecimal pricePerKm,
        java.math.BigDecimal totalCost
    ) {}
}
