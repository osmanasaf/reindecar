package com.reindecar.dto.customer;

public record AuthorizedPersonResponse(
    Long id,
    String nationalId,
    String firstName,
    String lastName,
    String fullName,
    String title,
    String phone,
    String email,
    boolean isPrimary,
    boolean active
) {}
