package com.reindecar.controller.leasing;

import com.reindecar.dto.leasing.VehicleSwapRequest;
import com.reindecar.dto.leasing.VehicleSwapResponse;
import com.reindecar.service.leasing.VehicleSwapService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/leasing")
@RequiredArgsConstructor
@Tag(name = "Vehicle Swap", description = "Leasing vehicle swap management")
public class VehicleSwapController {

    private final VehicleSwapService swapService;

    @PostMapping("/{rentalId}/swap")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Swap vehicle for rental")
    public ResponseEntity<VehicleSwapResponse> swap(
            @PathVariable Long rentalId,
            @Valid @RequestBody VehicleSwapRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        String processedBy = userDetails != null ? userDetails.getUsername() : "admin";
        VehicleSwapResponse response = swapService.swapVehicle(rentalId, request, processedBy);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{rentalId}/swap-history")
    @Operation(summary = "Get swap history for rental")
    public ResponseEntity<List<VehicleSwapResponse>> getHistory(@PathVariable Long rentalId) {
        return ResponseEntity.ok(swapService.getSwapHistory(rentalId));
    }

    @GetMapping("/swaps/{id}")
    @Operation(summary = "Get swap by ID")
    public ResponseEntity<VehicleSwapResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(swapService.getById(id));
    }
}
