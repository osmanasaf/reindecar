package com.reindecar.dto.contract;

public record ContractTermResponse(
    Long id,
    String title,
    String content,
    boolean required,
    int sortOrder
) {}
