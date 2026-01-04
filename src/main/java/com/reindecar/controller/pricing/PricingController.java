package com.reindecar.controller.pricing;

import com.reindecar.common.dto.ApiResponse;
import com.reindecar.dto.pricing.CalculatePriceRequest;
import com.reindecar.dto.pricing.PriceCalculationResponse;
import com.reindecar.service.pricing.PriceCalculationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/pricing")
@RequiredArgsConstructor
@Tag(name = "Pricing", description = "Price calculation endpoints")
public class PricingController {

    private final PriceCalculationService priceCalculationService;

    @PostMapping("/calculate")
    @Operation(summary = "Calculate price", description = "Calculates rental price using strategy pattern")
    public ApiResponse<PriceCalculationResponse> calculatePrice(@Valid @RequestBody CalculatePriceRequest request) {
        PriceCalculationResponse response = priceCalculationService.calculatePrice(request);
        return ApiResponse.success(response);
    }
}
