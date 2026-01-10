 package com.reindecar.service.customer;

import com.reindecar.common.exception.BusinessException;
import com.reindecar.common.exception.ErrorCode;

import com.reindecar.common.exception.BusinessException;

import com.reindecar.common.exception.EntityNotFoundException;
import com.reindecar.common.exception.ErrorCode;
import com.reindecar.common.service.BaseService;
import com.reindecar.dto.customer.AuthorizedPersonResponse;
import com.reindecar.dto.customer.CreateAuthorizedPersonRequest;
import com.reindecar.dto.customer.UpdateAuthorizedPersonRequest;
import com.reindecar.entity.customer.AuthorizedPerson;
import com.reindecar.entity.customer.CustomerCompany;
import com.reindecar.exception.customer.CustomerNotFoundException;
import com.reindecar.mapper.customer.AuthorizedPersonMapper;
import com.reindecar.repository.customer.AuthorizedPersonRepository;
import com.reindecar.repository.customer.CustomerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@Transactional(readOnly = true)
public class AuthorizedPersonService extends BaseService<AuthorizedPerson, Long, AuthorizedPersonRepository> {

    private final AuthorizedPersonMapper mapper;
    private final CustomerRepository customerRepository;

    public AuthorizedPersonService(
            AuthorizedPersonRepository repository,
            AuthorizedPersonMapper mapper,
            CustomerRepository customerRepository) {
        super(repository, "AuthorizedPerson");
        this.mapper = mapper;
        this.customerRepository = customerRepository;
    }

    public List<AuthorizedPersonResponse> getAllByCompany(Long companyCustomerId) {
        validateCompanyExists(companyCustomerId);
        return repository.findByCompanyCustomerId(companyCustomerId).stream()
                .map(mapper::toResponse)
                .toList();
    }

    public AuthorizedPersonResponse getById(Long id) {
        return findById(id, mapper::toResponse);
    }

    @Transactional
    public AuthorizedPersonResponse createAuthorizedPerson(Long companyCustomerId, CreateAuthorizedPersonRequest request) {
        CustomerCompany company = validateAndGetCompany(companyCustomerId);
        
        AuthorizedPerson person = mapper.toEntity(request, companyCustomerId);
        
        if (request.isPrimary()) {
            unsetExistingPrimaryContact(companyCustomerId);
        }
        
        company.addAuthorizedPerson(person);
        return create(person, mapper::toResponse);
    }

    @Transactional
    public AuthorizedPersonResponse updateAuthorizedPerson(Long id, UpdateAuthorizedPersonRequest request) {
        return update(id, person -> {
            person.updateInfo(
                    request.firstName(),
                    request.lastName(),
                    request.title(),
                    request.phone(),
                    request.email()
            );
            return person;
        }, mapper::toResponse);
    }

    @Transactional
    public void setAsPrimary(Long id) {
        AuthorizedPerson person = repository.findById(id)
                .orElseThrow(() -> createNotFoundException(id));
        
        unsetExistingPrimaryContact(person.getCompanyCustomerId());
        person.setAsPrimary();
        repository.save(person);
    }

    @Transactional
    public void deactivate(Long id) {
        update(id, person -> {
            validateNotLastActivePerson(person.getCompanyCustomerId(), id);
            person.deactivate();
            return person;
        }, mapper::toResponse);
    }

    @Transactional
    public void activate(Long id) {
        update(id, person -> {
            person.activate();
            return person;
        }, mapper::toResponse);
    }

    @Override
    protected EntityNotFoundException createNotFoundException(Long id) {
        return new CustomerNotFoundException(id);
    }

    @Override
    protected Long extractId(AuthorizedPerson entity) {
        return entity.getId();
    }

    private CustomerCompany validateAndGetCompany(Long companyCustomerId) {
        return (CustomerCompany) customerRepository.findById(companyCustomerId)
                .orElseThrow(() -> new CustomerNotFoundException(companyCustomerId));
    }

    private void validateCompanyExists(Long companyCustomerId) {
        if (!customerRepository.existsById(companyCustomerId)) {
            throw new CustomerNotFoundException(companyCustomerId);
        }
    }

    private void unsetExistingPrimaryContact(Long companyCustomerId) {
        repository.findPrimaryByCompanyCustomerId(companyCustomerId)
                .ifPresent(AuthorizedPerson::unsetPrimary);
    }

    private void validateNotLastActivePerson(Long companyCustomerId, Long personId) {
        long activeCount = repository.countActiveByCompanyCustomerId(companyCustomerId);
        if (activeCount <= 1) {
            throw new BusinessException(ErrorCode.INVALID_OPERATION, "Cannot deactivate the last active authorized person");
        }
    }
}
