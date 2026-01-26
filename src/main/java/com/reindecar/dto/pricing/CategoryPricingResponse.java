package com.reindecar.dto.pricing;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(description = "Kategori fiyatlandırma yanıtı")
public record CategoryPricingResponse(
    @Schema(description = "Fiyatlandırma ID")
    Long id,

    @Schema(description = "Kategori ID")
    Long categoryId,

    @Schema(description = "Kategori adı")
    String categoryName,

    @Schema(description = "Günlük fiyat")
    BigDecimal dailyPrice,

    @Schema(description = "Haftalık fiyat")
    BigDecimal weeklyPrice,

    @Schema(description = "Aylık fiyat")
    BigDecimal monthlyPrice,

    @Schema(description = "Yıllık fiyat")
    BigDecimal yearlyPrice,

    @Schema(description = "Para birimi")
    String currency,

    @Schema(description = "Geçerlilik başlangıç tarihi")
    LocalDate validFrom,

    @Schema(description = "Geçerlilik bitiş tarihi")
    LocalDate validTo,

    @Schema(description = "Aktif mi")
    boolean active
) {}
