package com.reindecar.common.controller;

import com.reindecar.common.dto.ApiResponse;
import com.reindecar.common.dto.PageResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

public abstract class BaseController<REQUEST, RESPONSE, ID> {

    @GetMapping
    public ApiResponse<PageResponse<RESPONSE>> getAll(Pageable pageable) {
        return ApiResponse.success(findAll(pageable));
    }

    @GetMapping("/{id}")
    public ApiResponse<RESPONSE> getById(@PathVariable ID id) {
        return ApiResponse.success(findById(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<RESPONSE> create(@RequestBody REQUEST request) {
        return ApiResponse.success(getCreateMessage(), createEntity(request));
    }

    @PutMapping("/{id}")
    public ApiResponse<RESPONSE> update(@PathVariable ID id, @RequestBody REQUEST request) {
        return ApiResponse.success(getUpdateMessage(), updateEntity(id, request));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ApiResponse<Void> delete(@PathVariable ID id) {
        deleteEntity(id);
        return ApiResponse.success(getDeleteMessage(), null);
    }

    protected abstract PageResponse<RESPONSE> findAll(Pageable pageable);
    protected abstract RESPONSE findById(ID id);
    protected abstract RESPONSE createEntity(REQUEST request);
    protected abstract RESPONSE updateEntity(ID id, REQUEST request);
    protected abstract void deleteEntity(ID id);
    
    protected abstract String getCreateMessage();
    protected abstract String getUpdateMessage();
    protected abstract String getDeleteMessage();
}
