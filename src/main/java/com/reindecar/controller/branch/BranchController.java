package com.reindecar.controller.branch;

import com.reindecar.dto.branch.BranchResponse;
import com.reindecar.dto.branch.BranchSummaryResponse;
import com.reindecar.dto.branch.CreateBranchRequest;
import com.reindecar.dto.branch.UpdateBranchRequest;
import com.reindecar.service.branch.BranchService;
import com.reindecar.common.dto.ApiResponse;
import com.reindecar.common.dto.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/branches")
@RequiredArgsConstructor
@Tag(name = "Branch", description = "Branch management endpoints")
public class BranchController {

    private final BranchService branchService;

    @GetMapping
    @Operation(summary = "Get all branches", description = "Returns paginated list of all branches")
    public ApiResponse<PageResponse<BranchSummaryResponse>> getAllBranches(
            @Parameter(description = "Pagination and sorting parameters. Valid sort fields: name, code, city, district, createdAt", 
                       example = "page=0&size=20&sort=name,asc")
            @PageableDefault(size = 20, sort = "name", direction = Sort.Direction.ASC) Pageable pageable) {
        PageResponse<BranchSummaryResponse> branches = branchService.getAllBranches(pageable);
        return ApiResponse.success(branches);
    }

    @GetMapping("/active")
    @Operation(summary = "Get active branches", description = "Returns paginated list of active branches only")
    public ApiResponse<PageResponse<BranchSummaryResponse>> getActiveBranches(
            @Parameter(description = "Pagination and sorting parameters. Valid sort fields: name, code, city, district, createdAt", 
                       example = "page=0&size=20&sort=name,asc")
            @PageableDefault(size = 20, sort = "name", direction = Sort.Direction.ASC) Pageable pageable) {
        PageResponse<BranchSummaryResponse> branches = branchService.getActiveBranches(pageable);
        return ApiResponse.success(branches);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get branch by ID", description = "Returns detailed information about a specific branch")
    public ApiResponse<BranchResponse> getBranchById(@PathVariable Long id) {
        BranchResponse branch = branchService.getBranchById(id);
        return ApiResponse.success(branch);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create new branch", description = "Creates a new branch with the provided information")
    public ApiResponse<BranchResponse> createBranch(@Valid @RequestBody CreateBranchRequest request) {
        BranchResponse branch = branchService.createBranch(request);
        return ApiResponse.success("Branch created successfully", branch);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update branch", description = "Updates an existing branch's information")
    public ApiResponse<BranchResponse> updateBranch(
            @PathVariable Long id,
            @Valid @RequestBody UpdateBranchRequest request) {
        BranchResponse branch = branchService.updateBranch(id, request);
        return ApiResponse.success("Branch updated successfully", branch);
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Toggle branch status", description = "Activates or deactivates a branch")
    public ApiResponse<Void> toggleBranchStatus(@PathVariable Long id) {
        branchService.toggleBranchStatus(id);
        return ApiResponse.success("Branch status updated successfully", null);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete branch", description = "Soft deletes a branch")
    public ApiResponse<Void> deleteBranch(@PathVariable Long id) {
        branchService.deleteBranch(id);
        return ApiResponse.success("Branch deleted successfully", null);
    }
}
