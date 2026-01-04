package com.reindecar.repository.customer;

import com.reindecar.entity.customer.CustomerPerson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerPersonRepository extends JpaRepository<CustomerPerson, Long> {

    @Query("SELECT cp FROM CustomerPerson cp WHERE cp.nationalId = :nationalId AND cp.deleted = false")
    Optional<CustomerPerson> findByNationalIdAndDeletedFalse(String nationalId);

    @Query("SELECT CASE WHEN COUNT(cp) > 0 THEN true ELSE false END FROM CustomerPerson cp WHERE cp.nationalId = :nationalId AND cp.deleted = false")
    boolean existsByNationalIdAndDeletedFalse(String nationalId);
}
