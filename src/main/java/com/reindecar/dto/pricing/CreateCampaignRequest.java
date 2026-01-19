package com.reindecar.dto.pricing;

import com.reindecar.entity.pricing.Campaign;
import com.reindecar.entity.pricing.RentalType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record CreateCampaignRequest(
    @NotBlank
    @Size(max = 100)
    String name,

    @Size(max = 500)
    String description,

    @NotNull
    Campaign.DiscountType discountType,

    @NotNull
    @DecimalMin("0.01")
    BigDecimal discountValue,

    @NotEmpty
    List<RentalType> applicableRentalTypes,

    @NotNull
    LocalDate validFrom,

    @NotNull
    LocalDate validTo,

    Integer minTermMonths,

    Long categoryId
) {}
