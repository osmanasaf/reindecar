package com.reindecar.repository.customer;

import com.reindecar.entity.customer.Customer;
import com.reindecar.entity.customer.CustomerType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Optional<Customer> findByPublicId(UUID publicId);

    @Query("SELECT c FROM Customer c WHERE c.deleted = false")
    Page<Customer> findAllActive(Pageable pageable);

    @Query("SELECT c FROM Customer c WHERE c.customerType = :type AND c.deleted = false")
    Page<Customer> findByCustomerTypeAndDeletedFalse(CustomerType type, Pageable pageable);

    @Query("SELECT c FROM Customer c WHERE c.blacklisted = true AND c.deleted = false")
    Page<Customer> findBlacklisted(Pageable pageable);

    @Query("SELECT c FROM Customer c WHERE c.deleted = false AND c.id = :id")
    Optional<Customer> findByIdAndNotDeleted(Long id);
}
