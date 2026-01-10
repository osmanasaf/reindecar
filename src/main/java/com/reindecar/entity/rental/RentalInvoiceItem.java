package com.reindecar.entity.rental;

import com.reindecar.common.entity.BaseEntity;
import com.reindecar.common.valueobject.Money;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "rental_invoice_items")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RentalInvoiceItem extends BaseEntity {

    @Column(name = "invoice_id")
    private Long invoiceId;

    @NotBlank
    @Size(max = 200)
    @Column(nullable = false, length = 200)
    private String description;

    @Min(1)
    @Column(nullable = false)
    private int quantity;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "unit_price_amount")),
        @AttributeOverride(name = "currency", column = @Column(name = "unit_price_currency"))
    })
    private Money unitPrice;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "total_price_amount")),
        @AttributeOverride(name = "currency", column = @Column(name = "total_price_currency"))
    })
    private Money totalPrice;

    @Column(nullable = false)
    private int sortOrder;

    public static RentalInvoiceItem create(
            String description,
            int quantity,
            Money unitPrice,
            Money totalPrice,
            int sortOrder) {

        RentalInvoiceItem item = new RentalInvoiceItem();
        item.description = description;
        item.quantity = quantity;
        item.unitPrice = unitPrice;
        item.totalPrice = totalPrice;
        item.sortOrder = sortOrder;
        return item;
    }
}
