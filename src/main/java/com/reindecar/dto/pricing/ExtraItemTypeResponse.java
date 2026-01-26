package com.reindecar.dto.pricing;

import com.reindecar.entity.pricing.CalculationType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "Ek kalem türü yanıtı")
public record ExtraItemTypeResponse(
    @Schema(description = "Kalem türü ID")
    Long id,

    @Schema(description = "Kalem kodu")
    String code,

    @Schema(description = "Kalem adı")
    String name,

    @Schema(description = "Açıklama")
    String description,

    @Schema(description = "Varsayılan tutar")
    BigDecimal defaultAmount,

    @Schema(description = "Para birimi")
    String currency,

    @Schema(description = "Hesaplama tipi")
    CalculationType calculationType,

    @Schema(description = "Sıralama")
    Integer sortOrder,

    @Schema(description = "Aktif mi")
    boolean active
) {}
