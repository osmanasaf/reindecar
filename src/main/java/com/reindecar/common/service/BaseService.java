package com.reindecar.common.service;

import com.reindecar.common.dto.PageResponse;
import com.reindecar.common.exception.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.function.Function;

@Slf4j
@Transactional(readOnly = true)
public abstract class BaseService<T, ID, R extends JpaRepository<T, ID>> {

    protected final R repository;
    protected final String entityName;

    protected BaseService(R repository, String entityName) {
        this.repository = repository;
        this.entityName = entityName;
    }

    public <D> PageResponse<D> findAll(Pageable pageable, Function<T, D> mapper) {
        log.info("Fetching all {} with pagination: {}", entityName, pageable);
        Page<T> entities = repository.findAll(pageable);
        return PageResponse.of(entities.map(mapper));
    }

    public <D> D findById(ID id, Function<T, D> mapper) {
        log.info("Fetching {} by id: {}", entityName, id);
        T entity = findEntityByIdOrThrow(id);
        return mapper.apply(entity);
    }

    @Transactional
    public <D> D create(T entity, Function<T, D> mapper) {
        log.info("Creating new {}", entityName);
        T saved = repository.save(entity);
        log.info("{} created with id: {}", entityName, extractId(saved));
        return mapper.apply(saved);
    }

    @Transactional
    public <D> D update(ID id, Function<T, T> updater, Function<T, D> mapper) {
        log.info("Updating {} with id: {}", entityName, id);
        T entity = findEntityByIdOrThrow(id);
        T updated = updater.apply(entity);
        T saved = repository.save(updated);
        log.info("{} updated with id: {}", entityName, id);
        return mapper.apply(saved);
    }

    @Transactional
    public void delete(ID id) {
        log.info("Deleting {} with id: {}", entityName, id);
        T entity = findEntityByIdOrThrow(id);
        repository.delete(entity);
        log.info("{} deleted with id: {}", entityName, id);
    }

    protected T findEntityByIdOrThrow(ID id) {
        return repository.findById(id)
            .orElseThrow(() -> createNotFoundException(id));
    }

    protected abstract EntityNotFoundException createNotFoundException(ID id);
    
    protected abstract ID extractId(T entity);
}
