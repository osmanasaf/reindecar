package com.reindecar.service.rental;

import com.reindecar.common.statemachine.StateTransition;
import com.reindecar.entity.rental.RentalStatus;
import com.reindecar.exception.rental.InvalidRentalStatusException;

import java.util.Map;
import java.util.Set;

import static com.reindecar.entity.rental.RentalStatus.*;

public class RentalStatusTransition implements StateTransition<RentalStatus> {

    private static final Map<RentalStatus, Set<RentalStatus>> ALLOWED_TRANSITIONS = Map.of(
        DRAFT, Set.of(RESERVED, CANCELLED),
        RESERVED, Set.of(ACTIVE, CANCELLED),
        ACTIVE, Set.of(RETURN_PENDING, OVERDUE),
        RETURN_PENDING, Set.of(CLOSED),
        OVERDUE, Set.of(RETURN_PENDING),
        CLOSED, Set.of(),
        CANCELLED, Set.of()
    );

    @Override
    public boolean canTransition(RentalStatus from, RentalStatus to) {
        if (from == null || to == null) {
            return false;
        }
        return ALLOWED_TRANSITIONS.getOrDefault(from, Set.of()).contains(to);
    }

    @Override
    public void onTransition(RentalStatus from, RentalStatus to) {
        if (!canTransition(from, to)) {
            throw new InvalidRentalStatusException(from, to);
        }
    }
}
