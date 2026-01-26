package com.reindecar.controller.damage;

import com.reindecar.common.dto.ApiResponse;
import com.reindecar.common.dto.PageResponse;
import com.reindecar.dto.damage.CreateDamageReportRequest;
import com.reindecar.dto.damage.DamageReportResponse;
import com.reindecar.dto.damage.MarkDamageRepairedRequest;
import com.reindecar.dto.damage.UpdateDamageReportRequest;
import com.reindecar.dto.damage.VehicleDamageMapResponse;
import com.reindecar.service.damage.DamageService;
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
@RequestMapping("/api/v1/damages")
@RequiredArgsConstructor
@Tag(name = "Damage", description = "Hasar yönetimi endpoint'leri")
public class DamageController {

    private final DamageService damageService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Hasar raporu oluştur", description = "Yeni bir hasar raporu oluşturur")
    public ApiResponse<DamageReportResponse> createDamageReport(
            @Valid @RequestBody CreateDamageReportRequest request) {
        DamageReportResponse response = damageService.createDamageReport(request);
        return ApiResponse.success("Hasar raporu başarıyla oluşturuldu", response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Hasar detayı", description = "Belirtilen ID'ye sahip hasar raporunu döner")
    public ApiResponse<DamageReportResponse> getDamageById(@PathVariable Long id) {
        DamageReportResponse response = damageService.getDamageById(id);
        return ApiResponse.success(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Hasar güncelle", description = "Mevcut hasar raporunu günceller")
    public ApiResponse<DamageReportResponse> updateDamageReport(
            @PathVariable Long id,
            @Valid @RequestBody UpdateDamageReportRequest request) {
        DamageReportResponse response = damageService.updateDamageReport(id, request);
        return ApiResponse.success("Hasar raporu başarıyla güncellendi", response);
    }

    @PatchMapping("/{id}/repair")
    @Operation(summary = "Onarım bilgisi ekle", description = "Hasarı onarılmış olarak işaretler")
    public ApiResponse<DamageReportResponse> markAsRepaired(
            @PathVariable Long id,
            @Valid @RequestBody MarkDamageRepairedRequest request) {
        DamageReportResponse response = damageService.markAsRepaired(id, request);
        return ApiResponse.success("Hasar onarılmış olarak işaretlendi", response);
    }

    @GetMapping("/vehicle/{vehicleId}")
    @Operation(summary = "Araç hasarları", description = "Belirtilen araca ait tüm hasarları listeler")
    public ApiResponse<PageResponse<DamageReportResponse>> getDamagesByVehicle(
            @PathVariable Long vehicleId,
            @PageableDefault(size = 20, sort = "reportDate", direction = Sort.Direction.DESC) Pageable pageable) {
        PageResponse<DamageReportResponse> response = damageService.getDamagesByVehicle(vehicleId, pageable);
        return ApiResponse.success(response);
    }

    @GetMapping("/vehicle/{vehicleId}/map")
    @Operation(summary = "Araç hasar haritası", description = "Araç üzerindeki aktif hasarların zone bazlı haritasını döner")
    public ApiResponse<VehicleDamageMapResponse> getVehicleDamageMap(@PathVariable Long vehicleId) {
        VehicleDamageMapResponse response = damageService.getVehicleDamageMap(vehicleId);
        return ApiResponse.success(response);
    }

    @GetMapping("/rental/{rentalId}")
    @Operation(summary = "Kiralama hasarları", description = "Belirtilen kiralamaya ait tüm hasarları listeler")
    public ApiResponse<PageResponse<DamageReportResponse>> getDamagesByRental(
            @PathVariable Long rentalId,
            @PageableDefault(size = 20, sort = "reportDate", direction = Sort.Direction.DESC) Pageable pageable) {
        PageResponse<DamageReportResponse> response = damageService.getDamagesByRental(rentalId, pageable);
        return ApiResponse.success(response);
    }
}
