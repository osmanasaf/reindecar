package com.reindecar.entity.customer;

import com.reindecar.common.validation.ValidTurkishTaxNumber;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@DiscriminatorValue("COMPANY")
@Getter
public class CustomerCompany extends Customer {

    @NotBlank(message = "Company name is required")
    @Size(max = 200)
    @Column(length = 200)
    private String companyName;

    @ValidTurkishTaxNumber
    @Column(length = 20, unique = true)
    private String taxNumber;

    @Size(max = 100)
    @Column(length = 100)
    private String taxOffice;

    @Size(max = 50)
    @Column(length = 50)
    private String tradeRegisterNo;

    @Size(max = 500)
    @Column(length = 500)
    private String invoiceAddress;

    @Size(max = 100)
    @Column(length = 100)
    private String contactPersonName;

    @Size(max = 20)
    @Column(length = 20)
    private String contactPersonPhone;

    @Size(max = 100)
    @Column(length = 100)
    private String sector;

    @Column(name = "employee_count")
    private Integer employeeCount;

    @OneToMany(mappedBy = "companyCustomerId", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<AuthorizedPerson> authorizedPersons = new ArrayList<>();

    protected CustomerCompany() {
        super(CustomerType.COMPANY);
    }

    public static CustomerCompany create(
            String companyName,
            String taxNumber,
            String taxOffice,
            String tradeRegisterNo,
            String phone,
            String email,
            String address,
            String city,
            String invoiceAddress,
            String contactPersonName,
            String contactPersonPhone,
            String sector,
            Integer employeeCount) {
        
        CustomerCompany customer = new CustomerCompany();
        customer.companyName = companyName;
        customer.taxNumber = taxNumber;
        customer.taxOffice = taxOffice;
        customer.tradeRegisterNo = tradeRegisterNo;
        customer.invoiceAddress = invoiceAddress;
        customer.contactPersonName = contactPersonName;
        customer.contactPersonPhone = contactPersonPhone;
        customer.sector = sector;
        customer.employeeCount = employeeCount;
        customer.setContactInfo(phone, email, address, city);
        return customer;
    }

    public void updateInfo(
            String companyName,
            String taxOffice,
            String tradeRegisterNo,
            String phone,
            String email,
            String address,
            String city,
            String invoiceAddress,
            String contactPersonName,
            String contactPersonPhone,
            String sector,
            Integer employeeCount) {
        
        this.companyName = companyName;
        this.taxOffice = taxOffice;
        this.tradeRegisterNo = tradeRegisterNo;
        this.invoiceAddress = invoiceAddress;
        this.contactPersonName = contactPersonName;
        this.contactPersonPhone = contactPersonPhone;
        this.sector = sector;
        this.employeeCount = employeeCount;
        setContactInfo(phone, email, address, city);
    }

    public void addAuthorizedPerson(AuthorizedPerson person) {
        if (person.isPrimary()) {
            authorizedPersons.forEach(AuthorizedPerson::unsetPrimary);
        }
        authorizedPersons.add(person);
    }

    public void removeAuthorizedPerson(AuthorizedPerson person) {
        authorizedPersons.remove(person);
    }

    public AuthorizedPerson getPrimaryContact() {
        return authorizedPersons.stream()
                .filter(AuthorizedPerson::isPrimary)
                .filter(AuthorizedPerson::isActive)
                .findFirst()
                .orElse(null);
    }

    public List<AuthorizedPerson> getActiveAuthorizedPersons() {
        return authorizedPersons.stream()
                .filter(AuthorizedPerson::isActive)
                .toList();
    }

    public boolean hasActiveAuthorizedPerson() {
        return authorizedPersons.stream()
                .anyMatch(AuthorizedPerson::isActive);
    }

    @Override
    public String getDisplayName() {
        return companyName;
    }
}
