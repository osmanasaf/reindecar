package com.reindecar.entity.customer;

import com.reindecar.common.entity.BaseEntity;
import com.reindecar.common.validation.ValidTurkishNationalId;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "drivers")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Driver extends BaseEntity {

    @Column(nullable = false, name = "customer_id")
    private Long customerId;

    @ValidTurkishNationalId
    @NotBlank(message = "National ID is required")
    @Column(nullable = false, length = 11)
    private String nationalId;

    @NotBlank(message = "First name is required")
    @Size(max = 50)
    @Column(nullable = false, length = 50)
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(max = 50)
    @Column(nullable = false, length = 50)
    private String lastName;

    @Pattern(regexp = "^[0-9]{10}$", message = "Phone must be 10 digits")
    @Column(length = 20)
    private String phone;

    @NotBlank(message = "License number is required")
    @Size(max = 50)
    @Column(nullable = false, length = 50)
    private String licenseNumber;

    @Size(max = 10)
    @Column(length = 10)
    private String licenseClass;

    @Future(message = "License expiry date must be in the future")
    @NotNull(message = "License expiry date is required")
    @Column(nullable = false)
    private LocalDate licenseExpiryDate;

    @Column(nullable = false, name = "is_primary_driver")
    private boolean isPrimaryDriver = false;

    @Column(nullable = false)
    private boolean active = true;

    public static Driver create(
            Long customerId,
            String nationalId,
            String firstName,
            String lastName,
            String phone,
            String licenseNumber,
            String licenseClass,
            LocalDate licenseExpiryDate,
            boolean isPrimary) {
        
        Driver driver = new Driver();
        driver.customerId = customerId;
        driver.nationalId = nationalId;
        driver.firstName = firstName;
        driver.lastName = lastName;
        driver.phone = phone;
        driver.licenseNumber = licenseNumber;
        driver.licenseClass = licenseClass;
        driver.licenseExpiryDate = licenseExpiryDate;
        driver.isPrimaryDriver = isPrimary;
        driver.active = true;
        return driver;
    }

    public void updateInfo(
            String firstName,
            String lastName,
            String phone,
            String licenseNumber,
            String licenseClass,
            LocalDate licenseExpiryDate,
            boolean isPrimary) {
        
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.licenseNumber = licenseNumber;
        this.licenseClass = licenseClass;
        this.licenseExpiryDate = licenseExpiryDate;
        this.isPrimaryDriver = isPrimary;
    }

    public void activate() {
        this.active = true;
    }

    public void deactivate() {
        this.active = false;
    }

    public boolean isLicenseExpired() {
        return licenseExpiryDate.isBefore(LocalDate.now());
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public boolean isPrimary() {
        return isPrimaryDriver;
    }

    public boolean isActive() {
        return active;
    }
}
