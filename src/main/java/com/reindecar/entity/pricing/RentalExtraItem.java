package com.reindecar.entity.pricing;

import com.reindecar.common.entity.BaseEntity;
import com.reindecar.common.valueobject.Money;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Kiralamaya eklenen ek kalemler.
 * Önceden tanımlı veya serbest kalem olabilir.
 */
@Entity
@Table(name = "rental_extra_item")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RentalExtraItem extends BaseEntity {

    @NotNull
    @Column(name = "rental_id", nullable = false)
    private Long rentalId;

    @Column(name = "item_type_id")
    private Long itemTypeId;

    @Size(max = 100)
    @Column(name = "custom_name", length = 100)
    private String customName;

    @Size(max = 500)
    @Column(length = 500)
    private String description;

    @NotNull
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "amount", nullable = false)),
        @AttributeOverride(name = "currency", column = @Column(name = "currency", nullable = false, length = 3))
    })
    private Money amount;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "calculation_type", nullable = false, length = 20)
    private CalculationType calculationType;

    public static RentalExtraItem createFromType(Long rentalId, Long itemTypeId, Money amount, CalculationType calculationType) {
        var item = new RentalExtraItem();
        item.rentalId = rentalId;
        item.itemTypeId = itemTypeId;
        item.amount = amount;
        item.calculationType = calculationType;
        return item;
    }

    public static RentalExtraItem createCustom(Long rentalId, String customName, String description, Money amount, CalculationType calculationType) {
        var item = new RentalExtraItem();
        item.rentalId = rentalId;
        item.customName = customName;
        item.description = description;
        item.amount = amount;
        item.calculationType = calculationType;
        return item;
    }

    public boolean isPredefined() {
        return itemTypeId != null;
    }

    public boolean isCustom() {
        return itemTypeId == null && customName != null;
    }

    public Money calculateTotal(Money baseRentalPrice, int termMonths) {
        return switch (calculationType) {
            case FIXED -> amount;
            case PERCENTAGE -> {
                var percentageAmount = baseRentalPrice.getAmount()
                    .multiply(amount.getAmount())
                    .divide(BigDecimal.valueOf(100), 2, java.math.RoundingMode.HALF_UP);
                yield Money.of(percentageAmount, amount.getCurrency());
            }
            case PER_MONTH -> amount.multiply(termMonths);
        };
    }

    public void updateAmount(Money amount) {
        this.amount = amount;
    }

    public void updateDescription(String description) {
        this.description = description;
    }
}
