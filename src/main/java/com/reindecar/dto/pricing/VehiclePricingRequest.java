package com.reindecar.dto.pricing;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

@Schema(description = "Araç özel fiyatlandırma isteği")
public record VehiclePricingRequest(
    @NotNull(message = "Araç ID zorunludur")
    @Schema(description = "Araç ID", example = "1")
    Long vehicleId,

    @Positive(message = "Günlük fiyat pozitif olmalıdır")
    @Schema(description = "Günlük fiyat (null ise kategori fiyatı kullanılır)", example = "1200.00")
    BigDecimal dailyPrice,

    @Positive(message = "Haftalık fiyat pozitif olmalıdır")
    @Schema(description = "Haftalık fiyat (null ise kategori fiyatı kullanılır)", example = "7000.00")
    BigDecimal weeklyPrice,

    @Positive(message = "Aylık fiyat pozitif olmalıdır")
    @Schema(description = "Aylık fiyat (null ise kategori fiyatı kullanılır)", example = "25000.00")
    BigDecimal monthlyPrice,

    @Positive(message = "Yıllık fiyat pozitif olmalıdır")
    @Schema(description = "Yıllık fiyat (null ise kategori fiyatı kullanılır)", example = "250000.00")
    BigDecimal yearlyPrice,

    @Schema(description = "Para birimi", example = "TRY", defaultValue = "TRY")
    String currency
) {
    public VehiclePricingRequest {
        if (currency == null || currency.isBlank()) {
            currency = "TRY";
        }
    }
}
