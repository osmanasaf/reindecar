package com.reindecar.dto.customer;

import com.reindecar.common.validation.ValidTurkishNationalId;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

@Schema(description = "Request to create a personal customer")
public record CreatePersonalCustomerRequest(
    @ValidTurkishNationalId
    @NotBlank(message = "National ID is required")
    @Schema(description = "Turkish National ID (TC Kimlik No)", example = "12345678901")
    String nationalId,

    @NotBlank(message = "First name is required")
    @Size(max = 50)
    @Schema(description = "Customer's first name", example = "Ahmet")
    String firstName,

    @NotBlank(message = "Last name is required")
    @Size(max = 50)
    @Schema(description = "Customer's last name", example = "Yılmaz")
    String lastName,

    @Past(message = "Birth date must be in the past")
    @Schema(description = "Customer's birth date", example = "1990-05-15")
    LocalDate birthDate,

    @NotBlank(message = "Phone is required")
    @Size(max = 20)
    @Schema(description = "Customer's phone number", example = "5551234567")
    String phone,

    @Email
    @Size(max = 100)
    @Schema(description = "Customer's email address", example = "ahmet.yilmaz@example.com")
    String email,

    @Size(max = 500)
    @Schema(description = "Customer's address", example = "Atatürk Caddesi No:123 Daire:5")
    String address,

    @Size(max = 50)
    @Schema(description = "Customer's city", example = "İstanbul")
    String city,

    @Size(max = 50)
    @Schema(description = "Driver's license number", example = "A1B2C3D4E5")
    String licenseNumber,

    @Size(max = 10)
    @Schema(description = "Driver's license class", example = "B")
    String licenseClass,

    @Future(message = "License expiry date must be in the future")
    @Schema(description = "Driver's license expiry date", example = "2028-12-31")
    LocalDate licenseExpiryDate,

    @Schema(description = "Customer's credit score (0-2000)", example = "1200", minimum = "0", maximum = "2000")
    Integer creditScore
) {}
