package com.reindecar.service.vehicle;

import com.reindecar.common.statemachine.StateTransition;
import com.reindecar.entity.vehicle.VehicleStatus;
import com.reindecar.exception.vehicle.InvalidStatusTransitionException;

import java.util.Map;
import java.util.Set;

import static com.reindecar.entity.vehicle.VehicleStatus.*;

public class VehicleStatusTransition implements StateTransition<VehicleStatus> {

    private static final Map<VehicleStatus, Set<VehicleStatus>> ALLOWED_TRANSITIONS = Map.of(
        AVAILABLE, Set.of(RESERVED, MAINTENANCE, DAMAGED, INACTIVE, SOLD),
        RESERVED, Set.of(RENTED, AVAILABLE),
        RENTED, Set.of(AVAILABLE, DAMAGED),
        MAINTENANCE, Set.of(AVAILABLE),
        DAMAGED, Set.of(AVAILABLE),
        INACTIVE, Set.of(AVAILABLE, SOLD),
        SOLD, Set.of()
    );

    @Override
    public boolean canTransition(VehicleStatus from, VehicleStatus to) {
        if (from == null || to == null) {
            return false;
        }
        return ALLOWED_TRANSITIONS.getOrDefault(from, Set.of()).contains(to);
    }

    @Override
    public void onTransition(VehicleStatus from, VehicleStatus to) {
        if (!canTransition(from, to)) {
            throw new InvalidStatusTransitionException(from, to);
        }
    }
}
