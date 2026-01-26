package com.reindecar.repository.pricing;

import com.reindecar.entity.pricing.VehiclePricing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VehiclePricingRepository extends JpaRepository<VehiclePricing, Long> {

    Optional<VehiclePricing> findByVehicleIdAndActiveTrue(Long vehicleId);

    Optional<VehiclePricing> findByVehicleId(Long vehicleId);

    boolean existsByVehicleId(Long vehicleId);
}
