package com.reindecar.service.pricing;

import com.reindecar.common.exception.DuplicateEntityException;
import com.reindecar.common.exception.EntityNotFoundException;
import com.reindecar.common.valueobject.Money;
import com.reindecar.dto.pricing.CreateLeasingPlanRequest;
import com.reindecar.dto.pricing.LeasingPlanResponse;
import com.reindecar.entity.pricing.LeasingPlan;
import com.reindecar.entity.vehicle.VehicleCategory;
import com.reindecar.repository.pricing.LeasingPlanRepository;
import com.reindecar.repository.vehicle.VehicleCategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class LeasingPlanService {

    private final LeasingPlanRepository leasingPlanRepository;
    private final VehicleCategoryRepository vehicleCategoryRepository;

    public List<LeasingPlanResponse> getAllPlans() {
        return leasingPlanRepository.findByActiveTrue().stream()
            .map(this::toResponse)
            .toList();
    }

    public List<LeasingPlanResponse> getPlansByCategory(Long categoryId) {
        return leasingPlanRepository.findByCategoryIdAndActiveTrue(categoryId).stream()
            .map(this::toResponse)
            .toList();
    }

    public LeasingPlanResponse getPlanById(Long id) {
        LeasingPlan plan = leasingPlanRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("LeasingPlan", id));
        return toResponse(plan);
    }

    @Transactional
    public LeasingPlanResponse createPlan(CreateLeasingPlanRequest request) {
        log.info("Creating leasing plan for category: {}, term: {} months", 
            request.categoryId(), request.termMonths());

        validateCategoryExists(request.categoryId());
        validateNoDuplicate(request.categoryId(), request.termMonths());

        String currency = request.currency() != null ? request.currency() : Money.DEFAULT_CURRENCY;
        Money monthlyPrice = Money.of(request.monthlyBasePrice(), currency);

        LeasingPlan plan = LeasingPlan.create(
            request.categoryId(),
            request.termMonths(),
            monthlyPrice,
            request.includedKmPerMonth()
        );

        if (request.validFrom() != null || request.validTo() != null) {
            plan.setValidityPeriod(request.validFrom(), request.validTo());
        }

        LeasingPlan saved = leasingPlanRepository.save(plan);
        log.info("Leasing plan created with id: {}", saved.getId());

        return toResponse(saved);
    }

    @Transactional
    public LeasingPlanResponse updatePlan(Long id, CreateLeasingPlanRequest request) {
        log.info("Updating leasing plan: {}", id);

        LeasingPlan plan = leasingPlanRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("LeasingPlan", id));

        String currency = request.currency() != null ? request.currency() : Money.DEFAULT_CURRENCY;
        Money monthlyPrice = Money.of(request.monthlyBasePrice(), currency);

        plan.updatePricing(monthlyPrice, request.includedKmPerMonth());
        plan.setValidityPeriod(request.validFrom(), request.validTo());

        LeasingPlan saved = leasingPlanRepository.save(plan);
        return toResponse(saved);
    }

    @Transactional
    public void activatePlan(Long id) {
        LeasingPlan plan = leasingPlanRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("LeasingPlan", id));
        plan.activate();
        leasingPlanRepository.save(plan);
    }

    @Transactional
    public void deactivatePlan(Long id) {
        LeasingPlan plan = leasingPlanRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("LeasingPlan", id));
        plan.deactivate();
        leasingPlanRepository.save(plan);
    }

    private void validateCategoryExists(Long categoryId) {
        if (!vehicleCategoryRepository.existsById(categoryId)) {
            throw new EntityNotFoundException("VehicleCategory", categoryId);
        }
    }

    private void validateNoDuplicate(Long categoryId, int termMonths) {
        if (leasingPlanRepository.existsByCategoryIdAndTermMonths(categoryId, termMonths)) {
            throw new DuplicateEntityException("LeasingPlan", 
                "categoryId+termMonths", categoryId + "/" + termMonths);
        }
    }

    private LeasingPlanResponse toResponse(LeasingPlan plan) {
        String categoryName = vehicleCategoryRepository.findById(plan.getCategoryId())
            .map(VehicleCategory::getName)
            .orElse("");

        return new LeasingPlanResponse(
            plan.getId(),
            plan.getCategoryId(),
            categoryName,
            plan.getTermMonths(),
            plan.getMonthlyBasePrice().getAmount(),
            plan.getMonthlyBasePrice().getCurrency(),
            plan.getIncludedKmPerMonth(),
            plan.calculateTotalContractPrice().getAmount(),
            plan.getTotalIncludedKm(),
            plan.getValidFrom(),
            plan.getValidTo(),
            plan.isActive()
        );
    }
}
