package com.reindecar.controller.payment;

import com.reindecar.common.dto.ApiResponse;
import com.reindecar.common.dto.PageResponse;
import com.reindecar.dto.payment.PaymentResponse;
import com.reindecar.dto.payment.RecordPaymentRequest;
import com.reindecar.entity.payment.Payment;
import com.reindecar.service.payment.PaymentService;
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
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
@Tag(name = "Payment", description = "Payment management endpoints")
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping
    @Operation(summary = "Get all payments", description = "Returns paginated list of all payments")
    public ApiResponse<PageResponse<PaymentResponse>> getAllPayments(
            @PageableDefault(size = 20, sort = "paidAt", direction = Sort.Direction.DESC) Pageable pageable) {
        PageResponse<PaymentResponse> payments = paymentService.getAllPayments(pageable);
        return ApiResponse.success(payments);
    }

    @GetMapping("/rental/{rentalId}")
    @Operation(summary = "Get payments by rental", description = "Returns all payments for specific rental")
    public ApiResponse<List<PaymentResponse>> getPaymentsByRentalId(@PathVariable Long rentalId) {
        List<Payment> payments = paymentService.getPaymentsByRentalId(rentalId);
        List<PaymentResponse> responses = payments.stream()
            .map(p -> new PaymentResponse(
                p.getId(),
                p.getRentalId(),
                p.getAmount().getAmount(),
                p.getAmount().getCurrency(),
                p.getMethod(),
                p.getStatus(),
                p.getTransactionRef(),
                p.getInvoiceRef(),
                p.getPaidAt(),
                p.getCreatedBy()
            ))
            .collect(Collectors.toList());
        return ApiResponse.success(responses);
    }

    @PostMapping("/rental/{rentalId}")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Record payment", description = "Records a payment for rental")
    public ApiResponse<PaymentResponse> recordPayment(
            @PathVariable Long rentalId,
            @Valid @RequestBody RecordPaymentRequest request,
            Authentication authentication) {
        String createdBy = authentication.getName();
        Payment payment = paymentService.recordPayment(rentalId, request, createdBy);
        
        PaymentResponse response = new PaymentResponse(
            payment.getId(),
            payment.getRentalId(),
            payment.getAmount().getAmount(),
            payment.getAmount().getCurrency(),
            payment.getMethod(),
            payment.getStatus(),
            payment.getTransactionRef(),
            payment.getInvoiceRef(),
            payment.getPaidAt(),
            payment.getCreatedBy()
        );
        
        return ApiResponse.success("Payment recorded successfully", response);
    }
}
