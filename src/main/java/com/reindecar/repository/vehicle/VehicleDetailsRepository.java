package com.reindecar.repository.vehicle;

import com.reindecar.entity.vehicle.VehicleDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface VehicleDetailsRepository extends JpaRepository<VehicleDetails, Long> {

    Optional<VehicleDetails> findByVehicleId(Long vehicleId);

    @Query("SELECT vd FROM VehicleDetails vd WHERE vd.nextServiceDate <= :date")
    List<VehicleDetails> findWithServiceDueBefore(LocalDate date);

    @Query("SELECT vd FROM VehicleDetails vd WHERE vd.mtvDate <= :date")
    List<VehicleDetails> findWithMtvDueBefore(LocalDate date);

    @Query("SELECT vd FROM VehicleDetails vd WHERE vd.nextTireChangeDate <= :date")
    List<VehicleDetails> findWithTireChangeDueBefore(LocalDate date);
}
