package com.reindecar.repository.pricing;

import com.reindecar.entity.pricing.Campaign;
import com.reindecar.entity.pricing.RentalType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface CampaignRepository extends JpaRepository<Campaign, Long> {

    List<Campaign> findByActiveTrue();

    @Query("SELECT c FROM Campaign c WHERE c.active = true " +
           "AND c.validFrom <= :date AND c.validTo >= :date")
    List<Campaign> findActiveCampaigns(LocalDate date);

    @Query("SELECT c FROM Campaign c JOIN c.applicableRentalTypes rt " +
           "WHERE c.active = true " +
           "AND c.validFrom <= :date AND c.validTo >= :date " +
           "AND rt = :rentalType " +
           "AND (c.categoryId IS NULL OR c.categoryId = :categoryId)")
    List<Campaign> findApplicableCampaigns(RentalType rentalType, Long categoryId, LocalDate date);

    @Query("SELECT c FROM Campaign c JOIN c.applicableRentalTypes rt " +
           "WHERE c.active = true " +
           "AND c.validFrom <= :date AND c.validTo >= :date " +
           "AND rt = :rentalType " +
           "AND (c.categoryId IS NULL OR c.categoryId = :categoryId) " +
           "AND (c.minTermMonths IS NULL OR c.minTermMonths <= :termMonths)")
    List<Campaign> findApplicableCampaignsForLeasing(RentalType rentalType, Long categoryId, LocalDate date, int termMonths);
}
