package com.reindecar.service.pricing;

import com.reindecar.dto.pricing.TermDiscountRequest;
import com.reindecar.dto.pricing.TermDiscountResponse;
import com.reindecar.entity.pricing.TermDiscount;
import com.reindecar.entity.vehicle.VehicleCategory;
import com.reindecar.repository.pricing.TermDiscountRepository;
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
public class TermDiscountService {

    private final TermDiscountRepository discountRepository;
    private final VehicleCategoryRepository categoryRepository;

    public List<TermDiscountResponse> findAll() {
        return discountRepository.findByActiveTrue()
            .stream()
            .map(this::toResponse)
            .toList();
    }

    public List<TermDiscountResponse> findByCategoryId(Long categoryId) {
        return discountRepository.findByCategoryIdAndActiveTrue(categoryId)
            .stream()
            .map(this::toResponse)
            .toList();
    }

    public List<TermDiscountResponse> findByTermMonths(Integer termMonths) {
        return discountRepository.findByTermMonthsAndActiveTrue(termMonths)
            .stream()
            .map(this::toResponse)
            .toList();
    }

    @Transactional
    public TermDiscountResponse create(TermDiscountRequest request) {
        TermDiscount discount = switch (request.discountType()) {
            case PERCENTAGE -> request.categoryId() != null
                ? TermDiscount.createPercentage(request.categoryId(), request.termMonths(), request.discountValue())
                : TermDiscount.createGlobalPercentage(request.termMonths(), request.discountValue());
            case FIXED_AMOUNT -> request.categoryId() != null
                ? TermDiscount.createFixedAmount(request.categoryId(), request.termMonths(), request.discountValue())
                : TermDiscount.createGlobalFixedAmount(request.termMonths(), request.discountValue());
        };

        discount = discountRepository.save(discount);
        log.info("Vade iskontosu oluşturuldu: termMonths={}, type={}, value={}",
            request.termMonths(), request.discountType(), request.discountValue());

        return toResponse(discount);
    }

    @Transactional
    public TermDiscountResponse update(Long id, TermDiscountRequest request) {
        TermDiscount discount = discountRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("İskonto bulunamadı: " + id));

        discount.update(request.discountType(), request.discountValue());
        discount = discountRepository.save(discount);

        log.info("Vade iskontosu güncellendi: id={}", id);
        return toResponse(discount);
    }

    @Transactional
    public void deactivate(Long id) {
        TermDiscount discount = discountRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("İskonto bulunamadı: " + id));

        discount.deactivate();
        discountRepository.save(discount);
        log.info("Vade iskontosu deaktif edildi: id={}", id);
    }

    private TermDiscountResponse toResponse(TermDiscount discount) {
        String categoryName = null;
        if (discount.getCategoryId() != null) {
            categoryName = categoryRepository.findById(discount.getCategoryId())
                .map(VehicleCategory::getName)
                .orElse(null);
        }

        return new TermDiscountResponse(
            discount.getId(),
            discount.getCategoryId(),
            categoryName,
            discount.getTermMonths(),
            discount.getDiscountType(),
            discount.getDiscountValue(),
            discount.isActive()
        );
    }
}
