package com.reindecar.common.exception;

public class DuplicateEntityException extends BusinessException {

    public DuplicateEntityException(String entityName, String field, Object value) {
        super(ErrorCode.DUPLICATE_ENTITY, String.format("%s zaten mevcut: %s = %s", entityName, field, value));
    }

    public DuplicateEntityException(String message) {
        super(ErrorCode.DUPLICATE_ENTITY, message);
    }
}
