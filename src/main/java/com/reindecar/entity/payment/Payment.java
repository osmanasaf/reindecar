package com.reindecar.entity.payment;

import com.reindecar.common.entity.BaseEntity;
import com.reindecar.common.valueobject.Money;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "payments")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Payment extends BaseEntity {

    @NotNull(message = "Rental ID is required")
    @Column(nullable = false, name = "rental_id")
    private Long rentalId;

    @NotNull(message = "Amount is required")
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "amount")),
        @AttributeOverride(name = "currency", column = @Column(name = "currency"))
    })
    private Money amount;

    @NotNull(message = "Payment method is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PaymentMethod method;

    @Size(max = 100)
    @Column(length = 100)
    private String transactionRef;

    @NotNull(message = "Status is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PaymentStatus status;

    @NotNull(message = "Payment date is required")
    @Column(nullable = false)
    private Instant paidAt;

    @Size(max = 100)
    @Column(length = 100)
    private String invoiceRef;

    @Size(max = 500)
    @Column(length = 500)
    private String notes;

    @NotBlank(message = "Created by is required")
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String createdBy;

    @NotNull(message = "Created at is required")
    @Column(nullable = false)
    private Instant createdAt;

    public static Payment create(
            Long rentalId,
            Money amount,
            PaymentMethod method,
            String transactionRef,
            String invoiceRef,
            String notes,
            String createdBy) {
        
        Payment payment = new Payment();
        payment.rentalId = rentalId;
        payment.amount = amount;
        payment.method = method;
        payment.transactionRef = transactionRef;
        payment.status = PaymentStatus.COMPLETED;
        payment.paidAt = Instant.now();
        payment.invoiceRef = invoiceRef;
        payment.notes = notes;
        payment.createdBy = createdBy;
        payment.createdAt = Instant.now();
        return payment;
    }

    public void markAsRefunded() {
        this.status = PaymentStatus.REFUNDED;
    }

    public boolean isCompleted() {
        return status == PaymentStatus.COMPLETED;
    }
}
