package com.reindecar.repository.customer;

import com.reindecar.entity.customer.CustomerCompany;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerCompanyRepository extends JpaRepository<CustomerCompany, Long> {

    @Query("SELECT cc FROM CustomerCompany cc WHERE cc.taxNumber = :taxNumber AND cc.deleted = false")
    Optional<CustomerCompany> findByTaxNumberAndDeletedFalse(String taxNumber);

    @Query("SELECT CASE WHEN COUNT(cc) > 0 THEN true ELSE false END FROM CustomerCompany cc WHERE cc.taxNumber = :taxNumber AND cc.deleted = false")
    boolean existsByTaxNumberAndDeletedFalse(String taxNumber);
}
