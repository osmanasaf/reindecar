package com.reindecar.service.rental;

import com.reindecar.common.valueobject.Money;
import com.reindecar.dto.rental.RentalInvoiceResponse;
import com.reindecar.entity.pricing.KmBundle;
import com.reindecar.entity.pricing.RentalPricing;
import com.reindecar.entity.rental.Rental;
import com.reindecar.entity.rental.RentalInvoice;
import com.reindecar.entity.rental.RentalInvoiceItem;
import com.reindecar.repository.pricing.KmBundleRepository;
import com.reindecar.repository.pricing.RentalPricingRepository;
import com.reindecar.repository.rental.RentalInvoiceRepository;
import com.reindecar.repository.rental.RentalRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class RentalInvoiceService {

    private final RentalRepository rentalRepository;
    private final RentalInvoiceRepository invoiceRepository;
    private final RentalPricingRepository pricingRepository;
    private final KmBundleRepository kmBundleRepository;

    private static final AtomicLong invoiceCounter = new AtomicLong(1000);
    private static final BigDecimal DEFAULT_TAX_RATE = new BigDecimal("20");

    @Transactional
    public RentalInvoiceResponse generateInvoice(Long rentalId) {
        Rental rental = rentalRepository.findById(rentalId)
            .orElseThrow(() -> new IllegalArgumentException("Rental not found"));

        Optional<RentalInvoice> existing = invoiceRepository.findByRentalId(rentalId);
        if (existing.isPresent()) {
            return toResponse(existing.get());
        }

        String invoiceNumber = generateInvoiceNumber();
        RentalInvoice invoice = RentalInvoice.create(rentalId, invoiceNumber, DEFAULT_TAX_RATE);

        Money baseRentalAmount = calculateBaseRentalAmount(rental);
        invoice.setBaseRentalAmount(baseRentalAmount);
        invoice.addItem(
            "Temel Kira Bedeli (" + calculateRentalDays(rental) + " gün)",
            1,
            baseRentalAmount
        );

        if (rental.getEndKm() > 0 && rental.getStartKm() > 0) {
            int totalKm = rental.getEndKm() - rental.getStartKm();
            Money extraKmCost = calculateExtraKmCost(rental, totalKm);
            
            if (extraKmCost.getAmount().compareTo(BigDecimal.ZERO) > 0) {
                invoice.setExtraKmAmount(extraKmCost);
                invoice.addItem("Ekstra Km Ücreti", 1, extraKmCost);
            }
        }

        if (rental.getDiscountAmount() != null) {
            invoice.setDiscountAmount(rental.getDiscountAmount());
        }

        invoice.calculateTotals();

        RentalInvoice saved = invoiceRepository.save(invoice);
        log.info("Invoice generated: {} for rental {}", invoiceNumber, rentalId);

        return toResponse(saved);
    }

    @Transactional
    public RentalInvoiceResponse finalizeInvoice(Long invoiceId) {
        RentalInvoice invoice = invoiceRepository.findById(invoiceId)
            .orElseThrow(() -> new IllegalArgumentException("Invoice not found"));

        invoice.finalizeInvoice();
        invoiceRepository.save(invoice);

        return toResponse(invoice);
    }

    public RentalInvoiceResponse getByRentalId(Long rentalId) {
        RentalInvoice invoice = invoiceRepository.findByRentalId(rentalId)
            .orElseThrow(() -> new IllegalArgumentException("Invoice not found for rental"));
        return toResponse(invoice);
    }

    private Money calculateBaseRentalAmount(Rental rental) {
        long days = calculateRentalDays(rental);
        Money dailyPrice = rental.getDailyPrice();
        return dailyPrice.multiply((int) days);
    }

    private long calculateRentalDays(Rental rental) {
        LocalDate start = rental.getStartDate();
        LocalDate end = rental.getActualReturnDate() != null 
            ? rental.getActualReturnDate() 
            : rental.getEndDate();
        return ChronoUnit.DAYS.between(start, end) + 1;
    }

    private Money calculateExtraKmCost(Rental rental, int totalKm) {
        if (rental.getKmPackageId() != null) {
            Optional<KmBundle> bundle = kmBundleRepository.findById(rental.getKmPackageId());
            if (bundle.isPresent()) {
                return bundle.get().calculateExtraKmCost(totalKm);
            }
        }

        Optional<RentalPricing> pricing = pricingRepository
            .findByVehicleIdAndType(rental.getVehicleId(), rental.getRentalType());
        
        if (pricing.isPresent()) {
            return pricing.get().calculateExtraKmCost(totalKm);
        }

        return Money.zero("TRY");
    }

    private String generateInvoiceNumber() {
        int year = LocalDate.now().getYear();
        long counter = invoiceCounter.getAndIncrement();
        return String.format("INV-%d-%05d", year, counter);
    }

    private RentalInvoiceResponse toResponse(RentalInvoice inv) {
        List<com.reindecar.dto.rental.InvoiceItemResponse> items = inv.getItems().stream()
            .map(item -> new com.reindecar.dto.rental.InvoiceItemResponse(
                item.getDescription(),
                item.getQuantity(),
                item.getUnitPrice().getAmount(),
                item.getTotalPrice().getAmount()
            ))
            .collect(Collectors.toList());

        return new RentalInvoiceResponse(
            inv.getId(),
            inv.getRentalId(),
            inv.getInvoiceNumber(),
            inv.getInvoiceDate(),
            inv.getStatus(),
            inv.getBaseRentalAmount().getAmount(),
            inv.getExtraKmAmount().getAmount(),
            inv.getDiscountAmount() != null ? inv.getDiscountAmount().getAmount() : BigDecimal.ZERO,
            inv.getTotalAmount().getAmount(),
            inv.getTaxRate(),
            inv.getTaxAmount().getAmount(),
            inv.getGrandTotal().getAmount(),
            inv.getGrandTotal().getCurrency(),
            items,
            inv.getNotes()
        );
    }
}
