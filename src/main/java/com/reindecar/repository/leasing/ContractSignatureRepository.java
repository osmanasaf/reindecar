package com.reindecar.repository.leasing;

import com.reindecar.entity.leasing.ContractSignature;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContractSignatureRepository extends JpaRepository<ContractSignature, Long> {

    List<ContractSignature> findByContractId(Long contractId);

    Optional<ContractSignature> findByContractIdAndSignedBy(Long contractId, String signedBy);

    boolean existsByContractId(Long contractId);

    List<ContractSignature> findByRentalId(Long rentalId);
}
