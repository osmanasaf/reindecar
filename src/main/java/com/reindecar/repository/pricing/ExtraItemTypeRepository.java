package com.reindecar.repository.pricing;

import com.reindecar.entity.pricing.ExtraItemType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExtraItemTypeRepository extends JpaRepository<ExtraItemType, Long> {

    Optional<ExtraItemType> findByCode(String code);

    boolean existsByCode(String code);

    List<ExtraItemType> findByActiveTrueOrderBySortOrderAsc();

    List<ExtraItemType> findAllByOrderBySortOrderAsc();
}
