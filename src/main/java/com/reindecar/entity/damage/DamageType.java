package com.reindecar.entity.damage;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Hasar tiplerini temsil eder.
 */
@Getter
@RequiredArgsConstructor
public enum DamageType {
    SCRATCH("Çizik"),
    DENT("Göçük"),
    CRACK("Çatlak"),
    BROKEN_GLASS("Kırık Cam"),
    TIRE_DAMAGE("Lastik Hasarı"),
    INTERIOR_DAMAGE("İç Mekan Hasarı"),
    ENGINE_DAMAGE("Motor Hasarı"),
    ELECTRICAL("Elektrik Arızası"),
    ACCIDENT("Kaza"),
    OTHER("Diğer");

    private final String displayName;
}
