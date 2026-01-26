package com.reindecar.entity.rental;

import com.reindecar.common.exception.BusinessException;
import com.reindecar.common.exception.ErrorCode;

import com.reindecar.common.entity.BaseEntity;
import com.reindecar.common.statemachine.StateMachine;
import com.reindecar.common.valueobject.Money;
import com.reindecar.entity.customer.CustomerType;
import com.reindecar.entity.pricing.RentalType;
import com.reindecar.service.rental.RentalStatusTransition;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Entity
@Table(name = "rentals")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Rental extends BaseEntity {

    @NotBlank(message = "Rental number is required")
    @Size(max = 50)
    @Column(nullable = false, unique = true, length = 50)
    private String rentalNumber;

    @NotNull(message = "Rental type is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RentalType rentalType;

    @NotNull(message = "Vehicle ID is required")
    @Column(nullable = false, name = "vehicle_id")
    private Long vehicleId;

    @NotNull(message = "Customer ID is required")
    @Column(nullable = false, name = "customer_id")
    private Long customerId;

    @Enumerated(EnumType.STRING)
    @Column(name = "customer_type", nullable = false, length = 20)
    private CustomerType customerType;

    @Column(name = "contract_signer_id")
    private Long contractSignerId;

    @Size(max = 100)
    @Column(name = "contract_signer_name", length = 100)
    private String contractSignerName;

    @NotNull(message = "Branch ID is required")
    @Column(nullable = false, name = "branch_id")
    private Long branchId;

    @Column(name = "return_branch_id")
    private Long returnBranchId;

    @NotNull(message = "Status is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RentalStatus status;

    @NotNull(message = "Start date is required")
    @Column(nullable = false)
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    @Column(nullable = false)
    private LocalDate endDate;

    @Column
    private LocalDate actualReturnDate;

    @Min(value = 0, message = "Start km cannot be negative")
    @Column
    private int startKm;

    @Min(value = 0, message = "End km cannot be negative")
    @Column
    private int endKm;

    @Column(name = "km_package_id")
    private Long kmPackageId;

    @Column(name = "custom_included_km")
    private Integer customIncludedKm;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "custom_extra_km_price_amount")),
        @AttributeOverride(name = "currency", column = @Column(name = "custom_extra_km_price_currency"))
    })
    private Money customExtraKmPrice;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "daily_price_amount")),
        @AttributeOverride(name = "currency", column = @Column(name = "daily_price_currency"))
    })
    private Money dailyPrice;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "total_price_amount")),
        @AttributeOverride(name = "currency", column = @Column(name = "total_price_currency"))
    })
    private Money totalPrice;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "discount_amount")),
        @AttributeOverride(name = "currency", column = @Column(name = "discount_currency"))
    })
    private Money discountAmount;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "extra_km_charge_amount")),
        @AttributeOverride(name = "currency", column = @Column(name = "extra_km_charge_currency"))
    })
    private Money extraKmCharge;

    @Column(length = 1000)
    private String notes;

    @Column(nullable = false, length = 100)
    private String createdBy;

    @Column(nullable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    @Transient
    private static final StateMachine<RentalStatus> stateMachine = 
        new StateMachine<>(new RentalStatusTransition());

    public static Rental create(
            String rentalNumber,
            RentalType rentalType,
            Long vehicleId,
            Long customerId,
            CustomerType customerType,
            Long contractSignerId,
            String contractSignerName,
            Long branchId,
            Long returnBranchId,
            LocalDate startDate,
            LocalDate endDate,
            Long kmPackageId,
            Integer customIncludedKm,
            Money customExtraKmPrice,
            Money dailyPrice,
            Money totalPrice,
            Money discountAmount,
            String notes,
            String createdBy) {
        
        if (endDate.isBefore(startDate)) {
            throw new BusinessException(ErrorCode.INVALID_PARAMETER, "End date must be after start date");
        }
        
        if (startDate.isBefore(LocalDate.now().minusDays(1))) {
            throw new BusinessException(ErrorCode.INVALID_PARAMETER, "Start date cannot be in the past");
        }
        
        Rental rental = new Rental();
        rental.rentalNumber = rentalNumber;
        rental.rentalType = rentalType;
        rental.vehicleId = vehicleId;
        rental.customerId = customerId;
        rental.customerType = customerType;
        rental.contractSignerId = contractSignerId;
        rental.contractSignerName = contractSignerName;
        rental.branchId = branchId;
        rental.returnBranchId = returnBranchId;
        rental.status = RentalStatus.DRAFT;
        rental.startDate = startDate;
        rental.endDate = endDate;
        rental.kmPackageId = kmPackageId;
        rental.customIncludedKm = customIncludedKm;
        rental.customExtraKmPrice = customExtraKmPrice;
        rental.dailyPrice = dailyPrice;
        rental.totalPrice = totalPrice;
        rental.discountAmount = discountAmount != null ? discountAmount : Money.zero(dailyPrice.getCurrency());
        rental.extraKmCharge = Money.zero(dailyPrice.getCurrency());
        rental.notes = notes;
        rental.createdBy = createdBy;
        rental.createdAt = Instant.now();
        rental.updatedAt = Instant.now();
        return rental;
    }

    public boolean hasCustomKmLimit() {
        return customIncludedKm != null && customIncludedKm > 0;
    }

    public int getEffectiveIncludedKm(int packageIncludedKm) {
        return hasCustomKmLimit() ? customIncludedKm : packageIncludedKm;
    }

    public Money getEffectiveExtraKmPrice(Money packageExtraKmPrice) {
        return customExtraKmPrice != null ? customExtraKmPrice : packageExtraKmPrice;
    }

    public void reserve() {
        changeStatus(RentalStatus.RESERVED);
    }

    public void activate(int startKm) {
        changeStatus(RentalStatus.ACTIVE);
        this.startKm = startKm;
        this.updatedAt = Instant.now();
    }

    public void startReturn() {
        changeStatus(RentalStatus.RETURN_PENDING);
    }

    public void complete(LocalDate actualReturnDate, int endKm, Money extraKmCharge) {
        if (endKm < this.startKm) {
            throw new BusinessException(ErrorCode.INVALID_PARAMETER, "End km cannot be less than start km");
        }
        
        this.actualReturnDate = actualReturnDate;
        this.endKm = endKm;
        this.extraKmCharge = extraKmCharge;
        changeStatus(RentalStatus.CLOSED);
    }

    public void cancel() {
        if (!status.canBeCancelled()) {
            throw new BusinessException(ErrorCode.INVALID_OPERATION, "Rental cannot be cancelled in status: " + status);
        }
        changeStatus(RentalStatus.CANCELLED);
    }

    public void markAsOverdue() {
        if (status == RentalStatus.ACTIVE) {
            changeStatus(RentalStatus.OVERDUE);
        }
    }

    public void extend(LocalDate newEndDate) {
        if (newEndDate.isBefore(this.endDate)) {
            throw new BusinessException(ErrorCode.INVALID_PARAMETER, "New end date must be after current end date");
        }
        this.endDate = newEndDate;
        this.updatedAt = Instant.now();
    }

    public int getTotalDays() {
        return (int) ChronoUnit.DAYS.between(startDate, endDate) + 1;
    }

    public int getActualDays() {
        if (actualReturnDate == null) {
            return getTotalDays();
        }
        return (int) ChronoUnit.DAYS.between(startDate, actualReturnDate) + 1;
    }

    public int getTotalKm() {
        if (endKm == 0) {
            return 0;
        }
        return endKm - startKm;
    }

    public boolean isOverdue() {
        return status == RentalStatus.OVERDUE || 
               (status.isActive() && LocalDate.now().isAfter(endDate));
    }

    public int getOverdueDays() {
        if (!isOverdue()) {
            return 0;
        }
        LocalDate compareDate = actualReturnDate != null ? actualReturnDate : LocalDate.now();
        return (int) ChronoUnit.DAYS.between(endDate, compareDate);
    }

    public Money getGrandTotal() {
        return totalPrice.add(extraKmCharge).subtract(discountAmount);
    }

    private void changeStatus(RentalStatus newStatus) {
        stateMachine.transition(this.status, newStatus);
        this.status = newStatus;
        this.updatedAt = Instant.now();
    }
}
