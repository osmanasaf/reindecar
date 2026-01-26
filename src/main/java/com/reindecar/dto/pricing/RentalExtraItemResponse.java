package com.reindecar.dto.pricing;

import com.reindecar.entity.pricing.CalculationType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "Kiralama ek kalemi yanıtı")
public record RentalExtraItemResponse(
    @Schema(description = "Ek kalem ID")
    Long id,

    @Schema(description = "Kiralama ID")
    Long rentalId,

    @Schema(description = "Kalem türü ID (önceden tanımlı ise)")
    Long itemTypeId,

    @Schema(description = "Kalem adı")
    String name,

    @Schema(description = "Açıklama")
    String description,

    @Schema(description = "Birim tutar")
    BigDecimal amount,

    @Schema(description = "Para birimi")
    String currency,

    @Schema(description = "Hesaplama tipi")
    CalculationType calculationType,

    @Schema(description = "Hesaplanan toplam tutar")
    BigDecimal calculatedTotal
) {}
