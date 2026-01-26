package com.reindecar.controller.pricing;

import com.reindecar.common.dto.ApiResponse;
import com.reindecar.dto.pricing.CreateKmPackageRequest;
import com.reindecar.dto.pricing.KmPackageResponse;
import com.reindecar.dto.pricing.UpdateKmPackageRequest;
import com.reindecar.entity.pricing.RentalType;
import com.reindecar.service.pricing.KmPackageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/km-packages")
@RequiredArgsConstructor
@Tag(name = "KM Package", description = "Kilometer package management endpoints")
public class KmPackageController {

    private final KmPackageService kmPackageService;

    @GetMapping
    @Operation(summary = "Get all KM packages", description = "Returns all KM packages including inactive ones")
    public ApiResponse<List<KmPackageResponse>> getAllPackages() {
        List<KmPackageResponse> packages = kmPackageService.getAllPackages();
        return ApiResponse.success(packages);
    }

    @GetMapping("/active")
    @Operation(summary = "Get active KM packages", description = "Returns only active KM packages")
    public ApiResponse<List<KmPackageResponse>> getActivePackages() {
        List<KmPackageResponse> packages = kmPackageService.getActivePackages();
        return ApiResponse.success(packages);
    }

    @GetMapping("/by-rental-type/{rentalType}")
    @Operation(summary = "Get KM packages by rental type", description = "Returns active packages applicable for the given rental type")
    public ApiResponse<List<KmPackageResponse>> getPackagesByRentalType(
            @PathVariable RentalType rentalType) {
        List<KmPackageResponse> packages = kmPackageService.getPackagesByRentalType(rentalType);
        return ApiResponse.success(packages);
    }

    @GetMapping("/category/{categoryId}")
    @Operation(summary = "Get KM packages by category", description = "Returns active packages for a specific category")
    public ApiResponse<List<KmPackageResponse>> getPackagesByCategory(
            @PathVariable Long categoryId) {
        List<KmPackageResponse> packages = kmPackageService.getPackagesByCategory(categoryId);
        return ApiResponse.success(packages);
    }

    @GetMapping("/global")
    @Operation(summary = "Get global KM packages", description = "Returns active packages that apply to all categories")
    public ApiResponse<List<KmPackageResponse>> getGlobalPackages() {
        List<KmPackageResponse> packages = kmPackageService.getGlobalPackages();
        return ApiResponse.success(packages);
    }

    @GetMapping("/available/{categoryId}")
    @Operation(summary = "Get available KM packages for category", description = "Returns both category-specific and global packages")
    public ApiResponse<List<KmPackageResponse>> getAvailablePackagesForCategory(
            @PathVariable Long categoryId) {
        List<KmPackageResponse> packages = kmPackageService.getAvailablePackagesForCategory(categoryId);
        return ApiResponse.success(packages);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get KM package by ID", description = "Returns a specific KM package")
    public ApiResponse<KmPackageResponse> getPackageById(@PathVariable Long id) {
        KmPackageResponse kmPackage = kmPackageService.getPackageById(id);
        return ApiResponse.success(kmPackage);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create KM package", description = "Creates a new KM package")
    public ApiResponse<KmPackageResponse> createPackage(
            @Valid @RequestBody CreateKmPackageRequest request) {
        KmPackageResponse kmPackage = kmPackageService.createPackage(request);
        return ApiResponse.success("KM package created successfully", kmPackage);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update KM package", description = "Updates an existing KM package")
    public ApiResponse<KmPackageResponse> updatePackage(
            @PathVariable Long id,
            @Valid @RequestBody UpdateKmPackageRequest request) {
        KmPackageResponse kmPackage = kmPackageService.updatePackage(id, request);
        return ApiResponse.success("KM package updated successfully", kmPackage);
    }

    @PostMapping("/{id}/activate")
    @Operation(summary = "Activate KM package", description = "Activates a KM package")
    public ApiResponse<Void> activatePackage(@PathVariable Long id) {
        kmPackageService.activatePackage(id);
        return ApiResponse.success("KM package activated successfully", null);
    }

    @PostMapping("/{id}/deactivate")
    @Operation(summary = "Deactivate KM package", description = "Deactivates a KM package (soft delete)")
    public ApiResponse<Void> deactivatePackage(@PathVariable Long id) {
        kmPackageService.deactivatePackage(id);
        return ApiResponse.success("KM package deactivated successfully", null);
    }
}
