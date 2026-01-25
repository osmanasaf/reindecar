package com.reindecar.controller.vehicle;

import com.reindecar.common.dto.ApiResponse;
import com.reindecar.common.dto.PageResponse;
import com.reindecar.dto.vehicle.CreateVehicleRequest;
import com.reindecar.dto.vehicle.UpdateVehicleDetailsRequest;
import com.reindecar.dto.vehicle.UpdateVehicleStatusRequest;
import com.reindecar.dto.vehicle.VehicleDetailsResponse;
import com.reindecar.dto.vehicle.VehicleResponse;
import com.reindecar.dto.vehicle.VehicleStatusHistoryResponse;
import com.reindecar.service.vehicle.VehicleDetailsService;
import com.reindecar.service.vehicle.VehicleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/vehicles")
@RequiredArgsConstructor
@Tag(name = "Vehicle", description = "Vehicle management endpoints")
public class VehicleController {

    private final VehicleService vehicleService;
    private final VehicleDetailsService vehicleDetailsService;


    @GetMapping
    @Operation(summary = "Get all vehicles", description = "Returns paginated list of all vehicles")
    public ApiResponse<PageResponse<VehicleResponse>> getAllVehicles(
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        PageResponse<VehicleResponse> vehicles = vehicleService.getAllVehicles(pageable);
        return ApiResponse.success(vehicles);
    }

    @GetMapping("/available")
    @Operation(summary = "Get available vehicles", description = "Returns vehicles with AVAILABLE status")
    public ApiResponse<PageResponse<VehicleResponse>> getAvailableVehicles(
            @PageableDefault(size = 20, sort = "brand") Pageable pageable) {
        PageResponse<VehicleResponse> vehicles = vehicleService.getAvailableVehicles(pageable);
        return ApiResponse.success(vehicles);
    }

    @GetMapping("/available-for-period")
    @Operation(summary = "Get available vehicles for period", description = "Returns vehicles available for the given date range")
    public ApiResponse<PageResponse<VehicleResponse>> getAvailableVehiclesForPeriod(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @PageableDefault(size = 20, sort = "brand") Pageable pageable) {
        PageResponse<VehicleResponse> vehicles = vehicleService.getAvailableVehiclesForPeriod(startDate, endDate, pageable);
        return ApiResponse.success(vehicles);
    }

    @GetMapping("/branch/{branchId}")
    @Operation(summary = "Get vehicles by branch", description = "Returns vehicles for specific branch")
    public ApiResponse<PageResponse<VehicleResponse>> getVehiclesByBranch(
            @PathVariable Long branchId,
            @PageableDefault(size = 20) Pageable pageable) {
        PageResponse<VehicleResponse> vehicles = vehicleService.getVehiclesByBranch(branchId, pageable);
        return ApiResponse.success(vehicles);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get vehicle by ID", description = "Returns detailed information about a specific vehicle")
    public ApiResponse<VehicleResponse> getVehicleById(@PathVariable Long id) {
        VehicleResponse vehicle = vehicleService.getVehicleById(id);
        return ApiResponse.success(vehicle);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create vehicle", description = "Creates a new vehicle")
    public ApiResponse<VehicleResponse> createVehicle(@Valid @RequestBody CreateVehicleRequest request) {
        VehicleResponse vehicle = vehicleService.createVehicle(request);
        return ApiResponse.success("Vehicle created successfully", vehicle);
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Change vehicle status", description = "Changes vehicle status with StateMachine validation")
    public ApiResponse<VehicleResponse> changeStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateVehicleStatusRequest request,
            Authentication authentication) {
        String changedBy = authentication.getName();
        VehicleResponse vehicle = vehicleService.changeStatus(id, request, changedBy);
        return ApiResponse.success("Vehicle status changed successfully", vehicle);
    }

    @PatchMapping("/{id}/branch")
    @Operation(summary = "Change vehicle branch", description = "Changes vehicle branch (only when AVAILABLE)")
    public ApiResponse<Void> changeBranch(
            @PathVariable Long id,
            @RequestParam Long branchId) {
        vehicleService.changeBranch(id, branchId);
        return ApiResponse.success("Vehicle branch changed successfully", null);
    }

    @PatchMapping("/{id}/km")
    @Operation(summary = "Update kilometers", description = "Updates vehicle kilometers (cannot decrease)")
    public ApiResponse<Void> updateKilometers(
            @PathVariable Long id,
            @RequestParam int newKm) {
        vehicleService.updateKilometers(id, newKm);
        return ApiResponse.success("Vehicle kilometers updated successfully", null);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete vehicle", description = "Soft deletes a vehicle (cannot delete if RENTED)")
    public ApiResponse<Void> deleteVehicle(@PathVariable Long id) {
        vehicleService.deleteVehicle(id);
        return ApiResponse.success("Vehicle deleted successfully", null);
    }

    @GetMapping("/{id}/history")
    @Operation(summary = "Get vehicle status history", description = "Returns status change history for vehicle")
    public ApiResponse<PageResponse<VehicleStatusHistoryResponse>> getVehicleHistory(
            @PathVariable Long id,
            @PageableDefault(size = 20, sort = "changedAt", direction = Sort.Direction.DESC) Pageable pageable) {
        PageResponse<VehicleStatusHistoryResponse> history = vehicleService.getVehicleHistory(id, pageable);
        return ApiResponse.success(history);
    }

    @GetMapping("/{id}/details")
    @Operation(summary = "Get vehicle details", description = "Returns HGS, KABIS, service, finance info")
    public ApiResponse<VehicleDetailsResponse> getVehicleDetails(@PathVariable Long id) {
        VehicleDetailsResponse details = vehicleDetailsService.getByVehicleId(id);
        return ApiResponse.success(details);
    }

    @PutMapping("/{id}/details")
    @Operation(summary = "Update vehicle details", description = "Updates HGS, KABIS, service, finance info")
    public ApiResponse<VehicleDetailsResponse> updateVehicleDetails(
            @PathVariable Long id,
            @Valid @RequestBody UpdateVehicleDetailsRequest request) {
        VehicleDetailsResponse details = vehicleDetailsService.updateDetails(id, request);
        return ApiResponse.success("Vehicle details updated successfully", details);
    }
    @PatchMapping("/{id}/details")
    @Operation(summary = "Partially update vehicle details", description = "Partially updates HGS, KABIS, service, finance info")
    public ApiResponse<VehicleDetailsResponse> patchVehicleDetails(
            @PathVariable Long id,
            @Valid @RequestBody UpdateVehicleDetailsRequest request) {
        VehicleDetailsResponse details = vehicleDetailsService.updateDetails(id, request);
        return ApiResponse.success("Vehicle details updated successfully", details);
    }
}

