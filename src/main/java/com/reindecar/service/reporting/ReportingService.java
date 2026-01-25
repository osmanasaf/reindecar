package com.reindecar.service.reporting;

import com.reindecar.dto.reporting.RevenueByMonthResponse;
import com.reindecar.dto.reporting.UpcomingReturnResponse;
import com.reindecar.entity.customer.CustomerStatus;
import com.reindecar.entity.payment.Payment;
import com.reindecar.entity.payment.PaymentStatus;
import com.reindecar.entity.rental.Rental;
import com.reindecar.entity.rental.RentalStatus;
import com.reindecar.entity.vehicle.VehicleStatus;
import com.reindecar.common.exception.BusinessException;
import com.reindecar.common.exception.ErrorCode;
import com.reindecar.repository.customer.CustomerRepository;
import com.reindecar.repository.payment.PaymentRepository;
import com.reindecar.repository.rental.RentalRepository;
import com.reindecar.repository.vehicle.VehicleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ReportingService {

    private final RentalRepository rentalRepository;
    private final VehicleRepository vehicleRepository;
    private final PaymentRepository paymentRepository;
    private final CustomerRepository customerRepository;

    public Map<String, Object> getDashboardStats() {
        log.info("Generating dashboard statistics");

        Map<String, Object> stats = new HashMap<>();

        stats.put("totalRentals", rentalRepository.count());
        stats.put("totalVehicles", vehicleRepository.countByDeletedFalse());
        stats.put("totalCustomers", customerRepository.countByDeletedFalse());
        stats.put("totalPayments", paymentRepository.count());

        Map<String, Object> rentalStats = new LinkedHashMap<>();
        for (RentalStatus status : RentalStatus.values()) {
            if (status != RentalStatus.ACTIVE) {
                rentalStats.put(status.name().toLowerCase(), rentalRepository.countByStatus(status));
            }
        }
        rentalStats.put("active", rentalRepository.countByStatus(RentalStatus.ACTIVE) + rentalRepository.countByStatus(RentalStatus.OVERDUE));
        stats.put("rentals", rentalStats);

        Map<String, Object> vehicleStats = new LinkedHashMap<>();
        for (VehicleStatus status : VehicleStatus.values()) {
            vehicleStats.put(status.name().toLowerCase(), vehicleRepository.countByStatusAndDeletedFalse(status));
        }
        stats.put("vehicles", vehicleStats);

        Map<String, Object> customerStats = new LinkedHashMap<>();
        for (CustomerStatus status : CustomerStatus.values()) {
            customerStats.put(status.name().toLowerCase(), customerRepository.countByStatusAndDeletedFalse(status));
        }
        customerStats.put("blacklisted", customerRepository.countByBlacklistedTrueAndDeletedFalse());
        stats.put("customers", customerStats);

        Map<String, Object> paymentStats = new LinkedHashMap<>();
        for (PaymentStatus status : PaymentStatus.values()) {
            paymentStats.put(status.name().toLowerCase(), paymentRepository.countByStatus(status));
        }
        Map<String, Object> revenueByCurrency = new LinkedHashMap<>();
        for (Object[] row : paymentRepository.sumCompletedAmountsByCurrency()) {
            revenueByCurrency.put(String.valueOf(row[0]), row[1]);
        }
        paymentStats.put("revenueByCurrency", revenueByCurrency);
        stats.put("payments", paymentStats);

        return stats;
    }

    public List<UpcomingReturnResponse> getUpcomingReturns(int days) {
        if (days <= 0) {
            throw new BusinessException(ErrorCode.INVALID_PARAMETER, "days");
        }

        LocalDate today = LocalDate.now();
        LocalDate endDate = today.plusDays(days);

        List<Rental> rentals = rentalRepository.findUpcomingReturns(today, endDate);
        return rentals.stream()
                .map(rental -> new UpcomingReturnResponse(
                        rental.getId(),
                        rental.getRentalNumber(),
                        rental.getVehicleId(),
                        rental.getCustomerId(),
                        rental.getEndDate(),
                        rental.getStatus(),
                        ChronoUnit.DAYS.between(today, rental.getEndDate())
                ))
                .toList();
    }

    public List<RevenueByMonthResponse> getRevenueByMonths(int months) {
        if (months <= 0) {
            throw new BusinessException(ErrorCode.INVALID_PARAMETER, "months");
        }

        YearMonth current = YearMonth.now();
        YearMonth startMonth = current.minusMonths(months - 1L);
        LocalDate startDate = startMonth.atDay(1);
        LocalDate endDate = current.atEndOfMonth();

        ZoneId zoneId = ZoneId.systemDefault();
        Instant startInstant = startDate.atStartOfDay(zoneId).toInstant();
        Instant endInstant = endDate.plusDays(1).atStartOfDay(zoneId).toInstant();

        List<Payment> payments = paymentRepository.findByStatusAndPaidAtBetween(
                PaymentStatus.COMPLETED, startInstant, endInstant);

        Map<YearMonth, Map<String, BigDecimal>> totalsByMonth = new LinkedHashMap<>();
        for (int i = 0; i < months; i++) {
            YearMonth month = startMonth.plusMonths(i);
            totalsByMonth.put(month, new LinkedHashMap<>());
        }

        for (Payment payment : payments) {
            YearMonth month = YearMonth.from(payment.getPaidAt().atZone(zoneId));
            Map<String, BigDecimal> totals = totalsByMonth.computeIfAbsent(month, key -> new LinkedHashMap<>());
            String currency = payment.getAmount().getCurrency();
            BigDecimal amount = payment.getAmount().getAmount();
            totals.merge(currency, amount, BigDecimal::add);
        }

        return totalsByMonth.entrySet().stream()
                .map(entry -> {
                    Map<String, BigDecimal> totals = entry.getValue();
                    BigDecimal total = totals.values().stream()
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    return new RevenueByMonthResponse(entry.getKey().toString(), total, totals);
                })
                .toList();
    }
}
