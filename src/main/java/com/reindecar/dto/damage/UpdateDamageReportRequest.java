package com.reindecar.dto.damage;

import com.reindecar.entity.damage.DamageLocation;
import com.reindecar.entity.damage.DamageSeverity;
import com.reindecar.entity.damage.DamageType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
@Schema(description = "Hasar raporu güncelleme isteği")
public record UpdateDamageReportRequest(
    @NotNull(message = "Hasar tipi gereklidir")
    @Schema(description = "Hasar tipi")
    DamageType damageType,

    @NotNull(message = "Hasar lokasyonu gereklidir")
    @Schema(description = "Araç üzerindeki hasar lokasyonu")
    DamageLocation location,

    @NotNull(message = "Hasar şiddeti gereklidir")
    @Schema(description = "Hasar şiddet seviyesi")
    DamageSeverity severity,

    @NotBlank(message = "Açıklama gereklidir")
    @Size(max = 2000)
    @Schema(description = "Hasar açıklaması", example = "Ön tampon sağ köşesinde 10cm çizik")
    String description,

    @Schema(description = "Tahmini maliyet", example = "1500.00")
    BigDecimal estimatedCostAmount,

    @Schema(description = "Para birimi", example = "TRY")
    String estimatedCostCurrency
) {}
