package com.reindecar.dto.pricing;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

@Schema(description = "Km paketi oluşturma isteği (kademeli fiyatlandırma)")
public record CreateKmBundleRequest(
    @NotBlank(message = "Name is required")
    @Schema(description = "Paket adı", example = "Standart 3000 Km Paketi")
    String name,

    @Schema(description = "Paket açıklaması", example = "Aylık kiralama için standart km paketi")
    String description,

    @Min(value = 0, message = "Included km cannot be negative")
    @Schema(description = "Dahil olan km miktarı", example = "3000")
    int includedKm,

    @ArraySchema(schema = @Schema(implementation = KmTierRequest.class))
    @Schema(description = "Kademe fiyatları (ekstra km için)")
    List<KmTierRequest> tiers
) {}
