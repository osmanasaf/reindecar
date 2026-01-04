package com.reindecar.entity.vehicle;

public enum VehicleStatus {
    AVAILABLE,
    RESERVED,
    RENTED,
    MAINTENANCE,
    DAMAGED,
    INACTIVE,
    SOLD;

    public boolean isAvailableForRental() {
        return this == AVAILABLE;
    }

    public boolean isFinalState() {
        return this == SOLD;
    }

    public boolean isRented() {
        return this == RENTED;
    }
}
