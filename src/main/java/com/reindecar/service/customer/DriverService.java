package com.reindecar.service.customer;

import com.reindecar.common.dto.PageResponse;
import com.reindecar.common.exception.EntityNotFoundException;
import com.reindecar.common.service.BaseService;
import com.reindecar.dto.customer.CreateDriverRequest;
import com.reindecar.dto.customer.DriverResponse;
import com.reindecar.entity.customer.Driver;
import com.reindecar.exception.customer.CustomerNotFoundException;
import com.reindecar.exception.customer.DriverNotFoundException;
import com.reindecar.repository.customer.CustomerRepository;
import com.reindecar.repository.customer.DriverRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class DriverService extends BaseService<Driver, Long, DriverRepository> {

    private final CustomerRepository customerRepository;

    public DriverService(DriverRepository repository, CustomerRepository customerRepository) {
        super(repository, "Driver");
        this.customerRepository = customerRepository;
    }

    public DriverResponse getDriverById(Long id) {
        return findById(id, this::toResponse);
    }

    public PageResponse<DriverResponse> getDrivers(Pageable pageable, Long customerId, Boolean active) {
        Page<Driver> drivers = resolveDrivers(pageable, customerId, active);
        return PageResponse.of(drivers.map(this::toResponse));
    }

    private Page<Driver> resolveDrivers(Pageable pageable, Long customerId, Boolean active) {
        if (customerId == null && active == null) {
            return repository.findAll(pageable);
        }
        if (customerId != null && Boolean.TRUE.equals(active)) {
            return repository.findByCustomerIdAndActiveTrue(customerId, pageable);
        }
        if (customerId != null && Boolean.FALSE.equals(active)) {
            return repository.findByCustomerIdAndActiveFalse(customerId, pageable);
        }
        if (Boolean.TRUE.equals(active)) {
            return repository.findByActiveTrue(pageable);
        }
        if (Boolean.FALSE.equals(active)) {
            return repository.findByActiveFalse(pageable);
        }
        return repository.findAll(pageable);
    }

    @Transactional
    public DriverResponse createDriver(CreateDriverRequest request) {
        validateCustomerExists(request.customerId());
        Driver driver = toEntity(request);
        return create(driver, this::toResponse);
    }

    private void validateCustomerExists(Long customerId) {
        if (customerId == null) {
            return;
        }
        if (!customerRepository.existsById(customerId)) {
            throw new CustomerNotFoundException(customerId);
        }
    }

    private Driver toEntity(CreateDriverRequest request) {
        boolean primary = request.primary() != null && request.primary();
        return Driver.create(
            request.customerId(),
            request.nationalId(),
            request.firstName(),
            request.lastName(),
            request.phone(),
            request.licenseNumber(),
            request.licenseClass(),
            request.licenseExpiryDate(),
            primary
        );
    }

    private DriverResponse toResponse(Driver driver) {
        return new DriverResponse(
            driver.getId(),
            driver.getCustomerId(),
            driver.getNationalId(),
            driver.getFirstName(),
            driver.getLastName(),
            driver.getPhone(),
            driver.getLicenseNumber(),
            driver.getLicenseClass(),
            driver.getLicenseExpiryDate(),
            driver.isPrimary(),
            driver.isActive()
        );
    }

    @Override
    protected EntityNotFoundException createNotFoundException(Long id) {
        return new DriverNotFoundException(id);
    }

    @Override
    protected Long extractId(Driver entity) {
        return entity.getId();
    }
}
