package com.reindecar.dto.customer;

import com.reindecar.common.valueobject.CreditScore;
import com.reindecar.entity.customer.CustomerStatus;
import com.reindecar.entity.customer.CustomerType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.UUID;

@Schema(description = "Customer response with all details")
public record CustomerResponse(
    @Schema(description = "Customer ID", example = "1")
    Long id,
    
    @Schema(description = "Public UUID for external reference")
    UUID publicId,
    
    @Schema(description = "Customer type (PERSONAL or COMPANY)")
    CustomerType customerType,
    
    @Schema(description = "Customer status")
    CustomerStatus status,
    
    @Schema(description = "Display name of the customer", example = "Ahmet Yılmaz")
    String displayName,
    
    @Schema(description = "Phone number", example = "5551234567")
    String phone,
    
    @Schema(description = "Email address", example = "ahmet@example.com")
    String email,
    
    @Schema(description = "City", example = "İstanbul")
    String city,
    
    @Schema(description = "Is customer blacklisted?", example = "false")
    boolean blacklisted,
    
    @Schema(description = "Reason for blacklisting")
    String blacklistReason,
    
    @Schema(description = "Credit score (0-2000)", example = "1200")
    Integer creditScore,
    
    @Schema(description = "Credit rating")
    CreditScore.CreditRating creditRating,
    
    @Schema(description = "Personal customer information (only for PERSONAL type)")
    PersonalInfoResponse personalInfo,
    
    @Schema(description = "Company customer information (only for COMPANY type)")
    CompanyInfoResponse companyInfo,
    
    @Schema(description = "Creation timestamp")
    Instant createdAt
) {}
