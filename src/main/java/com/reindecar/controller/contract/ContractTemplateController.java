package com.reindecar.controller.contract;

import com.reindecar.dto.contract.ContractTemplateResponse;
import com.reindecar.dto.contract.ContractTermResponse;
import com.reindecar.dto.contract.CreateContractTemplateRequest;
import com.reindecar.dto.contract.CreateContractTermRequest;
import com.reindecar.entity.pricing.RentalType;
import com.reindecar.service.contract.ContractTemplateService;
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
@RequestMapping("/api/v1/contract-templates")
@RequiredArgsConstructor
@Tag(name = "Contract Template Management", description = "Contract template and term management")
public class ContractTemplateController {

    private final ContractTemplateService contractTemplateService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create contract template")
    public ResponseEntity<ContractTemplateResponse> create(@Valid @RequestBody CreateContractTemplateRequest request) {
        ContractTemplateResponse response = contractTemplateService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "Get all contract templates")
    public ResponseEntity<List<ContractTemplateResponse>> findAll() {
        return ResponseEntity.ok(contractTemplateService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get contract template by ID")
    public ResponseEntity<ContractTemplateResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(contractTemplateService.findById(id));
    }

    @GetMapping("/by-code/{code}")
    @Operation(summary = "Get contract template by code")
    public ResponseEntity<ContractTemplateResponse> findByCode(@PathVariable String code) {
        return ResponseEntity.ok(contractTemplateService.findByCode(code));
    }

    @GetMapping("/by-rental-type/{rentalType}")
    @Operation(summary = "Get latest template by rental type")
    public ResponseEntity<ContractTemplateResponse> findByRentalType(@PathVariable RentalType rentalType) {
        return ResponseEntity.ok(contractTemplateService.findLatestByRentalType(rentalType));
    }

    @PutMapping("/{id}/content")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update template content")
    public ResponseEntity<ContractTemplateResponse> updateContent(
            @PathVariable Long id,
            @RequestBody String content) {
        return ResponseEntity.ok(contractTemplateService.updateContent(id, content));
    }

    @PostMapping("/{id}/terms")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Add term to template")
    public ResponseEntity<ContractTermResponse> addTerm(
            @PathVariable Long id,
            @Valid @RequestBody CreateContractTermRequest request) {
        ContractTermResponse response = contractTemplateService.addTerm(id, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/{templateId}/terms/{termId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Remove term from template")
    public ResponseEntity<Void> removeTerm(@PathVariable Long templateId, @PathVariable Long termId) {
        contractTemplateService.removeTerm(templateId, termId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Activate template")
    public ResponseEntity<Void> activate(@PathVariable Long id) {
        contractTemplateService.activate(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Deactivate template")
    public ResponseEntity<Void> deactivate(@PathVariable Long id) {
        contractTemplateService.deactivate(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete template")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        contractTemplateService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
