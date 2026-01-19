package com.reindecar.entity.leasing;

import com.reindecar.common.entity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "contract_signatures", indexes = {
    @Index(name = "idx_signature_contract", columnList = "contract_id")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ContractSignature extends BaseEntity {

    @NotNull
    @Column(name = "contract_id", nullable = false)
    private Long contractId;

    @Column(name = "rental_id")
    private Long rentalId;

    @Enumerated(EnumType.STRING)
    @Column(name = "signature_type", nullable = false, length = 20)
    private SignatureType signatureType = SignatureType.DIGITAL;

    @Column(name = "signed_at", nullable = false)
    private Instant signedAt;

    @Column(name = "signed_by", nullable = false, length = 100)
    private String signedBy;

    @Column(name = "signatory_role", length = 50)
    private String signatoryRole;

    @Column(name = "ip_address", length = 50)
    private String ipAddress;

    @Column(name = "device_info", length = 200)
    private String deviceInfo;

    @Column(name = "user_agent", length = 500)
    private String userAgent;

    @Column(nullable = false)
    private boolean verified = false;

    @Column(name = "verification_code", length = 100)
    private String verificationCode;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    public enum SignatureType {
        DIGITAL, PHYSICAL, ESIGN
    }

    public static ContractSignature create(
            Long contractId,
            Long rentalId,
            String signedBy,
            String signatoryRole,
            SignatureType signatureType,
            String ipAddress,
            String deviceInfo,
            String userAgent) {

        ContractSignature signature = new ContractSignature();
        signature.contractId = contractId;
        signature.rentalId = rentalId;
        signature.signatureType = signatureType;
        signature.signedAt = Instant.now();
        signature.signedBy = signedBy;
        signature.signatoryRole = signatoryRole;
        signature.ipAddress = ipAddress;
        signature.deviceInfo = deviceInfo;
        signature.userAgent = userAgent;
        signature.verified = false;
        signature.verificationCode = generateVerificationCode();
        signature.createdAt = Instant.now();
        return signature;
    }

    private static String generateVerificationCode() {
        return java.util.UUID.randomUUID().toString().replace("-", "").substring(0, 16).toUpperCase();
    }

    public void verify() {
        this.verified = true;
    }

    public boolean isDigital() {
        return this.signatureType == SignatureType.DIGITAL || this.signatureType == SignatureType.ESIGN;
    }
}
