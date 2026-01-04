package com.reindecar.mapper.vehicle;

import com.reindecar.dto.vehicle.VehicleStatusHistoryResponse;
import com.reindecar.entity.vehicle.VehicleStatusHistory;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface VehicleStatusHistoryMapper {

    VehicleStatusHistoryResponse toResponse(VehicleStatusHistory history);
}
