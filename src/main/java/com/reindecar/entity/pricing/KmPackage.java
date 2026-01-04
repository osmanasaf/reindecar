package com.reindecar.entity.pricing;

import com.reindecar.common.entity.BaseEntity;
import com.reindecar.common.valueobject.Money;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "km_packages")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class KmPackage extends BaseEntity {

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false)
    private int includedKm;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "extra_km_price_amount")),
        @AttributeOverride(name = "currency", column = @Column(name = "extra_km_price_currency"))
    })
    private Money extraKmPrice;

    @ElementCollection(targetClass = RentalType.class)
    @CollectionTable(name = "km_package_applicable_types", joinColumns = @JoinColumn(name = "km_package_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "rental_type")
    private List<RentalType> applicableTypes = new ArrayList<>();

    @Column(nullable = false)
    private boolean unlimited = false;

    @Column(nullable = false)
    private boolean active = true;

    public static KmPackage create(
            String name,
            int includedKm,
            Money extraKmPrice,
            List<RentalType> applicableTypes,
            boolean isUnlimited) {
        
        KmPackage kmPackage = new KmPackage();
        kmPackage.name = name;
        kmPackage.includedKm = includedKm;
        kmPackage.extraKmPrice = extraKmPrice;
        kmPackage.applicableTypes = new ArrayList<>(applicableTypes);
        kmPackage.unlimited = isUnlimited;
        kmPackage.active = true;
        return kmPackage;
    }

    public boolean isApplicableFor(RentalType rentalType) {
        return active && applicableTypes.contains(rentalType);
    }

    public Money calculateExtraKmCost(int actualKm) {
        if (unlimited || actualKm <= includedKm) {
            return Money.zero(extraKmPrice.getCurrency());
        }

        int extraKm = actualKm - includedKm;
        return extraKmPrice.multiply(extraKm);
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

    public boolean isUnlimited() {
        return unlimited;
    }
}
