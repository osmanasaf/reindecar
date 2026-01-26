package com.reindecar.repository.vehicle;

import com.reindecar.entity.vehicle.VehicleStatusHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VehicleStatusHistoryRepository extends JpaRepository<VehicleStatusHistory, Long> {

    @Query("SELECT vsh FROM VehicleStatusHistory vsh WHERE vsh.vehicleId = :vehicleId ORDER BY vsh.changedAt DESC")
    Page<VehicleStatusHistory> findByVehicleIdOrderByChangedAtDesc(Long vehicleId, Pageable pageable);

    @Query("SELECT vsh FROM VehicleStatusHistory vsh WHERE vsh.vehicleId = :vehicleId ORDER BY vsh.changedAt DESC")
    List<VehicleStatusHistory> findAllByVehicleId(Long vehicleId);

    @Query("SELECT vsh FROM VehicleStatusHistory vsh WHERE vsh.vehicleId = :vehicleId ORDER BY vsh.changedAt DESC LIMIT 1")
    Optional<VehicleStatusHistory> findLatestByVehicleId(Long vehicleId);
}
