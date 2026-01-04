package com.reindecar.mapper.branch;

import com.reindecar.entity.branch.Branch;
import com.reindecar.dto.branch.BranchResponse;
import com.reindecar.dto.branch.BranchSummaryResponse;
import com.reindecar.dto.branch.CreateBranchRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BranchMapper {

    @Mapping(target = "vehicleCount", constant = "0")
    BranchResponse toResponse(Branch branch);

    BranchSummaryResponse toSummaryResponse(Branch branch);

    default Branch toEntity(CreateBranchRequest request) {
        return Branch.create(
            request.code(),
            request.name(),
            request.city(),
            request.district(),
            request.address(),
            request.phone(),
            request.email()
        );
    }
}
