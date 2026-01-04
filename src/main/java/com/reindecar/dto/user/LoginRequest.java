package com.reindecar.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
@Schema(description = "Login request")
public record LoginRequest(
    @NotBlank(message = "Username is required")
    @Schema(description = "Username", example = "admin")
    String username,

    @NotBlank(message = "Password is required")
    @Schema(description = "Password", example = "password123")
    String password
) {}
