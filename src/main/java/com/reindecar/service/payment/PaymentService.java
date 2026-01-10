package com.reindecar.service.payment;

import com.reindecar.common.dto.PageResponse;
import com.reindecar.common.valueobject.Money;
import com.reindecar.dto.payment.PaymentResponse;
import com.reindecar.dto.payment.RecordPaymentRequest;
import com.reindecar.entity.payment.Payment;
import com.reindecar.repository.payment.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class PaymentService {

    private final PaymentRepository paymentRepository;

    @Transactional
    public Payment recordPayment(Long rentalId, RecordPaymentRequest request, String createdBy) {
        log.info("Recording payment for rental: {}", rentalId);

        Money amount = Money.of(request.amount(), Money.DEFAULT_CURRENCY);

        Payment payment = Payment.create(
            rentalId,
            amount,
            request.method(),
            request.transactionRef(),
            request.invoiceRef(),
            request.notes(),
            createdBy
        );

        return paymentRepository.save(payment);
    }

    public List<Payment> getPaymentsByRentalId(Long rentalId) {
        return paymentRepository.findByRentalIdOrderByPaidAtDesc(rentalId);
    }

    public PageResponse<PaymentResponse> getAllPayments(Pageable pageable) {
        Page<Payment> payments = paymentRepository.findAllOrderByPaidAtDesc(pageable);
        return PageResponse.of(payments.map(this::toResponse));
    }

    private PaymentResponse toResponse(Payment payment) {
        return new PaymentResponse(
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
    }
}
