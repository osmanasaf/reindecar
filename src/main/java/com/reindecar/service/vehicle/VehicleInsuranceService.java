package com.reindecar.service.vehicle;

import com.reindecar.common.valueobject.Money;
import com.reindecar.dto.vehicle.CreateVehicleInsuranceRequest;
import com.reindecar.dto.vehicle.VehicleInsuranceResponse;
import com.reindecar.entity.vehicle.VehicleInsurance;
import com.reindecar.repository.vehicle.VehicleInsuranceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class VehicleInsuranceService {

    private final VehicleInsuranceRepository insuranceRepository;
    private static final int EXPIRING_SOON_DAYS = 30;

    public List<VehicleInsuranceResponse> getByVehicleId(Long vehicleId) {
        return insuranceRepository.findByVehicleIdAndActiveTrue(vehicleId)
            .stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }

    public List<VehicleInsuranceResponse> getAllByVehicleId(Long vehicleId) {
        return insuranceRepository.findAllByVehicleIdOrderByEndDateDesc(vehicleId)
            .stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }

    @Transactional
    public VehicleInsuranceResponse createInsurance(CreateVehicleInsuranceRequest request) {
        log.info("Creating insurance for vehicleId: {}, type: {}", request.vehicleId(), request.insuranceType());

        Money premium = request.premium() != null 
            ? Money.of(request.premium(), "TRY") 
            : null;
        Money coverage = request.coverage() != null 
            ? Money.of(request.coverage(), "TRY") 
            : null;

        VehicleInsurance insurance = VehicleInsurance.create(
            request.vehicleId(),
            request.insuranceType(),
            request.policyNumber(),
            request.company(),
            request.startDate(),
            request.endDate(),
            premium,
            coverage,
            request.contactPhone(),
            request.notes()
        );

        VehicleInsurance saved = insuranceRepository.save(insurance);
        return toResponse(saved);
    }

    @Transactional
    public void deactivateInsurance(Long id) {
        log.info("Deactivating insurance: {}", id);
        VehicleInsurance insurance = insuranceRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Insurance not found"));
        insurance.deactivate();
        insuranceRepository.save(insurance);
    }

    private VehicleInsuranceResponse toResponse(VehicleInsurance insurance) {
        return new VehicleInsuranceResponse(
            insurance.getId(),
            insurance.getVehicleId(),
            insurance.getInsuranceType(),
            insurance.getPolicyNumber(),
            insurance.getCompany(),
            insurance.getStartDate(),
            insurance.getEndDate(),
            insurance.getPremium() != null ? insurance.getPremium().getAmount() : null,
            insurance.getPremium() != null ? insurance.getPremium().getCurrency() : null,
            insurance.getCoverage() != null ? insurance.getCoverage().getAmount() : null,
            insurance.getCoverage() != null ? insurance.getCoverage().getCurrency() : null,
            insurance.getContactPhone(),
            insurance.getNotes(),
            insurance.isActive(),
            insurance.isExpired(),
            insurance.isExpiringSoon(EXPIRING_SOON_DAYS),
            insurance.isValid(),
            insurance.getCreatedAt()
        );
    }
}
