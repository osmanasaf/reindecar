package com.reindecar.dto.branch;

public record BranchSummaryResponse(
    Long id,
    String code,
    String name,
    String city,
    boolean active
) {
}
