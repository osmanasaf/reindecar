package com.reindecar.controller.leasing;

import com.reindecar.dto.leasing.EarlyTerminationResponse;
import com.reindecar.dto.leasing.RequestTerminationRequest;
import com.reindecar.service.leasing.EarlyTerminationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "Early Termination", description = "Leasing early termination management")
public class EarlyTerminationController {

    private final EarlyTerminationService terminationService;

    @PostMapping("/leasing/{rentalId}/terminate")
    @Operation(summary = "Request early termination")
    public ResponseEntity<EarlyTerminationResponse> requestTermination(
            @PathVariable Long rentalId,
            @Valid @RequestBody RequestTerminationRequest request) {
        EarlyTerminationResponse response = terminationService.requestTermination(rentalId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/leasing/{rentalId}/termination-preview")
    @Operation(summary = "Preview termination penalty")
    public ResponseEntity<EarlyTerminationResponse> previewTermination(
            @PathVariable Long rentalId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate terminationDate) {
        return ResponseEntity.ok(terminationService.previewTermination(rentalId, terminationDate));
    }

    @GetMapping("/leasing/{rentalId}/terminations")
    @Operation(summary = "Get termination history for rental")
    public ResponseEntity<List<EarlyTerminationResponse>> getByRental(@PathVariable Long rentalId) {
        return ResponseEntity.ok(terminationService.getByRental(rentalId));
    }

    @GetMapping("/early-terminations/{id}")
    @Operation(summary = "Get termination by ID")
    public ResponseEntity<EarlyTerminationResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(terminationService.getById(id));
    }

    @GetMapping("/early-terminations/pending")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get pending approval requests")
    public ResponseEntity<List<EarlyTerminationResponse>> getPending() {
        return ResponseEntity.ok(terminationService.getPendingApprovals());
    }

    @PostMapping("/early-terminations/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Approve termination")
    public ResponseEntity<Void> approve(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        String approvedBy = userDetails != null ? userDetails.getUsername() : "admin";
        terminationService.approve(id, approvedBy);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/early-terminations/{id}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Reject termination")
    public ResponseEntity<Void> reject(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        String rejectedBy = userDetails != null ? userDetails.getUsername() : "admin";
        terminationService.reject(id, rejectedBy);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/early-terminations/{id}/complete")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Complete termination")
    public ResponseEntity<Void> complete(@PathVariable Long id) {
        terminationService.complete(id);
        return ResponseEntity.ok().build();
    }
}
