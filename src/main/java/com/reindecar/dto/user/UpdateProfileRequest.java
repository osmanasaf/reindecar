package com.reindecar.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Request to update current user's profile")
public record UpdateProfileRequest(
        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        @Size(max = 100)
        String email,

        @NotBlank(message = "First name is required")
        @Size(max = 50)
        String firstName,

        @NotBlank(message = "Last name is required")
        @Size(max = 50)
        String lastName
) {
}
