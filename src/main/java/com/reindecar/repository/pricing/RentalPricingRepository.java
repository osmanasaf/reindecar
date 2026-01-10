package com.reindecar.repository.pricing;

import com.reindecar.entity.pricing.RentalPricing;
import com.reindecar.entity.pricing.RentalType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RentalPricingRepository extends JpaRepository<RentalPricing, Long> {

    @Query("SELECT rp FROM RentalPricing rp WHERE rp.vehicleId = :vehicleId AND rp.rentalType = :type AND rp.active = true")
    Optional<RentalPricing> findByVehicleIdAndType(Long vehicleId, RentalType type);

    @Query("SELECT rp FROM RentalPricing rp WHERE rp.customerId = :customerId AND rp.categoryId = :categoryId AND rp.rentalType = :type AND rp.active = true")
    Optional<RentalPricing> findByCustomerAndCategoryAndType(Long customerId, Long categoryId, RentalType type);

    @Query("SELECT rp FROM RentalPricing rp WHERE rp.categoryId = :categoryId AND rp.vehicleId IS NULL AND rp.customerId IS NULL AND rp.rentalType = :type AND rp.active = true")
    Optional<RentalPricing> findByCategoryAndType(Long categoryId, RentalType type);

    List<RentalPricing> findByVehicleIdAndActiveTrue(Long vehicleId);

    List<RentalPricing> findByCustomerIdAndActiveTrue(Long customerId);

    List<RentalPricing> findByCategoryIdAndActiveTrue(Long categoryId);
}
