package com.reindecar.repository.contract;

import com.reindecar.entity.contract.ContractTemplate;
import com.reindecar.entity.pricing.RentalType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ContractTemplateRepository extends JpaRepository<ContractTemplate, Long> {

    @Query("SELECT ct FROM ContractTemplate ct WHERE ct.rentalType = :rentalType AND ct.active = true ORDER BY ct.templateVersion DESC LIMIT 1")
    Optional<ContractTemplate> findLatestByRentalType(RentalType rentalType);

    Optional<ContractTemplate> findByCode(String code);
}
