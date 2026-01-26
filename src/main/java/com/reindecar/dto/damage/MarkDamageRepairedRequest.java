package com.reindecar.dto.damage;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
@Schema(description = "Hasar onarım bilgisi isteği")
public record MarkDamageRepairedRequest(
    @NotNull(message = "Onarım tarihi gereklidir")
    @Schema(description = "Onarım tarihi", example = "2026-01-26")
    LocalDate repairedDate,

    @Schema(description = "Onarım maliyeti", example = "1200.00")
    BigDecimal repairCostAmount,

    @Schema(description = "Para birimi", example = "TRY")
    String repairCostCurrency
) {}
