package com.reindecar.dto.pricing;

import com.reindecar.entity.pricing.RentalType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(description = "Araç/müşteri/kategori bazlı fiyat kuralı oluşturma isteği")
public record CreateRentalPricingRequest(
    @Schema(description = "Araç ID (araç bazlı fiyat için)", example = "1")
    Long vehicleId,

    @Schema(description = "Müşteri ID (müşteri bazlı fiyat için)", example = "1")
    Long customerId,

    @Schema(description = "Kategori ID (segment bazlı fiyat için)", example = "1")
    Long categoryId,

    @NotNull(message = "Rental type is required")
    @Schema(description = "Kiralama tipi", example = "MONTHLY")
    RentalType rentalType,

    @NotNull(message = "Monthly price is required")
    @Schema(description = "Aylık fiyat (TRY)", example = "25000.00")
    BigDecimal monthlyPrice,

    @Min(value = 0, message = "Km limit cannot be negative")
    @Schema(description = "Dahil olan km limiti", example = "3000")
    int kmLimit,

    @NotNull(message = "Extra km price is required")
    @Schema(description = "Ekstra km başına ücret (TRY)", example = "5.50")
    BigDecimal extraKmPrice,

    @Schema(description = "Geçerlilik başlangıç tarihi", example = "2026-01-01")
    LocalDate validFrom,

    @Schema(description = "Geçerlilik bitiş tarihi", example = "2026-12-31")
    LocalDate validTo,

    @Schema(description = "Notlar", example = "Premium müşteri özel fiyatı")
    String notes
) {}
