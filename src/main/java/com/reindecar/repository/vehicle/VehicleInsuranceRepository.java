package com.reindecar.repository.vehicle;

import com.reindecar.entity.vehicle.InsuranceType;
import com.reindecar.entity.vehicle.VehicleInsurance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface VehicleInsuranceRepository extends JpaRepository<VehicleInsurance, Long> {

    List<VehicleInsurance> findByVehicleIdAndActiveTrue(Long vehicleId);

    @Query("SELECT vi FROM VehicleInsurance vi WHERE vi.vehicleId = :vehicleId AND vi.insuranceType = :type AND vi.active = true")
    Optional<VehicleInsurance> findActiveByVehicleIdAndType(Long vehicleId, InsuranceType type);

    @Query("SELECT vi FROM VehicleInsurance vi WHERE vi.endDate <= :date AND vi.active = true")
    List<VehicleInsurance> findExpiringSoon(LocalDate date);

    @Query("SELECT vi FROM VehicleInsurance vi WHERE vi.vehicleId = :vehicleId ORDER BY vi.endDate DESC")
    List<VehicleInsurance> findAllByVehicleIdOrderByEndDateDesc(Long vehicleId);
}
