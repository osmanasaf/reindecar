package com.reindecar.entity.rental;

import com.reindecar.common.exception.BusinessException;
import com.reindecar.common.exception.ErrorCode;

import com.reindecar.common.entity.BaseEntity;
import com.reindecar.common.exception.BusinessException;
import com.reindecar.common.exception.ErrorCode;
import com.reindecar.common.valueobject.Money;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "rental_invoices")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RentalInvoice extends BaseEntity {

    @Column(name = "rental_id", nullable = false)
    private Long rentalId;

    @NotBlank
    @Size(max = 20)
    @Column(nullable = false, unique = true, length = 20)
    private String invoiceNumber;

    @Column(nullable = false)
    private LocalDate invoiceDate;

    // ===== CUSTOMER/COMPANY INFO =====
    @Column(name = "customer_id")
    private Long customerId;

    @Size(max = 200)
    @Column(length = 200)
    private String customerName;

    @Size(max = 200)
    @Column(length = 200)
    private String companyName;

    @Size(max = 20)
    @Column(length = 20)
    private String taxNumber;

    @Size(max = 100)
    @Column(length = 100)
    private String taxOffice;

    @Size(max = 20)
    @Column(length = 20)
    private String phone;

    @Size(max = 100)
    @Column(length = 100)
    private String email;

    @Size(max = 500)
    @Column(length = 500)
    private String billingAddress;

    // ===== VEHICLE INFO =====
    @Column(name = "vehicle_id")
    private Long vehicleId;

    @Size(max = 20)
    @Column(length = 20)
    private String plateNumber;

    @Size(max = 100)
    @Column(length = 100)
    private String vehicleDescription;

    // ===== RENTAL PERIOD =====
    private LocalDate rentalStartDate;
    private LocalDate rentalEndDate;
    private int rentalDays;
    private int startKm;
    private int endKm;
    private int totalKm;

    // ===== AMOUNTS =====
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "base_rental_amount")),
        @AttributeOverride(name = "currency", column = @Column(name = "base_rental_currency"))
    })
    private Money baseRentalAmount;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "extra_km_amount")),
        @AttributeOverride(name = "currency", column = @Column(name = "extra_km_currency"))
    })
    private Money extraKmAmount;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "discount_amount")),
        @AttributeOverride(name = "currency", column = @Column(name = "discount_currency"))
    })
    private Money discountAmount;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "total_amount")),
        @AttributeOverride(name = "currency", column = @Column(name = "total_currency"))
    })
    private Money totalAmount;

    @Column(precision = 5, scale = 2)
    private BigDecimal taxRate;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "tax_amount")),
        @AttributeOverride(name = "currency", column = @Column(name = "tax_currency"))
    })
    private Money taxAmount;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "grand_total_amount")),
        @AttributeOverride(name = "currency", column = @Column(name = "grand_total_currency"))
    })
    private Money grandTotal;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    private InvoiceStatus status;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "invoice_id")
    @OrderBy("sortOrder ASC")
    private List<RentalInvoiceItem> items = new ArrayList<>();

    @Size(max = 500)
    @Column(length = 500)
    private String notes;

    @Column(nullable = false)
    private Instant createdAt;

    public static RentalInvoice create(Long rentalId, String invoiceNumber, BigDecimal taxRate) {
        RentalInvoice invoice = new RentalInvoice();
        invoice.rentalId = rentalId;
        invoice.invoiceNumber = invoiceNumber;
        invoice.invoiceDate = LocalDate.now();
        invoice.taxRate = taxRate;
        invoice.status = InvoiceStatus.DRAFT;
        invoice.createdAt = Instant.now();
        invoice.baseRentalAmount = Money.zero("TRY");
        invoice.extraKmAmount = Money.zero("TRY");
        invoice.discountAmount = Money.zero("TRY");
        invoice.totalAmount = Money.zero("TRY");
        invoice.taxAmount = Money.zero("TRY");
        invoice.grandTotal = Money.zero("TRY");
        return invoice;
    }

    // ===== SETTER METHODS FOR DETAILS =====
    public void setCustomerDetails(Long customerId, String customerName, String companyName,
                                    String taxNumber, String taxOffice, String phone,
                                    String email, String billingAddress) {
        this.customerId = customerId;
        this.customerName = customerName;
        this.companyName = companyName;
        this.taxNumber = taxNumber;
        this.taxOffice = taxOffice;
        this.phone = phone;
        this.email = email;
        this.billingAddress = billingAddress;
    }

    public void setVehicleDetails(Long vehicleId, String plateNumber, String vehicleDescription) {
        this.vehicleId = vehicleId;
        this.plateNumber = plateNumber;
        this.vehicleDescription = vehicleDescription;
    }

    public void setRentalPeriod(LocalDate startDate, LocalDate endDate, int rentalDays,
                                 int startKm, int endKm) {
        this.rentalStartDate = startDate;
        this.rentalEndDate = endDate;
        this.rentalDays = rentalDays;
        this.startKm = startKm;
        this.endKm = endKm;
        this.totalKm = endKm - startKm;
    }

    public void addItem(String description, int quantity, Money unitPrice) {
        int nextOrder = items.size() + 1;
        Money totalPrice = unitPrice.multiply(quantity);
        RentalInvoiceItem item = RentalInvoiceItem.create(description, quantity, unitPrice, totalPrice, nextOrder);
        items.add(item);
    }

    public void setBaseRentalAmount(Money amount) {
        this.baseRentalAmount = amount;
    }

    public void setExtraKmAmount(Money amount) {
        this.extraKmAmount = amount;
    }

    public void setDiscountAmount(Money amount) {
        this.discountAmount = amount;
    }

    public void calculateTotals() {
        Money subtotal = baseRentalAmount.add(extraKmAmount);
        if (discountAmount != null) {
            subtotal = subtotal.subtract(discountAmount);
        }
        this.totalAmount = subtotal;

        if (taxRate != null && taxRate.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal taxMultiplier = taxRate.divide(BigDecimal.valueOf(100));
            this.taxAmount = totalAmount.multiply(taxMultiplier);
            this.grandTotal = totalAmount.add(taxAmount);
        } else {
            this.taxAmount = Money.zero(totalAmount.getCurrency());
            this.grandTotal = totalAmount;
        }
    }

    public void finalizeInvoice() {
        if (this.status != InvoiceStatus.DRAFT) {
            throw new BusinessException(ErrorCode.INVALID_OPERATION, "Only draft invoices can be finalized");
        }
        calculateTotals();
        this.status = InvoiceStatus.FINALIZED;
    }

    public void markAsPaid() {
        if (this.status == InvoiceStatus.CANCELLED) {
            throw new BusinessException(ErrorCode.INVALID_OPERATION, "Cancelled invoices cannot be marked as paid");
        }
        this.status = InvoiceStatus.PAID;
    }

    public void cancel() {
        if (this.status == InvoiceStatus.PAID) {
            throw new BusinessException(ErrorCode.INVALID_OPERATION, "Paid invoices cannot be cancelled");
        }
        this.status = InvoiceStatus.CANCELLED;
    }

    public boolean isDraft() {
        return status == InvoiceStatus.DRAFT;
    }

    public boolean isPaid() {
        return status == InvoiceStatus.PAID;
    }
}
