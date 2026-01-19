package com.reindecar.repository.leasing;

import com.reindecar.entity.leasing.LeasingInvoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface LeasingInvoiceRepository extends JpaRepository<LeasingInvoice, Long> {

    Optional<LeasingInvoice> findByInvoiceNumber(String invoiceNumber);

    List<LeasingInvoice> findByRentalIdOrderByPeriodStartDesc(Long rentalId);

    List<LeasingInvoice> findByCustomerIdOrderByPeriodStartDesc(Long customerId);

    List<LeasingInvoice> findByStatus(LeasingInvoice.InvoiceStatus status);

    boolean existsByRentalIdAndPeriodStartAndPeriodEnd(Long rentalId, LocalDate periodStart, LocalDate periodEnd);

    @Query("SELECT i FROM LeasingInvoice i WHERE i.status = 'SENT' AND i.dueDate < :today")
    List<LeasingInvoice> findOverdueInvoices(LocalDate today);

    @Query("SELECT i FROM LeasingInvoice i WHERE i.rentalId = :rentalId AND i.status != 'CANCELLED' AND i.status != 'PAID'")
    List<LeasingInvoice> findUnpaidByRentalId(Long rentalId);
}
