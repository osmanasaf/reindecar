package com.reindecar.repository.rental;

import com.reindecar.entity.rental.Rental;
import com.reindecar.entity.rental.RentalStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface RentalRepository extends JpaRepository<Rental, Long> {

    Optional<Rental> findByRentalNumber(String rentalNumber);

    @Query("SELECT r FROM Rental r WHERE r.status = :status")
    Page<Rental> findByStatus(RentalStatus status, Pageable pageable);

    @Query("SELECT r FROM Rental r WHERE r.status IN ('ACTIVE', 'OVERDUE')")
    Page<Rental> findActiveRentals(Pageable pageable);

    @Query("SELECT r FROM Rental r WHERE r.status = 'OVERDUE'")
    Page<Rental> findOverdueRentals(Pageable pageable);

    @Query("SELECT r FROM Rental r WHERE " +
           "r.vehicleId = :vehicleId AND " +
           "r.status NOT IN ('CLOSED', 'CANCELLED') AND " +
           "((r.startDate <= :endDate AND r.endDate >= :startDate))")
    List<Rental> findOverlappingRentals(Long vehicleId, LocalDate startDate, LocalDate endDate);

    @Query("SELECT r FROM Rental r WHERE r.customerId = :customerId ORDER BY r.startDate DESC")
    Page<Rental> findByCustomerId(Long customerId, Pageable pageable);

    @Query("SELECT r FROM Rental r WHERE r.customerId = :customerId AND r.status IN ('ACTIVE', 'OVERDUE') ORDER BY r.startDate DESC")
    List<Rental> findActiveByCustomerId(Long customerId);

    @Query("SELECT DISTINCT r.vehicleId FROM Rental r WHERE r.customerId = :customerId")
    List<Long> findVehicleIdsByCustomerId(Long customerId);

    @Query("SELECT DISTINCT r.vehicleId FROM Rental r WHERE r.customerId = :companyId AND r.customerType = 'COMPANY'")
    List<Long> findVehicleIdsByCompanyId(Long companyId);

    @Query("SELECT r FROM Rental r WHERE r.customerType = 'COMPANY' ORDER BY r.startDate DESC")
    Page<Rental> findAllCompanyRentals(Pageable pageable);

    @Query("SELECT r FROM Rental r WHERE r.vehicleId = :vehicleId ORDER BY r.startDate DESC")
    Page<Rental> findByVehicleId(Long vehicleId, Pageable pageable);

    @Query("SELECT r FROM Rental r WHERE r.vehicleId = :vehicleId ORDER BY r.startDate DESC")
    List<Rental> findAllByVehicleId(Long vehicleId);

    @Query("SELECT COUNT(r) FROM Rental r WHERE r.rentalNumber LIKE :prefix%")
    long countByRentalNumberPrefix(String prefix);

    List<Rental> findByRentalTypeAndStatus(com.reindecar.entity.pricing.RentalType rentalType, RentalStatus status);

    long countByStatus(RentalStatus status);

    @Query("SELECT r FROM Rental r WHERE r.status NOT IN ('CLOSED', 'CANCELLED') " +
           "AND r.endDate BETWEEN :startDate AND :endDate " +
           "ORDER BY r.endDate ASC")
    List<Rental> findUpcomingReturns(LocalDate startDate, LocalDate endDate);

    @Query("SELECT COUNT(r) FROM Rental r WHERE r.customerId = :customerId " +
           "AND r.customerType = 'PERSONAL' " +
           "AND r.status IN ('RESERVED', 'ACTIVE', 'OVERDUE')")
    long countBlockingRentalsByPersonalCustomer(Long customerId);
}
