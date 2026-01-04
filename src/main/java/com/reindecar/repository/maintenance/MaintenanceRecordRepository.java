package com.reindecar.repository.maintenance;

import com.reindecar.entity.maintenance.MaintenanceRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface MaintenanceRecordRepository extends JpaRepository<MaintenanceRecord, Long> {

    @Query("SELECT m FROM MaintenanceRecord m WHERE m.vehicleId = :vehicleId ORDER BY m.maintenanceDate DESC")
    Page<MaintenanceRecord> findByVehicleIdOrderByMaintenanceDateDesc(Long vehicleId, Pageable pageable);
}
