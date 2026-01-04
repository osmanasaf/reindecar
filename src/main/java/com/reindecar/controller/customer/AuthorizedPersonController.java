package com.reindecar.controller.customer;

import com.reindecar.common.dto.ApiResponse;
import com.reindecar.dto.customer.AuthorizedPersonResponse;
import com.reindecar.dto.customer.CreateAuthorizedPersonRequest;
import com.reindecar.dto.customer.UpdateAuthorizedPersonRequest;
import com.reindecar.service.customer.AuthorizedPersonService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/customers/companies/{companyId}/authorized-persons")
@RequiredArgsConstructor
@Tag(name = "Authorized Person", description = "Authorized person management for company customers")
public class AuthorizedPersonController {

    private final AuthorizedPersonService service;

    @GetMapping
    @Operation(summary = "Get all authorized persons", description = "Returns list of all authorized persons for a company")
    public ApiResponse<List<AuthorizedPersonResponse>> getAllByCompany(@PathVariable Long companyId) {
        List<AuthorizedPersonResponse> persons = service.getAllByCompany(companyId);
        return ApiResponse.success(persons);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get authorized person by ID", description = "Returns detailed information about a specific authorized person")
    public ApiResponse<AuthorizedPersonResponse> getById(@PathVariable Long id) {
        AuthorizedPersonResponse person = service.getById(id);
        return ApiResponse.success(person);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create authorized person", description = "Creates a new authorized person for the company")
    public ApiResponse<AuthorizedPersonResponse> create(
            @PathVariable Long companyId,
            @Valid @RequestBody CreateAuthorizedPersonRequest request) {
        AuthorizedPersonResponse person = service.createAuthorizedPerson(companyId, request);
        return ApiResponse.success("Authorized person created successfully", person);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update authorized person", description = "Updates an existing authorized person's information")
    public ApiResponse<AuthorizedPersonResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateAuthorizedPersonRequest request) {
        AuthorizedPersonResponse person = service.updateAuthorizedPerson(id, request);
        return ApiResponse.success("Authorized person updated successfully", person);
    }

    @PatchMapping("/{id}/primary")
    @Operation(summary = "Set as primary contact", description = "Sets this person as the primary contact for the company")
    public ApiResponse<Void> setAsPrimary(@PathVariable Long id) {
        service.setAsPrimary(id);
        return ApiResponse.success("Primary contact updated successfully", null);
    }

    @PatchMapping("/{id}/deactivate")
    @Operation(summary = "Deactivate authorized person", description = "Deactivates an authorized person")
    public ApiResponse<Void> deactivate(@PathVariable Long id) {
        service.deactivate(id);
        return ApiResponse.success("Authorized person deactivated successfully", null);
    }

    @PatchMapping("/{id}/activate")
    @Operation(summary = "Activate authorized person", description = "Activates an authorized person")
    public ApiResponse<Void> activate(@PathVariable Long id) {
        service.activate(id);
        return ApiResponse.success("Authorized person activated successfully", null);
    }
}
