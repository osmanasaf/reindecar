package com.reindecar.dto.pricing;

import com.reindecar.entity.pricing.Campaign;
import com.reindecar.entity.pricing.RentalType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record CampaignResponse(
    Long id,
    String name,
    String description,
    Campaign.DiscountType discountType,
    BigDecimal discountValue,
    List<RentalType> applicableRentalTypes,
    LocalDate validFrom,
    LocalDate validTo,
    Integer minTermMonths,
    Long categoryId,
    boolean active
) {}
