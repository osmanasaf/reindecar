package com.reindecar.dto.customer;

import com.reindecar.common.validation.ValidTurkishNationalId;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

@Schema(description = "Request to create a driver")
public record CreateDriverRequest(
    @Schema(description = "Customer ID (optional)", example = "1")
    Long customerId,

    @ValidTurkishNationalId
    @NotBlank(message = "National ID is required")
    @Schema(description = "National ID", example = "12345678901")
    String nationalId,

    @NotBlank(message = "First name is required")
    @Size(max = 50)
    @Schema(description = "First name", example = "Ahmet")
    String firstName,

    @NotBlank(message = "Last name is required")
    @Size(max = 50)
    @Schema(description = "Last name", example = "Yilmaz")
    String lastName,

    @Pattern(regexp = "^[0-9]{10}$", message = "Phone must be 10 digits")
    @Schema(description = "Phone number", example = "5321234567")
    String phone,

    @NotBlank(message = "License number is required")
    @Size(max = 50)
    @Schema(description = "License number", example = "A1B2C3D4E5")
    String licenseNumber,

    @Size(max = 10)
    @Schema(description = "License class", example = "B")
    String licenseClass,

    @Future(message = "License expiry date must be in the future")
    @NotNull(message = "License expiry date is required")
    @Schema(description = "License expiry date", example = "2028-12-31")
    LocalDate licenseExpiryDate,

    @Schema(description = "Whether this driver is primary", example = "false")
    Boolean primary
) {}
