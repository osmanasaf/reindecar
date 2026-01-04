package com.reindecar.dto.branch;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateBranchRequest(
    @NotBlank(message = "Branch name is required")
    @Size(max = 100, message = "Branch name must not exceed 100 characters")
    String name,

    @NotBlank(message = "City is required")
    @Size(max = 50, message = "City must not exceed 50 characters")
    String city,

    @Size(max = 50, message = "District must not exceed 50 characters")
    String district,

    @Size(max = 500, message = "Address must not exceed 500 characters")
    String address,

    @Size(max = 20, message = "Phone must not exceed 20 characters")
    String phone,

    @Email(message = "Email must be valid")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    String email
) {
}
