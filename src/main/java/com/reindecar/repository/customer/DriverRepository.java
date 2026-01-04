package com.reindecar.repository.customer;

import com.reindecar.entity.customer.Driver;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DriverRepository extends JpaRepository<Driver, Long> {

    @Query("SELECT d FROM Driver d WHERE d.customerId = :customerId")
    Page<Driver> findByCustomerId(Long customerId, Pageable pageable);

    @Query("SELECT d FROM Driver d WHERE d.id = :id AND d.customerId = :customerId")
    Optional<Driver> findByIdAndCustomerId(Long id, Long customerId);

    @Query("SELECT COUNT(d) FROM Driver d WHERE d.customerId = :customerId AND d.active = true")
    long countActiveByCustomerId(Long customerId);
}
