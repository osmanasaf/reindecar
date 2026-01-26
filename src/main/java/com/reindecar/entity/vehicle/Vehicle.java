package com.reindecar.entity.vehicle;

import com.reindecar.common.exception.BusinessException;
import com.reindecar.common.exception.ErrorCode;

import com.reindecar.common.entity.AuditableEntity;
import com.reindecar.common.statemachine.StateMachine;
import com.reindecar.common.valueobject.Money;
import com.reindecar.service.vehicle.VehicleStatusTransition;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "vehicles")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Vehicle extends AuditableEntity {

    @NotBlank(message = "Plate number is required")
    @Size(max = 20)
    @Column(nullable = false, unique = true, length = 20)
    private String plateNumber;

    @NotBlank(message = "VIN is required")
    @Size(min = 17, max = 17, message = "VIN must be 17 characters")
    @Column(nullable = false, unique = true, length = 50)
    private String vinNumber;

    @NotBlank(message = "Brand is required")
    @Size(max = 50)
    @Column(nullable = false, length = 50)
    private String brand;

    @NotBlank(message = "Model is required")
    @Size(max = 50)
    @Column(nullable = false, length = 50)
    private String model;

    @Min(value = 1900, message = "Year must be after 1900")
    @Max(value = 2100, message = "Invalid year")
    @Column(nullable = false, name = "model_year")
    private int year;

    @Size(max = 30)
    @Column(length = 30)
    private String color;

    @NotNull(message = "Fuel type is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private FuelType fuelType;

    @NotNull(message = "Transmission is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Transmission transmission;

    @Min(value = 0, message = "Engine capacity cannot be negative")
    @Column
    private int engineCapacity;

    @Min(value = 1, message = "Seat count must be at least 1")
    @Max(value = 50, message = "Seat count cannot exceed 50")
    @Column
    private int seatCount;

    @NotNull(message = "Category ID is required")
    @Column(nullable = false, name = "category_id")
    private Long categoryId;

    @NotNull(message = "Branch ID is required")
    @Column(nullable = false, name = "branch_id")
    private Long branchId;

    @NotNull(message = "Status is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private VehicleStatus status;

    @Min(value = 0, message = "Kilometers cannot be negative")
    @Column(nullable = false)
    private int currentKm;

    @Column
    private LocalDate insuranceExpiryDate;

    @Column
    private LocalDate inspectionExpiryDate;

    @Column
    private LocalDate registrationDate;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "daily_price_amount")),
        @AttributeOverride(name = "currency", column = @Column(name = "daily_price_currency"))
    })
    private Money dailyPrice;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "weekly_price_amount")),
        @AttributeOverride(name = "currency", column = @Column(name = "weekly_price_currency"))
    })
    private Money weeklyPrice;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "monthly_price_amount")),
        @AttributeOverride(name = "currency", column = @Column(name = "monthly_price_currency"))
    })
    private Money monthlyPrice;

    @Size(max = 1000)
    @Column(length = 1000)
    private String notes;

    @Transient
    private static final StateMachine<VehicleStatus> stateMachine = 
        new StateMachine<>(new VehicleStatusTransition());

    public static Vehicle create(
            String plateNumber,
            String vinNumber,
            String brand,
            String model,
            int year,
            String color,
            FuelType fuelType,
            Transmission transmission,
            int engineCapacity,
            int seatCount,
            Long categoryId,
            Long branchId,
            int currentKm,
            LocalDate insuranceExpiryDate,
            LocalDate inspectionExpiryDate,
            LocalDate registrationDate,
            Money dailyPrice,
            Money weeklyPrice,
            Money monthlyPrice,
            String notes) {
        
        if (year < 1900 || year > LocalDate.now().getYear() + 1) {
            throw new BusinessException(ErrorCode.INVALID_PARAMETER, "Invalid vehicle year");
        }
        
        if (currentKm < 0) {
            throw new BusinessException(ErrorCode.INVALID_PARAMETER, "Kilometers cannot be negative");
        }
        
        if (seatCount < 1 || seatCount > 50) {
            throw new BusinessException(ErrorCode.INVALID_PARAMETER, "Seat count must be between 1 and 50");
        }
        
        Vehicle vehicle = new Vehicle();
        vehicle.plateNumber = plateNumber.toUpperCase();
        vehicle.vinNumber = vinNumber.toUpperCase();
        vehicle.brand = brand;
        vehicle.model = model;
        vehicle.year = year;
        vehicle.color = color;
        vehicle.fuelType = fuelType;
        vehicle.transmission = transmission;
        vehicle.engineCapacity = engineCapacity;
        vehicle.seatCount = seatCount;
        vehicle.categoryId = categoryId;
        vehicle.branchId = branchId;
        vehicle.status = VehicleStatus.AVAILABLE;
        vehicle.currentKm = currentKm;
        vehicle.insuranceExpiryDate = insuranceExpiryDate;
        vehicle.inspectionExpiryDate = inspectionExpiryDate;
        vehicle.registrationDate = registrationDate;
        vehicle.dailyPrice = dailyPrice;
        vehicle.weeklyPrice = weeklyPrice;
        vehicle.monthlyPrice = monthlyPrice;
        vehicle.notes = notes;
        return vehicle;
    }

    public void updateInfo(
            String brand,
            String model,
            int year,
            String color,
            int engineCapacity,
            int seatCount,
            LocalDate insuranceExpiryDate,
            LocalDate inspectionExpiryDate,
            Money dailyPrice,
            Money weeklyPrice,
            Money monthlyPrice,
            String notes) {
        
        this.brand = brand;
        this.model = model;
        this.year = year;
        this.color = color;
        this.engineCapacity = engineCapacity;
        this.seatCount = seatCount;
        this.insuranceExpiryDate = insuranceExpiryDate;
        this.inspectionExpiryDate = inspectionExpiryDate;
        this.dailyPrice = dailyPrice;
        this.weeklyPrice = weeklyPrice;
        this.monthlyPrice = monthlyPrice;
        this.notes = notes;
    }

    public void changeStatus(VehicleStatus newStatus) {
        stateMachine.transition(this.status, newStatus);
        this.status = newStatus;
    }

    public void changeBranch(Long newBranchId) {
        if (!this.status.isAvailableForRental()) {
            throw new BusinessException(ErrorCode.INVALID_OPERATION, "Can only change branch when vehicle is AVAILABLE");
        }
        this.branchId = newBranchId;
    }

    public void updateKilometers(int newKm) {
        if (newKm < this.currentKm) {
            throw new BusinessException(ErrorCode.INVALID_PARAMETER, "Kilometers cannot decrease");
        }
        this.currentKm = newKm;
    }

    public boolean isInsuranceExpiringSoon() {
        if (insuranceExpiryDate == null) {
            return false;
        }
        return insuranceExpiryDate.isBefore(LocalDate.now().plusDays(30));
    }

    public boolean isInspectionExpiringSoon() {
        if (inspectionExpiryDate == null) {
            return false;
        }
        return inspectionExpiryDate.isBefore(LocalDate.now().plusDays(30));
    }

    public Money getEffectiveDailyPrice(Money categoryDefaultPrice) {
        return dailyPrice != null ? dailyPrice : categoryDefaultPrice;
    }

    public Money getDailyPrice() {
        return dailyPrice;
    }

    public Money getWeeklyPrice() {
        return weeklyPrice;
    }

    public Money getMonthlyPrice() {
        return monthlyPrice;
    }

    public void updatePricing(Money dailyPrice, Money weeklyPrice, Money monthlyPrice) {
        this.dailyPrice = dailyPrice;
        this.weeklyPrice = weeklyPrice;
        this.monthlyPrice = monthlyPrice;
    }

    public String getDisplayName() {
        return brand + " " + model + " (" + year + ")";
    }
}
