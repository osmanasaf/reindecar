package com.reindecar.service.leasing;

import com.reindecar.common.exception.BusinessException;
import com.reindecar.common.exception.ErrorCode;
import com.reindecar.dto.leasing.KmRecordResponse;
import com.reindecar.dto.leasing.KmSummaryResponse;
import com.reindecar.dto.leasing.RecordKmRequest;
import com.reindecar.entity.leasing.LeasingKmRecord;
import com.reindecar.entity.pricing.CustomerContract;
import com.reindecar.entity.rental.Rental;
import com.reindecar.repository.leasing.LeasingKmRecordRepository;
import com.reindecar.repository.pricing.CustomerContractRepository;
import com.reindecar.repository.rental.RentalRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class KmTrackingService {

    private final LeasingKmRecordRepository kmRecordRepository;
    private final RentalRepository rentalRepository;
    private final CustomerContractRepository contractRepository;

    @Transactional
    public KmRecordResponse recordKm(Long rentalId, RecordKmRequest request, String recordedBy) {
        Rental rental = getRentalOrThrow(rentalId);
        
        LocalDate recordDate = request.recordDate() != null ? request.recordDate() : LocalDate.now();
        String periodYearMonth = YearMonth.from(recordDate).toString();
        
        if (kmRecordRepository.existsByRentalIdAndPeriodYearMonth(rentalId, periodYearMonth)) {
            throw new BusinessException(ErrorCode.DUPLICATE_ENTITY, 
                "KM record already exists for period: " + periodYearMonth);
        }
        
        Optional<LeasingKmRecord> lastRecord = kmRecordRepository.findTopByRentalIdOrderByRecordDateDesc(rentalId);
        int previousKm = lastRecord.map(LeasingKmRecord::getCurrentKm).orElse(0);
        int rolloverFromPrevious = lastRecord.map(LeasingKmRecord::getRolloverToNext).orElse(0);
        
        if (request.currentKm() < previousKm) {
            throw new BusinessException(ErrorCode.INVALID_PARAMETER, 
                "Current KM cannot be less than previous KM: " + previousKm);
        }
        
        int monthlyAllowance = getMonthlyAllowance(rental);
        
        LeasingKmRecord record = LeasingKmRecord.create(
            rentalId,
            recordDate,
            request.currentKm(),
            previousKm,
            monthlyAllowance,
            rolloverFromPrevious,
            recordedBy
        );
        
        LeasingKmRecord saved = kmRecordRepository.save(record);
        log.info("KM recorded for rental {}: {} km", rentalId, request.currentKm());
        
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<KmRecordResponse> getKmHistory(Long rentalId) {
        getRentalOrThrow(rentalId);
        return kmRecordRepository.findByRentalIdOrderByRecordDateDesc(rentalId)
            .stream()
            .map(this::toResponse)
            .toList();
    }

    @Transactional(readOnly = true)
    public KmSummaryResponse getKmSummary(Long rentalId) {
        Rental rental = getRentalOrThrow(rentalId);
        
        List<LeasingKmRecord> records = kmRecordRepository.findByRentalIdOrderByRecordDateDesc(rentalId);
        
        if (records.isEmpty()) {
            return new KmSummaryResponse(
                rentalId, 0, 0, 0, 
                getMonthlyAllowance(rental), 0, null, 0
            );
        }
        
        LeasingKmRecord lastRecord = records.get(0);
        Integer totalUsed = kmRecordRepository.getTotalUsedKm(rentalId);
        Integer totalExcess = kmRecordRepository.getTotalExcessKm(rentalId);
        
        return new KmSummaryResponse(
            rentalId,
            totalUsed != null ? totalUsed : 0,
            totalExcess != null ? totalExcess : 0,
            lastRecord.getRolloverToNext(),
            getMonthlyAllowance(rental),
            records.size(),
            lastRecord.getRecordDate().toString(),
            lastRecord.getCurrentKm()
        );
    }

    @Transactional(readOnly = true)
    public int getCurrentRollover(Long rentalId) {
        return kmRecordRepository.findTopByRentalIdOrderByRecordDateDesc(rentalId)
            .map(LeasingKmRecord::getRolloverToNext)
            .orElse(0);
    }

    @Transactional(readOnly = true)
    public int getTotalExcessKm(Long rentalId) {
        Integer total = kmRecordRepository.getTotalExcessKm(rentalId);
        return total != null ? total : 0;
    }

    private Rental getRentalOrThrow(Long rentalId) {
        return rentalRepository.findById(rentalId)
            .orElseThrow(() -> new BusinessException(ErrorCode.RENTAL_NOT_FOUND, rentalId.toString()));
    }

    private int getMonthlyAllowance(Rental rental) {
        Optional<CustomerContract> contract = contractRepository.findActiveContractsByCustomer(
            rental.getCustomerId(), LocalDate.now()
        ).stream().findFirst();
        
        return contract.map(CustomerContract::getIncludedKmPerMonth).orElse(2500);
    }

    private KmRecordResponse toResponse(LeasingKmRecord record) {
        return new KmRecordResponse(
            record.getId(),
            record.getRentalId(),
            record.getRecordDate(),
            record.getPeriodYearMonth(),
            record.getCurrentKm(),
            record.getPreviousKm(),
            record.getUsedKm(),
            record.getMonthlyAllowance(),
            record.getExcessKm(),
            record.getRolloverFromPrevious(),
            record.getRolloverToNext(),
            record.getRecordedBy(),
            record.getCreatedAt()
        );
    }
}
