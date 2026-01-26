package com.reindecar.entity.maintenance;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Bakım/tamir tiplerini temsil eder.
 * Frontend harita renklendirmesi için kullanılır.
 */
@Getter
@RequiredArgsConstructor
public enum MaintenanceType {
    REPAIR("Tamir", "#2196F3"),                    // Mavi
    PAINT("Boyama", "#9C27B0"),                    // Mor
    PART_REPLACEMENT("Parça Değişimi", "#00BCD4"), // Turkuaz
    SERVICE("Servis Bakımı", "#4CAF50"),           // Yeşil
    INSPECTION("Muayene", "#9E9E9E"),              // Gri
    TIRE_CHANGE("Lastik Değişimi", "#795548"),     // Kahverengi
    OIL_CHANGE("Yağ Değişimi", "#FF9800"),         // Turuncu
    FILTER_CHANGE("Filtre Değişimi", "#607D8B"),   // Mavi Gri
    BRAKE_SERVICE("Fren Servisi", "#F44336"),      // Kırmızı
    ELECTRICAL_REPAIR("Elektrik Tamiri", "#FFEB3B"), // Sarı
    BODY_WORK("Kaporta İşi", "#3F51B5"),           // Indigo
    OTHER("Diğer", "#757575");                      // Koyu Gri

    private final String displayName;
    private final String colorCode;
}
