package com.reindecar.exception.vehicle;

import com.reindecar.common.exception.BusinessException;
import com.reindecar.entity.vehicle.VehicleStatus;

public class InvalidStatusTransitionException extends BusinessException {

    public InvalidStatusTransitionException(VehicleStatus from, VehicleStatus to) {
        super("VEHICLE_001", 
              String.format("Invalid status transition from %s to %s", from, to));
    }
}
