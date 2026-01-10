package com.reindecar.service.pricing;

import com.reindecar.common.valueobject.Money;
import com.reindecar.dto.pricing.CreateRentalPricingRequest;
import com.reindecar.dto.pricing.RentalPricingResponse;
import com.reindecar.entity.pricing.RentalPricing;
import com.reindecar.entity.pricing.RentalType;
import com.reindecar.entity.vehicle.Vehicle;
import com.reindecar.repository.pricing.RentalPricingRepository;
import com.reindecar.repository.vehicle.VehicleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class RentalPricingService {

    private final RentalPricingRepository pricingRepository;
    private final VehicleRepository vehicleRepository;

    public Optional<RentalPricingResponse> findApplicablePricing(
            Long vehicleId, Long customerId, RentalType rentalType) {

        Vehicle vehicle = vehicleRepository.findById(vehicleId)
            .orElseThrow(() -> new IllegalArgumentException("Vehicle not found"));

        Optional<RentalPricing> vehicleSpecific = pricingRepository
            .findByVehicleIdAndType(vehicleId, rentalType);
        if (vehicleSpecific.isPresent()) {
            return vehicleSpecific.map(this::toResponse);
        }

        if (customerId != null) {
            Optional<RentalPricing> customerSpecific = pricingRepository
                .findByCustomerAndCategoryAndType(customerId, vehicle.getCategoryId(), rentalType);
            if (customerSpecific.isPresent()) {
                return customerSpecific.map(this::toResponse);
            }
        }

        return pricingRepository
            .findByCategoryAndType(vehicle.getCategoryId(), rentalType)
            .map(this::toResponse);
    }

    @Transactional
    public RentalPricingResponse createPricing(CreateRentalPricingRequest request) {
        log.info("Creating rental pricing: vehicle={}, customer={}, category={}",
            request.vehicleId(), request.customerId(), request.categoryId());

        Money monthlyPrice = Money.of(request.monthlyPrice(), Money.DEFAULT_CURRENCY);
        Money extraKmPrice = Money.of(request.extraKmPrice(), Money.DEFAULT_CURRENCY);

        RentalPricing pricing;
        if (request.vehicleId() != null) {
            pricing = RentalPricing.createForVehicle(
                request.vehicleId(), request.rentalType(),
                monthlyPrice, request.kmLimit(), extraKmPrice
            );
        } else if (request.customerId() != null) {
            pricing = RentalPricing.createForCustomer(
                request.customerId(), request.categoryId(), request.rentalType(),
                monthlyPrice, request.kmLimit(), extraKmPrice
            );
        } else {
            pricing = RentalPricing.createForCategory(
                request.categoryId(), request.rentalType(),
                monthlyPrice, request.kmLimit(), extraKmPrice
            );
        }

        if (request.validFrom() != null || request.validTo() != null) {
            pricing.setValidityPeriod(request.validFrom(), request.validTo());
        }

        RentalPricing saved = pricingRepository.save(pricing);
        return toResponse(saved);
    }

    public List<RentalPricingResponse> getByVehicleId(Long vehicleId) {
        return pricingRepository.findByVehicleIdAndActiveTrue(vehicleId)
            .stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }

    public List<RentalPricingResponse> getByCustomerId(Long customerId) {
        return pricingRepository.findByCustomerIdAndActiveTrue(customerId)
            .stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }

    @Transactional
    public void deactivate(Long id) {
        RentalPricing pricing = pricingRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Pricing not found"));
        pricing.deactivate();
        pricingRepository.save(pricing);
    }

    private RentalPricingResponse toResponse(RentalPricing p) {
        String level = p.isVehicleSpecific() ? "VEHICLE" :
                       p.isCustomerSpecific() ? "CUSTOMER" : "CATEGORY";

        return new RentalPricingResponse(
            p.getId(),
            p.getVehicleId(),
            p.getCustomerId(),
            p.getCategoryId(),
            p.getRentalType(),
            p.getMonthlyPrice().getAmount(),
            p.getMonthlyPrice().getCurrency(),
            p.getKmLimit(),
            p.getExtraKmPrice().getAmount(),
            p.getValidFrom(),
            p.getValidTo(),
            p.isActive(),
            p.getNotes(),
            level
        );
    }
}
