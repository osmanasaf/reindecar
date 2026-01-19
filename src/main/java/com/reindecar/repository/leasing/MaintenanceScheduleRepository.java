package com.reindecar.repository.leasing;

import com.reindecar.entity.leasing.MaintenanceSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface MaintenanceScheduleRepository extends JpaRepository<MaintenanceSchedule, Long> {

    List<MaintenanceSchedule> findByRentalIdAndStatusNot(Long rentalId, MaintenanceSchedule.ScheduleStatus status);

    List<MaintenanceSchedule> findByVehicleIdOrderByCreatedAtDesc(Long vehicleId);

    List<MaintenanceSchedule> findByStatus(MaintenanceSchedule.ScheduleStatus status);

    @Query("SELECT m FROM MaintenanceSchedule m WHERE m.status IN ('SCHEDULED', 'DUE') " +
           "AND m.reminderSent = false " +
           "AND ((m.nextMaintenanceDate IS NOT NULL AND m.nextMaintenanceDate <= :reminderDate) " +
           "OR m.status = 'DUE')")
    List<MaintenanceSchedule> findDueForReminder(LocalDate reminderDate);

    Optional<MaintenanceSchedule> findByRentalIdAndStatus(Long rentalId, MaintenanceSchedule.ScheduleStatus status);

    @Query("SELECT m FROM MaintenanceSchedule m WHERE m.status IN ('DUE', 'OVERDUE')")
    List<MaintenanceSchedule> findDueOrOverdue();
}
