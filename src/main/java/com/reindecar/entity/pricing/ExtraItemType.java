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
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Önceden tanımlı ek kalem türleri.
 * Örn: Vergi, Bakım, Sigorta, Lastik vb.
 */
@Entity
@Table(name = "extra_item_type")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ExtraItemType extends BaseEntity {

    @NotBlank
    @Size(max = 50)
    @Column(nullable = false, unique = true, length = 50)
    private String code;

    @NotBlank
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String name;

    @Size(max = 500)
    @Column(length = 500)
    private String description;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "default_amount")),
        @AttributeOverride(name = "currency", column = @Column(name = "default_currency", length = 3))
    })
    private Money defaultAmount;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "calculation_type", nullable = false, length = 20)
    private CalculationType calculationType;

    @Column(name = "sort_order")
    private Integer sortOrder;

    @Column(nullable = false)
    private boolean active = true;

    public static ExtraItemType create(
            String code,
            String name,
            String description,
            Money defaultAmount,
            CalculationType calculationType) {
        var type = new ExtraItemType();
        type.code = code.toUpperCase();
        type.name = name;
        type.description = description;
        type.defaultAmount = defaultAmount;
        type.calculationType = calculationType;
        type.active = true;
        return type;
    }

    public void update(String name, String description, Money defaultAmount, CalculationType calculationType) {
        this.name = name;
        this.description = description;
        this.defaultAmount = defaultAmount;
        this.calculationType = calculationType;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    public void activate() {
        this.active = true;
    }

    public void deactivate() {
        this.active = false;
    }
}
