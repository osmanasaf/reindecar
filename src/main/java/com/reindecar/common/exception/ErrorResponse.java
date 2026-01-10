package com.reindecar.common.exception;

import java.time.Instant;
import java.util.Map;

public record ErrorResponse(
    String code,
    String message,
    Instant timestamp,
    String path,
    Map<String, String> details
) {
    public static ErrorResponse of(ErrorCode errorCode, String path, Object... args) {
        return new ErrorResponse(
            errorCode.getCode(),
            errorCode.formatMessage(args),
            Instant.now(),
            path,
            null
        );
    }

    public static ErrorResponse of(ErrorCode errorCode, String path, Map<String, String> details) {
        return new ErrorResponse(
            errorCode.getCode(),
            errorCode.getMessageTemplate(),
            Instant.now(),
            path,
            details
        );
    }

    public static ErrorResponse of(String code, String message, String path) {
        return new ErrorResponse(code, message, Instant.now(), path, null);
    }
}
