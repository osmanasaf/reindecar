package com.reindecar.repository.pricing;

import com.reindecar.entity.pricing.VehicleCategoryPricing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface VehicleCategoryPricingRepository extends JpaRepository<VehicleCategoryPricing, Long> {

    List<VehicleCategoryPricing> findByCategoryIdAndActiveTrue(Long categoryId);

    @Query("""
        SELECT p FROM VehicleCategoryPricing p
        WHERE p.categoryId = :categoryId
        AND p.active = true
        AND (p.validFrom IS NULL OR p.validFrom <= :date)
        AND (p.validTo IS NULL OR p.validTo >= :date)
        ORDER BY p.validFrom DESC
        """)
    Optional<VehicleCategoryPricing> findApplicablePricing(
        @Param("categoryId") Long categoryId,
        @Param("date") LocalDate date
    );

    @Query("""
        SELECT p FROM VehicleCategoryPricing p
        WHERE p.categoryId = :categoryId
        AND p.active = true
        ORDER BY p.validFrom DESC
        """)
    List<VehicleCategoryPricing> findAllByCategoryIdOrderByValidFromDesc(
        @Param("categoryId") Long categoryId
    );

    boolean existsByCategoryIdAndActiveTrueAndValidFromLessThanEqualAndValidToGreaterThanEqual(
        Long categoryId, LocalDate validFrom, LocalDate validTo
    );
}
