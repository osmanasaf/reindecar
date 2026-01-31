package com.reindecar.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    ENTITY_NOT_FOUND("E001", "%s bulunamadı", HttpStatus.NOT_FOUND),
    DUPLICATE_ENTITY("E002", "Bu kayıt zaten mevcut: %s", HttpStatus.CONFLICT),
    INVALID_OPERATION("E003", "Geçersiz işlem: %s", HttpStatus.BAD_REQUEST),
    VALIDATION_ERROR("E004", "Validasyon hatası", HttpStatus.BAD_REQUEST),
    INVALID_PARAMETER("E005", "Geçersiz parametre: %s", HttpStatus.BAD_REQUEST),

    VEHICLE_NOT_FOUND("V001", "Araç bulunamadı: %s", HttpStatus.NOT_FOUND),
    VEHICLE_NOT_AVAILABLE("V002", "Araç müsait değil: %s", HttpStatus.CONFLICT),
    VEHICLE_ALREADY_RENTED("V003", "Araç zaten kiralanmış", HttpStatus.CONFLICT),
    VEHICLE_INVALID_STATE("V004", "Geçersiz araç durumu geçişi: %s -> %s", HttpStatus.BAD_REQUEST),

    CUSTOMER_NOT_FOUND("C001", "Müşteri bulunamadı: %s", HttpStatus.NOT_FOUND),
    CUSTOMER_BLACKLISTED("C002", "Müşteri kara listede: %s", HttpStatus.FORBIDDEN),
    CUSTOMER_INVALID_LICENSE("C003", "Geçersiz ehliyet bilgisi", HttpStatus.BAD_REQUEST),

    RENTAL_NOT_FOUND("R001", "Kiralama bulunamadı: %s", HttpStatus.NOT_FOUND),
    RENTAL_OVERLAP("R002", "Bu tarihler için çakışan kiralama mevcut: %s", HttpStatus.CONFLICT),
    RENTAL_INVALID_STATE("R003", "Bu işlem için geçersiz kiralama durumu: %s", HttpStatus.BAD_REQUEST),
    RENTAL_ALREADY_COMPLETED("R004", "Kiralama zaten tamamlanmış", HttpStatus.CONFLICT),
    RENTAL_ALREADY_CANCELLED("R005", "Kiralama zaten iptal edilmiş", HttpStatus.CONFLICT),

    BRANCH_NOT_FOUND("B001", "Şube bulunamadı: %s", HttpStatus.NOT_FOUND),
    BRANCH_INACTIVE("B002", "Şube aktif değil: %s", HttpStatus.BAD_REQUEST),

    PRICING_NOT_FOUND("P001", "Fiyat kuralı bulunamadı", HttpStatus.NOT_FOUND),
    PRICING_INVALID("P002", "Geçersiz fiyat bilgisi: %s", HttpStatus.BAD_REQUEST),

    INVOICE_NOT_FOUND("I001", "Fatura bulunamadı: %s", HttpStatus.NOT_FOUND),
    INVOICE_ALREADY_FINALIZED("I002", "Fatura zaten onaylanmış", HttpStatus.CONFLICT),
    INVOICE_ALREADY_PAID("I003", "Fatura zaten ödenmiş", HttpStatus.CONFLICT),

    FILE_NOT_FOUND("F001", "Dosya bulunamadı: %s", HttpStatus.NOT_FOUND),
    FILE_UPLOAD_FAILED("F002", "Dosya yükleme başarısız: %s", HttpStatus.INTERNAL_SERVER_ERROR),
    FILE_TYPE_NOT_ALLOWED("F003", "Dosya tipi desteklenmiyor: %s", HttpStatus.BAD_REQUEST),

    UNAUTHORIZED("A001", "Kimlik doğrulama başarısız: %s", HttpStatus.UNAUTHORIZED),
    ACCESS_DENIED("A002", "Bu işlem için yetkiniz bulunmamaktadır", HttpStatus.FORBIDDEN),
    TOKEN_EXPIRED("A003", "Oturum süresi doldu", HttpStatus.UNAUTHORIZED),
    WEAK_PASSWORD("A004", "Zayıf şifre: %s", HttpStatus.BAD_REQUEST),

    INTERNAL_ERROR("S001", "Beklenmeyen bir hata oluştu", HttpStatus.INTERNAL_SERVER_ERROR),
    DATABASE_ERROR("S002", "Veritabanı hatası", HttpStatus.INTERNAL_SERVER_ERROR),
    EXTERNAL_SERVICE_ERROR("S003", "Dış servis hatası: %s", HttpStatus.SERVICE_UNAVAILABLE),
    JSON_PARSE_ERROR("S004", "JSON formatı hatalı: %s", HttpStatus.BAD_REQUEST),
    MISSING_PARAMETER("S005", "Eksik parametre: %s", HttpStatus.BAD_REQUEST),
    METHOD_NOT_ALLOWED("S006", "HTTP metodu desteklenmiyor: %s", HttpStatus.METHOD_NOT_ALLOWED),
    CONSTRAINT_VIOLATION("S007", "Veri bütünlük hatası: %s", HttpStatus.CONFLICT);

    private final String code;
    private final String messageTemplate;
    private final HttpStatus httpStatus;

    public String formatMessage(Object... args) {
        if (args == null || args.length == 0) {
            return messageTemplate.replace(": %s", "").replace("%s", "");
        }
        return String.format(messageTemplate, args);
    }
}
