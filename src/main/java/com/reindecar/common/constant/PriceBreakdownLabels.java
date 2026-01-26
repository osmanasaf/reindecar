package com.reindecar.common.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PriceBreakdownLabels {

    public static final String TOTAL = "Toplam";
    public static final String DAILY_PRICE_FORMAT = "Günlük Fiyat (%d gün)";
    public static final String WEEKLY_PRICE_FORMAT = "Haftalık Fiyat (%d hafta)";
    public static final String REMAINING_DAYS_FORMAT = "Kalan Günler (%d gün)";
    public static final String MONTHLY_PRICE_FORMAT = "Aylık Fiyat (%d ay)";
    public static final String BASE_PRICE_FORMAT = "Baz Fiyat (%d ay)";
    public static final String BASE_PRICE_MONTHS_FORMAT = "Base Price (%d months)";
    public static final String TERM_DISCOUNT = "Vade İskontosu";
    public static final String DISCOUNT_FORMAT = "Discount: %s";
    public static final String TOTAL_NET_PRICE = "Total Net Price";

    public static String dailyPrice(int days) {
        return String.format(DAILY_PRICE_FORMAT, days);
    }

    public static String weeklyPrice(int weeks) {
        return String.format(WEEKLY_PRICE_FORMAT, weeks);
    }

    public static String remainingDays(int days) {
        return String.format(REMAINING_DAYS_FORMAT, days);
    }

    public static String monthlyPrice(int months) {
        return String.format(MONTHLY_PRICE_FORMAT, months);
    }

    public static String basePrice(int months) {
        return String.format(BASE_PRICE_FORMAT, months);
    }

    public static String discount(String discountName) {
        return String.format(DISCOUNT_FORMAT, discountName);
    }
}
