package com.reindecar.controller.pricing;

import com.reindecar.common.dto.ApiResponse;
import com.reindecar.dto.pricing.CreateRentalPricingRequest;
import com.reindecar.dto.pricing.RentalPricingResponse;
import com.reindecar.entity.pricing.RentalType;
import com.reindecar.service.pricing.RentalPricingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/rental-pricing")
@RequiredArgsConstructor
@Tag(name = "Rental Pricing", description = "Vehicle/Customer/Category specific pricing")
public class RentalPricingController {

    private final RentalPricingService pricingService;

    @GetMapping("/find")
    @Operation(summary = "Find applicable pricing", description = "Finds pricing by priority: Vehicle > Customer > Category")
    public ApiResponse<RentalPricingResponse> findPricing(
            @RequestParam Long vehicleId,
            @RequestParam(required = false) Long customerId,
            @RequestParam RentalType rentalType) {
        
        return pricingService.findApplicablePricing(vehicleId, customerId, rentalType)
            .map(ApiResponse::success)
            .orElse(ApiResponse.error("No pricing found for this combination"));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create pricing", description = "Creates vehicle/customer/category specific pricing")
    public ApiResponse<RentalPricingResponse> createPricing(
            @Valid @RequestBody CreateRentalPricingRequest request) {
        RentalPricingResponse response = pricingService.createPricing(request);
        return ApiResponse.success("Pricing created successfully", response);
    }

    @GetMapping("/vehicle/{vehicleId}")
    @Operation(summary = "Get vehicle pricing", description = "Returns all pricing for vehicle")
    public ApiResponse<List<RentalPricingResponse>> getByVehicle(@PathVariable Long vehicleId) {
        List<RentalPricingResponse> pricing = pricingService.getByVehicleId(vehicleId);
        return ApiResponse.success(pricing);
    }

    @GetMapping("/customer/{customerId}")
    @Operation(summary = "Get customer pricing", description = "Returns all pricing for customer")
    public ApiResponse<List<RentalPricingResponse>> getByCustomer(@PathVariable Long customerId) {
        List<RentalPricingResponse> pricing = pricingService.getByCustomerId(customerId);
        return ApiResponse.success(pricing);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Deactivate pricing", description = "Deactivates pricing")
    public ApiResponse<Void> deactivate(@PathVariable Long id) {
        pricingService.deactivate(id);
        return ApiResponse.success("Pricing deactivated", null);
    }
}
