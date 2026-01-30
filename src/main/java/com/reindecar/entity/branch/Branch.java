package com.reindecar.entity.branch;

import com.reindecar.common.entity.AuditableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "branches")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Branch extends AuditableEntity {

    @NotBlank(message = "Branch code is required")
    @Size(min = 2, max = 10)
    @Pattern(regexp = "^[A-Z0-9]+$", message = "Code must be alphanumeric uppercase")
    @Column(nullable = false, unique = true, length = 10)
    private String code;

    @NotBlank(message = "Branch name is required")
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String name;

    @NotBlank(message = "City is required")
    @Size(max = 50)
    @Column(nullable = false, length = 50)
    private String city;

    @Size(max = 50)
    @Column(length = 50)
    private String district;

    @Size(max = 500)
    @Column(length = 500)
    private String address;

    @Pattern(regexp = "^[0-9]{10}$", message = "Phone must be 10 digits")
    @Size(max = 20)
    @Column(length = 20)
    private String phone;

    @Email(message = "Invalid email format")
    @Size(max = 100)
    @Column(length = 100)
    private String email;

    @Column(nullable = false)
    private boolean active = true;

    public static Branch create(String code, String name, String city, String district, 
                                 String address, String phone, String email) {
        Branch branch = new Branch();
        branch.code = code.toUpperCase();
        branch.name = name;
        branch.city = city;
        branch.district = district;
        branch.address = address;
        branch.phone = phone;
        branch.email = email;
        branch.active = true;
        return branch;
    }

    public void updateInfo(String name, String city, String district, 
                          String address, String phone, String email) {
        if (name != null) this.name = name;
        if (city != null) this.city = city;
        if (district != null) this.district = district;
        if (address != null) this.address = address;
        if (phone != null) this.phone = phone;
        if (email != null) this.email = email;
    }

    public void activate() {
        this.active = true;
    }

    public void deactivate() {
        this.active = false;
    }

    public boolean isActive() {
        return active;
    }
}
