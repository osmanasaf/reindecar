package com.reindecar.dto.user;

import com.reindecar.entity.user.Role;

import java.time.Instant;

public record UserResponse(
    Long id,
    String username,
    String email,
    String firstName,
    String lastName,
    String fullName,
    Role role,
    Long branchId,
    String branchName,
    boolean active,
    Instant lastLoginAt,
    Instant createdAt
) {
}
