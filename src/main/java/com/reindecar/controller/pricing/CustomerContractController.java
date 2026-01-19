package com.reindecar.controller.pricing;

import com.reindecar.dto.pricing.CreateCustomerContractRequest;
import com.reindecar.dto.pricing.CustomerContractResponse;
import com.reindecar.service.pricing.CustomerContractService;
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
@RequestMapping("/api/v1/customer-contracts")
@RequiredArgsConstructor
@Tag(name = "Customer Contract Management", description = "Corporate customer contract management")
public class CustomerContractController {

    private final CustomerContractService customerContractService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create customer contract")
    public ResponseEntity<CustomerContractResponse> create(@Valid @RequestBody CreateCustomerContractRequest request) {
        CustomerContractResponse response = customerContractService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all customer contracts")
    public ResponseEntity<List<CustomerContractResponse>> findAll() {
        return ResponseEntity.ok(customerContractService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get customer contract by ID")
    public ResponseEntity<CustomerContractResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(customerContractService.findById(id));
    }

    @GetMapping("/by-number/{contractNumber}")
    @Operation(summary = "Get customer contract by contract number")
    public ResponseEntity<CustomerContractResponse> findByContractNumber(@PathVariable String contractNumber) {
        return ResponseEntity.ok(customerContractService.findByContractNumber(contractNumber));
    }

    @GetMapping("/customer/{customerId}")
    @Operation(summary = "Get contracts by customer ID")
    public ResponseEntity<List<CustomerContractResponse>> findByCustomerId(@PathVariable Long customerId) {
        return ResponseEntity.ok(customerContractService.findByCustomerId(customerId));
    }

    @GetMapping("/customer/{customerId}/active")
    @Operation(summary = "Get active contracts by customer ID")
    public ResponseEntity<List<CustomerContractResponse>> findActiveByCustomerId(@PathVariable Long customerId) {
        return ResponseEntity.ok(customerContractService.findActiveByCustomerId(customerId));
    }

    @PostMapping("/{id}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Activate contract")
    public ResponseEntity<Void> activate(@PathVariable Long id) {
        customerContractService.activate(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/suspend")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Suspend contract")
    public ResponseEntity<Void> suspend(@PathVariable Long id) {
        customerContractService.suspend(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/terminate")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Terminate contract")
    public ResponseEntity<Void> terminate(@PathVariable Long id) {
        customerContractService.terminate(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/complete")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Complete contract")
    public ResponseEntity<Void> complete(@PathVariable Long id) {
        customerContractService.complete(id);
        return ResponseEntity.ok().build();
    }
}
