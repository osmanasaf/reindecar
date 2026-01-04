package com.reindecar.service.contract;

import com.reindecar.dto.contract.CreateContractRequest;
import com.reindecar.dto.contract.SignContractRequest;
import com.reindecar.entity.contract.Contract;
import com.reindecar.entity.contract.ContractTemplate;
import com.reindecar.entity.rental.Rental;
import com.reindecar.repository.contract.ContractRepository;
import com.reindecar.repository.contract.ContractTemplateRepository;
import com.reindecar.repository.rental.RentalRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Year;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ContractService {

    private final ContractRepository contractRepository;
    private final ContractTemplateRepository templateRepository;
    private final RentalRepository rentalRepository;

    @Transactional
    public Contract createContract(CreateContractRequest request) {
        log.info("Creating contract for rental: {}", request.rentalId());

        Rental rental = rentalRepository.findById(request.rentalId())
            .orElseThrow(() -> new IllegalArgumentException("Rental not found"));

        ContractTemplate template;
        if (request.templateId() != null) {
            template = templateRepository.findById(request.templateId())
                .orElseThrow(() -> new IllegalArgumentException("Template not found"));
        } else {
            template = templateRepository.findLatestByRentalType(rental.getRentalType())
                .orElseThrow(() -> new IllegalArgumentException("No template found for rental type"));
        }

        String contractNumber = generateContractNumber();
        String content = template.getContent();

        Contract contract = Contract.create(
            contractNumber,
            rental.getId(),
            template.getId(),
            content,
            rental.getStartDate(),
            rental.getEndDate()
        );

        return contractRepository.save(contract);
    }

    @Transactional
    public Contract signContract(Long id, SignContractRequest request) {
        log.info("Signing contract: {}", id);

        Contract contract = contractRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Contract not found"));

        contract.sign(request.signedBy(), request.signatureMethod());
        return contractRepository.save(contract);
    }

    @Transactional
    public void cancelContract(Long id) {
        log.info("Cancelling contract: {}", id);

        Contract contract = contractRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Contract not found"));

        contract.cancel();
        contractRepository.save(contract);
    }

    public Contract getContractById(Long id) {
        return contractRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Contract not found"));
    }

    public Contract getContractByRentalId(Long rentalId) {
        return contractRepository.findByRentalId(rentalId)
            .orElseThrow(() -> new IllegalArgumentException("Contract not found for rental"));
    }

    private String generateContractNumber() {
        String prefix = "CNT-" + Year.now().getValue() + "-";
        long count = contractRepository.countByContractNumberPrefix(prefix);
        return String.format("%s%05d", prefix, count + 1);
    }
}
