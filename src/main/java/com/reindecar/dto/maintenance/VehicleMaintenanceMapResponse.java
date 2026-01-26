package com.reindecar.dto.maintenance;

import com.reindecar.entity.maintenance.MaintenanceType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Schema(description = "Araç bakım haritası yanıtı")
public record VehicleMaintenanceMapResponse(
    @Schema(description = "Araç ID")
    Long vehicleId,

    @Schema(description = "Araç plakası")
    String vehiclePlate,

    @Schema(description = "Toplam bakım sayısı")
    int totalMaintenanceCount,

    @Schema(description = "Zone bazlı bakım özeti (key: zoneId)")
    Map<Integer, ZoneMaintenanceInfo> zones,

    @Schema(description = "Tüm bakım detayları")
    List<MaintenanceRecordResponse> maintenances
) {
    @Schema(description = "Zone bakım bilgisi")
    public record ZoneMaintenanceInfo(
        @Schema(description = "Zone ID")
        int zoneId,

        @Schema(description = "Zone'daki bakım sayısı")
        int maintenanceCount,

        @Schema(description = "Son bakım tipi")
        MaintenanceType lastMaintenanceType,

        @Schema(description = "Son bakım tarihi")
        LocalDate lastMaintenanceDate,

        @Schema(description = "Renk kodu (son bakım tipine göre)")
        String colorCode,

        @Schema(description = "Bu zone'daki bakım ID'leri")
        List<Long> maintenanceIds
    ) {}
}
