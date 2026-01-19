package com.reindecar.repository.leasing;

import com.reindecar.entity.leasing.LeasingKmRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LeasingKmRecordRepository extends JpaRepository<LeasingKmRecord, Long> {

    List<LeasingKmRecord> findByRentalIdOrderByRecordDateDesc(Long rentalId);

    Optional<LeasingKmRecord> findTopByRentalIdOrderByRecordDateDesc(Long rentalId);

    Optional<LeasingKmRecord> findByRentalIdAndPeriodYearMonth(Long rentalId, String periodYearMonth);

    @Query("SELECT SUM(r.excessKm) FROM LeasingKmRecord r WHERE r.rentalId = :rentalId")
    Integer getTotalExcessKm(Long rentalId);

    @Query("SELECT SUM(r.usedKm) FROM LeasingKmRecord r WHERE r.rentalId = :rentalId")
    Integer getTotalUsedKm(Long rentalId);

    boolean existsByRentalIdAndPeriodYearMonth(Long rentalId, String periodYearMonth);
}
