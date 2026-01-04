package com.reindecar.service.reporting;

import com.reindecar.repository.rental.RentalRepository;
import com.reindecar.repository.vehicle.VehicleRepository;
import com.reindecar.repository.payment.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ReportingService {

    private final RentalRepository rentalRepository;
    private final VehicleRepository vehicleRepository;
    private final PaymentRepository paymentRepository;

    public Map<String, Object> getDashboardStats() {
        log.info("Generating dashboard statistics");

        Map<String, Object> stats = new HashMap<>();
        
        stats.put("totalRentals", rentalRepository.count());
        stats.put("totalVehicles", vehicleRepository.count());
        stats.put("totalPayments", paymentRepository.count());
        
        return stats;
    }
}
