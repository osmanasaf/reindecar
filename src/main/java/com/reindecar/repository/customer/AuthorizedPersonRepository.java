package com.reindecar.repository.customer;

import com.reindecar.entity.customer.AuthorizedPerson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AuthorizedPersonRepository extends JpaRepository<AuthorizedPerson, Long> {

    List<AuthorizedPerson> findByCompanyCustomerId(Long companyCustomerId);

    @Query("SELECT ap FROM AuthorizedPerson ap WHERE ap.companyCustomerId = :companyCustomerId AND ap.isPrimary = true")
    Optional<AuthorizedPerson> findPrimaryByCompanyCustomerId(Long companyCustomerId);

    @Query("SELECT COUNT(ap) FROM AuthorizedPerson ap WHERE ap.companyCustomerId = :companyCustomerId AND ap.active = true")
    long countActiveByCompanyCustomerId(Long companyCustomerId);
}
