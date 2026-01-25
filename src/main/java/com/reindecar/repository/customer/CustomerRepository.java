package com.reindecar.repository.customer;

import com.reindecar.entity.customer.Customer;
import com.reindecar.entity.customer.CustomerStatus;
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

    @Query("SELECT c FROM Customer c " +
           "WHERE c.deleted = false AND (" +
           "LOWER(c.phone) LIKE :query OR " +
           "LOWER(c.email) LIKE :query OR " +
           "LOWER(c.city) LIKE :query OR " +
           "LOWER(c.address) LIKE :query OR " +
           "LOWER(TREAT(c AS com.reindecar.entity.customer.CustomerPerson).firstName) LIKE :query OR " +
           "LOWER(TREAT(c AS com.reindecar.entity.customer.CustomerPerson).lastName) LIKE :query OR " +
           "LOWER(TREAT(c AS com.reindecar.entity.customer.CustomerPerson).nationalId) LIKE :query OR " +
           "LOWER(TREAT(c AS com.reindecar.entity.customer.CustomerCompany).companyName) LIKE :query OR " +
           "LOWER(TREAT(c AS com.reindecar.entity.customer.CustomerCompany).taxNumber) LIKE :query OR " +
           "LOWER(TREAT(c AS com.reindecar.entity.customer.CustomerCompany).tradeRegisterNo) LIKE :query OR " +
           "LOWER(TREAT(c AS com.reindecar.entity.customer.CustomerCompany).contactPersonName) LIKE :query OR " +
           "LOWER(TREAT(c AS com.reindecar.entity.customer.CustomerCompany).contactPersonPhone) LIKE :query " +
           ")")
    Page<Customer> searchActiveCustomers(String query, Pageable pageable);

    long countByDeletedFalse();

    long countByStatusAndDeletedFalse(CustomerStatus status);

    long countByBlacklistedTrueAndDeletedFalse();
}
