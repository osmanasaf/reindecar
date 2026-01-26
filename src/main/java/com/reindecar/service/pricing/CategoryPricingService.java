package com.reindecar.service.pricing;

import com.reindecar.common.valueobject.Money;
import com.reindecar.dto.pricing.CategoryPricingRequest;
import com.reindecar.dto.pricing.CategoryPricingResponse;
import com.reindecar.entity.pricing.VehicleCategoryPricing;
import com.reindecar.entity.vehicle.VehicleCategory;
import com.reindecar.repository.pricing.VehicleCategoryPricingRepository;
import com.reindecar.repository.vehicle.VehicleCategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class CategoryPricingService {

    private final VehicleCategoryPricingRepository pricingRepository;
    private final VehicleCategoryRepository categoryRepository;

    public List<CategoryPricingResponse> findByCategoryId(Long categoryId) {
        VehicleCategory category = categoryRepository.findById(categoryId)
            .orElseThrow(() -> new IllegalArgumentException("Kategori bulunamadı: " + categoryId));

        return pricingRepository.findAllByCategoryIdOrderByValidFromDesc(categoryId)
            .stream()
            .map(pricing -> toResponse(pricing, category.getName()))
            .toList();
    }

    public CategoryPricingResponse findApplicable(Long categoryId, LocalDate date) {
        VehicleCategory category = categoryRepository.findById(categoryId)
            .orElseThrow(() -> new IllegalArgumentException("Kategori bulunamadı: " + categoryId));

        return pricingRepository.findApplicablePricing(categoryId, date)
            .map(pricing -> toResponse(pricing, category.getName()))
            .orElse(null);
    }

    @Transactional
    public CategoryPricingResponse create(CategoryPricingRequest request) {
        VehicleCategory category = categoryRepository.findById(request.categoryId())
            .orElseThrow(() -> new IllegalArgumentException("Kategori bulunamadı: " + request.categoryId()));

        String currency = request.currency();

        VehicleCategoryPricing pricing = VehicleCategoryPricing.create(
            request.categoryId(),
            Money.of(request.dailyPrice(), currency),
            Money.of(request.weeklyPrice(), currency),
            Money.of(request.monthlyPrice(), currency),
            Money.of(request.yearlyPrice(), currency),
            request.validFrom(),
            request.validTo()
        );

        pricing = pricingRepository.save(pricing);
        log.info("Kategori fiyatlandırması oluşturuldu: categoryId={}, id={}",
            request.categoryId(), pricing.getId());

        return toResponse(pricing, category.getName());
    }

    @Transactional
    public CategoryPricingResponse update(Long id, CategoryPricingRequest request) {
        VehicleCategoryPricing pricing = pricingRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Fiyatlandırma bulunamadı: " + id));

        VehicleCategory category = categoryRepository.findById(pricing.getCategoryId())
            .orElseThrow(() -> new IllegalArgumentException("Kategori bulunamadı"));

        String currency = request.currency();

        pricing.updatePrices(
            Money.of(request.dailyPrice(), currency),
            Money.of(request.weeklyPrice(), currency),
            Money.of(request.monthlyPrice(), currency),
            Money.of(request.yearlyPrice(), currency)
        );
        pricing.updateValidity(request.validFrom(), request.validTo());

        pricing = pricingRepository.save(pricing);
        log.info("Kategori fiyatlandırması güncellendi: id={}", id);

        return toResponse(pricing, category.getName());
    }

    @Transactional
    public void deactivate(Long id) {
        VehicleCategoryPricing pricing = pricingRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Fiyatlandırma bulunamadı: " + id));

        pricing.deactivate();
        pricingRepository.save(pricing);
        log.info("Kategori fiyatlandırması deaktif edildi: id={}", id);
    }

    private CategoryPricingResponse toResponse(VehicleCategoryPricing pricing, String categoryName) {
        return new CategoryPricingResponse(
            pricing.getId(),
            pricing.getCategoryId(),
            categoryName,
            pricing.getDailyPrice() != null ? pricing.getDailyPrice().getAmount() : null,
            pricing.getWeeklyPrice() != null ? pricing.getWeeklyPrice().getAmount() : null,
            pricing.getMonthlyPrice() != null ? pricing.getMonthlyPrice().getAmount() : null,
            pricing.getYearlyPrice() != null ? pricing.getYearlyPrice().getAmount() : null,
            pricing.getDailyPrice() != null ? pricing.getDailyPrice().getCurrency() : "TRY",
            pricing.getValidFrom(),
            pricing.getValidTo(),
            pricing.isActive()
        );
    }
}
