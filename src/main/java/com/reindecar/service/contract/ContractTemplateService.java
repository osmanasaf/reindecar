package com.reindecar.service.contract;

import com.reindecar.common.exception.BusinessException;
import com.reindecar.common.exception.ErrorCode;
import com.reindecar.dto.contract.*;
import com.reindecar.entity.contract.ContractTemplate;
import com.reindecar.entity.contract.ContractTerm;
import com.reindecar.entity.pricing.RentalType;
import com.reindecar.repository.contract.ContractTemplateRepository;
import com.reindecar.repository.contract.ContractTermRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ContractTemplateService {

    private final ContractTemplateRepository contractTemplateRepository;
    private final ContractTermRepository contractTermRepository;

    @Transactional
    public ContractTemplateResponse create(CreateContractTemplateRequest request) {
        if (contractTemplateRepository.findByCode(request.code()).isPresent()) {
            throw new BusinessException(ErrorCode.DUPLICATE_ENTITY, "Template with code already exists: " + request.code());
        }

        ContractTemplate template = ContractTemplate.create(
            request.code(),
            request.name(),
            request.rentalType(),
            request.content()
        );

        ContractTemplate saved = contractTemplateRepository.save(template);
        log.info("Contract template created: {}", saved.getCode());
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<ContractTemplateResponse> findAll() {
        return contractTemplateRepository.findAll().stream()
            .map(this::toResponse)
            .toList();
    }

    @Transactional(readOnly = true)
    public ContractTemplateResponse findById(Long id) {
        return toResponse(getTemplateOrThrow(id));
    }

    @Transactional(readOnly = true)
    public ContractTemplateResponse findByCode(String code) {
        return contractTemplateRepository.findByCode(code)
            .map(this::toResponse)
            .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND, "Template not found: " + code));
    }

    @Transactional(readOnly = true)
    public ContractTemplateResponse findLatestByRentalType(RentalType rentalType) {
        return contractTemplateRepository.findLatestByRentalType(rentalType)
            .map(this::toResponse)
            .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND, "No template found for rental type: " + rentalType));
    }

    @Transactional
    public ContractTemplateResponse updateContent(Long id, String newContent) {
        ContractTemplate template = getTemplateOrThrow(id);
        template.updateContent(newContent);
        ContractTemplate saved = contractTemplateRepository.save(template);
        log.info("Contract template updated: {}", saved.getCode());
        return toResponse(saved);
    }

    @Transactional
    public ContractTermResponse addTerm(Long templateId, CreateContractTermRequest request) {
        ContractTemplate template = getTemplateOrThrow(templateId);
        
        ContractTerm term = ContractTerm.create(
            templateId,
            request.title(),
            request.content(),
            request.required(),
            request.sortOrder()
        );

        ContractTerm saved = contractTermRepository.save(term);
        log.info("Term added to template {}: {}", template.getCode(), saved.getTitle());
        return toTermResponse(saved);
    }

    @Transactional
    public void removeTerm(Long templateId, Long termId) {
        getTemplateOrThrow(templateId);
        contractTermRepository.deleteById(termId);
        log.info("Term removed from template: {}", termId);
    }

    @Transactional
    public void activate(Long id) {
        ContractTemplate template = getTemplateOrThrow(id);
        template.activate();
        contractTemplateRepository.save(template);
        log.info("Template activated: {}", template.getCode());
    }

    @Transactional
    public void deactivate(Long id) {
        ContractTemplate template = getTemplateOrThrow(id);
        template.deactivate();
        contractTemplateRepository.save(template);
        log.info("Template deactivated: {}", template.getCode());
    }

    @Transactional
    public void delete(Long id) {
        ContractTemplate template = getTemplateOrThrow(id);
        contractTermRepository.deleteByTemplateId(id);
        contractTemplateRepository.delete(template);
        log.info("Template deleted: {}", template.getCode());
    }

    private ContractTemplate getTemplateOrThrow(Long id) {
        return contractTemplateRepository.findById(id)
            .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND, "Template not found: " + id));
    }

    private ContractTemplateResponse toResponse(ContractTemplate template) {
        List<ContractTermResponse> terms = contractTermRepository.findByTemplateIdOrderBySortOrderAsc(template.getId())
            .stream()
            .map(this::toTermResponse)
            .toList();

        return new ContractTemplateResponse(
            template.getId(),
            template.getCode(),
            template.getName(),
            template.getRentalType(),
            template.getTemplateVersion(),
            template.isActive(),
            terms,
            template.getCreatedAt()
        );
    }

    private ContractTermResponse toTermResponse(ContractTerm term) {
        return new ContractTermResponse(
            term.getId(),
            term.getTitle(),
            term.getContent(),
            term.isRequired(),
            term.getSortOrder()
        );
    }
}
