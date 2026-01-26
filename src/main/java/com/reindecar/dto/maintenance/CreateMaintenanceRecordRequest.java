package com.reindecar.dto.maintenance;

import com.reindecar.entity.maintenance.MaintenanceType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Builder
@Schema(description = "Bakım kaydı oluşturma isteği")
public record CreateMaintenanceRecordRequest(
    @NotNull(message = "Araç ID gereklidir")
    @Schema(description = "Araç ID", example = "1")
    Long vehicleId,

    @NotNull(message = "Bakım tipi gereklidir")
    @Schema(description = "Bakım/tamir tipi")
    MaintenanceType maintenanceType,

    @NotNull(message = "Bakım tarihi gereklidir")
    @Schema(description = "Bakım tarihi", example = "2026-01-26")
    LocalDate maintenanceDate,

    @Min(value = 0, message = "Kilometre negatif olamaz")
    @Schema(description = "Bakım anındaki kilometre", example = "50000")
    int currentKm,

    @Schema(description = "Bakım maliyeti", example = "2500.00")
    BigDecimal costAmount,

    @Schema(description = "Para birimi", example = "TRY")
    String costCurrency,

    @Size(max = 200)
    @Schema(description = "Servis sağlayıcı", example = "Toyota Yetkili Servis")
    String serviceProvider,

    @Size(max = 1000)
    @Schema(description = "Bakım açıklaması", example = "Sol ön çamurluk boyama ve göçük düzeltme")
    String description,

    @Schema(description = "Etkilenen zone ID'leri (araç haritası için)", example = "[4, 6]")
    List<Integer> affectedZones,

    @Schema(description = "Değiştirilen parçalar", example = "[\"Sol ayna\", \"Ön tampon\"]")
    List<String> partsReplaced,

    @Size(max = 50)
    @Schema(description = "Boyama rengi (boyama yapıldıysa)", example = "Beyaz")
    String paintColor
) {}
