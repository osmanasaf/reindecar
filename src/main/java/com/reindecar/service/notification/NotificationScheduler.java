package com.reindecar.service.notification;

import com.reindecar.entity.notification.NotificationPriority;
import com.reindecar.entity.notification.NotificationType;
import com.reindecar.entity.vehicle.VehicleDetails;
import com.reindecar.entity.vehicle.VehicleInsurance;
import com.reindecar.repository.vehicle.VehicleDetailsRepository;
import com.reindecar.repository.vehicle.VehicleInsuranceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationScheduler {

    private final NotificationService notificationService;
    private final VehicleDetailsRepository vehicleDetailsRepository;
    private final VehicleInsuranceRepository vehicleInsuranceRepository;

    private static final int DAYS_BEFORE_DUE = 7;
    private static final Long ADMIN_USER_ID = 1L;

    @Scheduled(cron = "0 0 8 * * *")
    public void runDailyChecks() {
        log.info("Starting daily notification checks...");
        
        checkServiceDueDates();
        checkMtvDueDates();
        checkTireChangeDueDates();
        checkInsuranceExpiry();
        
        log.info("Daily notification checks completed");
    }

    public void triggerManualCheck() {
        log.info("Manual notification check triggered");
        runDailyChecks();
    }

    private void checkServiceDueDates() {
        LocalDate threshold = LocalDate.now().plusDays(DAYS_BEFORE_DUE);
        List<VehicleDetails> dueSoon = vehicleDetailsRepository.findWithServiceDueBefore(threshold);
        
        for (VehicleDetails details : dueSoon) {
            notificationService.createNotification(
                NotificationType.SERVICE_DUE,
                NotificationPriority.HIGH,
                "Servis Zamanı Yaklaşıyor",
                "Araç servis tarihi yaklaşıyor. Lütfen kontrol edin.",
                "VEHICLE",
                details.getVehicleId(),
                ADMIN_USER_ID
            );
        }
        log.debug("Service due check: {} notifications created", dueSoon.size());
    }

    private void checkMtvDueDates() {
        LocalDate threshold = LocalDate.now().plusDays(DAYS_BEFORE_DUE);
        List<VehicleDetails> dueSoon = vehicleDetailsRepository.findWithMtvDueBefore(threshold);
        
        for (VehicleDetails details : dueSoon) {
            notificationService.createNotification(
                NotificationType.MTV_DUE,
                NotificationPriority.HIGH,
                "MTV Tarihi Yaklaşıyor",
                "Araç MTV ödeme tarihi yaklaşıyor.",
                "VEHICLE",
                details.getVehicleId(),
                ADMIN_USER_ID
            );
        }
        log.debug("MTV due check: {} notifications created", dueSoon.size());
    }

    private void checkTireChangeDueDates() {
        LocalDate threshold = LocalDate.now().plusDays(DAYS_BEFORE_DUE);
        List<VehicleDetails> dueSoon = vehicleDetailsRepository.findWithTireChangeDueBefore(threshold);
        
        for (VehicleDetails details : dueSoon) {
            notificationService.createNotification(
                NotificationType.TIRE_CHANGE_DUE,
                NotificationPriority.NORMAL,
                "Lastik Değişim Zamanı",
                "Araç lastik değişim tarihi yaklaşıyor.",
                "VEHICLE",
                details.getVehicleId(),
                ADMIN_USER_ID
            );
        }
        log.debug("Tire change due check: {} notifications created", dueSoon.size());
    }

    private void checkInsuranceExpiry() {
        LocalDate threshold = LocalDate.now().plusDays(DAYS_BEFORE_DUE);
        List<VehicleInsurance> expiringSoon = vehicleInsuranceRepository.findExpiringSoon(threshold);
        
        for (VehicleInsurance insurance : expiringSoon) {
            notificationService.createNotification(
                NotificationType.INSURANCE_EXPIRING,
                NotificationPriority.URGENT,
                "Sigorta Süresi Doluyor",
                insurance.getInsuranceType().name() + " poliçesi süresi doluyor.",
                "VEHICLE",
                insurance.getVehicleId(),
                ADMIN_USER_ID
            );
        }
        log.debug("Insurance expiry check: {} notifications created", expiringSoon.size());
    }
}
