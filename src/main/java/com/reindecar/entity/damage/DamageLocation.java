package com.reindecar.entity.damage;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Araç üzerindeki hasar lokasyonlarını temsil eder.
 * Her lokasyon, frontend harita görseli için bir zone numarasına sahiptir.
 */
@Getter
@RequiredArgsConstructor
public enum DamageLocation {
    FRONT_BUMPER(1, "Ön Tampon"),
    REAR_BUMPER(7, "Arka Tampon"),
    HOOD(3, "Kaput"),
    TRUNK(8, "Bagaj"),
    ROOF(13, "Tavan"),
    FRONT_LEFT_FENDER(4, "Sol Ön Çamurluk"),
    FRONT_RIGHT_FENDER(1, "Sağ Ön Çamurluk"),
    REAR_LEFT_FENDER(4, "Sol Arka Çamurluk"),
    REAR_RIGHT_FENDER(10, "Sağ Arka Çamurluk"),
    LEFT_FRONT_DOOR(6, "Sol Ön Kapı"),
    LEFT_REAR_DOOR(6, "Sol Arka Kapı"),
    RIGHT_FRONT_DOOR(12, "Sağ Ön Kapı"),
    RIGHT_REAR_DOOR(12, "Sağ Arka Kapı"),
    WINDSHIELD(2, "Ön Cam"),
    REAR_WINDOW(9, "Arka Cam"),
    LEFT_MIRROR(6, "Sol Ayna"),
    RIGHT_MIRROR(12, "Sağ Ayna"),
    INTERIOR(13, "İç Mekan"),
    WHEEL_FRONT_LEFT(4, "Sol Ön Tekerlek"),
    WHEEL_FRONT_RIGHT(1, "Sağ Ön Tekerlek"),
    WHEEL_REAR_LEFT(7, "Sol Arka Tekerlek"),
    WHEEL_REAR_RIGHT(10, "Sağ Arka Tekerlek");

    private final int zoneId;
    private final String displayName;
}
