package com.reindecar.dto.reporting;

import java.math.BigDecimal;
import java.util.Map;

public record RevenueByMonthResponse(
        String month,
        BigDecimal total,
        Map<String, BigDecimal> totalsByCurrency
) {
}
