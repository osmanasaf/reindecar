package com.reindecar.dto.customer;

import com.reindecar.common.validation.ValidTurkishTaxNumber;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

@Schema(description = "Request to create a company customer")
public record CreateCompanyCustomerRequest(
    @NotBlank(message = "Company name is required")
    @Size(max = 200)
    @Schema(description = "Company name", example = "Acme Teknoloji A.Ş.")
    String companyName,

    @ValidTurkishTaxNumber
    @NotBlank(message = "Tax number is required")
    @Size(max = 20)
    @Schema(description = "Turkish Tax Number (Vergi No)", example = "1234567890")
    String taxNumber,

    @NotBlank(message = "Tax office is required")
    @Size(max = 100)
    @Schema(description = "Tax office name", example = "Kadıköy Vergi Dairesi")
    String taxOffice,

    @Size(max = 50)
    @Schema(description = "Trade register number", example = "123456")
    String tradeRegisterNo,

    @NotBlank(message = "Phone is required")
    @Size(max = 20)
    @Schema(description = "Company phone number", example = "2165551234")
    String phone,

    @Email
    @Size(max = 100)
    @Schema(description = "Company email address", example = "info@acmetech.com")
    String email,

    @Size(max = 500)
    @Schema(description = "Company address", example = "Teknoloji Caddesi No:45 Kat:3")
    String address,

    @Size(max = 50)
    @Schema(description = "Company city", example = "İstanbul")
    String city,

    @NotBlank(message = "Invoice address is required")
    @Size(max = 500)
    @Schema(description = "Invoice address", example = "Teknoloji Caddesi No:45 Kat:3 Kadıköy/İstanbul")
    String invoiceAddress,

    @Size(max = 100)
    @Schema(description = "Contact person name", example = "Mehmet Demir")
    String contactPersonName,

    @Size(max = 20)
    @Schema(description = "Contact person phone", example = "5559876543")
    String contactPersonPhone,

    @Size(max = 100)
    @Schema(description = "Company sector/industry", example = "Teknoloji")
    String sector,

    @Schema(description = "Number of employees", example = "150")
    Integer employeeCount,

    @Schema(description = "Company's credit score (0-2000)", example = "1500", minimum = "0", maximum = "2000")
    Integer creditScore,

    @NotEmpty(message = "At least one authorized person is required")
    @Valid
    @Schema(description = "List of authorized persons for the company")
    List<CreateAuthorizedPersonRequest> authorizedPersons
) {}
