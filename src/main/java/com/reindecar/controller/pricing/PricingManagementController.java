package com.reindecar.controller.pricing;

import com.reindecar.dto.pricing.CategoryPricingRequest;
import com.reindecar.dto.pricing.CategoryPricingResponse;
import com.reindecar.dto.pricing.ExtraItemTypeRequest;
import com.reindecar.dto.pricing.ExtraItemTypeResponse;
import com.reindecar.dto.pricing.RentalExtraItemRequest;
import com.reindecar.dto.pricing.RentalExtraItemResponse;
import com.reindecar.dto.pricing.TermDiscountRequest;
import com.reindecar.dto.pricing.TermDiscountResponse;
import com.reindecar.dto.pricing.VehiclePricingRequest;
import com.reindecar.dto.pricing.VehiclePricingResponse;
import com.reindecar.service.pricing.CategoryPricingService;
import com.reindecar.service.pricing.ExtraItemService;
import com.reindecar.service.pricing.TermDiscountService;
import com.reindecar.service.pricing.VehiclePricingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/pricing")
@RequiredArgsConstructor
@Tag(name = "Fiyatlandırma Yönetimi", description = "Kategori, araç ve vade bazlı fiyatlandırma işlemleri")
public class PricingManagementController {

    private final CategoryPricingService categoryPricingService;
    private final VehiclePricingService vehiclePricingService;
    private final TermDiscountService termDiscountService;
    private final ExtraItemService extraItemService;

    // ==================== Kategori Fiyatlandırma ====================

    @GetMapping("/categories/{categoryId}")
    @Operation(summary = "Kategori fiyatlarını getir")
    public ResponseEntity<List<CategoryPricingResponse>> getCategoryPricing(
            @PathVariable Long categoryId) {
        return ResponseEntity.ok(categoryPricingService.findByCategoryId(categoryId));
    }

    @GetMapping("/categories/{categoryId}/applicable")
    @Operation(summary = "Belirli tarih için geçerli kategori fiyatını getir")
    public ResponseEntity<CategoryPricingResponse> getApplicableCategoryPricing(
            @PathVariable Long categoryId,
            @RequestParam(required = false) LocalDate date) {
        LocalDate targetDate = date != null ? date : LocalDate.now();
        CategoryPricingResponse response = categoryPricingService.findApplicable(categoryId, targetDate);
        return response != null ? ResponseEntity.ok(response) : ResponseEntity.notFound().build();
    }

    @PostMapping("/categories")
    @Operation(summary = "Kategori fiyatlandırması oluştur")
    public ResponseEntity<CategoryPricingResponse> createCategoryPricing(
            @Valid @RequestBody CategoryPricingRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(categoryPricingService.create(request));
    }

    @PutMapping("/categories/{id}")
    @Operation(summary = "Kategori fiyatlandırmasını güncelle")
    public ResponseEntity<CategoryPricingResponse> updateCategoryPricing(
            @PathVariable Long id,
            @Valid @RequestBody CategoryPricingRequest request) {
        return ResponseEntity.ok(categoryPricingService.update(id, request));
    }

    @DeleteMapping("/categories/{id}")
    @Operation(summary = "Kategori fiyatlandırmasını deaktif et")
    public ResponseEntity<Void> deactivateCategoryPricing(@PathVariable Long id) {
        categoryPricingService.deactivate(id);
        return ResponseEntity.noContent().build();
    }

    // ==================== Araç Fiyatlandırma ====================

    @GetMapping("/vehicles/{vehicleId}")
    @Operation(summary = "Araç özel fiyatlarını getir")
    public ResponseEntity<VehiclePricingResponse> getVehiclePricing(
            @PathVariable Long vehicleId) {
        VehiclePricingResponse response = vehiclePricingService.findByVehicleId(vehicleId);
        return response != null ? ResponseEntity.ok(response) : ResponseEntity.notFound().build();
    }

    @PostMapping("/vehicles")
    @Operation(summary = "Araç özel fiyatlandırması oluştur/güncelle")
    public ResponseEntity<VehiclePricingResponse> createOrUpdateVehiclePricing(
            @Valid @RequestBody VehiclePricingRequest request) {
        return ResponseEntity.ok(vehiclePricingService.createOrUpdate(request));
    }

    @DeleteMapping("/vehicles/{vehicleId}")
    @Operation(summary = "Araç özel fiyatlandırmasını deaktif et")
    public ResponseEntity<Void> deactivateVehiclePricing(@PathVariable Long vehicleId) {
        vehiclePricingService.deactivate(vehicleId);
        return ResponseEntity.noContent().build();
    }

