package com.reindecar.mapper.customer;

import com.reindecar.dto.customer.AuthorizedPersonResponse;
import com.reindecar.dto.customer.CreateAuthorizedPersonRequest;
import com.reindecar.entity.customer.AuthorizedPerson;
import org.springframework.stereotype.Component;

@Component
public class AuthorizedPersonMapper {

    public AuthorizedPerson toEntity(CreateAuthorizedPersonRequest request, Long companyCustomerId) {
        return AuthorizedPerson.create(
                companyCustomerId,
                request.nationalId(),
                request.firstName(),
                request.lastName(),
                request.title(),
                request.phone(),
                request.email(),
                request.isPrimary()
        );
    }

    public AuthorizedPersonResponse toResponse(AuthorizedPerson person) {
        return new AuthorizedPersonResponse(
                person.getId(),
                person.getNationalId(),
                person.getFirstName(),
                person.getLastName(),
                person.getFullName(),
                person.getTitle(),
                person.getPhone(),
                person.getEmail(),
                person.isPrimary(),
                person.isActive()
        );
    }
}
