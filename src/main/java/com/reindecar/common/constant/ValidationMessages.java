package com.reindecar.common.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ValidationMessages {

    public static final String ENTITY_NOT_FOUND = "%s bulunamadı: %s";
    public static final String DUPLICATE_ENTITY = "Bu kayıt zaten mevcut: %s";

    public static final String VEHICLE_NOT_FOUND = "Araç bulunamadı: %s";
    public static final String VEHICLE_NOT_AVAILABLE = "Araç müsait değil: %s";
    public static final String VEHICLE_CANNOT_DELETE_RENTED = "Kiralık araç silinemez";

    public static final String CUSTOMER_NOT_FOUND = "Müşteri bulunamadı: %s";
    public static final String CATEGORY_NOT_FOUND = "Kategori bulunamadı: %s";
    public static final String PRICING_NOT_FOUND = "Fiyatlandırma bulunamadı: %s";

    public static final String DRIVER_NOT_ACTIVE = "Sürücü aktif değil";
    public static final String DRIVER_LICENSE_EXPIRED = "Sürücü ehliyetinin süresi dolmuş";
    public static final String DRIVER_ALREADY_ADDED = "Sürücü bu kiralamaya zaten eklenmiş";
    public static final String DRIVER_UNKNOWN = "Bilinmeyen Sürücü";
    public static final String DRIVER_DUPLICATE_IN_REQUEST = "Sürücü listesinde tekrar eden kayıt var";
    public static final String RENTAL_PRIMARY_DRIVER_NOT_IN_LIST = "Ana sürücü listede olmalıdır";
    public static final String RENTAL_DRIVER_REQUIRED = "Kiralama için en az bir sürücü gereklidir";
    public static final String RENTAL_PRIMARY_DRIVER_REQUIRED = "Ana sürücü belirtilmelidir";

    public static final String RENTAL_CANNOT_MODIFY_STATUS = "Bu durumdaki kiralama için sürücü değiştirilemez: %s";
    public static final String RENTAL_DATE_INVALID = "Başlangıç tarihi bitiş tarihinden önce olmalıdır";
    public static final String DRIVER_ALREADY_IN_ACTIVE_RENTAL = "Sürücü zaten aktif bir kiralamada: %s";
    public static final String PERSONAL_CUSTOMER_RENTAL_LIMIT_EXCEEDED = "Bireysel müşteri aynı anda sadece bir kiralama yapabilir";

    public static final String EXTRA_ITEM_CODE_EXISTS = "Bu kod zaten kullanılıyor: %s";
    public static final String EXTRA_ITEM_NOT_FOUND = "Kalem türü bulunamadı: %s";
    public static final String EXTRA_ITEM_NAME_REQUIRED = "Serbest kalem için ad zorunludur";
    public static final String EXTRA_ITEM_UNKNOWN = "Bilinmeyen Kalem";

    public static final String DISCOUNT_NOT_FOUND = "İskonto bulunamadı: %s";
    public static final String INVOICE_ALREADY_EXISTS = "Bu dönem için fatura zaten mevcut: %s";
}
