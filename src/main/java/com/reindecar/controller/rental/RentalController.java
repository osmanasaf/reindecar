package com.reindecar.controller.rental;

import com.reindecar.common.dto.ApiResponse;
import com.reindecar.common.dto.PageResponse;
import com.reindecar.dto.rental.*;
import com.reindecar.dto.vehicle.VehicleResponse;
import com.reindecar.service.rental.RentalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/rentals")
@RequiredArgsConstructor
@Tag(name = "Rental", description = "Rental management endpoints (CORE DOMAIN)")
public class RentalController {

    private final RentalService rentalService;

    @GetMapping
    @Operation(summary = "Get all rentals", description = "Returns paginated list of all rentals")
    public ApiResponse<PageResponse<RentalResponse>> getAllRentals(
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        PageResponse<RentalResponse> rentals = rentalService.getAllRentals(pageable);
        return ApiResponse.success(rentals);
    }

    @GetMapping("/active")
    @Operation(summary = "Get active rentals", description = "Returns active and overdue rentals")
    public ApiResponse<PageResponse<RentalResponse>> getActiveRentals(
            @PageableDefault(size = 20) Pageable pageable) {
        PageResponse<RentalResponse> rentals = rentalService.getActiveRentals(pageable);
        return ApiResponse.success(rentals);
    }

    @GetMapping("/overdue")
    @Operation(summary = "Get overdue rentals", description = "Returns rentals past their end date")
    public ApiResponse<PageResponse<RentalResponse>> getOverdueRentals(
            @PageableDefault(size = 20) Pageable pageable) {
        PageResponse<RentalResponse> rentals = rentalService.getOverdueRentals(pageable);
        return ApiResponse.success(rentals);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get rental by ID", description = "Returns detailed rental information")
    public ApiResponse<RentalResponse> getRentalById(@PathVariable Long id) {
        RentalResponse rental = rentalService.getRentalById(id);
        return ApiResponse.success(rental);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create rental", description = "Creates new rental (DRAFT status)")
    public ApiResponse<RentalResponse> createRental(
            @Valid @RequestBody CreateRentalRequest request,
            Authentication authentication) {
        String createdBy = authentication.getName();
        RentalResponse rental = rentalService.createRental(request, createdBy);
        return ApiResponse.success("Rental created successfully", rental);
    }

    @PostMapping("/{id}/reserve")
    @Operation(summary = "Reserve rental", description = "Changes status from DRAFT to RESERVED")
    public ApiResponse<RentalResponse> reserveRental(@PathVariable Long id) {
        RentalResponse rental = rentalService.reserveRental(id);
        return ApiResponse.success("Rental reserved successfully", rental);
    }

    @PostMapping("/{id}/activate")
    @Operation(summary = "Activate rental", description = "Changes status to ACTIVE (vehicle handover)")
    public ApiResponse<RentalResponse> activateRental(
            @PathVariable Long id,
            @Valid @RequestBody ActivateRentalRequest request) {
        RentalResponse rental = rentalService.activateRental(id, request);
        return ApiResponse.success("Rental activated successfully", rental);
    }

    @PostMapping("/{id}/start-return")
    @Operation(summary = "Start return", description = "Changes status to RETURN_PENDING")
    public ApiResponse<RentalResponse> startReturn(@PathVariable Long id) {
        RentalResponse rental = rentalService.startReturn(id);
        return ApiResponse.success("Return process started", rental);
    }

    @PostMapping("/{id}/complete")
    @Operation(summary = "Complete rental", description = "Completes rental and calculates final charges")
    public ApiResponse<RentalResponse> completeRental(
            @PathVariable Long id,
            @Valid @RequestBody CompleteRentalRequest request) {
        RentalResponse rental = rentalService.completeRental(id, request);
        return ApiResponse.success("Rental completed successfully", rental);
    }

    @PostMapping("/{id}/cancel")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Cancel rental", description = "Cancels rental (only DRAFT or RESERVED)")
    public ApiResponse<Void> cancelRental(@PathVariable Long id) {
        rentalService.cancelRental(id);
        return ApiResponse.success("Rental cancelled successfully", null);
    }

    @GetMapping("/customer/{customerId}")
    @Operation(summary = "Müşteri kiralamaları", description = "Belirtilen müşterinin tüm kiralamalarını döner")
    public ApiResponse<PageResponse<RentalResponse>> getRentalsByCustomer(
            @PathVariable Long customerId,
            @PageableDefault(size = 20, sort = "startDate", direction = Sort.Direction.DESC) Pageable pageable) {
        PageResponse<RentalResponse> rentals = rentalService.getRentalsByCustomerId(customerId, pageable);
        return ApiResponse.success(rentals);
    }

    @GetMapping("/customer/{customerId}/active")
    @Operation(summary = "Müşteri aktif kiralamaları", description = "Belirtilen müşterinin aktif kiralamalarını döner")
    public ApiResponse<List<RentalResponse>> getActiveRentalsByCustomer(@PathVariable Long customerId) {
        List<RentalResponse> rentals = rentalService.getActiveRentalsByCustomerId(customerId);
        return ApiResponse.success(rentals);
    }

    @GetMapping("/customer/{customerId}/vehicles")
    @Operation(summary = "Müşteriye verilen araçlar", description = "Müşteriye daha önce kiralanan araçları döner")
    public ApiResponse<List<VehicleResponse>> getVehiclesByCustomer(@PathVariable Long customerId) {
        List<VehicleResponse> vehicles = rentalService.getVehiclesByCustomerId(customerId);
        return ApiResponse.success(vehicles);
    }
}
