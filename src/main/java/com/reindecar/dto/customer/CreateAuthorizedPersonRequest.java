package com.reindecar.dto.customer;

import com.reindecar.common.validation.ValidTurkishNationalId;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Request to create an authorized person for a company")
public record CreateAuthorizedPersonRequest(
    @ValidTurkishNationalId
    @NotBlank(message = "National ID is required")
    @Schema(description = "Turkish National ID (TC Kimlik No)", example = "98765432109")
    String nationalId,

    @NotBlank(message = "First name is required")
    @Size(max = 50)
    @Schema(description = "Authorized person's first name", example = "Ayşe")
    String firstName,

    @NotBlank(message = "Last name is required")
    @Size(max = 50)
    @Schema(description = "Authorized person's last name", example = "Kaya")
    String lastName,

    @Size(max = 100)
    @Schema(description = "Job title in the company", example = "Genel Müdür")
    String title,

    @Size(max = 20)
    @Schema(description = "Phone number", example = "5551112233")
    String phone,

    @Email
    @Size(max = 100)
    @Schema(description = "Email address", example = "ayse.kaya@acmetech.com")
    String email,

    @Schema(description = "Is this the primary contact person?", example = "true")
    boolean isPrimary
) {}
