package com.reindecar.controller.pricing;

import com.reindecar.dto.pricing.*;
import com.reindecar.dto.pricing.UpdateLeasingPlanRequest; // Explicit import if * didn't cover recent addition (though * should cover it if in same package) -> actually * covers it if file exists. But explicit is safer if I am unsure about package. 
// Actually UpdateLeasingPlanRequest is in com.reindecar.dto.pricing. So import com.reindecar.dto.pricing.*; covers it.
// I will just rely on * unless it fails. 
// Wait, I created UpdateLeasingPlanRequest in the SAME package. So `import com.reindecar.dto.pricing.*;` is sufficient.
// However, I'll update it just to be sure I didn't break anything.
import com.reindecar.service.pricing.LeasingPlanService;
import com.reindecar.service.pricing.LeasingPriceCalculationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/leasing")
@RequiredArgsConstructor
@Tag(name = "Leasing", description = "Leasing plan management and price calculation")
public class LeasingController {

    private final LeasingPlanService leasingPlanService;
    private final LeasingPriceCalculationService priceCalculationService;

    @PostMapping("/calculate")
    @Operation(summary = "Calculate leasing price", description = "Calculate total leasing price with breakdown")
    public ResponseEntity<LeasingPriceCalculationResponse> calculatePrice(
            @Valid @RequestBody CalculateLeasingPriceRequest request) {
        return ResponseEntity.ok(priceCalculationService.calculateLeasingPrice(request));
    }

    @GetMapping("/plans")
    @Operation(summary = "Get all leasing plans")
    public ResponseEntity<List<LeasingPlanResponse>> getAllPlans() {
        return ResponseEntity.ok(leasingPlanService.getAllPlans());
    }

    @GetMapping("/plans/category/{categoryId}")
    @Operation(summary = "Get leasing plans by category")
    public ResponseEntity<List<LeasingPlanResponse>> getPlansByCategory(@PathVariable Long categoryId) {
        return ResponseEntity.ok(leasingPlanService.getPlansByCategory(categoryId));
    }

    @GetMapping("/plans/{id}")
    @Operation(summary = "Get leasing plan by ID")
    public ResponseEntity<LeasingPlanResponse> getPlanById(@PathVariable Long id) {
        return ResponseEntity.ok(leasingPlanService.getPlanById(id));
    }

    @PostMapping("/plans")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Create leasing plan")
    public ResponseEntity<LeasingPlanResponse> createPlan(
            @Valid @RequestBody CreateLeasingPlanRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(leasingPlanService.createPlan(request));
    }

    @PutMapping("/plans/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Update leasing plan")
    public ResponseEntity<LeasingPlanResponse> updatePlan(
            @PathVariable Long id,
            @Valid @RequestBody UpdateLeasingPlanRequest request) {
        return ResponseEntity.ok(leasingPlanService.updatePlan(id, request));
    }

    @PatchMapping("/plans/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Partially update leasing plan")
    public ResponseEntity<LeasingPlanResponse> patchPlan(
            @PathVariable Long id,
            @Valid @RequestBody UpdateLeasingPlanRequest request) {
        return ResponseEntity.ok(leasingPlanService.updatePlan(id, request));
    }

    @PostMapping("/plans/{id}/activate")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Activate leasing plan")
    public ResponseEntity<Void> activatePlan(@PathVariable Long id) {
        leasingPlanService.activatePlan(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/plans/{id}/deactivate")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Deactivate leasing plan")
    public ResponseEntity<Void> deactivatePlan(@PathVariable Long id) {
        leasingPlanService.deactivatePlan(id);
        return ResponseEntity.ok().build();
    }
}
