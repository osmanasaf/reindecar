package com.reindecar.repository.pricing;

import com.reindecar.entity.pricing.PricingRule;
import com.reindecar.entity.pricing.RentalType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PricingRuleRepository extends JpaRepository<PricingRule, Long> {

    @Query("SELECT pr FROM PricingRule pr WHERE pr.categoryId = :categoryId AND pr.rentalType = :rentalType AND pr.active = true")
    List<PricingRule> findByCategoryIdAndRentalTypeAndActive(Long categoryId, RentalType rentalType);

    @Query("SELECT pr FROM PricingRule pr WHERE " +
           "pr.categoryId = :categoryId AND " +
           "pr.rentalType = :rentalType AND " +
           "pr.active = true AND " +
           "pr.minDays <= :days AND " +
           "(pr.maxDays = 0 OR pr.maxDays >= :days) AND " +
           "(pr.validFrom IS NULL OR pr.validFrom <= :date) AND " +
           "(pr.validTo IS NULL OR pr.validTo >= :date)")
    List<PricingRule> findApplicableRules(Long categoryId, RentalType rentalType, int days, LocalDate date);
}
