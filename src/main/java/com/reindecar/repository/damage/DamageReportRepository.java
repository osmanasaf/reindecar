package com.reindecar.repository.damage;

import com.reindecar.entity.damage.DamageReport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface DamageReportRepository extends JpaRepository<DamageReport, Long> {

    @Query("SELECT d FROM DamageReport d WHERE d.vehicleId = :vehicleId ORDER BY d.reportDate DESC")
    Page<DamageReport> findByVehicleIdOrderByReportDateDesc(Long vehicleId, Pageable pageable);

    @Query("SELECT d FROM DamageReport d WHERE d.rentalId = :rentalId ORDER BY d.reportDate DESC")
    Page<DamageReport> findByRentalIdOrderByReportDateDesc(Long rentalId, Pageable pageable);
}
