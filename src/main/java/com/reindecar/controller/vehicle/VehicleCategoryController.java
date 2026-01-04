package com.reindecar.controller.vehicle;

import com.reindecar.common.dto.ApiResponse;
import com.reindecar.dto.vehicle.CreateCategoryRequest;
import com.reindecar.dto.vehicle.VehicleCategoryResponse;
import com.reindecar.service.vehicle.VehicleCategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/vehicle-categories")
@RequiredArgsConstructor
@Tag(name = "Vehicle Category", description = "Vehicle category management endpoints")
public class VehicleCategoryController {

    private final VehicleCategoryService categoryService;

    @GetMapping
    @Operation(summary = "Get all active categories", description = "Returns all active vehicle categories sorted by sort order")
    public ApiResponse<List<VehicleCategoryResponse>> getActiveCategories() {
        List<VehicleCategoryResponse> categories = categoryService.getActiveCategories();
        return ApiResponse.success(categories);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get category by ID", description = "Returns detailed information about a specific category")
    public ApiResponse<VehicleCategoryResponse> getCategoryById(@PathVariable Long id) {
        VehicleCategoryResponse category = categoryService.getCategoryById(id);
        return ApiResponse.success(category);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create category", description = "Creates a new vehicle category")
    public ApiResponse<VehicleCategoryResponse> createCategory(@Valid @RequestBody CreateCategoryRequest request) {
        VehicleCategoryResponse category = categoryService.createCategory(request);
        return ApiResponse.success("Category created successfully", category);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete category", description = "Deletes a vehicle category")
    public ApiResponse<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ApiResponse.success("Category deleted successfully", null);
    }
}
