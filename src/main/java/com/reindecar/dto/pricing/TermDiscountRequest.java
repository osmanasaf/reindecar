package com.reindecar.dto.pricing;

import com.reindecar.entity.pricing.DiscountType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

@Schema(description = "Vade iskontosu isteği")
public record TermDiscountRequest(
    @Schema(description = "Kategori ID (null ise genel iskonto)", example = "1")
    Long categoryId,

    @NotNull(message = "Vade süresi zorunludur")
    @Min(value = 12, message = "Vade süresi en az 12 ay olmalıdır")
    @Schema(description = "Vade süresi (ay)", example = "24")
    Integer termMonths,

    @NotNull(message = "İskonto tipi zorunludur")
    @Schema(description = "İskonto tipi", example = "PERCENTAGE")
    DiscountType discountType,

    @NotNull(message = "İskonto değeri zorunludur")
    @Positive(message = "İskonto değeri pozitif olmalıdır")
    @Schema(description = "İskonto değeri (% veya sabit tutar)", example = "15.00")
    BigDecimal discountValue
) {}
