package com.reindecar.entity.pricing;

import com.reindecar.common.entity.BaseEntity;
import com.reindecar.common.valueobject.Money;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDate;


@Entity
@Table(name = "customer_contracts")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CustomerContract extends BaseEntity {

    @NotNull
    @Column(name = "customer_id", nullable = false)
    private Long customerId;

    @NotNull
    @Column(name = "category_id", nullable = false)
    private Long categoryId;

    @Size(max = 50)
    @Column(name = "contract_number", unique = true, length = 50)
    private String contractNumber;

    @Min(12)
    @Column(name = "term_months", nullable = false)
    private int termMonths;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "negotiated_monthly_price_amount", nullable = false)),
        @AttributeOverride(name = "currency", column = @Column(name = "negotiated_monthly_price_currency", nullable = false))
    })
    private Money negotiatedMonthlyPrice;

    @Min(0)
    @Column(name = "included_km_per_month", nullable = false)
    private int includedKmPerMonth;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "extra_km_price_amount")),
        @AttributeOverride(name = "currency", column = @Column(name = "extra_km_price_currency"))
    })
    private Money extraKmPrice;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ContractStatus status;

    @Size(max = 500)
    @Column(length = 500)
    private String notes;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public static CustomerContract create(
            Long customerId,
            Long categoryId,
            String contractNumber,
            int termMonths,
            Money negotiatedMonthlyPrice,
            int includedKmPerMonth,
            Money extraKmPrice,
            LocalDate startDate) {

        CustomerContract contract = new CustomerContract();
        contract.customerId = customerId;
        contract.categoryId = categoryId;
        contract.contractNumber = contractNumber;
        contract.termMonths = termMonths;
        contract.negotiatedMonthlyPrice = negotiatedMonthlyPrice;
        contract.includedKmPerMonth = includedKmPerMonth;
        contract.extraKmPrice = extraKmPrice;
        contract.startDate = startDate;
        contract.endDate = startDate.plusMonths(termMonths);
        contract.status = ContractStatus.DRAFT;
        contract.createdAt = Instant.now();
        contract.updatedAt = Instant.now();
        return contract;
    }

    public void activate() {
        this.status = ContractStatus.ACTIVE;
        this.updatedAt = Instant.now();
    }

    public void suspend() {
        this.status = ContractStatus.SUSPENDED;
        this.updatedAt = Instant.now();
    }

    public void terminate() {
        this.status = ContractStatus.TERMINATED;
        this.updatedAt = Instant.now();
    }

    public void complete() {
        this.status = ContractStatus.COMPLETED;
        this.updatedAt = Instant.now();
    }

    public boolean isApplicable(LocalDate date) {
        if (status != ContractStatus.ACTIVE) return false;
        return !date.isBefore(startDate) && !date.isAfter(endDate);
    }

    public Money calculateTotalContractPrice() {
        return negotiatedMonthlyPrice.multiply(termMonths);
    }

    public Money calculateExtraKmCost(int exceededKm) {
        if (extraKmPrice == null || exceededKm <= 0) {
            return Money.zero(negotiatedMonthlyPrice.getCurrency());
        }
        return extraKmPrice.multiply(exceededKm);
    }

    public int getTotalIncludedKm() {
        return includedKmPerMonth * termMonths;
    }

    public enum ContractStatus {
        DRAFT,
        ACTIVE,
        SUSPENDED,
        TERMINATED,
        COMPLETED
    }
}
