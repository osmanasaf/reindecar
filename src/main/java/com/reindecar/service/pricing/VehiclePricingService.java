package com.reindecar.service.pricing;

import com.reindecar.common.valueobject.Money;
import com.reindecar.dto.pricing.VehiclePricingRequest;
import com.reindecar.dto.pricing.VehiclePricingResponse;
import com.reindecar.entity.pricing.VehiclePricing;
import com.reindecar.entity.vehicle.Vehicle;
import com.reindecar.repository.pricing.VehiclePricingRepository;
import com.reindecar.repository.vehicle.VehicleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class VehiclePricingService {

    private final VehiclePricingRepository pricingRepository;
    private final VehicleRepository vehicleRepository;

    public VehiclePricingResponse findByVehicleId(Long vehicleId) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
            .orElseThrow(() -> new IllegalArgumentException("Araç bulunamadı: " + vehicleId));

        return pricingRepository.findByVehicleIdAndActiveTrue(vehicleId)
            .map(pricing -> toResponse(pricing, vehicle.getDisplayName()))
            .orElse(null);
    }

    @Transactional
    public VehiclePricingResponse createOrUpdate(VehiclePricingRequest request) {
        Vehicle vehicle = vehicleRepository.findById(request.vehicleId())
            .orElseThrow(() -> new IllegalArgumentException("Araç bulunamadı: " + request.vehicleId()));

        VehiclePricing pricing = pricingRepository.findByVehicleId(request.vehicleId())
            .orElseGet(() -> VehiclePricing.create(request.vehicleId()));

        String currency = request.currency();

        if (request.dailyPrice() != null) {
            pricing.setDailyPrice(Money.of(request.dailyPrice(), currency));
        }
        if (request.weeklyPrice() != null) {
            pricing.setWeeklyPrice(Money.of(request.weeklyPrice(), currency));
        }
        if (request.monthlyPrice() != null) {
            pricing.setMonthlyPrice(Money.of(request.monthlyPrice(), currency));
        }
        if (request.yearlyPrice() != null) {
            pricing.setYearlyPrice(Money.of(request.yearlyPrice(), currency));
        }

        pricing = pricingRepository.save(pricing);
        log.info("Araç fiyatlandırması kaydedildi: vehicleId={}, id={}",
            request.vehicleId(), pricing.getId());

        return toResponse(pricing, vehicle.getDisplayName());
    }

    @Transactional
    public void deactivate(Long vehicleId) {
        VehiclePricing pricing = pricingRepository.findByVehicleIdAndActiveTrue(vehicleId)
            .orElseThrow(() -> new IllegalArgumentException("Araç fiyatlandırması bulunamadı: " + vehicleId));

        pricing.deactivate();
        pricingRepository.save(pricing);
        log.info("Araç fiyatlandırması deaktif edildi: vehicleId={}", vehicleId);
    }

    private VehiclePricingResponse toResponse(VehiclePricing pricing, String vehicleName) {
        String currency = "TRY";
        if (pricing.getDailyPrice() != null) {
            currency = pricing.getDailyPrice().getCurrency();
        }

        return new VehiclePricingResponse(
            pricing.getId(),
            pricing.getVehicleId(),
            vehicleName,
            pricing.getDailyPrice() != null ? pricing.getDailyPrice().getAmount() : null,
            pricing.getWeeklyPrice() != null ? pricing.getWeeklyPrice().getAmount() : null,
            pricing.getMonthlyPrice() != null ? pricing.getMonthlyPrice().getAmount() : null,
            pricing.getYearlyPrice() != null ? pricing.getYearlyPrice().getAmount() : null,
            currency,
            pricing.isActive()
        );
    }
}
