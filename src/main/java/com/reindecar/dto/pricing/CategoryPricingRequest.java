package com.reindecar.dto.pricing;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(description = "Kategori fiyatlandırma isteği")
public record CategoryPricingRequest(
    @NotNull(message = "Kategori ID zorunludur")
    @Schema(description = "Kategori ID", example = "1")
    Long categoryId,

    @NotNull(message = "Günlük fiyat zorunludur")
    @Positive(message = "Günlük fiyat pozitif olmalıdır")
    @Schema(description = "Günlük fiyat", example = "1000.00")
    BigDecimal dailyPrice,

    @NotNull(message = "Haftalık fiyat zorunludur")
    @Positive(message = "Haftalık fiyat pozitif olmalıdır")
    @Schema(description = "Haftalık fiyat (7 gün)", example = "6000.00")
    BigDecimal weeklyPrice,

    @NotNull(message = "Aylık fiyat zorunludur")
    @Positive(message = "Aylık fiyat pozitif olmalıdır")
    @Schema(description = "Aylık fiyat", example = "20000.00")
    BigDecimal monthlyPrice,

    @NotNull(message = "Yıllık fiyat zorunludur")
    @Positive(message = "Yıllık fiyat pozitif olmalıdır")
    @Schema(description = "Yıllık fiyat (12 ay)", example = "200000.00")
    BigDecimal yearlyPrice,

    @Schema(description = "Para birimi", example = "TRY", defaultValue = "TRY")
    String currency,

    @Schema(description = "Geçerlilik başlangıç tarihi", example = "2026-01-01")
    LocalDate validFrom,

    @Schema(description = "Geçerlilik bitiş tarihi", example = "2026-12-31")
    LocalDate validTo
) {
    public CategoryPricingRequest {
        if (currency == null || currency.isBlank()) {
            currency = "TRY";
        }
    }
}
