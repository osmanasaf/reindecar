package com.reindecar.entity.pricing;

import com.reindecar.common.entity.BaseEntity;
import com.reindecar.common.valueobject.Money;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;


@Entity
@Table(name = "leasing_plans", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"category_id", "term_months"})
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LeasingPlan extends BaseEntity {

    @NotNull
    @Column(name = "category_id", nullable = false)
    private Long categoryId;

    @Min(12)
    @Column(name = "term_months", nullable = false)
    private int termMonths;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "monthly_base_price_amount", nullable = false)),
        @AttributeOverride(name = "currency", column = @Column(name = "monthly_base_price_currency", nullable = false))
    })
    private Money monthlyBasePrice;

    @Min(0)
    @Column(name = "included_km_per_month", nullable = false)
    private int includedKmPerMonth;

    @Column(name = "valid_from")
    private LocalDate validFrom;

    @Column(name = "valid_to")
    private LocalDate validTo;

    @Column(nullable = false)
    private boolean active = true;

    public static LeasingPlan create(
            Long categoryId,
            int termMonths,
            Money monthlyBasePrice,
            int includedKmPerMonth) {

        LeasingPlan plan = new LeasingPlan();
        plan.categoryId = categoryId;
        plan.termMonths = termMonths;
        plan.monthlyBasePrice = monthlyBasePrice;
        plan.includedKmPerMonth = includedKmPerMonth;
        plan.active = true;
        return plan;
    }

    public void updatePricing(Money monthlyBasePrice, int includedKmPerMonth) {
        this.monthlyBasePrice = monthlyBasePrice;
        this.includedKmPerMonth = includedKmPerMonth;
    }

    public void setValidityPeriod(LocalDate from, LocalDate to) {
        this.validFrom = from;
        this.validTo = to;
    }

    public boolean isApplicable(LocalDate date) {
        if (!active) return false;
        if (validFrom != null && date.isBefore(validFrom)) return false;
        if (validTo != null && date.isAfter(validTo)) return false;
        return true;
    }

    public Money calculateTotalContractPrice() {
        return monthlyBasePrice.multiply(termMonths);
    }

    public int getTotalIncludedKm() {
        return includedKmPerMonth * termMonths;
    }

    public void activate() {
        this.active = true;
    }

    public void deactivate() {
        this.active = false;
    }
}
