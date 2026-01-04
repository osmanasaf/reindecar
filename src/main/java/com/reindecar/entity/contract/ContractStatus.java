package com.reindecar.entity.contract;

public enum ContractStatus {
    DRAFT,
    PENDING_SIGNATURE,
    SIGNED,
    EXPIRED,
    RENEWED,
    CANCELLED;

    public boolean canBeSigned() {
        return this == DRAFT || this == PENDING_SIGNATURE;
    }

    public boolean canBeRenewed() {
        return this == SIGNED;
    }

    public boolean canBeCancelled() {
        return this == DRAFT || this == PENDING_SIGNATURE || this == SIGNED;
    }

    public boolean isFinalState() {
        return this == EXPIRED || this == RENEWED || this == CANCELLED;
    }
}
