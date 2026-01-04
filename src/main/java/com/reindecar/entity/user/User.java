package com.reindecar.entity.user;

import com.reindecar.common.entity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50)
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "Username can only contain letters, numbers, and underscores")
    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Size(max = 100)
    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @NotBlank(message = "Password hash is required")
    @Size(max = 255)
    @Column(nullable = false, length = 255)
    private String passwordHash;

    @NotBlank(message = "First name is required")
    @Size(max = 50)
    @Column(nullable = false, length = 50)
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(max = 50)
    @Column(nullable = false, length = 50)
    private String lastName;

    @NotNull(message = "Role is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role;

    @Column(name = "branch_id")
    private Long branchId;

    @Column(nullable = false)
    private boolean active = true;

    @Column
    private Instant lastLoginAt;

    public static User create(String username, String email, String passwordHash,
                              String firstName, String lastName, Role role, Long branchId) {
        User user = new User();
        user.username = username.toLowerCase();
        user.email = email.toLowerCase();
        user.passwordHash = passwordHash;
        user.firstName = firstName;
        user.lastName = lastName;
        user.role = role;
        user.branchId = branchId;
        user.active = true;
        return user;
    }

    public void updateInfo(String email, String firstName, String lastName, Long branchId) {
        this.email = email.toLowerCase();
        this.firstName = firstName;
        this.lastName = lastName;
        this.branchId = branchId;
    }

    public void changePassword(String newPasswordHash) {
        this.passwordHash = newPasswordHash;
    }

    public void activate() {
        this.active = true;
    }

    public void deactivate() {
        this.active = false;
    }

    public void updateLastLogin() {
        this.lastLoginAt = Instant.now();
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public boolean isAdmin() {
        return role == Role.ADMIN;
    }

    public boolean isOperator() {
        return role == Role.OPERATOR;
    }

    public boolean isActive() {
        return active;
    }
}
