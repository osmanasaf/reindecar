package com.reindecar.repository.contract;

import com.reindecar.entity.contract.Contract;
import com.reindecar.entity.contract.ContractStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ContractRepository extends JpaRepository<Contract, Long> {

    Optional<Contract> findByContractNumber(String contractNumber);

    @Query("SELECT c FROM Contract c WHERE c.rentalId = :rentalId")
    Optional<Contract> findByRentalId(Long rentalId);

    @Query("SELECT COUNT(c) FROM Contract c WHERE c.contractNumber LIKE :prefix%")
    long countByContractNumberPrefix(String prefix);
}
