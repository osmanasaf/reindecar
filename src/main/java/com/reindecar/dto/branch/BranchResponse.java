package com.reindecar.dto.branch;

import java.time.Instant;

public record BranchResponse(
    Long id,
    String code,
    String name,
    String city,
    String district,
    String address,
    String phone,
    String email,
    boolean active,
    int vehicleCount,
    Instant createdAt,
    Instant updatedAt
) {
}
