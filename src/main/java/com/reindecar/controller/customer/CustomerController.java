package com.reindecar.controller.customer;

import com.reindecar.common.dto.ApiResponse;
import com.reindecar.common.dto.PageResponse;
import com.reindecar.dto.customer.*;
import com.reindecar.entity.customer.CustomerType;
import com.reindecar.service.customer.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
@Tag(name = "Customer", description = "Customer management endpoints")
public class CustomerController {

    private final CustomerService customerService;

    @GetMapping
    @Operation(summary = "Get all customers", description = "Returns paginated list of all customers")
    public ApiResponse<PageResponse<CustomerResponse>> getAllCustomers(
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        PageResponse<CustomerResponse> customers = customerService.getAllCustomers(pageable);
        return ApiResponse.success(customers);
    }

    @GetMapping("/type/{type}")
    @Operation(summary = "Get customers by type", description = "Returns paginated list of customers by type (PERSONAL/COMPANY)")
    public ApiResponse<PageResponse<CustomerResponse>> getCustomersByType(
            @PathVariable CustomerType type,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        PageResponse<CustomerResponse> customers = customerService.getCustomersByType(type, pageable);
        return ApiResponse.success(customers);
    }

    @GetMapping("/blacklisted")
    @Operation(summary = "Get blacklisted customers", description = "Returns paginated list of blacklisted customers")
    public ApiResponse<PageResponse<CustomerResponse>> getBlacklistedCustomers(
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        PageResponse<CustomerResponse> customers = customerService.getBlacklistedCustomers(pageable);
        return ApiResponse.success(customers);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get customer by ID", description = "Returns detailed information about a specific customer")
    public ApiResponse<CustomerResponse> getCustomerById(@PathVariable Long id) {
        CustomerResponse customer = customerService.getCustomerById(id);
        return ApiResponse.success(customer);
    }

    @GetMapping("/public/{publicId}")
    @Operation(summary = "Get customer by public ID", description = "Returns customer by public UUID")
    public ApiResponse<CustomerResponse> getCustomerByPublicId(@PathVariable UUID publicId) {
        CustomerResponse customer = customerService.getCustomerByPublicId(publicId);
        return ApiResponse.success(customer);
    }

    @PostMapping("/personal")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create personal customer", description = "Creates a new personal (individual) customer")
    public ApiResponse<CustomerResponse> createPersonalCustomer(@Valid @RequestBody CreatePersonalCustomerRequest request) {
        CustomerResponse customer = customerService.createPersonalCustomer(request);
        return ApiResponse.success("Personal customer created successfully", customer);
    }

    @PostMapping("/company")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create company customer", description = "Creates a new company (corporate) customer")
    public ApiResponse<CustomerResponse> createCompanyCustomer(@Valid @RequestBody CreateCompanyCustomerRequest request) {
        CustomerResponse customer = customerService.createCompanyCustomer(request);
        return ApiResponse.success("Company customer created successfully", customer);
    }

    @PatchMapping("/{id}/blacklist")
    @Operation(summary = "Blacklist customer", description = "Add customer to blacklist")
    public ApiResponse<Void> blacklistCustomer(
            @PathVariable Long id,
            @Valid @RequestBody BlacklistRequest request) {
        customerService.blacklistCustomer(id, request);
        return ApiResponse.success("Customer blacklisted successfully", null);
    }

    @PatchMapping("/{id}/unblacklist")
    @Operation(summary = "Remove from blacklist", description = "Remove customer from blacklist")
    public ApiResponse<Void> removeFromBlacklist(@PathVariable Long id) {
        customerService.removeFromBlacklist(id);
        return ApiResponse.success("Customer removed from blacklist successfully", null);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete customer", description = "Soft deletes a customer")
    public ApiResponse<Void> deleteCustomer(@PathVariable Long id) {
        customerService.deleteCustomer(id);
        return ApiResponse.success("Customer deleted successfully", null);
    }
}
