package com.reindecar.controller.pricing;

import com.reindecar.dto.pricing.CampaignResponse;
import com.reindecar.dto.pricing.CreateCampaignRequest;
import com.reindecar.service.pricing.CampaignService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/campaigns")
@RequiredArgsConstructor
@Tag(name = "Campaign Management", description = "Discount campaign management")
public class CampaignController {

    private final CampaignService campaignService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create campaign")
    public ResponseEntity<CampaignResponse> create(@Valid @RequestBody CreateCampaignRequest request) {
        CampaignResponse response = campaignService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "Get all campaigns")
    public ResponseEntity<List<CampaignResponse>> findAll() {
        return ResponseEntity.ok(campaignService.findAll());
    }

    @GetMapping("/active")
    @Operation(summary = "Get active campaigns")
    public ResponseEntity<List<CampaignResponse>> findActive() {
        return ResponseEntity.ok(campaignService.findActive());
    }

    @GetMapping("/active/today")
    @Operation(summary = "Get campaigns active today")
    public ResponseEntity<List<CampaignResponse>> findActiveToday() {
        return ResponseEntity.ok(campaignService.findActiveCampaignsForDate(LocalDate.now()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get campaign by ID")
    public ResponseEntity<CampaignResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(campaignService.findById(id));
    }

    @PostMapping("/{id}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Activate campaign")
    public ResponseEntity<Void> activate(@PathVariable Long id) {
        campaignService.activate(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Deactivate campaign")
    public ResponseEntity<Void> deactivate(@PathVariable Long id) {
        campaignService.deactivate(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete campaign")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        campaignService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
