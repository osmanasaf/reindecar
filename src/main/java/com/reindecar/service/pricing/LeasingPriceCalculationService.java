package com.reindecar.service.pricing;

import com.reindecar.common.constant.DomainConstants;
import com.reindecar.common.constant.PriceBreakdownLabels;
import com.reindecar.common.constant.ValidationMessages;
import com.reindecar.common.exception.BusinessException;
import com.reindecar.common.exception.ErrorCode;
import com.reindecar.common.valueobject.Money;
import com.reindecar.dto.pricing.*;
import com.reindecar.entity.pricing.*;
import com.reindecar.entity.vehicle.Vehicle;
import com.reindecar.entity.vehicle.VehicleCategory;
import com.reindecar.repository.pricing.*;
import com.reindecar.repository.vehicle.VehicleCategoryRepository;
import com.reindecar.repository.vehicle.VehicleRepository;
import com.reindecar.service.pricing.strategy.PriceCalculationContext;
import com.reindecar.service.pricing.strategy.PriceCalculationStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class LeasingPriceCalculationService {

    private static final int DAYS_PER_MONTH = 30;
    private static final int DECIMAL_SCALE = 2;

    private final List<PriceCalculationStrategy> strategies;
    private final VehicleRepository vehicleRepository;
    private final VehicleCategoryRepository vehicleCategoryRepository;
    private final LeasingPlanRepository leasingPlanRepository;
    private final CustomerContractRepository customerContractRepository;
    private final CampaignRepository campaignRepository;
    private final KmPackageRepository kmPackageRepository;

    public LeasingPriceCalculationResponse calculateLeasingPrice(CalculateLeasingPriceRequest request) {
        log.info("Calculating leasing price for vehicle: {}, term: {} months", 
            request.vehicleId(), request.termMonths());

        Vehicle vehicle = findVehicleOrThrow(request.vehicleId());
        VehicleCategory category = findCategoryOrThrow(vehicle.getCategoryId());

        LocalDate startDate = request.startDate();
        LocalDate endDate = startDate.plusMonths(request.termMonths());

        PriceCalculationContext context = buildPriceContext(request, vehicle, category, startDate, endDate);

        LeasingPriceComponents priceComponents = calculatePriceComponents(context, vehicle, startDate, request.termMonths());
        KmPackageResponse kmPackageResponse = buildKmPackageResponse(request.kmPackageId());

        return buildResponse(request, vehicle, startDate, endDate, priceComponents, kmPackageResponse);
    }

    private Vehicle findVehicleOrThrow(Long vehicleId) {
        return vehicleRepository.findById(vehicleId)
            .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND, 
                String.format(ValidationMessages.VEHICLE_NOT_FOUND, vehicleId)));
    }

    private VehicleCategory findCategoryOrThrow(Long categoryId) {
        return vehicleCategoryRepository.findById(categoryId)
            .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND, 
                String.format(ValidationMessages.CATEGORY_NOT_FOUND, categoryId)));
    }

    private PriceCalculationContext buildPriceContext(CalculateLeasingPriceRequest request, Vehicle vehicle, 
                                                       VehicleCategory category, LocalDate startDate, LocalDate endDate) {
        return PriceCalculationContext.builder()
            .vehicleId(request.vehicleId())
            .categoryId(vehicle.getCategoryId())
            .customerId(request.customerId())
            .rentalType(RentalType.LEASING)
            .startDate(startDate)
            .endDate(endDate)
            .termMonths(request.termMonths())
            .kmPackageId(request.kmPackageId())
            .isLeasing(true)
            .dailyPrice(vehicle.getDailyPrice())
            .weeklyPrice(vehicle.getWeeklyPrice())
            .monthlyPrice(vehicle.getMonthlyPrice())
            .categoryDefaultPrice(category.getDefaultDailyPrice())
            .build();
    }

    private LeasingPriceComponents calculatePriceComponents(PriceCalculationContext context, Vehicle vehicle, 
                                                            LocalDate startDate, int termMonths) {
        Money basePrice = calculateBasePrice(context);
        String pricingSource = determinePricingSource(context, startDate);
        int includedKmPerMonth = getIncludedKmPerMonth(context, startDate);

        List<LeasingPriceCalculationResponse.AppliedDiscount> appliedDiscounts = new ArrayList<>();
        Money totalDiscount = calculateCampaignDiscounts(vehicle, startDate, termMonths, basePrice, appliedDiscounts);
        Money netPrice = basePrice.subtract(totalDiscount);

        return new LeasingPriceComponents(basePrice, totalDiscount, netPrice, includedKmPerMonth, pricingSource, appliedDiscounts);
    }

    private Money calculateCampaignDiscounts(Vehicle vehicle, LocalDate startDate, int termMonths, 
                                             Money basePrice, List<LeasingPriceCalculationResponse.AppliedDiscount> appliedDiscounts) {
        Money totalDiscount = Money.zero(basePrice.getCurrency());
        
        List<Campaign> applicableCampaigns = campaignRepository.findApplicableCampaignsForLeasing(
            RentalType.LEASING, vehicle.getCategoryId(), startDate, termMonths);
        
        for (Campaign campaign : applicableCampaigns) {
            Money discount = campaign.getDiscountAmount(basePrice);
            totalDiscount = totalDiscount.add(discount);
            appliedDiscounts.add(new LeasingPriceCalculationResponse.AppliedDiscount(
                campaign.getName(),
                campaign.getDiscountType().name(),
                campaign.getDiscountValue(),
                discount.getAmount()
            ));
        }
        return totalDiscount;
    }

    private KmPackageResponse buildKmPackageResponse(Long kmPackageId) {
        if (kmPackageId == null) {
            return null;
        }
        return kmPackageRepository.findByIdAndActive(kmPackageId)
            .map(kp -> new KmPackageResponse(
                kp.getId(), kp.getName(), kp.getIncludedKm(),
                kp.getExtraKmPrice().getAmount(), kp.getExtraKmPrice().getCurrency(),
                kp.getApplicableTypes(), kp.isUnlimited(), kp.isActive(),
                kp.getCategoryId(), null, kp.isGlobal()
            ))
            .orElse(null);
    }

    private LeasingPriceCalculationResponse buildResponse(CalculateLeasingPriceRequest request, Vehicle vehicle,
                                                          LocalDate startDate, LocalDate endDate,
                                                          LeasingPriceComponents components,
                                                          KmPackageResponse kmPackageResponse) {
        int termMonths = request.termMonths();
        BigDecimal termMonthsBd = BigDecimal.valueOf(termMonths);

        List<PriceBreakdownItem> breakdown = buildBreakdown(
            components.basePrice(), components.netPrice(), 
            termMonths, components.appliedDiscounts());

        return new LeasingPriceCalculationResponse(
            vehicle.getId(),
            vehicle.getDisplayName(),
            request.customerId(),
            termMonths,
            startDate,
            endDate,
            divideByTerm(components.basePrice().getAmount(), termMonthsBd),
            divideByTerm(components.totalDiscount().getAmount(), termMonthsBd),
            divideByTerm(components.netPrice().getAmount(), termMonthsBd),
            components.basePrice().getAmount(),
            components.totalDiscount().getAmount(),
            components.netPrice().getAmount(),
            components.includedKmPerMonth(),
            components.includedKmPerMonth() * termMonths,
            kmPackageResponse,
            components.basePrice().getCurrency(),
            breakdown,
            components.appliedDiscounts(),
            components.pricingSource()
        );
    }

    private BigDecimal divideByTerm(BigDecimal amount, BigDecimal termMonths) {
        return amount.divide(termMonths, DECIMAL_SCALE, RoundingMode.HALF_UP);
    }

    private record LeasingPriceComponents(
        Money basePrice, Money totalDiscount, Money netPrice, 
        int includedKmPerMonth, String pricingSource,
        List<LeasingPriceCalculationResponse.AppliedDiscount> appliedDiscounts
    ) {}

    private Money calculateBasePrice(PriceCalculationContext context) {
        return strategies.stream()
            .sorted(Comparator.comparing(PriceCalculationStrategy::getPriority).reversed())
            .map(strategy -> strategy.calculatePrice(context))
            .filter(Objects::nonNull)
            .findFirst()
            .orElseGet(() -> calculateFallbackPrice(context));
    }

    private Money calculateFallbackPrice(PriceCalculationContext context) {
        Money basePrice = resolveBasePrice(context);
        return basePrice.multiply(context.getEffectiveTermMonths());
    }

    private Money resolveBasePrice(PriceCalculationContext context) {
        if (context.getMonthlyPrice() != null) {
            return context.getMonthlyPrice();
        }
        if (context.getCategoryDefaultPrice() != null) {
            return context.getCategoryDefaultPrice().multiply(DAYS_PER_MONTH);
        }
        return Money.zero();
    }

    private String determinePricingSource(PriceCalculationContext context, LocalDate date) {
        if (hasActiveCustomerContract(context, date)) {
            return DomainConstants.PRICE_SOURCE_CUSTOMER_CONTRACT;
        }
        if (hasApplicableLeasingPlan(context, date)) {
            return DomainConstants.PRICE_SOURCE_LEASING_PLAN;
        }
        return DomainConstants.PRICE_SOURCE_CATEGORY_DEFAULT;
    }

    private boolean hasActiveCustomerContract(PriceCalculationContext context, LocalDate date) {
        return context.getCustomerId() != null &&
            customerContractRepository.findActiveContract(
                context.getCustomerId(), context.getCategoryId(), date).isPresent();
    }

    private boolean hasApplicableLeasingPlan(PriceCalculationContext context, LocalDate date) {
        return leasingPlanRepository.findApplicablePlan(
            context.getCategoryId(), context.getEffectiveTermMonths(), date).isPresent();
    }

    private int getIncludedKmPerMonth(PriceCalculationContext context, LocalDate date) {
        if (context.getCustomerId() != null) {
            var contract = customerContractRepository.findActiveContract(
                context.getCustomerId(), context.getCategoryId(), date);
            if (contract.isPresent()) {
                return contract.get().getIncludedKmPerMonth();
            }
        }
        var plan = leasingPlanRepository.findApplicablePlan(
            context.getCategoryId(), context.getEffectiveTermMonths(), date);
        return plan.map(LeasingPlan::getIncludedKmPerMonth).orElse(0);
    }

    private List<PriceBreakdownItem> buildBreakdown(
            Money basePrice, Money netPrice, 
            int termMonths, List<LeasingPriceCalculationResponse.AppliedDiscount> discounts) {
        
        List<PriceBreakdownItem> breakdown = new ArrayList<>();
        
        breakdown.add(new PriceBreakdownItem(
            String.format(PriceBreakdownLabels.BASE_PRICE_MONTHS_FORMAT, termMonths),
            basePrice.getAmount()
        ));

        for (var discount : discounts) {
            breakdown.add(new PriceBreakdownItem(
                PriceBreakdownLabels.discount(discount.name()),
                discount.savedAmount().negate()
            ));
        }

        breakdown.add(new PriceBreakdownItem(
            PriceBreakdownLabels.TOTAL_NET_PRICE,
            netPrice.getAmount()
        ));

        return breakdown;
    }
}
