package com.reindecar.exception.damage;

import com.reindecar.common.exception.EntityNotFoundException;

public class DamageReportNotFoundException extends EntityNotFoundException {

    public DamageReportNotFoundException(Long id) {
        super("DamageReport", id);
    }
}
