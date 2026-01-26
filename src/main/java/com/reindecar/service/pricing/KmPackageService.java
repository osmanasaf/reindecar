package com.reindecar.service.pricing;

import com.reindecar.common.exception.EntityNotFoundException;
import com.reindecar.common.valueobject.Money;
import com.reindecar.dto.pricing.CreateKmPackageRequest;
import com.reindecar.dto.pricing.KmPackageResponse;
import com.reindecar.dto.pricing.UpdateKmPackageRequest;
import com.reindecar.entity.pricing.KmPackage;
import com.reindecar.entity.pricing.RentalType;
import com.reindecar.entity.vehicle.VehicleCategory;
import com.reindecar.mapper.pricing.KmPackageMapper;
import com.reindecar.repository.pricing.KmPackageRepository;
import com.reindecar.repository.vehicle.VehicleCategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class KmPackageService {

    private final KmPackageRepository kmPackageRepository;
    private final KmPackageMapper kmPackageMapper;
    private final VehicleCategoryRepository vehicleCategoryRepository;

    @Transactional(readOnly = true)
    public List<KmPackageResponse> getAllPackages() {
        return kmPackageRepository.findAll().stream()
            .map(this::toResponseWithCategoryName)
            .toList();
    }

    @Transactional(readOnly = true)
    public List<KmPackageResponse> getActivePackages() {
        return kmPackageRepository.findActivePackages().stream()
            .map(this::toResponseWithCategoryName)
            .toList();
    }

    @Transactional(readOnly = true)
    public List<KmPackageResponse> getPackagesByRentalType(RentalType rentalType) {
        return kmPackageRepository.findActivePackages().stream()
            .filter(pkg -> pkg.isApplicableFor(rentalType))
            .map(this::toResponseWithCategoryName)
            .toList();
    }

    @Transactional(readOnly = true)
    public KmPackageResponse getPackageById(Long id) {
        KmPackage kmPackage = kmPackageRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("KmPackage", id));
        return toResponseWithCategoryName(kmPackage);
    }

    @Transactional(readOnly = true)
    public List<KmPackageResponse> getPackagesByCategory(Long categoryId) {
        return kmPackageRepository.findByCategoryIdAndActiveTrue(categoryId).stream()
            .map(this::toResponseWithCategoryName)
            .toList();
    }

    @Transactional(readOnly = true)
    public List<KmPackageResponse> getGlobalPackages() {
        return kmPackageRepository.findGlobalActivePackages().stream()
            .map(this::toResponseWithCategoryName)
            .toList();
    }

    @Transactional(readOnly = true)
    public List<KmPackageResponse> getAvailablePackagesForCategory(Long categoryId) {
        return kmPackageRepository.findAvailableForCategory(categoryId).stream()
            .map(this::toResponseWithCategoryName)
            .toList();
    }

    @Transactional
    public KmPackageResponse createPackage(CreateKmPackageRequest request) {
        log.info("Creating KM package: {}", request.name());
        
        if (request.categoryId() != null) {
            validateCategoryExists(request.categoryId());
        }
        
        KmPackage kmPackage = kmPackageMapper.toEntity(request);
        KmPackage savedPackage = kmPackageRepository.save(kmPackage);
        
        log.info("KM package created with ID: {}", savedPackage.getId());
        return toResponseWithCategoryName(savedPackage);
    }

    @Transactional
    public KmPackageResponse updatePackage(Long id, UpdateKmPackageRequest request) {
        log.info("Updating KM package: {}", id);
        
        KmPackage kmPackage = kmPackageRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("KmPackage", id));

        if (request.categoryId() != null) {
            validateCategoryExists(request.categoryId());
        }

        Money extraKmPrice = request.extraKmPrice() != null 
            ? Money.tl(request.extraKmPrice()) 
            : null;

        kmPackage.update(
            request.name(),
            request.includedKm(),
            extraKmPrice,
            request.applicableTypes(),
            request.unlimited(),
            request.active(),
            request.categoryId()
        );

        KmPackage updatedPackage = kmPackageRepository.save(kmPackage);
        log.info("KM package updated: {}", id);
        
        return toResponseWithCategoryName(updatedPackage);
    }

    @Transactional
    public void deactivatePackage(Long id) {
        log.info("Deactivating KM package: {}", id);
        
        KmPackage kmPackage = kmPackageRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("KmPackage", id));
        
        kmPackage.deactivate();
        kmPackageRepository.save(kmPackage);
        
        log.info("KM package deactivated: {}", id);
    }

    @Transactional
    public void activatePackage(Long id) {
        log.info("Activating KM package: {}", id);
        
        KmPackage kmPackage = kmPackageRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("KmPackage", id));
        
        kmPackage.activate();
        kmPackageRepository.save(kmPackage);
        
        log.info("KM package activated: {}", id);
    }

    private void validateCategoryExists(Long categoryId) {
        if (!vehicleCategoryRepository.existsById(categoryId)) {
            throw new EntityNotFoundException("VehicleCategory", categoryId);
        }
    }

    private KmPackageResponse toResponseWithCategoryName(KmPackage kmPackage) {
        String categoryName = null;
        if (kmPackage.getCategoryId() != null) {
            categoryName = vehicleCategoryRepository.findById(kmPackage.getCategoryId())
                .map(VehicleCategory::getName)
                .orElse(null);
        }
        return kmPackageMapper.toResponseWithCategoryName(kmPackage, categoryName);
    }
}
