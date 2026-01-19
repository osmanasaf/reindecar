package com.reindecar.controller.leasing;

import com.reindecar.dto.leasing.CreateMaintenanceScheduleRequest;
import com.reindecar.dto.leasing.MaintenanceScheduleResponse;
import com.reindecar.service.leasing.MaintenanceScheduleService;
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
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "Maintenance Schedule", description = "Leasing vehicle maintenance scheduling")
public class MaintenanceScheduleController {

    private final MaintenanceScheduleService scheduleService;

    @PostMapping("/leasing/{rentalId}/maintenance")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create maintenance schedule")
    public ResponseEntity<MaintenanceScheduleResponse> create(
            @PathVariable Long rentalId,
            @Valid @RequestBody CreateMaintenanceScheduleRequest request) {
        MaintenanceScheduleResponse response = scheduleService.create(rentalId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/leasing/{rentalId}/maintenance")
    @Operation(summary = "Get maintenance schedules for rental")
    public ResponseEntity<List<MaintenanceScheduleResponse>> getByRental(@PathVariable Long rentalId) {
        return ResponseEntity.ok(scheduleService.getByRental(rentalId));
    }

    @GetMapping("/maintenance-schedules/due")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get due maintenance schedules")
    public ResponseEntity<List<MaintenanceScheduleResponse>> getDue() {
        return ResponseEntity.ok(scheduleService.getDueSchedules());
    }

    @GetMapping("/maintenance-schedules/{id}")
    @Operation(summary = "Get schedule by ID")
    public ResponseEntity<MaintenanceScheduleResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(scheduleService.getById(id));
    }

    @PostMapping("/maintenance-schedules/{id}/complete")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Complete maintenance")
    public ResponseEntity<Void> complete(@PathVariable Long id, @RequestParam int completedAtKm) {
        scheduleService.complete(id, completedAtKm);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/maintenance-schedules/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Cancel schedule")
    public ResponseEntity<Void> cancel(@PathVariable Long id) {
        scheduleService.cancel(id);
        return ResponseEntity.noContent().build();
    }
}
