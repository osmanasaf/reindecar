package com.reindecar.common.exception;

public class DuplicateEntityException extends BusinessException {

    private static final String CODE = "DUPLICATE_ENTITY";

    public DuplicateEntityException(String entityName, String field, Object value) {
        super(CODE, String.format("%s zaten mevcut: %s = %s", entityName, field, value));
    }

    public DuplicateEntityException(String message) {
        super(CODE, message);
    }
}

