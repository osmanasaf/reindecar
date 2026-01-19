package com.reindecar.repository.contract;

import com.reindecar.entity.contract.ContractTerm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContractTermRepository extends JpaRepository<ContractTerm, Long> {

    List<ContractTerm> findByTemplateIdOrderBySortOrderAsc(Long templateId);
    
    void deleteByTemplateId(Long templateId);
}
