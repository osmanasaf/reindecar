package com.reindecar.exception.maintenance;

import com.reindecar.common.exception.EntityNotFoundException;

public class MaintenanceRecordNotFoundException extends EntityNotFoundException {

    public MaintenanceRecordNotFoundException(Long id) {
        super("MaintenanceRecord", id);
    }
}
