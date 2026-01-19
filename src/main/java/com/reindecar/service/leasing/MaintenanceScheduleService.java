package com.reindecar.service.leasing;

import com.reindecar.common.exception.BusinessException;
import com.reindecar.common.exception.ErrorCode;
import com.reindecar.dto.leasing.CreateMaintenanceScheduleRequest;
import com.reindecar.dto.leasing.MaintenanceScheduleResponse;
import com.reindecar.entity.leasing.MaintenanceSchedule;
import com.reindecar.entity.rental.Rental;
import com.reindecar.repository.leasing.MaintenanceScheduleRepository;
import com.reindecar.repository.rental.RentalRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MaintenanceScheduleService {

    private static final int DEFAULT_KM_INTERVAL = 10000;
    private static final int DEFAULT_DAYS_INTERVAL = 180;

    private final MaintenanceScheduleRepository scheduleRepository;
    private final RentalRepository rentalRepository;

    @Transactional
    public MaintenanceScheduleResponse create(Long rentalId, CreateMaintenanceScheduleRequest request) {
        Rental rental = getRentalOrThrow(rentalId);
        
        MaintenanceSchedule schedule;
        int currentKm = request.currentKm() != null ? request.currentKm() : 0;
        
        switch (request.scheduleType()) {
            case KM_BASED -> {
                int intervalKm = request.intervalKm() != null ? request.intervalKm() : DEFAULT_KM_INTERVAL;
                schedule = MaintenanceSchedule.createKmBased(rentalId, rental.getVehicleId(), currentKm, intervalKm);
            }
            case TIME_BASED -> {
                int intervalDays = request.intervalDays() != null ? request.intervalDays() : DEFAULT_DAYS_INTERVAL;
                schedule = MaintenanceSchedule.createTimeBased(rentalId, rental.getVehicleId(), intervalDays);
            }
            case HYBRID -> {
                int intervalKm = request.intervalKm() != null ? request.intervalKm() : DEFAULT_KM_INTERVAL;
                int intervalDays = request.intervalDays() != null ? request.intervalDays() : DEFAULT_DAYS_INTERVAL;
                schedule = MaintenanceSchedule.createHybrid(rentalId, rental.getVehicleId(), currentKm, intervalKm, intervalDays);
            }
            default -> throw new BusinessException(ErrorCode.INVALID_PARAMETER, "Invalid schedule type");
        }
        
        MaintenanceSchedule saved = scheduleRepository.save(schedule);
        log.info("Maintenance schedule created for rental {}: type={}", rentalId, request.scheduleType());
        
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<MaintenanceScheduleResponse> getByRental(Long rentalId) {
        return scheduleRepository.findByRentalIdAndStatusNot(rentalId, MaintenanceSchedule.ScheduleStatus.CANCELLED)
            .stream()
            .map(this::toResponse)
            .toList();
    }

    @Transactional(readOnly = true)
    public List<MaintenanceScheduleResponse> getDueSchedules() {
        return scheduleRepository.findDueOrOverdue()
            .stream()
            .map(this::toResponse)
            .toList();
    }

    @Transactional(readOnly = true)
    public MaintenanceScheduleResponse getById(Long id) {
        return toResponse(getScheduleOrThrow(id));
    }

    @Transactional
    public void complete(Long id, int completedAtKm) {
        MaintenanceSchedule schedule = getScheduleOrThrow(id);
        schedule.complete(completedAtKm);
        scheduleRepository.save(schedule);
        log.info("Maintenance schedule completed: {} at {} km", id, completedAtKm);
    }

    @Transactional
    public void cancel(Long id) {
        MaintenanceSchedule schedule = getScheduleOrThrow(id);
        schedule.cancel();
        scheduleRepository.save(schedule);
        log.info("Maintenance schedule cancelled: {}", id);
    }

    @Transactional
    public void updateStatuses() {
        LocalDate today = LocalDate.now();
        List<MaintenanceSchedule> schedules = scheduleRepository.findByStatus(MaintenanceSchedule.ScheduleStatus.SCHEDULED);
        
        for (MaintenanceSchedule schedule : schedules) {
            schedule.checkDueStatus(0, today);
            scheduleRepository.save(schedule);
        }
        
        log.info("Maintenance statuses updated");
    }

    @Transactional
    public List<MaintenanceScheduleResponse> getAndMarkDueForReminder() {
        LocalDate reminderDate = LocalDate.now().plusDays(7);
        List<MaintenanceSchedule> dueSchedules = scheduleRepository.findDueForReminder(reminderDate);
        
        for (MaintenanceSchedule schedule : dueSchedules) {
            schedule.markReminderSent();
            scheduleRepository.save(schedule);
        }
        
        return dueSchedules.stream().map(this::toResponse).toList();
    }

    private Rental getRentalOrThrow(Long rentalId) {
        return rentalRepository.findById(rentalId)
            .orElseThrow(() -> new BusinessException(ErrorCode.RENTAL_NOT_FOUND, rentalId.toString()));
    }

    private MaintenanceSchedule getScheduleOrThrow(Long id) {
        return scheduleRepository.findById(id)
            .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND, "Schedule not found: " + id));
    }

    private MaintenanceScheduleResponse toResponse(MaintenanceSchedule schedule) {
        return new MaintenanceScheduleResponse(
            schedule.getId(),
            schedule.getRentalId(),
            schedule.getVehicleId(),
            schedule.getScheduleType(),
            schedule.getNextMaintenanceKm(),
            schedule.getNextMaintenanceDate(),
            schedule.getLastMaintenanceKm(),
            schedule.getLastMaintenanceDate(),
            schedule.getMaintenanceIntervalKm(),
            schedule.getMaintenanceIntervalDays(),
            schedule.isReminderSent(),
            schedule.getStatus(),
            schedule.getNotes(),
            schedule.getCreatedAt()
        );
    }
}
