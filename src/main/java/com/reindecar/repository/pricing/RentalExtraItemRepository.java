package com.reindecar.repository.pricing;

import com.reindecar.entity.pricing.RentalExtraItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RentalExtraItemRepository extends JpaRepository<RentalExtraItem, Long> {

    List<RentalExtraItem> findByRentalId(Long rentalId);

    @Query("""
        SELECT i FROM RentalExtraItem i
        WHERE i.rentalId = :rentalId
        AND i.itemTypeId IS NOT NULL
        """)
    List<RentalExtraItem> findPredefinedItemsByRentalId(@Param("rentalId") Long rentalId);

    @Query("""
        SELECT i FROM RentalExtraItem i
        WHERE i.rentalId = :rentalId
        AND i.itemTypeId IS NULL
        """)
    List<RentalExtraItem> findCustomItemsByRentalId(@Param("rentalId") Long rentalId);

    void deleteByRentalId(Long rentalId);
}
