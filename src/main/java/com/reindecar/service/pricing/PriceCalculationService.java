package com.reindecar.service.pricing;

import com.reindecar.common.valueobject.Money;
import com.reindecar.dto.pricing.CalculatePriceRequest;
import com.reindecar.dto.pricing.KmPackageResponse;
import com.reindecar.dto.pricing.PriceBreakdownItem;
import com.reindecar.dto.pricing.PriceCalculationResponse;
import com.reindecar.entity.pricing.KmPackage;
import com.reindecar.entity.vehicle.Vehicle;
import com.reindecar.entity.vehicle.VehicleCategory;
import com.reindecar.repository.pricing.KmPackageRepository;
import com.reindecar.repository.vehicle.VehicleCategoryRepository;
import com.reindecar.repository.vehicle.VehicleRepository;
import com.reindecar.service.pricing.strategy.PriceCalculationContext;
import com.reindecar.service.pricing.strategy.PriceCalculationStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class PriceCalculationService {

    private final List<PriceCalculationStrategy> strategies;
    private final VehicleRepository vehicleRepository;
    private final VehicleCategoryRepository vehicleCategoryRepository;
    private final KmPackageRepository kmPackageRepository;

    public PriceCalculationResponse calculatePrice(CalculatePriceRequest request) {
        log.info("Calculating price for vehicle: {}", request.vehicleId());

        Vehicle vehicle = vehicleRepository.findById(request.vehicleId())
            .orElseThrow(() -> new IllegalArgumentException("Vehicle not found"));

        VehicleCategory category = vehicleCategoryRepository.findById(vehicle.getCategoryId())
            .orElseThrow(() -> new IllegalArgumentException("Category not found"));

        int totalDays = (int) ChronoUnit.DAYS.between(request.startDate(), request.endDate()) + 1;

        PriceCalculationContext context = PriceCalculationContext.builder()
            .vehicleId(request.vehicleId())
            .categoryId(vehicle.getCategoryId())
            .customerId(request.customerId())
            .rentalType(request.rentalType())
            .startDate(request.startDate())
            .endDate(request.endDate())
            .totalDays(totalDays)
            .categoryDefaultPrice(category.getDefaultDailyPrice())
            .build();

        Money finalPrice = strategies.stream()
            .sorted(Comparator.comparing(PriceCalculationStrategy::getPriority).reversed())
            .map(strategy -> {
                Money price = strategy.calculatePrice(context);
                if (price != null) {
                    log.info("Strategy {} calculated price: {}", strategy.getStrategyName(), price);
                }
                return price;
            })
            .filter(Objects::nonNull)
            .findFirst()
            .orElse(context.getCategoryDefaultPrice().multiply(totalDays));

        List<PriceBreakdownItem> breakdown = buildBreakdown(context, finalPrice);

        KmPackageResponse kmPackageResponse = null;
        if (request.kmPackageId() != null) {
            KmPackage kmPackage = kmPackageRepository.findByIdAndActive(request.kmPackageId())
                .orElse(null);
            if (kmPackage != null) {
                kmPackageResponse = new KmPackageResponse(
                    kmPackage.getId(),
                    kmPackage.getName(),
                    kmPackage.getIncludedKm(),
                    kmPackage.getExtraKmPrice().getAmount(),
                    kmPackage.isUnlimited()
                );
            }
        }

        return new PriceCalculationResponse(
            vehicle.getId(),
            vehicle.getDisplayName(),
            request.customerId(),
            request.rentalType(),
            request.startDate(),
            request.endDate(),
            totalDays,
            category.getDefaultDailyPrice().getAmount(),
            finalPrice.getAmount(),
            finalPrice.getAmount(),
            finalPrice.getCurrency(),
            kmPackageResponse,
            breakdown
        );
    }

    private List<PriceBreakdownItem> buildBreakdown(PriceCalculationContext context, Money finalPrice) {
        List<PriceBreakdownItem> breakdown = new ArrayList<>();
        
        breakdown.add(new PriceBreakdownItem(
            "Base Price (" + context.getTotalDays() + " days)",
            context.getCategoryDefaultPrice().multiply(context.getTotalDays()).getAmount()
        ));

        breakdown.add(new PriceBreakdownItem(
            "Final Total",
            finalPrice.getAmount()
        ));

        return breakdown;
    }
}
