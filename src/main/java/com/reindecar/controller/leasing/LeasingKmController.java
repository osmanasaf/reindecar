package com.reindecar.controller.leasing;

import com.reindecar.dto.leasing.KmRecordResponse;
import com.reindecar.dto.leasing.KmSummaryResponse;
import com.reindecar.dto.leasing.RecordKmRequest;
import com.reindecar.service.leasing.KmTrackingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/leasing")
@RequiredArgsConstructor
@Tag(name = "Leasing KM Tracking", description = "Kilometer tracking for leasing rentals")
public class LeasingKmController {

    private final KmTrackingService kmTrackingService;

    @PostMapping("/{rentalId}/km-records")
    @Operation(summary = "Record current KM reading")
    public ResponseEntity<KmRecordResponse> recordKm(
            @PathVariable Long rentalId,
            @Valid @RequestBody RecordKmRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        String recordedBy = userDetails != null ? userDetails.getUsername() : "system";
        KmRecordResponse response = kmTrackingService.recordKm(rentalId, request, recordedBy);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{rentalId}/km-records")
    @Operation(summary = "Get KM history for rental")
    public ResponseEntity<List<KmRecordResponse>> getKmHistory(@PathVariable Long rentalId) {
        return ResponseEntity.ok(kmTrackingService.getKmHistory(rentalId));
    }

    @GetMapping("/{rentalId}/km-summary")
    @Operation(summary = "Get KM summary for rental")
    public ResponseEntity<KmSummaryResponse> getKmSummary(@PathVariable Long rentalId) {
        return ResponseEntity.ok(kmTrackingService.getKmSummary(rentalId));
    }
}
