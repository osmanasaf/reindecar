package com.reindecar.service.leasing;

import com.reindecar.common.exception.BusinessException;
import com.reindecar.common.exception.ErrorCode;
import com.reindecar.common.valueobject.Money;
import com.reindecar.dto.leasing.EarlyTerminationResponse;
import com.reindecar.dto.leasing.RequestTerminationRequest;
import com.reindecar.entity.leasing.EarlyTermination;
import com.reindecar.entity.pricing.CustomerContract;
import com.reindecar.entity.rental.Rental;
import com.reindecar.repository.leasing.EarlyTerminationRepository;
import com.reindecar.repository.pricing.CustomerContractRepository;
import com.reindecar.repository.rental.RentalRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class EarlyTerminationService {

    private final EarlyTerminationRepository terminationRepository;
    private final RentalRepository rentalRepository;
    private final CustomerContractRepository contractRepository;
    private final KmTrackingService kmTrackingService;

    @Transactional
    public EarlyTerminationResponse requestTermination(Long rentalId, RequestTerminationRequest request) {
        Rental rental = getRentalOrThrow(rentalId);
        
        boolean hasActiveRequest = terminationRepository.existsByRentalIdAndStatusIn(
            rentalId,
            Arrays.asList(
                EarlyTermination.TerminationStatus.REQUESTED,
                EarlyTermination.TerminationStatus.UNDER_REVIEW,
                EarlyTermination.TerminationStatus.APPROVED
            )
        );
        
        if (hasActiveRequest) {
            throw new BusinessException(ErrorCode.INVALID_OPERATION, 
                "Active termination request already exists for rental: " + rentalId);
        }
        
        Optional<CustomerContract> contractOpt = findActiveContract(rental.getCustomerId());
        Money monthlyRent = contractOpt
            .map(CustomerContract::getNegotiatedMonthlyPrice)
            .orElse(Money.of(new BigDecimal("5000"), Money.DEFAULT_CURRENCY));
        
        LocalDate contractEndDate = rental.getEndDate();
        
        EarlyTermination termination = EarlyTermination.create(
            rentalId,
            contractOpt.map(CustomerContract::getId).orElse(null),
            request.terminationDate(),
            contractEndDate,
            monthlyRent,
            request.reason()
        );
        
        int totalExcessKm = kmTrackingService.getTotalExcessKm(rentalId);
        if (totalExcessKm > 0) {
            BigDecimal excessKmPrice = contractOpt
                .map(c -> c.getExtraKmPrice() != null ? c.getExtraKmPrice().getAmount() : new BigDecimal("0.50"))
                .orElse(new BigDecimal("0.50"));
            Money excessKmCharge = Money.of(
                excessKmPrice.multiply(BigDecimal.valueOf(totalExcessKm)),
                monthlyRent.getCurrency()
            );
            termination.setExcessKmCharge(excessKmCharge);
        }
        
        EarlyTermination saved = terminationRepository.save(termination);
        log.info("Termination requested for rental {}: {} TRY penalty", rentalId, saved.getTotalAmount().getAmount());
        
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public EarlyTerminationResponse previewTermination(Long rentalId, LocalDate terminationDate) {
        Rental rental = getRentalOrThrow(rentalId);
        
        Optional<CustomerContract> contractOpt = findActiveContract(rental.getCustomerId());
        Money monthlyRent = contractOpt
            .map(CustomerContract::getNegotiatedMonthlyPrice)
            .orElse(Money.of(new BigDecimal("5000"), Money.DEFAULT_CURRENCY));
        
        EarlyTermination preview = EarlyTermination.create(
            rentalId,
            contractOpt.map(CustomerContract::getId).orElse(null),
            terminationDate,
            rental.getEndDate(),
            monthlyRent,
            null
        );
        
        int totalExcessKm = kmTrackingService.getTotalExcessKm(rentalId);
        if (totalExcessKm > 0) {
            BigDecimal excessKmPrice = contractOpt
                .map(c -> c.getExtraKmPrice() != null ? c.getExtraKmPrice().getAmount() : new BigDecimal("0.50"))
                .orElse(new BigDecimal("0.50"));
            Money excessKmCharge = Money.of(
                excessKmPrice.multiply(BigDecimal.valueOf(totalExcessKm)),
                monthlyRent.getCurrency()
            );
            preview.setExcessKmCharge(excessKmCharge);
        }
        
        return toResponse(preview);
    }

    @Transactional(readOnly = true)
    public EarlyTerminationResponse getById(Long id) {
        return toResponse(getTerminationOrThrow(id));
    }

    @Transactional(readOnly = true)
    public List<EarlyTerminationResponse> getByRental(Long rentalId) {
        return terminationRepository.findByRentalId(rentalId)
            .stream()
            .map(this::toResponse)
            .toList();
    }

    @Transactional(readOnly = true)
    public List<EarlyTerminationResponse> getPendingApprovals() {
        return terminationRepository.findByStatus(EarlyTermination.TerminationStatus.REQUESTED)
            .stream()
            .map(this::toResponse)
            .toList();
    }

    @Transactional
    public void approve(Long id, String approvedBy) {
        EarlyTermination termination = getTerminationOrThrow(id);
        termination.approve(approvedBy);
        terminationRepository.save(termination);
        log.info("Termination approved: {} by {}", id, approvedBy);
    }

    @Transactional
    public void reject(Long id, String rejectedBy) {
        EarlyTermination termination = getTerminationOrThrow(id);
        termination.reject(rejectedBy);
        terminationRepository.save(termination);
        log.info("Termination rejected: {} by {}", id, rejectedBy);
    }

    @Transactional
    public void complete(Long id) {
        EarlyTermination termination = getTerminationOrThrow(id);
        termination.complete();
        terminationRepository.save(termination);
        log.info("Termination completed: {}", id);
    }

    private Rental getRentalOrThrow(Long rentalId) {
        return rentalRepository.findById(rentalId)
            .orElseThrow(() -> new BusinessException(ErrorCode.RENTAL_NOT_FOUND, rentalId.toString()));
    }

    private EarlyTermination getTerminationOrThrow(Long id) {
        return terminationRepository.findById(id)
            .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND, "Termination not found: " + id));
    }

    private Optional<CustomerContract> findActiveContract(Long customerId) {
        return contractRepository.findActiveContractsByCustomer(customerId, LocalDate.now())
            .stream()
            .findFirst();
    }

    private EarlyTerminationResponse toResponse(EarlyTermination termination) {
        return new EarlyTerminationResponse(
            termination.getId(),
            termination.getRentalId(),
            termination.getContractId(),
            termination.getTerminationDate(),
            termination.getContractEndDate(),
            termination.getRemainingMonths(),
            termination.getPenaltyRate(),
            termination.getMonthlyRent() != null ? termination.getMonthlyRent().getAmount() : null,
            termination.getPenaltyAmount().getAmount(),
            termination.getExcessKmCharge() != null ? termination.getExcessKmCharge().getAmount() : null,
            termination.getTotalAmount().getAmount(),
            termination.getTotalAmount().getCurrency(),
            termination.getReason(),
            termination.getStatus(),
            termination.getApprovedBy(),
            termination.getApprovedAt(),
            termination.getCreatedAt()
        );
    }
}
