package com.reindecar.service.leasing;

import com.reindecar.common.exception.BusinessException;
import com.reindecar.common.exception.ErrorCode;
import com.reindecar.dto.leasing.ContractSignatureResponse;
import com.reindecar.dto.leasing.SignContractRequest;
import com.reindecar.entity.leasing.ContractSignature;
import com.reindecar.repository.leasing.ContractSignatureRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ContractSignatureService {

    private final ContractSignatureRepository signatureRepository;

    @Transactional
    public ContractSignatureResponse sign(
            Long contractId,
            Long rentalId,
            SignContractRequest request,
            String ipAddress,
            String deviceInfo,
            String userAgent) {

        ContractSignature.SignatureType signatureType = request.signatureType() != null 
            ? request.signatureType() 
            : ContractSignature.SignatureType.DIGITAL;

        ContractSignature signature = ContractSignature.create(
            contractId,
            rentalId,
            request.signedBy(),
            request.signatoryRole(),
            signatureType,
            ipAddress,
            deviceInfo,
            userAgent
        );

        ContractSignature saved = signatureRepository.save(signature);
        log.info("Contract {} signed by {} from IP {}", contractId, request.signedBy(), ipAddress);

        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<ContractSignatureResponse> getByContract(Long contractId) {
        return signatureRepository.findByContractId(contractId)
            .stream()
            .map(this::toResponse)
            .toList();
    }

    @Transactional(readOnly = true)
    public ContractSignatureResponse getById(Long id) {
        return toResponse(getSignatureOrThrow(id));
    }

    @Transactional(readOnly = true)
    public boolean isContractSigned(Long contractId) {
        return signatureRepository.existsByContractId(contractId);
    }

    @Transactional
    public void verify(Long signatureId) {
        ContractSignature signature = getSignatureOrThrow(signatureId);
        signature.verify();
        signatureRepository.save(signature);
        log.info("Signature {} verified", signatureId);
    }

    private ContractSignature getSignatureOrThrow(Long id) {
        return signatureRepository.findById(id)
            .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND, "Signature not found: " + id));
    }

    private ContractSignatureResponse toResponse(ContractSignature signature) {
        return new ContractSignatureResponse(
            signature.getId(),
            signature.getContractId(),
            signature.getRentalId(),
            signature.getSignatureType(),
            signature.getSignedAt(),
            signature.getSignedBy(),
            signature.getSignatoryRole(),
            signature.getIpAddress(),
            signature.getDeviceInfo(),
            signature.isVerified(),
            signature.getVerificationCode(),
            signature.getCreatedAt()
        );
    }
}
