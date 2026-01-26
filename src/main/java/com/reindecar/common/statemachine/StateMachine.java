package com.reindecar.common.statemachine;

import com.reindecar.common.exception.InvalidOperationException;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class StateMachine<S> {

    private final Map<S, Set<S>> transitions = new HashMap<>();
    private final StateTransition<S> transitionHandler;

    public StateMachine(StateTransition<S> transitionHandler) {
        this.transitionHandler = transitionHandler;
    }

    public void addTransition(S from, S... toStates) {
        transitions.put(from, Set.of(toStates));
    }

    public void transition(S from, S to) {
        if (!canTransition(from, to)) {
            throw new InvalidOperationException(
                String.format("Invalid state transition from %s to %s", from, to)
            );
        }
        transitionHandler.onTransition(from, to);
    }

    public boolean canTransition(S from, S to) {
        // Önce transition handler'ı kontrol et
        if (transitionHandler.canTransition(from, to)) {
            return true;
        }
        // Fallback: internal transitions map
        Set<S> allowedStates = transitions.get(from);
        return allowedStates != null && allowedStates.contains(to);
    }
}
