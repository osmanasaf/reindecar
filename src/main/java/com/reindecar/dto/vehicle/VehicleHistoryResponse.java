package com.reindecar.dto.vehicle;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Schema(description = "Araç geçmişi birleşik görünüm yanıtı")
public record VehicleHistoryResponse(
    @Schema(description = "Araç ID")
    Long vehicleId,

    @Schema(description = "Araç plakası")
    String vehiclePlate,

    @Schema(description = "Araç marka ve model")
    String vehicleName,

    @Schema(description = "Kiralama geçmişi")
    List<RentalHistoryItem> rentals,

    @Schema(description = "Bakım geçmişi")
    List<MaintenanceHistoryItem> maintenances,

    @Schema(description = "Hasar geçmişi")
    List<DamageHistoryItem> damages,

    @Schema(description = "Durum değişikliği geçmişi")
    List<StatusChangeItem> statusChanges
) {
    @Schema(description = "Kiralama geçmişi öğesi")
    public record RentalHistoryItem(
        Long id,
        String rentalNumber,
        Long customerId,
        String customerName,
        LocalDate startDate,
        LocalDate endDate,
        LocalDate actualReturnDate,
        String status,
        int startKm,
        int endKm,
        BigDecimal totalAmount,
        String currency,
        Instant createdAt
    ) {}

    @Schema(description = "Bakım geçmişi öğesi")
    public record MaintenanceHistoryItem(
        Long id,
        String maintenanceType,
        String maintenanceTypeDisplayName,
        LocalDate maintenanceDate,
        int currentKm,
        BigDecimal costAmount,
        String costCurrency,
        String serviceProvider,
        String description,
        List<Integer> affectedZones,
        Instant createdAt
    ) {}

    @Schema(description = "Hasar geçmişi öğesi")
    public record DamageHistoryItem(
        Long id,
        String damageType,
        String damageTypeDisplayName,
        String location,
        String locationDisplayName,
        int zoneId,
        String severity,
        String severityDisplayName,
        LocalDate reportDate,
        String description,
        BigDecimal estimatedCostAmount,
        String estimatedCostCurrency,
        boolean repaired,
        LocalDate repairedDate,
        BigDecimal repairCostAmount,
        String repairCostCurrency,
        Instant createdAt
    ) {}

    @Schema(description = "Durum değişikliği öğesi")
    public record StatusChangeItem(
        Long id,
        String fromStatus,
        String toStatus,
        String source,
        Long referenceId,
        String reason,
        String changedBy,
        Instant changedAt
    ) {}
}
