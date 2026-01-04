package com.reindecar.common.statemachine;

public interface StateTransition<S> {
    boolean canTransition(S from, S to);
    void onTransition(S from, S to);
}
