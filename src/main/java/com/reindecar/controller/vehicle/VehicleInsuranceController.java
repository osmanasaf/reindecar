package com.reindecar.controller.vehicle;

import com.reindecar.common.dto.ApiResponse;
import com.reindecar.dto.vehicle.CreateVehicleInsuranceRequest;
import com.reindecar.dto.vehicle.VehicleInsuranceResponse;
import com.reindecar.service.vehicle.VehicleInsuranceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/vehicle-insurances")
@RequiredArgsConstructor
@Tag(name = "Vehicle Insurance", description = "Vehicle insurance management endpoints")
public class VehicleInsuranceController {

    private final VehicleInsuranceService vehicleInsuranceService;

    @GetMapping("/vehicle/{vehicleId}")
    @Operation(summary = "Get vehicle insurances", description = "Returns all active insurances for vehicle")
    public ApiResponse<List<VehicleInsuranceResponse>> getByVehicleId(@PathVariable Long vehicleId) {
        List<VehicleInsuranceResponse> insurances = vehicleInsuranceService.getByVehicleId(vehicleId);
        return ApiResponse.success(insurances);
    }

    @GetMapping("/vehicle/{vehicleId}/all")
    @Operation(summary = "Get all vehicle insurances", description = "Returns all insurances including inactive")
    public ApiResponse<List<VehicleInsuranceResponse>> getAllByVehicleId(@PathVariable Long vehicleId) {
        List<VehicleInsuranceResponse> insurances = vehicleInsuranceService.getAllByVehicleId(vehicleId);
        return ApiResponse.success(insurances);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create insurance", description = "Creates a new insurance policy")
    public ApiResponse<VehicleInsuranceResponse> createInsurance(
            @Valid @RequestBody CreateVehicleInsuranceRequest request) {
        VehicleInsuranceResponse insurance = vehicleInsuranceService.createInsurance(request);
        return ApiResponse.success("Insurance created successfully", insurance);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Deactivate insurance", description = "Deactivates an insurance policy")
    public ApiResponse<Void> deactivateInsurance(@PathVariable Long id) {
        vehicleInsuranceService.deactivateInsurance(id);
        return ApiResponse.success("Insurance deactivated successfully", null);
    }
}
