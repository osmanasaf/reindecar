package com.reindecar.repository.pricing;

import com.reindecar.entity.pricing.Season;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface SeasonRepository extends JpaRepository<Season, Long> {

    List<Season> findByActiveTrue();

    @Query("SELECT s FROM Season s WHERE s.active = true AND s.startDate <= :date AND s.endDate >= :date")
    Optional<Season> findActiveSeasonForDate(LocalDate date);

    @Query("SELECT s FROM Season s WHERE s.active = true AND " +
           "((s.startDate <= :endDate AND s.endDate >= :startDate))")
    List<Season> findOverlappingSeasons(LocalDate startDate, LocalDate endDate);
}

