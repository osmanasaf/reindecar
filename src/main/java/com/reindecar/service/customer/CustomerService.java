package com.reindecar.service.customer;

import com.reindecar.common.dto.PageResponse;
import com.reindecar.common.exception.DuplicateEntityException;
import com.reindecar.common.exception.EntityNotFoundException;
import com.reindecar.common.service.BaseService;
import com.reindecar.dto.customer.*;
import com.reindecar.entity.customer.*;
import com.reindecar.exception.customer.CustomerNotFoundException;
import com.reindecar.mapper.customer.AuthorizedPersonMapper;
import com.reindecar.mapper.customer.CustomerMapper;
import com.reindecar.repository.customer.CustomerCompanyRepository;
import com.reindecar.repository.customer.CustomerPersonRepository;
import com.reindecar.repository.customer.CustomerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Slf4j
@Transactional(readOnly = true)
public class CustomerService extends BaseService<Customer, Long, CustomerRepository> {

    private final CustomerPersonRepository customerPersonRepository;
    private final CustomerCompanyRepository customerCompanyRepository;
    private final CustomerMapper customerMapper;
    private final AuthorizedPersonMapper authorizedPersonMapper;

    public CustomerService(
            CustomerRepository repository,
            CustomerPersonRepository customerPersonRepository,
            CustomerCompanyRepository customerCompanyRepository,
            CustomerMapper customerMapper,
            AuthorizedPersonMapper authorizedPersonMapper) {
        super(repository, "Customer");
        this.customerPersonRepository = customerPersonRepository;
        this.customerCompanyRepository = customerCompanyRepository;
        this.customerMapper = customerMapper;
        this.authorizedPersonMapper = authorizedPersonMapper;
    }

    public PageResponse<CustomerResponse> getAllCustomers(Pageable pageable) {
        log.info("Fetching all customers with pagination: {}", pageable);
        Page<Customer> customers = repository.findAllActive(pageable);
        return PageResponse.of(customers.map(customerMapper::toResponse));
    }

    public PageResponse<CustomerResponse> getCustomersByType(CustomerType type, Pageable pageable) {
        log.info("Fetching customers by type: {} with pagination: {}", type, pageable);
        Page<Customer> customers = repository.findByCustomerTypeAndDeletedFalse(type, pageable);
        return PageResponse.of(customers.map(customerMapper::toResponse));
    }

    public PageResponse<CustomerResponse> getBlacklistedCustomers(Pageable pageable) {
        log.info("Fetching blacklisted customers with pagination: {}", pageable);
        Page<Customer> customers = repository.findBlacklisted(pageable);
        return PageResponse.of(customers.map(customerMapper::toResponse));
    }

    public CustomerResponse getCustomerById(Long id) {
        log.info("Fetching customer by id: {}", id);
        return findById(id, customerMapper::toResponse);
    }

    public CustomerResponse getCustomerByPublicId(UUID publicId) {
        log.info("Fetching customer by publicId: {}", publicId);
        Customer customer = repository.findByPublicId(publicId)
            .orElseThrow(() -> new CustomerNotFoundException(publicId.toString()));
        return customerMapper.toResponse(customer);
    }

    @Transactional
    public CustomerResponse createPersonalCustomer(CreatePersonalCustomerRequest request) {
        log.info("Creating personal customer with nationalId: {}", request.nationalId());

        validateUniqueNationalId(request.nationalId());

        CustomerPerson customer = customerMapper.toPersonalEntity(request);
        return create(customer, customerMapper::toResponse);
    }

    @Transactional
    public CustomerResponse createCompanyCustomer(CreateCompanyCustomerRequest request) {
        log.info("Creating company customer with taxNumber: {}", request.taxNumber());

        validateUniqueTaxNumber(request.taxNumber());

        CustomerCompany customer = customerMapper.toCompanyEntity(request, authorizedPersonMapper);
        CustomerCompany savedCustomer = repository.save(customer);
        
        request.authorizedPersons().forEach(personRequest -> {
            AuthorizedPerson person = authorizedPersonMapper.toEntity(personRequest, savedCustomer.getId());
            savedCustomer.addAuthorizedPerson(person);
        });
        
        return customerMapper.toResponse(savedCustomer);
    }

    @Transactional
    public void blacklistCustomer(Long id, BlacklistRequest request) {
        log.info("Blacklisting customer with id: {}", id);

        update(id, customer -> {
            customer.blacklist(request.reason());
            return customer;
        }, customerMapper::toResponse);

        log.info("Customer blacklisted successfully with id: {}", id);
    }

    @Transactional
    public void removeFromBlacklist(Long id) {
        log.info("Removing customer from blacklist with id: {}", id);

        update(id, customer -> {
            customer.removeFromBlacklist();
            return customer;
        }, customerMapper::toResponse);

        log.info("Customer removed from blacklist successfully with id: {}", id);
    }

    @Transactional
    public void deleteCustomer(Long id) {
        log.info("Deleting customer with id: {}", id);

        update(id, customer -> {
            customer.markAsDeleted();
            return customer;
        }, customerMapper::toResponse);

        log.info("Customer deleted successfully with id: {}", id);
    }

    @Override
    protected EntityNotFoundException createNotFoundException(Long id) {
        return new CustomerNotFoundException(id);
    }

    @Override
    protected Long extractId(Customer entity) {
        return entity.getId();
    }

    private void validateUniqueNationalId(String nationalId) {
        if (customerPersonRepository.existsByNationalIdAndDeletedFalse(nationalId)) {
            throw new DuplicateEntityException("Customer", "nationalId", nationalId);
        }
    }

    private void validateUniqueTaxNumber(String taxNumber) {
        if (customerCompanyRepository.existsByTaxNumberAndDeletedFalse(taxNumber)) {
            throw new DuplicateEntityException("Customer", "taxNumber", taxNumber);
        }
    }
}
