package com.reindecar.entity.leasing;

import com.reindecar.common.entity.BaseEntity;
import com.reindecar.common.valueobject.Money;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "leasing_invoices", indexes = {
    @Index(name = "idx_invoice_rental", columnList = "rental_id"),
    @Index(name = "idx_invoice_number", columnList = "invoice_number"),
    @Index(name = "idx_invoice_status", columnList = "status")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LeasingInvoice extends BaseEntity {

    @NotBlank
    @Column(name = "invoice_number", nullable = false, unique = true, length = 50)
    private String invoiceNumber;

    @NotNull
    @Column(name = "rental_id", nullable = false)
    private Long rentalId;

    @Column(name = "contract_id")
    private Long contractId;

    @Column(name = "customer_id", nullable = false)
    private Long customerId;

    @NotNull
    @Column(name = "period_start", nullable = false)
    private LocalDate periodStart;

    @NotNull
    @Column(name = "period_end", nullable = false)
    private LocalDate periodEnd;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "monthly_rent_amount", nullable = false)),
        @AttributeOverride(name = "currency", column = @Column(name = "monthly_rent_currency", length = 3))
    })
    private Money monthlyRent;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "excess_km_charge_amount")),
        @AttributeOverride(name = "currency", column = @Column(name = "excess_km_charge_currency", length = 3))
    })
    private Money excessKmCharge;

    @Column(name = "excess_km")
    private int excessKm;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "additional_charges_amount")),
        @AttributeOverride(name = "currency", column = @Column(name = "additional_charges_currency", length = 3))
    })
    private Money additionalCharges;

    @Column(name = "additional_charges_note", length = 500)
    private String additionalChargesNote;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "total_amount", nullable = false)),
        @AttributeOverride(name = "currency", column = @Column(name = "total_currency", length = 3))
    })
    private Money totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private InvoiceStatus status = InvoiceStatus.DRAFT;

    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    @Column(name = "paid_at")
    private Instant paidAt;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    public enum InvoiceStatus {
        DRAFT, PENDING, SENT, PAID, OVERDUE, CANCELLED
    }

    public static LeasingInvoice create(
            String invoiceNumber,
            Long rentalId,
            Long contractId,
            Long customerId,
            LocalDate periodStart,
            LocalDate periodEnd,
            Money monthlyRent,
            int excessKm,
            Money excessKmCharge,
            Money additionalCharges,
            String additionalChargesNote) {

        LeasingInvoice invoice = new LeasingInvoice();
        invoice.invoiceNumber = invoiceNumber;
        invoice.rentalId = rentalId;
        invoice.contractId = contractId;
        invoice.customerId = customerId;
        invoice.periodStart = periodStart;
        invoice.periodEnd = periodEnd;
        invoice.monthlyRent = monthlyRent;
        invoice.excessKm = excessKm;
        invoice.excessKmCharge = excessKmCharge != null ? excessKmCharge : Money.zero(monthlyRent.getCurrency());
        invoice.additionalCharges = additionalCharges != null ? additionalCharges : Money.zero(monthlyRent.getCurrency());
        invoice.additionalChargesNote = additionalChargesNote;
        invoice.status = InvoiceStatus.DRAFT;
        invoice.dueDate = periodEnd.plusDays(15);
        invoice.createdAt = Instant.now();

        invoice.calculateTotal();
        return invoice;
    }

    private void calculateTotal() {
        BigDecimal total = this.monthlyRent.getAmount();
        
        if (this.excessKmCharge != null) {
            total = total.add(this.excessKmCharge.getAmount());
        }
        if (this.additionalCharges != null) {
            total = total.add(this.additionalCharges.getAmount());
        }
        
        this.totalAmount = Money.of(total, this.monthlyRent.getCurrency());
    }

    public void send() {
        if (this.status != InvoiceStatus.DRAFT && this.status != InvoiceStatus.PENDING) {
            throw new IllegalStateException("Cannot send invoice in status: " + this.status);
        }
        this.status = InvoiceStatus.SENT;
    }

    public void markAsPaid() {
        this.status = InvoiceStatus.PAID;
        this.paidAt = Instant.now();
    }

    public void markAsOverdue() {
        if (this.status == InvoiceStatus.SENT && LocalDate.now().isAfter(this.dueDate)) {
            this.status = InvoiceStatus.OVERDUE;
        }
    }

    public void cancel() {
        this.status = InvoiceStatus.CANCELLED;
    }

    public boolean isPaid() {
        return this.status == InvoiceStatus.PAID;
    }

    public boolean isOverdue() {
        return this.status == InvoiceStatus.OVERDUE || 
               (this.status == InvoiceStatus.SENT && LocalDate.now().isAfter(this.dueDate));
    }
}
