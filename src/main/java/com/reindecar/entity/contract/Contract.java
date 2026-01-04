package com.reindecar.entity.contract;

import com.reindecar.common.entity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "contracts")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Contract extends BaseEntity {

    @NotBlank(message = "Contract number is required")
    @Size(max = 50)
    @Column(nullable = false, unique = true, length = 50)
    private String contractNumber;

    @NotNull(message = "Rental ID is required")
    @Column(nullable = false, name = "rental_id")
    private Long rentalId;

    @NotNull(message = "Template ID is required")
    @Column(nullable = false, name = "template_id")
    private Long templateId;

    @Min(value = 1, message = "Contract version must be at least 1")
    @Column(nullable = false)
    private int contractVersion = 1;

    @NotNull(message = "Status is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ContractStatus status;

    @NotBlank(message = "Content is required")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column
    private Instant signedAt;

    @Size(max = 100)
    @Column(length = 100)
    private String signedBy;

    @Size(max = 50)
    @Column(length = 50)
    private String signatureMethod;

    @NotNull(message = "Valid from date is required")
    @Column(nullable = false)
    private LocalDate validFrom;

    @NotNull(message = "Valid to date is required")
    @Column(nullable = false)
    private LocalDate validTo;

    @Column(name = "renewed_from_id")
    private Long renewedFromId;

    @Size(max = 1000)
    @Column(length = 1000)
    private String notes;

    @Column(nullable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    public static Contract create(
            String contractNumber,
            Long rentalId,
            Long templateId,
            String content,
            LocalDate validFrom,
            LocalDate validTo) {
        
        Contract contract = new Contract();
        contract.contractNumber = contractNumber;
        contract.rentalId = rentalId;
        contract.templateId = templateId;
        contract.contractVersion = 1;
        contract.status = ContractStatus.DRAFT;
        contract.content = content;
        contract.validFrom = validFrom;
        contract.validTo = validTo;
        contract.createdAt = Instant.now();
        contract.updatedAt = Instant.now();
        return contract;
    }

    public void sign(String signedBy, String signatureMethod) {
        if (!status.canBeSigned()) {
            throw new IllegalStateException("Contract cannot be signed in status: " + status);
        }
        
        this.signedAt = Instant.now();
        this.signedBy = signedBy;
        this.signatureMethod = signatureMethod;
        this.status = ContractStatus.SIGNED;
        this.updatedAt = Instant.now();
    }

    public void cancel() {
        if (!status.canBeCancelled()) {
            throw new IllegalStateException("Contract cannot be cancelled in status: " + status);
        }
        
        this.status = ContractStatus.CANCELLED;
        this.updatedAt = Instant.now();
    }

    public void markAsRenewed() {
        this.status = ContractStatus.RENEWED;
        this.updatedAt = Instant.now();
    }

    public void markAsExpired() {
        if (status == ContractStatus.SIGNED && LocalDate.now().isAfter(validTo)) {
            this.status = ContractStatus.EXPIRED;
            this.updatedAt = Instant.now();
        }
    }

    public boolean isExpired() {
        return status == ContractStatus.EXPIRED || 
               (status == ContractStatus.SIGNED && LocalDate.now().isAfter(validTo));
    }

    public boolean isSigned() {
        return status == ContractStatus.SIGNED;
    }
}
