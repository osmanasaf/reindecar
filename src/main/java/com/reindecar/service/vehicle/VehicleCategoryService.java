package com.reindecar.service.vehicle;

import com.reindecar.common.exception.DuplicateEntityException;
import com.reindecar.common.exception.EntityNotFoundException;
import com.reindecar.common.service.BaseService;
import com.reindecar.dto.vehicle.CreateCategoryRequest;
import com.reindecar.dto.vehicle.VehicleCategoryResponse;
import com.reindecar.entity.vehicle.VehicleCategory;
import com.reindecar.exception.vehicle.VehicleCategoryNotFoundException;
import com.reindecar.mapper.vehicle.VehicleCategoryMapper;
import com.reindecar.repository.vehicle.VehicleCategoryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@Transactional(readOnly = true)
public class VehicleCategoryService extends BaseService<VehicleCategory, Long, VehicleCategoryRepository> {

    private final VehicleCategoryMapper categoryMapper;

    public VehicleCategoryService(
            VehicleCategoryRepository repository,
            VehicleCategoryMapper categoryMapper) {
        super(repository, "VehicleCategory");
        this.categoryMapper = categoryMapper;
    }

    public List<VehicleCategoryResponse> getActiveCategories() {
        log.info("Fetching active vehicle categories");
        return repository.findAllActiveOrderBySortOrder()
            .stream()
            .map(categoryMapper::toResponse)
            .toList();
    }

    public VehicleCategoryResponse getCategoryById(Long id) {
        log.info("Fetching category by id: {}", id);
        return findById(id, categoryMapper::toResponse);
    }

    @Transactional
    public VehicleCategoryResponse createCategory(CreateCategoryRequest request) {
        log.info("Creating vehicle category with code: {}", request.code());

        validateUniqueCode(request.code());

        VehicleCategory category = categoryMapper.toEntity(request);
        return create(category, categoryMapper::toResponse);
    }

    @Transactional
    public void deleteCategory(Long id) {
        log.info("Deleting category with id: {}", id);
        delete(id);
    }

    @Override
    protected EntityNotFoundException createNotFoundException(Long id) {
        return new VehicleCategoryNotFoundException(id);
    }

    @Override
    protected Long extractId(VehicleCategory entity) {
        return entity.getId();
    }

    private void validateUniqueCode(String code) {
        if (repository.existsByCode(code)) {
            throw new DuplicateEntityException("VehicleCategory", "code", code);
        }
    }
}
