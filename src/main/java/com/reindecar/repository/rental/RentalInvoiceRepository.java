package com.reindecar.repository.rental;

import com.reindecar.entity.rental.InvoiceStatus;
import com.reindecar.entity.rental.RentalInvoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RentalInvoiceRepository extends JpaRepository<RentalInvoice, Long> {

    Optional<RentalInvoice> findByRentalId(Long rentalId);

    Optional<RentalInvoice> findByInvoiceNumber(String invoiceNumber);

    List<RentalInvoice> findByStatus(InvoiceStatus status);
}
