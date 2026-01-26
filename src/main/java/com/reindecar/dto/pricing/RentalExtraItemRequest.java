package com.reindecar.dto.pricing;

import com.reindecar.entity.pricing.CalculationType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

@Schema(description = "Kiralama ek kalemi isteği")
public record RentalExtraItemRequest(
    @Schema(description = "Önceden tanımlı kalem türü ID (serbest kalem için null)")
    Long itemTypeId,

    @Size(max = 100, message = "Özel kalem adı en fazla 100 karakter olabilir")
    @Schema(description = "Özel kalem adı (serbest kalem için)", example = "Özel Sigorta")
    String customName,

    @Size(max = 500, message = "Açıklama en fazla 500 karakter olabilir")
    @Schema(description = "Açıklama", example = "Ek sigorta paketi")
    String description,

    @NotNull(message = "Tutar zorunludur")
    @Positive(message = "Tutar pozitif olmalıdır")
    @Schema(description = "Tutar", example = "5000.00")
    BigDecimal amount,

    @Schema(description = "Para birimi", example = "TRY", defaultValue = "TRY")
    String currency,

    @NotNull(message = "Hesaplama tipi zorunludur")
    @Schema(description = "Hesaplama tipi", example = "FIXED")
    CalculationType calculationType
) {
    public RentalExtraItemRequest {
        if (currency == null || currency.isBlank()) {
            currency = "TRY";
        }
    }
}
