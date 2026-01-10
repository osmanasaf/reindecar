package com.reindecar.exception.vehicle;

import com.reindecar.common.exception.BusinessException;
import com.reindecar.common.exception.ErrorCode;
import com.reindecar.entity.vehicle.VehicleStatus;

public class InvalidStatusTransitionException extends BusinessException {

    public InvalidStatusTransitionException(VehicleStatus from, VehicleStatus to) {
        super(ErrorCode.VEHICLE_INVALID_STATE, from, to);
    }
}
