package com.reindecar.dto.damage;

import com.reindecar.entity.damage.DamageSeverity;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
import java.util.Map;

@Schema(description = "Araç hasar haritası yanıtı")
public record VehicleDamageMapResponse(
    @Schema(description = "Araç ID")
    Long vehicleId,

    @Schema(description = "Araç plakası")
    String vehiclePlate,

    @Schema(description = "Toplam aktif hasar sayısı")
    int totalActiveDamages,

    @Schema(description = "Zone bazlı hasar özeti (key: zoneId)")
    Map<Integer, ZoneDamageInfo> zones,

    @Schema(description = "Tüm hasar detayları")
    List<DamageReportResponse> damages
) {
    @Schema(description = "Zone hasar bilgisi")
    public record ZoneDamageInfo(
        @Schema(description = "Zone ID")
        int zoneId,

        @Schema(description = "Zone'daki hasar sayısı")
        int damageCount,

        @Schema(description = "En yüksek hasar şiddeti")
        DamageSeverity maxSeverity,

        @Schema(description = "Renk kodu (en yüksek şiddete göre)")
        String colorCode,

        @Schema(description = "Bu zone'daki hasar ID'leri")
        List<Long> damageIds
    ) {}
}
