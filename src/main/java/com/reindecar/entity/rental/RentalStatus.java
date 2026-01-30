package com.reindecar.entity.rental;

public enum RentalStatus {
    DRAFT,
    RESERVED,
    ACTIVE,
    RETURN_PENDING,
    CLOSED,
    CANCELLED,
    OVERDUE;

    public boolean isActive() {
        return this == ACTIVE || this == OVERDUE;
    }

    public boolean canBeCancelled() {
        return this == DRAFT || this == RESERVED;
    }

    public boolean canBeCompleted() {
        return this == ACTIVE || this == RETURN_PENDING || this == OVERDUE;
    }

    public boolean isFinalState() {
        return this == CLOSED || this == CANCELLED;
    }

    public boolean blocksVehicleOrDriver() {
        return this == RESERVED || this == ACTIVE || this == OVERDUE;
    }
}
