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
}
