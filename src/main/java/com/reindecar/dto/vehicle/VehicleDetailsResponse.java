package com.reindecar.dto.vehicle;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

public record VehicleDetailsResponse(
    Long id,
    Long vehicleId,
    String hgsNumber,
    BigDecimal hgsBalance,
    String hgsCurrency,
    Instant hgsLastUpdated,
    String kabisNumber,
    LocalDate mtvDate,
    LocalDate registrationDate,
    LocalDate nextServiceDate,
    Integer nextServiceKm,
    LocalDate lastServiceDate,
    LocalDate nextTireChangeDate,
    LocalDate creditEndDate,
    BigDecimal remainingCreditAmount,
    String creditCurrency,
    LocalDate purchaseDate,
    BigDecimal purchasePrice,
    String purchaseCurrency,
    boolean isHgsLow,
    boolean isServiceDueSoon,
    boolean isMtvDueSoon,
    boolean isTireChangeDueSoon
) {}
