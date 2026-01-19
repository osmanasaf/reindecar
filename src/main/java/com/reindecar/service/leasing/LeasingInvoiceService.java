package com.reindecar.service.leasing;

import com.reindecar.common.exception.BusinessException;
import com.reindecar.common.exception.ErrorCode;
import com.reindecar.common.valueobject.Money;
import com.reindecar.dto.leasing.GenerateInvoiceRequest;
import com.reindecar.dto.leasing.LeasingInvoiceResponse;
import com.reindecar.entity.leasing.LeasingInvoice;
import com.reindecar.entity.leasing.LeasingKmRecord;
import com.reindecar.entity.pricing.CustomerContract;
import com.reindecar.entity.rental.Rental;
import com.reindecar.repository.leasing.LeasingInvoiceRepository;
import com.reindecar.repository.leasing.LeasingKmRecordRepository;
import com.reindecar.repository.pricing.CustomerContractRepository;
import com.reindecar.repository.rental.RentalRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Year;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class LeasingInvoiceService {

    private static final String INVOICE_PREFIX = "INV-";
    private static final BigDecimal DEFAULT_EXCESS_KM_PRICE = new BigDecimal("0.50");

    private final LeasingInvoiceRepository invoiceRepository;
    private final RentalRepository rentalRepository;
    private final CustomerContractRepository contractRepository;
    private final LeasingKmRecordRepository kmRecordRepository;

    @Transactional
    public LeasingInvoiceResponse generateInvoice(GenerateInvoiceRequest request) {
        Rental rental = getRentalOrThrow(request.rentalId());
        
        YearMonth period = YearMonth.of(request.year(), request.month());
        LocalDate periodStart = period.atDay(1);
        LocalDate periodEnd = period.atEndOfMonth();
        
        if (invoiceRepository.existsByRentalIdAndPeriodStartAndPeriodEnd(
                request.rentalId(), periodStart, periodEnd)) {
            throw new BusinessException(ErrorCode.DUPLICATE_ENTITY, 
                "Invoice already exists for period: " + period);
        }
        
        Optional<CustomerContract> contractOpt = findActiveContract(rental.getCustomerId());
        Money monthlyRent = contractOpt
            .map(c -> c.getNegotiatedMonthlyPrice())
            .orElse(Money.of(new BigDecimal("5000"), Money.DEFAULT_CURRENCY));
        
        Optional<LeasingKmRecord> kmRecord = kmRecordRepository
            .findByRentalIdAndPeriodYearMonth(request.rentalId(), period.toString());
        
        int excessKm = kmRecord.map(LeasingKmRecord::getExcessKm).orElse(0);
        BigDecimal excessKmPrice = contractOpt
            .map(c -> c.getExtraKmPrice() != null ? c.getExtraKmPrice().getAmount() : DEFAULT_EXCESS_KM_PRICE)
            .orElse(DEFAULT_EXCESS_KM_PRICE);
        
        Money excessKmCharge = excessKm > 0 
            ? Money.of(excessKmPrice.multiply(BigDecimal.valueOf(excessKm)), monthlyRent.getCurrency())
            : null;
        
        Money additionalCharges = request.additionalCharges() != null 
            ? Money.of(request.additionalCharges(), monthlyRent.getCurrency())
            : null;
        
        String invoiceNumber = generateInvoiceNumber();
        
        LeasingInvoice invoice = LeasingInvoice.create(
            invoiceNumber,
            request.rentalId(),
            contractOpt.map(CustomerContract::getId).orElse(null),
            rental.getCustomerId(),
            periodStart,
            periodEnd,
            monthlyRent,
            excessKm,
            excessKmCharge,
            additionalCharges,
            request.additionalChargesNote()
        );
        
        LeasingInvoice saved = invoiceRepository.save(invoice);
        log.info("Invoice generated: {} for rental {}", invoiceNumber, request.rentalId());
        
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<LeasingInvoiceResponse> getInvoicesByRental(Long rentalId) {
        getRentalOrThrow(rentalId);
        return invoiceRepository.findByRentalIdOrderByPeriodStartDesc(rentalId)
            .stream()
            .map(this::toResponse)
            .toList();
    }

    @Transactional(readOnly = true)
    public List<LeasingInvoiceResponse> getInvoicesByCustomer(Long customerId) {
        return invoiceRepository.findByCustomerIdOrderByPeriodStartDesc(customerId)
            .stream()
            .map(this::toResponse)
            .toList();
    }

    @Transactional(readOnly = true)
    public LeasingInvoiceResponse getById(Long id) {
        return toResponse(getInvoiceOrThrow(id));
    }

    @Transactional
    public void markAsPaid(Long invoiceId) {
        LeasingInvoice invoice = getInvoiceOrThrow(invoiceId);
        invoice.markAsPaid();
        invoiceRepository.save(invoice);
        log.info("Invoice marked as paid: {}", invoice.getInvoiceNumber());
    }

    @Transactional
    public void sendInvoice(Long invoiceId) {
        LeasingInvoice invoice = getInvoiceOrThrow(invoiceId);
        invoice.send();
        invoiceRepository.save(invoice);
        log.info("Invoice sent: {}", invoice.getInvoiceNumber());
    }

    @Transactional
    public void cancelInvoice(Long invoiceId) {
        LeasingInvoice invoice = getInvoiceOrThrow(invoiceId);
        invoice.cancel();
        invoiceRepository.save(invoice);
        log.info("Invoice cancelled: {}", invoice.getInvoiceNumber());
    }

    @Transactional
    public void updateOverdueInvoices() {
        List<LeasingInvoice> overdueInvoices = invoiceRepository.findOverdueInvoices(LocalDate.now());
        for (LeasingInvoice invoice : overdueInvoices) {
            invoice.markAsOverdue();
            invoiceRepository.save(invoice);
            log.info("Invoice marked as overdue: {}", invoice.getInvoiceNumber());
        }
    }

    private Rental getRentalOrThrow(Long rentalId) {
        return rentalRepository.findById(rentalId)
            .orElseThrow(() -> new BusinessException(ErrorCode.RENTAL_NOT_FOUND, rentalId.toString()));
    }

    private LeasingInvoice getInvoiceOrThrow(Long invoiceId) {
        return invoiceRepository.findById(invoiceId)
            .orElseThrow(() -> new BusinessException(ErrorCode.INVOICE_NOT_FOUND, invoiceId.toString()));
    }

    private Optional<CustomerContract> findActiveContract(Long customerId) {
        return contractRepository.findActiveContractsByCustomer(customerId, LocalDate.now())
            .stream()
            .findFirst();
    }

    private String generateInvoiceNumber() {
        String prefix = INVOICE_PREFIX + Year.now().getValue() + "-";
        long count = invoiceRepository.count();
        return String.format("%s%06d", prefix, count + 1);
    }

    private LeasingInvoiceResponse toResponse(LeasingInvoice invoice) {
        return new LeasingInvoiceResponse(
            invoice.getId(),
            invoice.getInvoiceNumber(),
            invoice.getRentalId(),
            invoice.getContractId(),
            invoice.getCustomerId(),
            invoice.getPeriodStart(),
            invoice.getPeriodEnd(),
            invoice.getMonthlyRent().getAmount(),
            invoice.getExcessKm(),
            invoice.getExcessKmCharge() != null ? invoice.getExcessKmCharge().getAmount() : null,
            invoice.getAdditionalCharges() != null ? invoice.getAdditionalCharges().getAmount() : null,
            invoice.getAdditionalChargesNote(),
            invoice.getTotalAmount().getAmount(),
            invoice.getTotalAmount().getCurrency(),
            invoice.getStatus(),
            invoice.getDueDate(),
            invoice.getPaidAt(),
            invoice.getCreatedAt()
        );
    }
}
