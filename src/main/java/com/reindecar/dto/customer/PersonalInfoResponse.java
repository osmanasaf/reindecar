package com.reindecar.dto.customer;

import java.time.LocalDate;

public record PersonalInfoResponse(
    String nationalId,
    String firstName,
    String lastName,
    LocalDate birthDate,
    String licenseNumber,
    String licenseClass,
    LocalDate licenseExpiryDate,
    boolean licenseExpired
) {}
