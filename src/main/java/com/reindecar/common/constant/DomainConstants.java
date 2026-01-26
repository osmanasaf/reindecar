package com.reindecar.common.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DomainConstants {

    public static final String DEFAULT_CURRENCY = "TRY";
    public static final String SYSTEM_USER = "system";

    public static final String RENTAL_NUMBER_PREFIX = "RNT-";
    public static final String RENTAL_NUMBER_FORMAT = "%s%05d";
    public static final String INVOICE_NUMBER_PREFIX = "INV-";

    public static final String STATUS_CHANGE_SOURCE_RENTAL = "RENTAL";

    public static final String PRICE_SOURCE_CUSTOMER_CONTRACT = "CUSTOMER_CONTRACT";
    public static final String PRICE_SOURCE_LEASING_PLAN = "LEASING_PLAN";
    public static final String PRICE_SOURCE_CATEGORY_DEFAULT = "CATEGORY_DEFAULT";
}
