package com.reindecar.dto.pricing;

import com.reindecar.entity.pricing.RentalType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

@Schema(description = "Fiyat hesaplama isteği")
public record CalculatePriceRequest(
    @NotNull(message = "Vehicle ID is required")
    @Schema(description = "Araç ID", example = "1")
    Long vehicleId,

    @Schema(description = "Müşteri ID (özel fiyat için)", example = "1")
    Long customerId,

    @NotNull(message = "Rental type is required")
    @Schema(description = "Kiralama tipi", example = "MONTHLY")
    RentalType rentalType,

    @NotNull(message = "Start date is required")
    @Schema(description = "Kiralama başlangıç tarihi", example = "2026-01-15")
    LocalDate startDate,

    @NotNull(message = "End date is required")
    @Schema(description = "Kiralama bitiş tarihi", example = "2026-02-15")
    LocalDate endDate,

    @Schema(description = "Km paketi ID (opsiyonel)", example = "1")
    Long kmPackageId
) {}
