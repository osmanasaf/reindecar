package com.reindecar.repository.rental;

import com.reindecar.entity.rental.RentalDriver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RentalDriverRepository extends JpaRepository<RentalDriver, Long> {

    List<RentalDriver> findByRentalId(Long rentalId);

    List<RentalDriver> findByDriverId(Long driverId);

    Optional<RentalDriver> findByRentalIdAndDriverId(Long rentalId, Long driverId);

    Optional<RentalDriver> findByRentalIdAndPrimaryTrue(Long rentalId);

    boolean existsByRentalIdAndDriverId(Long rentalId, Long driverId);

    void deleteByRentalIdAndDriverId(Long rentalId, Long driverId);

    @Query("SELECT rd FROM RentalDriver rd WHERE rd.rentalId = :rentalId ORDER BY rd.primary DESC, rd.addedAt ASC")
    List<RentalDriver> findByRentalIdOrdered(Long rentalId);

    int countByRentalId(Long rentalId);

    @Query("SELECT COUNT(rd) > 0 FROM RentalDriver rd " +
           "JOIN Rental r ON rd.rentalId = r.id " +
           "WHERE rd.driverId = :driverId " +
           "AND r.status IN ('RESERVED', 'ACTIVE', 'OVERDUE')")
    boolean hasBlockingRental(Long driverId);

    @Query("SELECT rd FROM RentalDriver rd " +
           "JOIN Rental r ON rd.rentalId = r.id " +
           "WHERE rd.driverId = :driverId " +
           "AND r.status IN ('RESERVED', 'ACTIVE', 'OVERDUE')")
    List<RentalDriver> findBlockingRentalsByDriverId(Long driverId);
}
