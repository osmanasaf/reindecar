package com.reindecar.exception.branch;

import com.reindecar.common.exception.EntityNotFoundException;

public class BranchNotFoundException extends EntityNotFoundException {

    public BranchNotFoundException(Long id) {
        super("Branch", id);
    }

    public BranchNotFoundException(String code) {
        super("Branch with code: " + code + " not found");
    }
}
