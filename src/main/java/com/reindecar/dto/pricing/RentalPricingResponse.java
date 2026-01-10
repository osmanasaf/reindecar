package com.reindecar.dto.pricing;

import com.reindecar.entity.pricing.RentalType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(description = "Fiyat kuralı yanıtı")
public record RentalPricingResponse(
    @Schema(description = "Fiyat kuralı ID", example = "1")
    Long id,

    @Schema(description = "Araç ID (araç bazlı ise)", example = "1")
    Long vehicleId,

    @Schema(description = "Müşteri ID (müşteri bazlı ise)", example = "1")
    Long customerId,

    @Schema(description = "Kategori ID (segment bazlı ise)", example = "1")
    Long categoryId,

    @Schema(description = "Kiralama tipi")
    RentalType rentalType,

    @Schema(description = "Aylık fiyat", example = "25000.00")
    BigDecimal monthlyPrice,

    @Schema(description = "Para birimi", example = "TRY")
    String currency,

    @Schema(description = "Dahil km limiti", example = "3000")
    int kmLimit,

    @Schema(description = "Ekstra km ücreti", example = "5.50")
    BigDecimal extraKmPrice,

    @Schema(description = "Geçerlilik başlangıç tarihi", example = "2026-01-01")
    LocalDate validFrom,

    @Schema(description = "Geçerlilik bitiş tarihi", example = "2026-12-31")
    LocalDate validTo,

    @Schema(description = "Aktif mi?", example = "true")
    boolean active,

    @Schema(description = "Notlar", example = "Premium müşteri fiyatı")
    String notes,

    @Schema(description = "Fiyat seviyesi (VEHICLE/CUSTOMER/CATEGORY)", example = "VEHICLE")
    String pricingLevel
) {}
