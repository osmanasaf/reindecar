package com.reindecar.service.vehicle;

import com.reindecar.common.dto.PageResponse;
import com.reindecar.dto.vehicle.VehicleStatusHistoryResponse;
import com.reindecar.entity.vehicle.VehicleStatus;
import com.reindecar.entity.vehicle.VehicleStatusHistory;
import com.reindecar.mapper.vehicle.VehicleStatusHistoryMapper;
import com.reindecar.repository.vehicle.VehicleStatusHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class VehicleStatusService {

    private final VehicleStatusHistoryRepository historyRepository;
    private final VehicleStatusHistoryMapper historyMapper;

    @Transactional
    public void recordStatusChange(
            Long vehicleId,
            VehicleStatus oldStatus,
            VehicleStatus newStatus,
            String referenceType,
            Long referenceId,
            String reason,
            String changedBy) {
        
        log.info("Recording status change for vehicle {}: {} -> {}", vehicleId, oldStatus, newStatus);

        VehicleStatusHistory history = VehicleStatusHistory.create(
            vehicleId,
            oldStatus,
            newStatus,
            referenceType,
            referenceId,
            reason,
            changedBy
        );

        historyRepository.save(history);
    }

    public PageResponse<VehicleStatusHistoryResponse> getVehicleHistory(Long vehicleId, Pageable pageable) {
        log.info("Fetching status history for vehicle: {}", vehicleId);
        Page<VehicleStatusHistory> history = historyRepository.findByVehicleIdOrderByChangedAtDesc(vehicleId, pageable);
        return PageResponse.of(history.map(historyMapper::toResponse));
    }
}
