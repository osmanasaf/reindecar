package com.reindecar.controller.contract;

import com.reindecar.common.dto.ApiResponse;
import com.reindecar.dto.contract.CreateContractRequest;
import com.reindecar.dto.contract.SignContractRequest;
import com.reindecar.dto.contract.ContractResponse;
import com.reindecar.entity.contract.Contract;
import com.reindecar.service.contract.ContractService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/contracts")
@RequiredArgsConstructor
@Tag(name = "Contract", description = "Contract management endpoints")
public class ContractController {

    private final ContractService contractService;

    @GetMapping("/{id}")
    @Operation(summary = "Get contract by ID", description = "Returns contract details")
    public ApiResponse<ContractResponse> getContractById(@PathVariable Long id) {
        Contract contract = contractService.getContractById(id);
        return ApiResponse.success(toResponse(contract));
    }

    @GetMapping("/rental/{rentalId}")
    @Operation(summary = "Get contract by rental ID", description = "Returns contract for specific rental")
    public ApiResponse<ContractResponse> getContractByRentalId(@PathVariable Long rentalId) {
        Contract contract = contractService.getContractByRentalId(rentalId);
        return ApiResponse.success(toResponse(contract));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create contract", description = "Creates contract for rental")
    public ApiResponse<ContractResponse> createContract(@Valid @RequestBody CreateContractRequest request) {
        Contract contract = contractService.createContract(request);
        return ApiResponse.success("Contract created successfully", toResponse(contract));
    }

    @PostMapping("/{id}/sign")
    @Operation(summary = "Sign contract", description = "Signs the contract")
    public ApiResponse<ContractResponse> signContract(
            @PathVariable Long id,
            @Valid @RequestBody SignContractRequest request) {
        Contract contract = contractService.signContract(id, request);
        return ApiResponse.success("Contract signed successfully", toResponse(contract));
    }

    @PostMapping("/{id}/cancel")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Cancel contract", description = "Cancels the contract")
    public ApiResponse<Void> cancelContract(@PathVariable Long id) {
        contractService.cancelContract(id);
        return ApiResponse.success("Contract cancelled successfully", null);
    }

    private ContractResponse toResponse(Contract contract) {
        return new ContractResponse(
            contract.getId(),
            contract.getContractNumber(),
            contract.getRentalId(),
            contract.getTemplateId(),
            contract.getContractVersion(),
            contract.getStatus(),
            contract.getValidFrom(),
            contract.getValidTo(),
            contract.getSignedAt(),
            contract.getSignedBy(),
            contract.getSignatureMethod(),
            contract.isExpired(),
            contract.isSigned(),
            contract.getCreatedAt()
        );
    }
}
