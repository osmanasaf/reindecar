package com.reindecar.mapper.pricing;

import com.reindecar.dto.pricing.CreateSeasonRequest;
import com.reindecar.dto.pricing.SeasonResponse;
import com.reindecar.dto.pricing.UpdateSeasonRequest;
import com.reindecar.entity.pricing.Season;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = org.mapstruct.NullValuePropertyMappingStrategy.IGNORE)
public interface SeasonMapper {

    SeasonResponse toResponse(Season season);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "name", source = "name")
    @Mapping(target = "startDate", source = "startDate")
    @Mapping(target = "endDate", source = "endDate")
    @Mapping(target = "priceMultiplier", source = "priceMultiplier")
    @Mapping(target = "active", source = "active")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    void updateEntity(@MappingTarget Season season, UpdateSeasonRequest request);

    default Season toEntity(CreateSeasonRequest request) {
        return Season.create(
            request.name(),
            request.startDate(),
            request.endDate(),
            request.priceMultiplier()
        );
    }
}
