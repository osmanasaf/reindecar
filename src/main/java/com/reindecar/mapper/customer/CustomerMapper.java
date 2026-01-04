package com.reindecar.mapper.customer;

import com.reindecar.common.valueobject.CreditScore;
import com.reindecar.dto.customer.*;
import com.reindecar.entity.customer.AuthorizedPerson;
import com.reindecar.entity.customer.Customer;
import com.reindecar.entity.customer.CustomerCompany;
import com.reindecar.entity.customer.CustomerPerson;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = AuthorizedPersonMapper.class)
public interface CustomerMapper {

    default CustomerResponse toResponse(Customer customer) {
        if (customer instanceof CustomerPerson person) {
            return toPersonalResponse(person);
        } else if (customer instanceof CustomerCompany company) {
            return toCompanyResponse(company);
        }
        throw new IllegalArgumentException("Unknown customer type");
    }

    @Mapping(target = "displayName", expression = "java(customer.getDisplayName())")
    @Mapping(target = "personalInfo", expression = "java(toPersonalInfo(customer))")
    @Mapping(target = "companyInfo", ignore = true)
    @Mapping(target = "creditScore", expression = "java(customer.getCreditScoreValue())")
    @Mapping(target = "creditRating", expression = "java(customer.getCreditRating())")
    CustomerResponse toPersonalResponse(CustomerPerson customer);

    @Mapping(target = "displayName", expression = "java(customer.getDisplayName())")
    @Mapping(target = "personalInfo", ignore = true)
    @Mapping(target = "companyInfo", expression = "java(toCompanyInfo(customer))")
    @Mapping(target = "creditScore", expression = "java(customer.getCreditScoreValue())")
    @Mapping(target = "creditRating", expression = "java(customer.getCreditRating())")
    CustomerResponse toCompanyResponse(CustomerCompany customer);

    @Mapping(target = "licenseExpired", expression = "java(customer.isLicenseExpired())")
    PersonalInfoResponse toPersonalInfo(CustomerPerson customer);

    CompanyInfoResponse toCompanyInfo(CustomerCompany customer);

    default CustomerPerson toPersonalEntity(CreatePersonalCustomerRequest request) {
        CustomerPerson customer = CustomerPerson.create(
            request.nationalId(),
            request.firstName(),
            request.lastName(),
            request.birthDate(),
            request.phone(),
            request.email(),
            request.address(),
            request.city(),
            request.licenseNumber(),
            request.licenseClass(),
            request.licenseExpiryDate()
        );
        
        if (request.creditScore() != null) {
            customer.updateCreditScore(request.creditScore());
        }
        
        return customer;
    }

    default CustomerCompany toCompanyEntity(CreateCompanyCustomerRequest request, AuthorizedPersonMapper authorizedPersonMapper) {
        CustomerCompany customer = CustomerCompany.create(
            request.companyName(),
            request.taxNumber(),
            request.taxOffice(),
            request.tradeRegisterNo(),
            request.phone(),
            request.email(),
            request.address(),
            request.city(),
            request.invoiceAddress(),
            request.contactPersonName(),
            request.contactPersonPhone(),
            request.sector(),
            request.employeeCount()
        );
        
        if (request.creditScore() != null) {
            customer.updateCreditScore(request.creditScore());
        }
        
        return customer;
    }
}
