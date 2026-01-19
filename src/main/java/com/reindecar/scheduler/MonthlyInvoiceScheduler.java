package com.reindecar.scheduler;

import com.reindecar.dto.leasing.GenerateInvoiceRequest;
import com.reindecar.entity.pricing.RentalType;
import com.reindecar.entity.rental.Rental;
import com.reindecar.repository.rental.RentalRepository;
import com.reindecar.service.leasing.LeasingInvoiceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class MonthlyInvoiceScheduler {

    private final LeasingInvoiceService invoiceService;
    private final RentalRepository rentalRepository;

    @Scheduled(cron = "0 0 2 1 * ?")
    public void generateMonthlyInvoices() {
        log.info("Starting monthly invoice generation...");
        
        YearMonth previousMonth = YearMonth.now().minusMonths(1);
        
        List<Rental> activeLeasings = rentalRepository.findByRentalTypeAndStatus(
            RentalType.LEASING, 
            com.reindecar.entity.rental.RentalStatus.ACTIVE
        );
        
        int successCount = 0;
        int errorCount = 0;
        
        for (Rental rental : activeLeasings) {
            try {
                GenerateInvoiceRequest request = new GenerateInvoiceRequest(
                    rental.getId(),
                    previousMonth.getYear(),
                    previousMonth.getMonthValue(),
                    null,
                    null
                );
                invoiceService.generateInvoice(request);
                successCount++;
            } catch (Exception e) {
                log.error("Failed to generate invoice for rental {}: {}", rental.getId(), e.getMessage());
                errorCount++;
            }
        }
        
        log.info("Monthly invoice generation completed. Success: {}, Errors: {}", successCount, errorCount);
    }

    @Scheduled(cron = "0 0 3 * * ?")
    public void checkOverdueInvoices() {
        log.info("Checking for overdue invoices...");
        invoiceService.updateOverdueInvoices();
    }
}
