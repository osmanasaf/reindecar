package com.reindecar.common.exception;

import java.time.Instant;
import java.util.Map;

public record ErrorResponse(
    String code,
    String message,
    Instant timestamp,
    String path,
    String traceId,
    Map<String, String> details
) {
    public static ErrorResponse of(ErrorCode errorCode, String path, String traceId, Object... args) {
        return new ErrorResponse(
            errorCode.getCode(),
            errorCode.formatMessage(args),
            Instant.now(),
            path,
            traceId,
            null
        );
    }

    public static ErrorResponse of(ErrorCode errorCode, String path, String traceId, Map<String, String> details) {
        return new ErrorResponse(
            errorCode.getCode(),
            errorCode.getMessageTemplate(),
            Instant.now(),
            path,
            traceId,
            details
        );
    }

    public static ErrorResponse of(String code, String message, String path, String traceId) {
        return new ErrorResponse(code, message, Instant.now(), path, traceId, null);
    }
}
