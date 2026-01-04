package com.reindecar.service.damage;

import com.reindecar.common.dto.PageResponse;
import com.reindecar.common.valueobject.Money;
import com.reindecar.entity.damage.DamageReport;
import com.reindecar.repository.damage.DamageReportRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class DamageService {

    private final DamageReportRepository damageRepository;

    @Transactional
    public DamageReport reportDamage(
            Long vehicleId,
            Long rentalId,
            LocalDate reportDate,
            String damageType,
            String description,
            BigDecimal estimatedCost,
            String reportedBy) {
        
        log.info("Reporting damage for vehicle: {}", vehicleId);

        Money costMoney = Money.of(estimatedCost, "TRY");

        DamageReport report = DamageReport.create(
            vehicleId,
            rentalId,
            reportDate,
            damageType,
            description,
            costMoney,
            reportedBy
        );

        return damageRepository.save(report);
    }

    public PageResponse<DamageReport> getDamageByVehicle(Long vehicleId, Pageable pageable) {
        Page<DamageReport> reports = damageRepository.findByVehicleIdOrderByReportDateDesc(vehicleId, pageable);
        return PageResponse.of(reports);
    }

    public PageResponse<DamageReport> getDamageByRental(Long rentalId, Pageable pageable) {
        Page<DamageReport> reports = damageRepository.findByRentalIdOrderByReportDateDesc(rentalId, pageable);
        return PageResponse.of(reports);
    }
}
