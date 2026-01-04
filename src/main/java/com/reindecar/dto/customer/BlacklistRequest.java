package com.reindecar.dto.customer;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Request to blacklist a customer")
public record BlacklistRequest(
    @NotBlank(message = "Reason is required")
    @Size(max = 500)
    @Schema(description = "Reason for blacklisting the customer", example = "Ödeme yapılmadı ve iletişime geçilemiyor")
    String reason
) {}
