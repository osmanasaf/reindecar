package com.reindecar.service.pricing;

import com.reindecar.common.exception.BusinessException;
import com.reindecar.common.exception.ErrorCode;
import com.reindecar.common.valueobject.Money;
import com.reindecar.dto.pricing.CreateCustomerContractRequest;
import com.reindecar.dto.pricing.CustomerContractResponse;
import com.reindecar.entity.pricing.CustomerContract;
import com.reindecar.repository.pricing.CustomerContractRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Year;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerContractService {

    private static final String CONTRACT_PREFIX = "CC-";
    
    private final CustomerContractRepository customerContractRepository;

    @Transactional
    public CustomerContractResponse create(CreateCustomerContractRequest request) {
        String contractNumber = generateContractNumber();
        
        Money monthlyPrice = Money.of(request.negotiatedMonthlyPrice(), Money.DEFAULT_CURRENCY);
        Money extraKmPrice = request.extraKmPrice() != null 
            ? Money.of(request.extraKmPrice(), Money.DEFAULT_CURRENCY) 
            : null;

        CustomerContract contract = CustomerContract.create(
            request.customerId(),
            request.categoryId(),
            contractNumber,
            request.termMonths(),
            monthlyPrice,
            request.includedKmPerMonth(),
            extraKmPrice,
            request.startDate()
        );

        CustomerContract saved = customerContractRepository.save(contract);
        log.info("Customer contract created: {}", contractNumber);
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<CustomerContractResponse> findAll() {
        return customerContractRepository.findAll().stream()
            .map(this::toResponse)
            .toList();
    }

    @Transactional(readOnly = true)
    public List<CustomerContractResponse> findByCustomerId(Long customerId) {
        return customerContractRepository.findByCustomerId(customerId).stream()
            .map(this::toResponse)
            .toList();
    }

    @Transactional(readOnly = true)
    public List<CustomerContractResponse> findActiveByCustomerId(Long customerId) {
        return customerContractRepository.findByCustomerIdAndStatus(customerId, CustomerContract.ContractStatus.ACTIVE).stream()
            .map(this::toResponse)
            .toList();
    }

    @Transactional(readOnly = true)
    public CustomerContractResponse findById(Long id) {
        return toResponse(getContractOrThrow(id));
    }

    @Transactional(readOnly = true)
    public CustomerContractResponse findByContractNumber(String contractNumber) {
        return customerContractRepository.findByContractNumber(contractNumber)
            .map(this::toResponse)
            .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND, "Contract not found: " + contractNumber));
    }

    @Transactional
    public void activate(Long id) {
        CustomerContract contract = getContractOrThrow(id);
        contract.activate();
        customerContractRepository.save(contract);
        log.info("Contract activated: {}", contract.getContractNumber());
    }

    @Transactional
    public void suspend(Long id) {
        CustomerContract contract = getContractOrThrow(id);
        contract.suspend();
        customerContractRepository.save(contract);
        log.info("Contract suspended: {}", contract.getContractNumber());
    }

    @Transactional
    public void terminate(Long id) {
        CustomerContract contract = getContractOrThrow(id);
        contract.terminate();
        customerContractRepository.save(contract);
        log.info("Contract terminated: {}", contract.getContractNumber());
    }

    @Transactional
    public void complete(Long id) {
        CustomerContract contract = getContractOrThrow(id);
        contract.complete();
        customerContractRepository.save(contract);
        log.info("Contract completed: {}", contract.getContractNumber());
    }

    private CustomerContract getContractOrThrow(Long id) {
        return customerContractRepository.findById(id)
            .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND, "Contract not found: " + id));
    }

    private String generateContractNumber() {
        String prefix = CONTRACT_PREFIX + Year.now().getValue() + "-";
        long count = customerContractRepository.count();
        return String.format("%s%05d", prefix, count + 1);
    }

    private CustomerContractResponse toResponse(CustomerContract contract) {
        return new CustomerContractResponse(
            contract.getId(),
            contract.getCustomerId(),
            contract.getCategoryId(),
            contract.getContractNumber(),
            contract.getTermMonths(),
            contract.getNegotiatedMonthlyPrice().getAmount(),
            contract.getNegotiatedMonthlyPrice().getCurrency(),
            contract.getIncludedKmPerMonth(),
            contract.getExtraKmPrice() != null ? contract.getExtraKmPrice().getAmount() : null,
            contract.getStartDate(),
            contract.getEndDate(),
            contract.getStatus(),
            contract.getNotes(),
            contract.calculateTotalContractPrice().getAmount(),
            contract.getTotalIncludedKm(),
            contract.getCreatedAt()
        );
    }
}
