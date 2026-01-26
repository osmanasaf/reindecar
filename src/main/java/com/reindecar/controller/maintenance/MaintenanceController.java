package com.reindecar.controller.maintenance;

import com.reindecar.common.dto.ApiResponse;
import com.reindecar.common.dto.PageResponse;
import com.reindecar.dto.maintenance.CreateMaintenanceRecordRequest;
import com.reindecar.dto.maintenance.MaintenanceRecordResponse;
import com.reindecar.dto.maintenance.UpdateMaintenanceRecordRequest;
import com.reindecar.dto.maintenance.VehicleMaintenanceMapResponse;
import com.reindecar.service.maintenance.MaintenanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/maintenances")
@RequiredArgsConstructor
@Tag(name = "Maintenance", description = "Bakım yönetimi endpoint'leri")
public class MaintenanceController {

    private final MaintenanceService maintenanceService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Bakım kaydı oluştur", description = "Yeni bir bakım kaydı oluşturur")
    public ApiResponse<MaintenanceRecordResponse> createMaintenanceRecord(
            @Valid @RequestBody CreateMaintenanceRecordRequest request) {
        MaintenanceRecordResponse response = maintenanceService.createMaintenanceRecord(request);
        return ApiResponse.success("Bakım kaydı başarıyla oluşturuldu", response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Bakım detayı", description = "Belirtilen ID'ye sahip bakım kaydını döner")
    public ApiResponse<MaintenanceRecordResponse> getMaintenanceById(@PathVariable Long id) {
        MaintenanceRecordResponse response = maintenanceService.getMaintenanceById(id);
        return ApiResponse.success(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Bakım güncelle", description = "Mevcut bakım kaydını günceller")
    public ApiResponse<MaintenanceRecordResponse> updateMaintenanceRecord(
            @PathVariable Long id,
            @Valid @RequestBody UpdateMaintenanceRecordRequest request) {
        MaintenanceRecordResponse response = maintenanceService.updateMaintenanceRecord(id, request);
        return ApiResponse.success("Bakım kaydı başarıyla güncellendi", response);
    }

    @GetMapping("/vehicle/{vehicleId}")
    @Operation(summary = "Araç bakımları", description = "Belirtilen araca ait tüm bakımları listeler")
    public ApiResponse<PageResponse<MaintenanceRecordResponse>> getMaintenancesByVehicle(
            @PathVariable Long vehicleId,
            @PageableDefault(size = 20, sort = "maintenanceDate", direction = Sort.Direction.DESC) Pageable pageable) {
        PageResponse<MaintenanceRecordResponse> response = maintenanceService.getMaintenancesByVehicle(vehicleId, pageable);
        return ApiResponse.success(response);
    }

    @GetMapping("/vehicle/{vehicleId}/map")
    @Operation(summary = "Araç bakım haritası", description = "Araç üzerindeki bakımların zone bazlı haritasını döner")
    public ApiResponse<VehicleMaintenanceMapResponse> getVehicleMaintenanceMap(@PathVariable Long vehicleId) {
        VehicleMaintenanceMapResponse response = maintenanceService.getVehicleMaintenanceMap(vehicleId);
        return ApiResponse.success(response);
    }
}
