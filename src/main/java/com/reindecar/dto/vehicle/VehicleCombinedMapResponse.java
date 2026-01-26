package com.reindecar.dto.vehicle;

import com.reindecar.entity.damage.DamageSeverity;
import com.reindecar.entity.maintenance.MaintenanceType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Schema(description = "Araç birleşik harita yanıtı (Hasar + Bakım)")
public record VehicleCombinedMapResponse(
    @Schema(description = "Araç ID")
    Long vehicleId,

    @Schema(description = "Araç plakası")
    String vehiclePlate,

    @Schema(description = "Toplam aktif hasar sayısı")
    int totalActiveDamages,

    @Schema(description = "Toplam bakım sayısı")
    int totalMaintenances,

    @Schema(description = "Zone bazlı birleşik özet (key: zoneId)")
    Map<Integer, ZoneCombinedInfo> zones,

    @Schema(description = "Renk kodları açıklaması")
    ColorLegend legend
) {
    @Schema(description = "Zone birleşik bilgisi")
    public record ZoneCombinedInfo(
        @Schema(description = "Zone ID")
        int zoneId,

        @Schema(description = "Zone'daki hasar sayısı")
        int damageCount,

        @Schema(description = "Zone'daki bakım sayısı")
        int maintenanceCount,

        @Schema(description = "En yüksek hasar şiddeti (varsa)")
        DamageSeverity maxDamageSeverity,

        @Schema(description = "Hasar renk kodu (arka plan)")
        String damageColorCode,

        @Schema(description = "Son bakım tipi (varsa)")
        MaintenanceType lastMaintenanceType,

        @Schema(description = "Son bakım tarihi")
        LocalDate lastMaintenanceDate,

        @Schema(description = "Bakım renk kodu (kenar)")
        String maintenanceColorCode,

        @Schema(description = "Hasar var mı")
        boolean hasDamage,

        @Schema(description = "Bakım yapılmış mı")
        boolean hasMaintenance,

        @Schema(description = "Bu zone'daki hasar ID'leri")
        List<Long> damageIds,

        @Schema(description = "Bu zone'daki bakım ID'leri")
        List<Long> maintenanceIds
    ) {}

    @Schema(description = "Renk kodları açıklaması")
    public record ColorLegend(
        @Schema(description = "Hasar şiddet renkleri")
        Map<String, String> damageSeverityColors,

        @Schema(description = "Bakım tipi renkleri")
        Map<String, String> maintenanceTypeColors
    ) {}
}
