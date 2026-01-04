package com.reindecar.common.exception;

public class EntityNotFoundException extends BusinessException {

    private static final String CODE = "ENTITY_NOT_FOUND";

    public EntityNotFoundException(String entityName, Object id) {
        super(CODE, String.format("%s bulunamadı: %s", entityName, id));
    }

    public EntityNotFoundException(String message) {
        super(CODE, message);
    }
}

