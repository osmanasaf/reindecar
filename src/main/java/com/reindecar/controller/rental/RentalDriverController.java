package com.reindecar.controller.rental;

import com.reindecar.common.dto.ApiResponse;
import com.reindecar.dto.rental.AddDriverRequest;
import com.reindecar.dto.rental.RentalDriverResponse;
import com.reindecar.service.rental.RentalDriverService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/rentals/{rentalId}/drivers")
@RequiredArgsConstructor
@Tag(name = "Rental Driver", description = "Rental driver management endpoints")
public class RentalDriverController {

    private final RentalDriverService rentalDriverService;

    @GetMapping
    @Operation(summary = "Get rental drivers", description = "Returns all drivers assigned to a rental")
    public ApiResponse<List<RentalDriverResponse>> getDrivers(@PathVariable Long rentalId) {
        List<RentalDriverResponse> drivers = rentalDriverService.getDriversByRentalId(rentalId);
        return ApiResponse.success(drivers);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Add driver to rental", description = "Adds a driver to an existing rental")
    public ApiResponse<RentalDriverResponse> addDriver(
            @PathVariable Long rentalId,
            @Valid @RequestBody AddDriverRequest request,
            Authentication authentication) {
        String addedBy = authentication.getName();
        RentalDriverResponse driver = rentalDriverService.addDriverToRental(rentalId, request, addedBy);
        return ApiResponse.success("Driver added successfully", driver);
    }

    @DeleteMapping("/{driverId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Remove driver from rental", description = "Removes a driver from a rental")
    public ApiResponse<Void> removeDriver(
            @PathVariable Long rentalId,
            @PathVariable Long driverId) {
        rentalDriverService.removeDriverFromRental(rentalId, driverId);
        return ApiResponse.success("Driver removed successfully", null);
    }

    @PutMapping("/{driverId}/primary")
    @Operation(summary = "Set primary driver", description = "Sets a driver as the primary driver for the rental")
    public ApiResponse<RentalDriverResponse> setPrimaryDriver(
            @PathVariable Long rentalId,
            @PathVariable Long driverId) {
        RentalDriverResponse driver = rentalDriverService.setPrimaryDriver(rentalId, driverId);
        return ApiResponse.success("Primary driver updated", driver);
    }
}
