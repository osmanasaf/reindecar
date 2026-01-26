package com.reindecar.repository.pricing;

import com.reindecar.entity.pricing.TermDiscount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TermDiscountRepository extends JpaRepository<TermDiscount, Long> {

    List<TermDiscount> findByActiveTrue();

    List<TermDiscount> findByCategoryIdAndActiveTrue(Long categoryId);

    @Query("""
        SELECT d FROM TermDiscount d
        WHERE d.active = true
        AND d.termMonths = :termMonths
        AND (d.categoryId IS NULL OR d.categoryId = :categoryId)
        ORDER BY d.categoryId DESC NULLS LAST
        """)
    List<TermDiscount> findApplicableDiscounts(
        @Param("categoryId") Long categoryId,
        @Param("termMonths") Integer termMonths
    );

    @Query("""
        SELECT d FROM TermDiscount d
        WHERE d.active = true
        AND d.termMonths = :termMonths
        AND (d.categoryId IS NULL OR d.categoryId = :categoryId)
        ORDER BY d.categoryId DESC NULLS LAST
        """)
    Optional<TermDiscount> findBestDiscount(
        @Param("categoryId") Long categoryId,
        @Param("termMonths") Integer termMonths
    );

    Optional<TermDiscount> findByCategoryIdAndTermMonths(Long categoryId, Integer termMonths);

    List<TermDiscount> findByTermMonthsAndActiveTrue(Integer termMonths);
}
