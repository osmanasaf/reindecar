package com.reindecar.service.pricing;

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

        Vehicle vehicle = vehicleRepository.findById(request.vehicleId())
            .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND, "Vehicle not found"));

        VehicleCategory category = vehicleCategoryRepository.findById(vehicle.getCategoryId())
            .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND, "Category not found"));

        LocalDate startDate = request.startDate();
        LocalDate endDate = startDate.plusMonths(request.termMonths());

        PriceCalculationContext context = PriceCalculationContext.builder()
            .vehicleId(request.vehicleId())
            .categoryId(vehicle.getCategoryId())
            .customerId(request.customerId())
            .rentalType(RentalType.LEASING)
            .startDate(startDate)
            .endDate(endDate)
            .termMonths(request.termMonths())
            .kmPackageId(request.kmPackageId())
            .isLeasing(true)
            .categoryDefaultPrice(category.getDefaultDailyPrice())
            .build();

        Money basePrice = calculateBasePrice(context);
        String pricingSource = determinePricingSource(context, startDate);
        int includedKmPerMonth = getIncludedKmPerMonth(context, startDate);

        List<LeasingPriceCalculationResponse.AppliedDiscount> appliedDiscounts = new ArrayList<>();
        Money totalDiscount = Money.zero(basePrice.getCurrency());
        
        List<Campaign> applicableCampaigns = campaignRepository.findApplicableCampaignsForLeasing(
            RentalType.LEASING, vehicle.getCategoryId(), startDate, request.termMonths());
        
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

        Money netPrice = basePrice.subtract(totalDiscount);
        Money monthlyNetPrice = Money.of(
            netPrice.getAmount().divide(BigDecimal.valueOf(request.termMonths()), 2, java.math.RoundingMode.HALF_UP),
            netPrice.getCurrency()
        );

        KmPackageResponse kmPackageResponse = null;
        if (request.kmPackageId() != null) {
            kmPackageResponse = kmPackageRepository.findByIdAndActive(request.kmPackageId())
                .map(kp -> new KmPackageResponse(
                    kp.getId(), kp.getName(), kp.getIncludedKm(), 
                    kp.getExtraKmPrice().getAmount(), kp.isUnlimited()
                ))
                .orElse(null);
        }

        List<PriceBreakdownItem> breakdown = buildBreakdown(
            basePrice, totalDiscount, netPrice, request.termMonths(), appliedDiscounts);

        return new LeasingPriceCalculationResponse(
            vehicle.getId(),
            vehicle.getDisplayName(),
            request.customerId(),
            request.termMonths(),
            startDate,
            endDate,
            basePrice.getAmount().divide(BigDecimal.valueOf(request.termMonths()), 2, java.math.RoundingMode.HALF_UP),
            totalDiscount.getAmount().divide(BigDecimal.valueOf(request.termMonths()), 2, java.math.RoundingMode.HALF_UP),
            monthlyNetPrice.getAmount(),
            basePrice.getAmount(),
            totalDiscount.getAmount(),
            netPrice.getAmount(),
            includedKmPerMonth,
            includedKmPerMonth * request.termMonths(),
            kmPackageResponse,
            basePrice.getCurrency(),
            breakdown,
            appliedDiscounts,
            pricingSource
        );
    }

    private Money calculateBasePrice(PriceCalculationContext context) {
        return strategies.stream()
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
            .orElseGet(() -> {
                return context.getCategoryDefaultPrice()
                    .multiply(DAYS_PER_MONTH)
                    .multiply(context.getEffectiveTermMonths());
            });
    }

    private String determinePricingSource(PriceCalculationContext context, LocalDate date) {
        if (context.getCustomerId() != null) {
            if (customerContractRepository.findActiveContract(
                    context.getCustomerId(), context.getCategoryId(), date).isPresent()) {
                return "CUSTOMER_CONTRACT";
            }
        }
        if (leasingPlanRepository.findApplicablePlan(
                context.getCategoryId(), context.getEffectiveTermMonths(), date).isPresent()) {
            return "LEASING_PLAN";
        }
        return "CATEGORY_DEFAULT";
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
            Money basePrice, Money totalDiscount, Money netPrice, 
            int termMonths, List<LeasingPriceCalculationResponse.AppliedDiscount> discounts) {
        
        List<PriceBreakdownItem> breakdown = new ArrayList<>();
        
        breakdown.add(new PriceBreakdownItem(
            "Base Price (" + termMonths + " months)",
            basePrice.getAmount()
        ));

        for (var discount : discounts) {
            breakdown.add(new PriceBreakdownItem(
                "Discount: " + discount.name(),
                discount.savedAmount().negate()
            ));
        }

        breakdown.add(new PriceBreakdownItem(
            "Total Net Price",
            netPrice.getAmount()
        ));

        return breakdown;
    }
}
