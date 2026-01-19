package com.reindecar.controller.leasing;

import com.reindecar.dto.leasing.GenerateInvoiceRequest;
import com.reindecar.dto.leasing.LeasingInvoiceResponse;
import com.reindecar.service.leasing.LeasingInvoiceService;
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
@RequestMapping("/api/v1/leasing-invoices")
@RequiredArgsConstructor
@Tag(name = "Leasing Invoices", description = "Monthly leasing invoice management")
public class LeasingInvoiceController {

    private final LeasingInvoiceService invoiceService;

    @PostMapping("/generate")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Generate monthly invoice")
    public ResponseEntity<LeasingInvoiceResponse> generate(@Valid @RequestBody GenerateInvoiceRequest request) {
        LeasingInvoiceResponse response = invoiceService.generateInvoice(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get invoice by ID")
    public ResponseEntity<LeasingInvoiceResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(invoiceService.getById(id));
    }

    @GetMapping("/rental/{rentalId}")
    @Operation(summary = "Get invoices by rental")
    public ResponseEntity<List<LeasingInvoiceResponse>> getByRental(@PathVariable Long rentalId) {
        return ResponseEntity.ok(invoiceService.getInvoicesByRental(rentalId));
    }

    @GetMapping("/customer/{customerId}")
    @Operation(summary = "Get invoices by customer")
    public ResponseEntity<List<LeasingInvoiceResponse>> getByCustomer(@PathVariable Long customerId) {
        return ResponseEntity.ok(invoiceService.getInvoicesByCustomer(customerId));
    }

    @PostMapping("/{id}/send")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Send invoice")
    public ResponseEntity<Void> send(@PathVariable Long id) {
        invoiceService.sendInvoice(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/mark-paid")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Mark invoice as paid")
    public ResponseEntity<Void> markAsPaid(@PathVariable Long id) {
        invoiceService.markAsPaid(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/cancel")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Cancel invoice")
    public ResponseEntity<Void> cancel(@PathVariable Long id) {
        invoiceService.cancelInvoice(id);
        return ResponseEntity.ok().build();
    }
}
