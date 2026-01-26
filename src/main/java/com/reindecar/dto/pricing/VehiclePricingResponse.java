package com.reindecar.dto.pricing;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "Araç fiyatlandırma yanıtı")
public record VehiclePricingResponse(
    @Schema(description = "Fiyatlandırma ID")
    Long id,

    @Schema(description = "Araç ID")
    Long vehicleId,

    @Schema(description = "Araç adı")
    String vehicleName,

    @Schema(description = "Günlük fiyat (null ise kategori fiyatı kullanılır)")
    BigDecimal dailyPrice,

    @Schema(description = "Haftalık fiyat (null ise kategori fiyatı kullanılır)")
    BigDecimal weeklyPrice,

    @Schema(description = "Aylık fiyat (null ise kategori fiyatı kullanılır)")
    BigDecimal monthlyPrice,

    @Schema(description = "Yıllık fiyat (null ise kategori fiyatı kullanılır)")
    BigDecimal yearlyPrice,

    @Schema(description = "Para birimi")
    String currency,

    @Schema(description = "Aktif mi")
    boolean active
) {}
