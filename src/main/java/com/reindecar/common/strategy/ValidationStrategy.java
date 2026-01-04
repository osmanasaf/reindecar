package com.reindecar.common.strategy;

public interface ValidationStrategy<T> {
    void validate(T entity);
}
