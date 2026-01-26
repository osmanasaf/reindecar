package com.reindecar.dto.pricing;

import com.reindecar.entity.pricing.DiscountType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "Vade iskontosu yanıtı")
public record TermDiscountResponse(
    @Schema(description = "İskonto ID")
    Long id,

    @Schema(description = "Kategori ID (null ise genel iskonto)")
    Long categoryId,

    @Schema(description = "Kategori adı")
    String categoryName,

    @Schema(description = "Vade süresi (ay)")
    Integer termMonths,

    @Schema(description = "İskonto tipi")
    DiscountType discountType,

    @Schema(description = "İskonto değeri")
    BigDecimal discountValue,

    @Schema(description = "Aktif mi")
    boolean active
) {}
