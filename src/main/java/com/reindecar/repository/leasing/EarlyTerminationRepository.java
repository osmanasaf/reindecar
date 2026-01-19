package com.reindecar.repository.leasing;

import com.reindecar.entity.leasing.EarlyTermination;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EarlyTerminationRepository extends JpaRepository<EarlyTermination, Long> {

    Optional<EarlyTermination> findByRentalIdAndStatusNot(Long rentalId, EarlyTermination.TerminationStatus status);

    List<EarlyTermination> findByStatus(EarlyTermination.TerminationStatus status);

    List<EarlyTermination> findByRentalId(Long rentalId);

    boolean existsByRentalIdAndStatusIn(Long rentalId, List<EarlyTermination.TerminationStatus> statuses);
}
