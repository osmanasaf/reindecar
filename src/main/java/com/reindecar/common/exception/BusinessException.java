package com.reindecar.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class BusinessException extends RuntimeException {

    private final ErrorCode errorCode;
    private final Object[] args;

    public BusinessException(ErrorCode errorCode, Object... args) {
        super(errorCode.formatMessage(args));
        this.errorCode = errorCode;
        this.args = args;
    }

    public BusinessException(ErrorCode errorCode, Throwable cause, Object... args) {
        super(errorCode.formatMessage(args), cause);
        this.errorCode = errorCode;
        this.args = args;
    }

    public static BusinessException of(ErrorCode errorCode, Object... args) {
        return new BusinessException(errorCode, args);
    }

    public static BusinessException entityNotFound(String entityName, Object id) {
        return new BusinessException(ErrorCode.ENTITY_NOT_FOUND, entityName + " (ID: " + id + ")");
    }

    public static BusinessException duplicate(String description) {
        return new BusinessException(ErrorCode.DUPLICATE_ENTITY, description);
    }

    public static BusinessException invalidOperation(String reason) {
        return new BusinessException(ErrorCode.INVALID_OPERATION, reason);
    }

    public String getCode() {
        return errorCode.getCode();
    }

    public HttpStatus getHttpStatus() {
        return errorCode.getHttpStatus();
    }

    public String getFormattedMessage() {
        return errorCode.formatMessage(args);
    }
}
