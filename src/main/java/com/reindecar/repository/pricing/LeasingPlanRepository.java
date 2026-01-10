package com.reindecar.repository.pricing;

import com.reindecar.entity.pricing.LeasingPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface LeasingPlanRepository extends JpaRepository<LeasingPlan, Long> {

    List<LeasingPlan> findByCategoryIdAndActiveTrue(Long categoryId);

    Optional<LeasingPlan> findByCategoryIdAndTermMonthsAndActiveTrue(Long categoryId, int termMonths);

    @Query("SELECT lp FROM LeasingPlan lp WHERE lp.categoryId = :categoryId " +
           "AND lp.termMonths = :termMonths AND lp.active = true " +
           "AND (lp.validFrom IS NULL OR lp.validFrom <= :date) " +
           "AND (lp.validTo IS NULL OR lp.validTo >= :date)")
    Optional<LeasingPlan> findApplicablePlan(Long categoryId, int termMonths, LocalDate date);

    List<LeasingPlan> findByActiveTrue();

    boolean existsByCategoryIdAndTermMonths(Long categoryId, int termMonths);
}
