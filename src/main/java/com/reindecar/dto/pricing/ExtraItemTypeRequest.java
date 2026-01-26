package com.reindecar.dto.pricing;

import com.reindecar.entity.pricing.CalculationType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

@Schema(description = "Ek kalem türü isteği")
public record ExtraItemTypeRequest(
    @NotBlank(message = "Kod zorunludur")
    @Size(max = 50, message = "Kod en fazla 50 karakter olabilir")
    @Schema(description = "Kalem kodu", example = "TAX")
    String code,

    @NotBlank(message = "Ad zorunludur")
    @Size(max = 100, message = "Ad en fazla 100 karakter olabilir")
    @Schema(description = "Kalem adı", example = "Vergi")
    String name,

    @Size(max = 500, message = "Açıklama en fazla 500 karakter olabilir")
    @Schema(description = "Açıklama", example = "KDV ve diğer vergiler")
    String description,

    @Schema(description = "Varsayılan tutar", example = "1000.00")
    BigDecimal defaultAmount,

    @Schema(description = "Para birimi", example = "TRY", defaultValue = "TRY")
    String currency,

    @NotNull(message = "Hesaplama tipi zorunludur")
    @Schema(description = "Hesaplama tipi", example = "FIXED")
    CalculationType calculationType,

    @Schema(description = "Sıralama", example = "1")
    Integer sortOrder
) {
    public ExtraItemTypeRequest {
        if (currency == null || currency.isBlank()) {
            currency = "TRY";
        }
    }
}