    // ==================== Vade İskontoları ====================

    @GetMapping("/term-discounts")
    @Operation(summary = "Tüm vade iskontolarını listele")
    public ResponseEntity<List<TermDiscountResponse>> getAllTermDiscounts() {
        return ResponseEntity.ok(termDiscountService.findAll());
    }

    @GetMapping("/term-discounts/category/{categoryId}")
    @Operation(summary = "Kategoriye göre vade iskontolarını listele")
    public ResponseEntity<List<TermDiscountResponse>> getTermDiscountsByCategory(
            @PathVariable Long categoryId) {
        return ResponseEntity.ok(termDiscountService.findByCategoryId(categoryId));
    }

    @GetMapping("/term-discounts/term/{termMonths}")
    @Operation(summary = "Vade süresine göre iskontoları listele")
    public ResponseEntity<List<TermDiscountResponse>> getTermDiscountsByTerm(
            @PathVariable Integer termMonths) {
        return ResponseEntity.ok(termDiscountService.findByTermMonths(termMonths));
    }

    @PostMapping("/term-discounts")
    @Operation(summary = "Vade iskontosu oluştur")
    public ResponseEntity<TermDiscountResponse> createTermDiscount(
            @Valid @RequestBody TermDiscountRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(termDiscountService.create(request));
    }

    @PutMapping("/term-discounts/{id}")
    @Operation(summary = "Vade iskontosunu güncelle")
    public ResponseEntity<TermDiscountResponse> updateTermDiscount(
            @PathVariable Long id,
            @Valid @RequestBody TermDiscountRequest request) {
        return ResponseEntity.ok(termDiscountService.update(id, request));
    }

    @DeleteMapping("/term-discounts/{id}")
    @Operation(summary = "Vade iskontosunu deaktif et")
    public ResponseEntity<Void> deactivateTermDiscount(@PathVariable Long id) {
        termDiscountService.deactivate(id);
        return ResponseEntity.noContent().build();
    }

    // ==================== Ek Kalem Türleri ====================

    @GetMapping("/extra-item-types")
    @Operation(summary = "Tüm ek kalem türlerini listele")
    public ResponseEntity<List<ExtraItemTypeResponse>> getAllExtraItemTypes() {
        return ResponseEntity.ok(extraItemService.findAllTypes());
    }

    @PostMapping("/extra-item-types")
    @Operation(summary = "Ek kalem türü oluştur")
    public ResponseEntity<ExtraItemTypeResponse> createExtraItemType(
            @Valid @RequestBody ExtraItemTypeRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(extraItemService.createType(request));
    }

    @PutMapping("/extra-item-types/{id}")
    @Operation(summary = "Ek kalem türünü güncelle")
    public ResponseEntity<ExtraItemTypeResponse> updateExtraItemType(
            @PathVariable Long id,
            @Valid @RequestBody ExtraItemTypeRequest request) {
        return ResponseEntity.ok(extraItemService.updateType(id, request));
    }

    @DeleteMapping("/extra-item-types/{id}")
    @Operation(summary = "Ek kalem türünü deaktif et")
    public ResponseEntity<Void> deactivateExtraItemType(@PathVariable Long id) {
        extraItemService.deactivateType(id);
        return ResponseEntity.noContent().build();
    }

    // ==================== Kiralama Ek Kalemleri ====================

    @GetMapping("/rentals/{rentalId}/extra-items")
    @Operation(summary = "Kiralama ek kalemlerini listele")
    public ResponseEntity<List<RentalExtraItemResponse>> getRentalExtraItems(
            @PathVariable Long rentalId) {
        return ResponseEntity.ok(extraItemService.findByRentalId(rentalId));
    }

    @PostMapping("/rentals/{rentalId}/extra-items")
    @Operation(summary = "Kiralamaya ek kalem ekle")
    public ResponseEntity<RentalExtraItemResponse> addExtraItemToRental(
            @PathVariable Long rentalId,
            @Valid @RequestBody RentalExtraItemRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(extraItemService.addItemToRental(rentalId, request));
    }

    @DeleteMapping("/extra-items/{itemId}")
    @Operation(summary = "Kiralamadan ek kalem sil")
    public ResponseEntity<Void> removeExtraItemFromRental(@PathVariable Long itemId) {
        extraItemService.removeItemFromRental(itemId);
        return ResponseEntity.noContent().build();
    }
}
