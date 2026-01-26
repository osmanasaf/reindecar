package com.reindecar.service.pricing;

import com.reindecar.common.constant.PriceBreakdownLabels;
import com.reindecar.common.constant.ValidationMessages;
import com.reindecar.common.valueobject.Money;
import com.reindecar.dto.pricing.CalculatePriceRequest;
import com.reindecar.dto.pricing.KmPackageResponse;
import com.reindecar.dto.pricing.PriceBreakdownItem;
import com.reindecar.dto.pricing.PriceCalculationResponse;
import com.reindecar.entity.pricing.RentalType;
import com.reindecar.entity.vehicle.Vehicle;
import com.reindecar.repository.pricing.KmPackageRepository;
import com.reindecar.repository.vehicle.VehicleRepository;
import com.reindecar.service.pricing.strategy.PriceCalculationContext;
import com.reindecar.service.pricing.strategy.PriceCalculationStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
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

    private static final int DAYS_PER_WEEK = 7;

    private final List<PriceCalculationStrategy> strategies;
    private final VehicleRepository vehicleRepository;
    private final KmPackageRepository kmPackageRepository;

    public PriceCalculationResponse calculatePrice(CalculatePriceRequest request) {
        log.info("Fiyat hesaplanıyor: vehicleId={}, rentalType={}",
            request.vehicleId(), request.rentalType());

        Vehicle vehicle = vehicleRepository.findById(request.vehicleId())
            .orElseThrow(() -> new IllegalArgumentException(
                String.format(ValidationMessages.VEHICLE_NOT_FOUND, request.vehicleId())));

        int totalDays = (int) ChronoUnit.DAYS.between(request.startDate(), request.endDate()) + 1;

        PriceCalculationContext context = buildContext(request, vehicle, totalDays);

        Money finalPrice = calculateFinalPrice(context);

        List<PriceBreakdownItem> breakdown = buildBreakdown(context, finalPrice);

        KmPackageResponse kmPackageResponse = buildKmPackageResponse(request.kmPackageId());

        BigDecimal dailyPriceAmount = vehicle.getDailyPrice() != null 
            ? vehicle.getDailyPrice().getAmount() 
            : BigDecimal.ZERO;
        
        BigDecimal weeklyPriceAmount = vehicle.getWeeklyPrice() != null 
            ? vehicle.getWeeklyPrice().getAmount() 
            : BigDecimal.ZERO;
        
        BigDecimal monthlyPriceAmount = vehicle.getMonthlyPrice() != null 
            ? vehicle.getMonthlyPrice().getAmount() 
            : BigDecimal.ZERO;

        BigDecimal unitPrice = getUnitPriceForType(request.rentalType(), dailyPriceAmount, weeklyPriceAmount, monthlyPriceAmount);

        return new PriceCalculationResponse(
            vehicle.getId(),
            vehicle.getDisplayName(),
            request.customerId(),
            request.rentalType(),
            request.startDate(),
            request.endDate(),
            totalDays,
            dailyPriceAmount,
            weeklyPriceAmount,
            monthlyPriceAmount,
            unitPrice,
            finalPrice.getAmount(),
            finalPrice.getAmount(),
            finalPrice.getCurrency(),
            kmPackageResponse,
            breakdown
        );
    }

    private BigDecimal getUnitPriceForType(RentalType rentalType, BigDecimal daily, BigDecimal weekly, BigDecimal monthly) {
        return switch (rentalType) {
            case DAILY -> daily;
            case WEEKLY -> weekly;
            case MONTHLY, LEASING -> monthly;
        };
    }

    private PriceCalculationContext buildContext(
            CalculatePriceRequest request,
            Vehicle vehicle,
            int totalDays) {

        return PriceCalculationContext.builder()
            .vehicleId(request.vehicleId())
            .categoryId(vehicle.getCategoryId())
            .customerId(request.customerId())
            .rentalType(request.rentalType())
            .startDate(request.startDate())
            .endDate(request.endDate())
            .totalDays(totalDays)
            .termMonths(request.termMonths() != null ? request.termMonths() : 0)
            .kmPackageId(request.kmPackageId())
            .dailyPrice(vehicle.getDailyPrice())
            .weeklyPrice(vehicle.getWeeklyPrice())
            .monthlyPrice(vehicle.getMonthlyPrice())
            .build();
    }

    private Money calculateFinalPrice(PriceCalculationContext context) {
        Money price = strategies.stream()
            .sorted(Comparator.comparing(PriceCalculationStrategy::getPriority).reversed())
            .map(strategy -> strategy.calculatePrice(context))
            .filter(Objects::nonNull)
            .findFirst()
            .orElseGet(() -> calculateFallbackPrice(context));

        log.debug("Hesaplanan fiyat: {}", price);
        return price;
    }

    private Money calculateFallbackPrice(PriceCalculationContext context) {
        Money dailyPrice = context.getDailyPrice();
        return dailyPrice != null ? dailyPrice.multiply(context.getTotalDays()) : Money.zero();
    }

    private List<PriceBreakdownItem> buildBreakdown(PriceCalculationContext context, Money finalPrice) {
        List<PriceBreakdownItem> breakdown = new ArrayList<>();

        RentalType rentalType = context.getRentalType();

        switch (rentalType) {
            case DAILY -> buildDailyBreakdown(context, breakdown);
            case WEEKLY -> buildWeeklyBreakdown(context, breakdown);
            case MONTHLY -> buildMonthlyBreakdown(context, breakdown);
            case LEASING -> buildLeasingBreakdown(context, finalPrice, breakdown);
        }

        breakdown.add(new PriceBreakdownItem(PriceBreakdownLabels.TOTAL, finalPrice.getAmount()));

        return breakdown;
    }

    private void buildDailyBreakdown(PriceCalculationContext context, List<PriceBreakdownItem> breakdown) {
        Money dailyPrice = context.getDailyPrice();
        if (dailyPrice != null) {
            breakdown.add(new PriceBreakdownItem(
                PriceBreakdownLabels.dailyPrice(context.getTotalDays()),
                dailyPrice.multiply(context.getTotalDays()).getAmount()
            ));
        }
    }

    private void buildWeeklyBreakdown(PriceCalculationContext context, List<PriceBreakdownItem> breakdown) {
        int totalDays = context.getTotalDays();
        int weeks = totalDays / DAYS_PER_WEEK;
        int remainingDays = totalDays % DAYS_PER_WEEK;

        Money weeklyPrice = context.getWeeklyPrice();
        Money dailyPrice = context.getDailyPrice();

        if (weeklyPrice != null && weeks > 0) {
            breakdown.add(new PriceBreakdownItem(
                PriceBreakdownLabels.weeklyPrice(weeks),
                weeklyPrice.multiply(weeks).getAmount()
            ));
        }
        if (dailyPrice != null && remainingDays > 0) {
            breakdown.add(new PriceBreakdownItem(
                PriceBreakdownLabels.remainingDays(remainingDays),
                dailyPrice.multiply(remainingDays).getAmount()
            ));
        }
    }

    private void buildMonthlyBreakdown(PriceCalculationContext context, List<PriceBreakdownItem> breakdown) {
        LocalDate startDate = context.getStartDate();
        LocalDate endDate = context.getEndDate();

        Period period = Period.between(startDate, endDate);
        int fullMonths = period.getYears() * 12 + period.getMonths();
        int remainingDays = period.getDays();

        Money monthlyPrice = context.getMonthlyPrice();
        Money dailyPrice = context.getDailyPrice();

        if (monthlyPrice != null && fullMonths > 0) {
            breakdown.add(new PriceBreakdownItem(
                PriceBreakdownLabels.monthlyPrice(fullMonths),
                monthlyPrice.multiply(fullMonths).getAmount()
            ));
        }
        if (dailyPrice != null && remainingDays > 0) {
            breakdown.add(new PriceBreakdownItem(
                PriceBreakdownLabels.remainingDays(remainingDays),
                dailyPrice.multiply(remainingDays).getAmount()
            ));
        }
    }

    private void buildLeasingBreakdown(PriceCalculationContext context, Money finalPrice, List<PriceBreakdownItem> breakdown) {
        int termMonths = context.getTermMonths();
        Money monthlyPrice = context.getMonthlyPrice();

        if (monthlyPrice != null && termMonths > 0) {
            Money basePrice = monthlyPrice.multiply(termMonths);
            breakdown.add(new PriceBreakdownItem(
                PriceBreakdownLabels.basePrice(termMonths),
                basePrice.getAmount()
            ));

            Money discount = basePrice.subtract(finalPrice);
            if (discount.isPositive()) {
                breakdown.add(new PriceBreakdownItem(
                    PriceBreakdownLabels.TERM_DISCOUNT,
                    discount.getAmount().negate()
                ));
            }
        }
    }

    private KmPackageResponse buildKmPackageResponse(Long kmPackageId) {
        if (kmPackageId == null) {
            return null;
        }

        return kmPackageRepository.findByIdAndActive(kmPackageId)
            .map(kmPackage -> new KmPackageResponse(
                kmPackage.getId(),
                kmPackage.getName(),
                kmPackage.getIncludedKm(),
                kmPackage.getExtraKmPrice().getAmount(),
                kmPackage.getExtraKmPrice().getCurrency(),
                kmPackage.getApplicableTypes(),
                kmPackage.isUnlimited(),
                kmPackage.isActive(),
                kmPackage.getCategoryId(),
                null,
                kmPackage.isGlobal()
            ))
            .orElse(null);
    }
}
