package com.reindecar.controller.pricing;

import com.reindecar.dto.pricing.CreateSeasonRequest;
import com.reindecar.dto.pricing.SeasonResponse;
import com.reindecar.dto.pricing.UpdateSeasonRequest;
import com.reindecar.service.pricing.SeasonService;
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
@RequestMapping("/api/v1/seasons")
@RequiredArgsConstructor
@Tag(name = "Season Management", description = "Season pricing multiplier management")
public class SeasonController {

    private final SeasonService seasonService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create season")
    public ResponseEntity<SeasonResponse> create(@Valid @RequestBody CreateSeasonRequest request) {
        SeasonResponse response = seasonService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "Get all seasons")
    public ResponseEntity<List<SeasonResponse>> findAll() {
        return ResponseEntity.ok(seasonService.findAll());
    }

    @GetMapping("/active")
    @Operation(summary = "Get active seasons")
    public ResponseEntity<List<SeasonResponse>> findActive() {
        return ResponseEntity.ok(seasonService.findActive());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get season by ID")
    public ResponseEntity<SeasonResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(seasonService.findById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update season")
    public ResponseEntity<SeasonResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateSeasonRequest request) {
        return ResponseEntity.ok(seasonService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete season")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        seasonService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Activate season")
    public ResponseEntity<Void> activate(@PathVariable Long id) {
        seasonService.activate(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Deactivate season")
    public ResponseEntity<Void> deactivate(@PathVariable Long id) {
        seasonService.deactivate(id);
        return ResponseEntity.ok().build();
    }
}
