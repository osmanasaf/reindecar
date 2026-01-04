package com.reindecar.dto.branch;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
@Schema(description = "Request to create a new branch")
public record CreateBranchRequest(
    @NotBlank(message = "Branch code is required")
    @Size(max = 10, message = "Branch code must not exceed 10 characters")
    @Pattern(regexp = "^[A-Z0-9]+$", message = "Code must be alphanumeric uppercase")
    @Schema(description = "Branch code (uppercase alphanumeric)", example = "IST01")
    String code,

    @NotBlank(message = "Branch name is required")
    @Size(max = 100, message = "Branch name must not exceed 100 characters")
    @Schema(description = "Branch name", example = "İstanbul Kadıköy Şubesi")
    String name,

    @NotBlank(message = "City is required")
    @Size(max = 50, message = "City must not exceed 50 characters")
    @Schema(description = "City", example = "İstanbul")
    String city,

    @Size(max = 50, message = "District must not exceed 50 characters")
    @Schema(description = "District", example = "Kadıköy")
    String district,

    @Size(max = 500, message = "Address must not exceed 500 characters")
    @Schema(description = "Full address", example = "Bağdat Caddesi No:123")
    String address,

    @Size(max = 20, message = "Phone must not exceed 20 characters")
    @Pattern(regexp = "^[0-9]{10}$", message = "Phone must be 10 digits")
    @Schema(description = "Phone number (10 digits)", example = "2165551234")
    String phone,

    @Email(message = "Email must be valid")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    @Schema(description = "Email address", example = "kadikoy@rentacar.com")
    String email
) {}
