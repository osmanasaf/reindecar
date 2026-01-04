package com.reindecar.dto.customer;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Company customer specific information")
public record CompanyInfoResponse(
    @Schema(description = "Company name", example = "Acme Teknoloji A.Ş.")
    String companyName,
    
    @Schema(description = "Tax number", example = "1234567890")
    String taxNumber,
    
    @Schema(description = "Tax office", example = "Kadıköy Vergi Dairesi")
    String taxOffice,
    
    @Schema(description = "Trade register number", example = "123456")
    String tradeRegisterNo,
    
    @Schema(description = "Invoice address")
    String invoiceAddress,
    
    @Schema(description = "Contact person name", example = "Mehmet Demir")
    String contactPersonName,
    
    @Schema(description = "Contact person phone", example = "5559876543")
    String contactPersonPhone,
    
    @Schema(description = "Company sector/industry", example = "Teknoloji")
    String sector,
    
    @Schema(description = "Number of employees", example = "150")
    Integer employeeCount
) {}
