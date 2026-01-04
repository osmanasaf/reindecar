package com.reindecar.repository.payment;

import com.reindecar.entity.payment.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    @Query("SELECT p FROM Payment p WHERE p.rentalId = :rentalId ORDER BY p.paidAt DESC")
    List<Payment> findByRentalIdOrderByPaidAtDesc(Long rentalId);

    @Query("SELECT p FROM Payment p ORDER BY p.paidAt DESC")
    Page<Payment> findAllOrderByPaidAtDesc(Pageable pageable);
}
