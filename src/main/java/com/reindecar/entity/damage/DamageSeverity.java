package com.reindecar.entity.damage;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Hasar şiddet seviyelerini temsil eder.
 * Frontend harita renklendirmesi için kullanılır.
 */
@Getter
@RequiredArgsConstructor
public enum DamageSeverity {
    MINOR(1, "Küçük", "#FFC107"),        // Sarı
    MODERATE(2, "Orta", "#FF9800"),      // Turuncu
    MAJOR(3, "Büyük", "#F44336"),        // Kırmızı
    CRITICAL(4, "Kritik", "#B71C1C");    // Koyu Kırmızı

    private final int priority;
    private final String displayName;
    private final String colorCode;
}
