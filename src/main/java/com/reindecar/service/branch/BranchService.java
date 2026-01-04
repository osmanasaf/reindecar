package com.reindecar.service.branch;

import com.reindecar.dto.branch.BranchResponse;
import com.reindecar.dto.branch.BranchSummaryResponse;
import com.reindecar.dto.branch.CreateBranchRequest;
import com.reindecar.dto.branch.UpdateBranchRequest;
import com.reindecar.entity.branch.Branch;
import com.reindecar.exception.branch.BranchNotFoundException;
import com.reindecar.mapper.branch.BranchMapper;
import com.reindecar.repository.branch.BranchRepository;
import com.reindecar.common.dto.PageResponse;
import com.reindecar.common.exception.DuplicateEntityException;
import com.reindecar.common.exception.EntityNotFoundException;
import com.reindecar.common.service.BaseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@Transactional(readOnly = true)
public class BranchService extends BaseService<Branch, Long, BranchRepository> {

    private final BranchMapper mapper;

    public BranchService(BranchRepository repository, BranchMapper mapper) {
        super(repository, "Branch");
        this.mapper = mapper;
    }

    public PageResponse<BranchSummaryResponse> getAllBranches(Pageable pageable) {
        Page<Branch> branches = repository.findAllActive(pageable);
        return PageResponse.of(branches.map(mapper::toSummaryResponse));
    }

    public PageResponse<BranchSummaryResponse> getActiveBranches(Pageable pageable) {
        Page<Branch> branches = repository.findAllActiveAndEnabled(pageable);
        return PageResponse.of(branches.map(mapper::toSummaryResponse));
    }

    public BranchResponse getBranchById(Long id) {
        return findById(id, mapper::toResponse);
    }

    @Transactional
    public BranchResponse createBranch(CreateBranchRequest request) {
        validateUniqueCode(request.code());
        Branch branch = mapper.toEntity(request);
        return create(branch, mapper::toResponse);
    }

    @Transactional
    public BranchResponse updateBranch(Long id, UpdateBranchRequest request) {
        return update(id, branch -> {
            branch.updateInfo(
                request.name(),
                request.city(),
                request.district(),
                request.address(),
                request.phone(),
                request.email()
            );
            return branch;
        }, mapper::toResponse);
    }

    @Transactional
    public void toggleBranchStatus(Long id) {
        update(id, branch -> {
            if (branch.isActive()) {
                branch.deactivate();
            } else {
                branch.activate();
            }
            return branch;
        }, mapper::toResponse);
    }

    @Transactional
    public void deleteBranch(Long id) {
        update(id, branch -> {
            branch.markAsDeleted();
            return branch;
        }, mapper::toResponse);
    }

    @Override
    protected EntityNotFoundException createNotFoundException(Long id) {
        return new BranchNotFoundException(id);
    }

    @Override
    protected Long extractId(Branch entity) {
        return entity.getId();
    }

    private void validateUniqueCode(String code) {
        if (repository.existsByCodeAndDeletedFalse(code.toUpperCase())) {
            throw new DuplicateEntityException("Branch", "code", code);
        }
    }
}
