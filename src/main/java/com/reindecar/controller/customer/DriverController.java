package com.reindecar.controller.customer;

import com.reindecar.common.dto.ApiResponse;
import com.reindecar.common.dto.PageResponse;
import com.reindecar.dto.customer.CreateDriverRequest;
import com.reindecar.dto.customer.DriverResponse;
import com.reindecar.service.customer.DriverService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/v1/drivers")
@RequiredArgsConstructor
@Tag(name = "Driver", description = "Driver management endpoints")
public class DriverController {

    private final DriverService driverService;

    @GetMapping
    @Operation(summary = "Get drivers", description = "Returns drivers, optionally filtered by customer")
    public ApiResponse<PageResponse<DriverResponse>> getDrivers(
            @RequestParam(required = false) Long customerId,
            @RequestParam(required = false) Boolean active,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        PageResponse<DriverResponse> drivers = driverService.getDrivers(pageable, customerId, active);
        return ApiResponse.success(drivers);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get driver by ID", description = "Returns driver information")
    public ApiResponse<DriverResponse> getDriverById(@PathVariable Long id) {
        DriverResponse driver = driverService.getDriverById(id);
        return ApiResponse.success(driver);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create driver", description = "Creates a new driver")
    public ApiResponse<DriverResponse> createDriver(@Valid @RequestBody CreateDriverRequest request) {
        DriverResponse driver = driverService.createDriver(request);
        return ApiResponse.success("Driver created successfully", driver);
    }
}
