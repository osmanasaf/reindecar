package com.reindecar.entity.customer;

import com.reindecar.common.validation.ValidTurkishNationalId;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@DiscriminatorValue("PERSONAL")
@Getter
public class CustomerPerson extends Customer {

    @ValidTurkishNationalId
    @Column(length = 11, unique = true)
    private String nationalId;

    @NotBlank(message = "First name is required")
    @Size(max = 50)
    @Column(length = 50)
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(max = 50)
    @Column(length = 50)
    private String lastName;

    @Past(message = "Birth date must be in the past")
    @Column
    private LocalDate birthDate;

    @Size(max = 50)
    @Column(length = 50)
    private String licenseNumber;

    @Size(max = 10)
    @Column(length = 10)
    private String licenseClass;

    @Column
    private LocalDate licenseIssueDate;

    @Future(message = "License expiry date must be in the future")
    @Column
    private LocalDate licenseExpiryDate;

    protected CustomerPerson() {
        super(CustomerType.PERSONAL);
    }

    public static CustomerPerson create(
            String nationalId,
            String firstName,
            String lastName,
            LocalDate birthDate,
            String phone,
            String email,
            String address,
            String city,
            String licenseNumber,
            String licenseClass,
            LocalDate licenseExpiryDate) {
        
        CustomerPerson customer = new CustomerPerson();
        customer.nationalId = nationalId;
        customer.firstName = firstName;
        customer.lastName = lastName;
        customer.birthDate = birthDate;
        customer.licenseNumber = licenseNumber;
        customer.licenseClass = licenseClass;
        customer.licenseExpiryDate = licenseExpiryDate;
        customer.setContactInfo(phone, email, address, city);
        return customer;
    }

    public void updateInfo(
            String firstName,
            String lastName,
            LocalDate birthDate,
            String phone,
            String email,
            String address,
            String city) {
        
        if (firstName != null) this.firstName = firstName;
        if (lastName != null) this.lastName = lastName;
        if (birthDate != null) this.birthDate = birthDate;
        setContactInfo(phone, email, address, city);
    }

    public void updateLicense(
            String licenseNumber,
            String licenseClass,
            LocalDate licenseIssueDate,
            LocalDate licenseExpiryDate) {
        
        if (licenseNumber != null) this.licenseNumber = licenseNumber;
        if (licenseClass != null) this.licenseClass = licenseClass;
        if (licenseIssueDate != null) this.licenseIssueDate = licenseIssueDate;
        if (licenseExpiryDate != null) this.licenseExpiryDate = licenseExpiryDate;
    }

    public boolean isLicenseExpired() {
        return licenseExpiryDate != null && licenseExpiryDate.isBefore(LocalDate.now());
    }

    @Override
    public String getDisplayName() {
        return firstName + " " + lastName;
    }
}
