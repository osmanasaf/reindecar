package com.reindecar.repository.pricing;

import com.reindecar.entity.pricing.CustomerContract;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerContractRepository extends JpaRepository<CustomerContract, Long> {

    List<CustomerContract> findByCustomerId(Long customerId);

    List<CustomerContract> findByCustomerIdAndStatus(Long customerId, CustomerContract.ContractStatus status);

    Optional<CustomerContract> findByContractNumber(String contractNumber);

    @Query("SELECT cc FROM CustomerContract cc WHERE cc.customerId = :customerId " +
           "AND cc.categoryId = :categoryId " +
           "AND cc.status = 'ACTIVE' " +
           "AND cc.startDate <= :date AND cc.endDate >= :date")
    Optional<CustomerContract> findActiveContract(Long customerId, Long categoryId, LocalDate date);

    @Query("SELECT cc FROM CustomerContract cc WHERE cc.customerId = :customerId " +
           "AND cc.status = 'ACTIVE' " +
           "AND cc.startDate <= :date AND cc.endDate >= :date")
    List<CustomerContract> findActiveContractsByCustomer(Long customerId, LocalDate date);

    boolean existsByContractNumber(String contractNumber);
}
