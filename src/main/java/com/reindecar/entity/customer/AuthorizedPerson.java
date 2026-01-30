package com.reindecar.entity.customer;

import com.reindecar.common.entity.BaseEntity;
import com.reindecar.common.validation.ValidTurkishNationalId;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "authorized_persons", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"company_customer_id", "nationalId"}, name = "uk_authorized_person_national_id"),
    @UniqueConstraint(columnNames = {"company_customer_id", "email"}, name = "uk_authorized_person_email"),
    @UniqueConstraint(columnNames = {"company_customer_id", "phone"}, name = "uk_authorized_person_phone")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AuthorizedPerson extends BaseEntity {

    @Column(nullable = false, name = "company_customer_id")
    private Long companyCustomerId;

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

    @Size(max = 100)
    @Column(length = 100)
    private String title;

    @Pattern(regexp = "^[0-9]{10}$", message = "Phone must be 10 digits")
    @Column(length = 20)
    private String phone;

    @Email(message = "Invalid email format")
    @Size(max = 100)
    @Column(length = 100)
    private String email;

    @Column(nullable = false, name = "is_primary")
    private boolean isPrimary = false;

    @Column(nullable = false)
    private boolean active = true;

    public static AuthorizedPerson create(
            Long companyCustomerId,
            String nationalId,
            String firstName,
            String lastName,
            String title,
            String phone,
            String email,
            boolean isPrimary) {
        
        AuthorizedPerson person = new AuthorizedPerson();
        person.companyCustomerId = companyCustomerId;
        person.nationalId = nationalId;
        person.firstName = firstName;
        person.lastName = lastName;
        person.title = title;
        person.phone = phone;
        person.email = email;
        person.isPrimary = isPrimary;
        person.active = true;
        return person;
    }

    public void updateInfo(
            String firstName,
            String lastName,
            String title,
            String phone,
            String email) {
        
        if (firstName != null) this.firstName = firstName;
        if (lastName != null) this.lastName = lastName;
        if (title != null) this.title = title;
        if (phone != null) this.phone = phone;
        if (email != null) this.email = email;
    }

    public void setAsPrimary() {
        this.isPrimary = true;
    }

    public void unsetPrimary() {
        this.isPrimary = false;
    }

    public void activate() {
        this.active = true;
    }

    public void deactivate() {
        this.active = false;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public boolean isPrimary() {
        return isPrimary;
    }

    public boolean isActive() {
        return active;
    }
}
