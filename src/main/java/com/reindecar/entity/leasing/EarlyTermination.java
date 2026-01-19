package com.reindecar.entity.leasing;

import com.reindecar.common.entity.BaseEntity;
import com.reindecar.common.valueobject.Money;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Entity
@Table(name = "early_terminations", indexes = {
    @Index(name = "idx_termination_rental", columnList = "rental_id"),
    @Index(name = "idx_termination_status", columnList = "status")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EarlyTermination extends BaseEntity {

    @NotNull
    @Column(name = "rental_id", nullable = false)
    private Long rentalId;

    @Column(name = "contract_id")
    private Long contractId;

    @NotNull
    @Column(name = "termination_date", nullable = false)
    private LocalDate terminationDate;

    @NotNull
    @Column(name = "contract_end_date", nullable = false)
    private LocalDate contractEndDate;

    @Column(name = "remaining_months", nullable = false)
    private int remainingMonths;

    @Column(name = "penalty_rate", nullable = false, precision = 5, scale = 2)
    private BigDecimal penaltyRate;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "monthly_rent_amount")),
        @AttributeOverride(name = "currency", column = @Column(name = "monthly_rent_currency", length = 3))
    })
    private Money monthlyRent;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "penalty_amount", nullable = false)),
        @AttributeOverride(name = "currency", column = @Column(name = "penalty_currency", length = 3))
    })
    private Money penaltyAmount;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "excess_km_charge_amount")),
        @AttributeOverride(name = "currency", column = @Column(name = "excess_km_charge_currency", length = 3))
    })
    private Money excessKmCharge;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "total_amount", nullable = false)),
        @AttributeOverride(name = "currency", column = @Column(name = "total_currency", length = 3))
    })
    private Money totalAmount;

    @Column(length = 500)
    private String reason;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TerminationStatus status = TerminationStatus.REQUESTED;

    @Column(name = "approved_by", length = 100)
    private String approvedBy;

    @Column(name = "approved_at")
    private Instant approvedAt;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    public enum TerminationStatus {
        REQUESTED, UNDER_REVIEW, APPROVED, REJECTED, COMPLETED
    }

    public static EarlyTermination create(
            Long rentalId,
            Long contractId,
            LocalDate terminationDate,
            LocalDate contractEndDate,
            Money monthlyRent,
            String reason) {

        EarlyTermination termination = new EarlyTermination();
        termination.rentalId = rentalId;
        termination.contractId = contractId;
        termination.terminationDate = terminationDate;
        termination.contractEndDate = contractEndDate;
        termination.monthlyRent = monthlyRent;
        termination.reason = reason;
        termination.status = TerminationStatus.REQUESTED;
        termination.createdAt = Instant.now();

        termination.calculatePenalty();
        return termination;
    }

    private void calculatePenalty() {
        long months = ChronoUnit.MONTHS.between(this.terminationDate, this.contractEndDate);
        this.remainingMonths = (int) Math.max(0, months);
        
        this.penaltyRate = determinePenaltyRate(this.remainingMonths);
        
        BigDecimal basePenalty = this.monthlyRent.getAmount()
            .multiply(BigDecimal.valueOf(this.remainingMonths))
            .multiply(this.penaltyRate);
        
        this.penaltyAmount = Money.of(basePenalty, this.monthlyRent.getCurrency());
        this.excessKmCharge = Money.zero(this.monthlyRent.getCurrency());
        
        calculateTotal();
    }

    private BigDecimal determinePenaltyRate(int remainingMonths) {
        if (remainingMonths > 24) {
            return new BigDecimal("1.00");
        } else if (remainingMonths > 12) {
            return new BigDecimal("0.75");
        } else if (remainingMonths > 6) {
            return new BigDecimal("0.50");
        } else {
            return new BigDecimal("0.25");
        }
    }

    public void setExcessKmCharge(Money excessKmCharge) {
        this.excessKmCharge = excessKmCharge;
        calculateTotal();
    }

    private void calculateTotal() {
        BigDecimal total = this.penaltyAmount.getAmount();
        if (this.excessKmCharge != null) {
            total = total.add(this.excessKmCharge.getAmount());
        }
        this.totalAmount = Money.of(total, this.penaltyAmount.getCurrency());
    }

    public void approve(String approvedBy) {
        if (this.status != TerminationStatus.REQUESTED && this.status != TerminationStatus.UNDER_REVIEW) {
            throw new IllegalStateException("Cannot approve termination in status: " + this.status);
        }
        this.status = TerminationStatus.APPROVED;
        this.approvedBy = approvedBy;
        this.approvedAt = Instant.now();
    }

    public void reject(String rejectedBy) {
        if (this.status != TerminationStatus.REQUESTED && this.status != TerminationStatus.UNDER_REVIEW) {
            throw new IllegalStateException("Cannot reject termination in status: " + this.status);
        }
        this.status = TerminationStatus.REJECTED;
        this.approvedBy = rejectedBy;
        this.approvedAt = Instant.now();
    }

    public void complete() {
        if (this.status != TerminationStatus.APPROVED) {
            throw new IllegalStateException("Cannot complete termination in status: " + this.status);
        }
        this.status = TerminationStatus.COMPLETED;
    }

    public void review() {
        if (this.status != TerminationStatus.REQUESTED) {
            throw new IllegalStateException("Cannot review termination in status: " + this.status);
        }
        this.status = TerminationStatus.UNDER_REVIEW;
    }
}
