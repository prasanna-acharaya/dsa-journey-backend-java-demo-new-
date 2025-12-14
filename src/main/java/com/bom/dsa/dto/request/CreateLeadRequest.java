package com.bom.dsa.dto.request;

import com.bom.dsa.enums.OccupationType;
import com.bom.dsa.enums.ProductType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Request DTO for creating a new lead.
 * Supports all loan product types with nested details.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateLeadRequest {

    @NotNull(message = "Product type is required")
    private ProductType productType;

    @Valid
    @NotNull(message = "Basic details are required")
    private BasicDetailsDto basicDetails;

    @Valid
    @NotNull(message = "Occupation details are required")
    private OccupationDetailsDto occupationDetails;

    @Valid
    private FinancialDetailsDto financialDetails;

    @Valid
    private LoanDetailsDto loanDetails;

    private List<@Valid DocumentDto> documents;

    // ======== Nested DTOs ========

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class BasicDetailsDto {
        private String salutation;

        @NotBlank(message = "First name is required")
        @Size(max = 100)
        private String firstName;

        @Size(max = 100)
        private String middleName;

        @NotBlank(message = "Last name is required")
        @Size(max = 100)
        private String lastName;

        @NotNull(message = "Date of birth is required")
        private LocalDate dateOfBirth;

        @NotBlank(message = "Gender is required")
        private String gender;

        @NotBlank(message = "Marital status is required")
        private String maritalStatus;

        private String qualification;

        @NotBlank(message = "Mobile number is required")
        @Pattern(regexp = "^[6-9]\\d{9}$", message = "Invalid mobile number")
        private String mobileNumber;

        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        private String emailAddress;

        @Valid
        private AddressDto currentAddress;

        @Valid
        private AddressDto permanentAddress;

        private Boolean sameAsCurrentAddress;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AddressDto {
        private String addressLine1;
        private String addressLine2;
        private String addressLine3;
        private String country;
        private String state;
        private String city;
        private String pincode;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OccupationDetailsDto {
        @NotNull(message = "Occupation type is required")
        private OccupationType occupationType;

        private String companyType;
        private String employerName;
        private String designation;
        private BigDecimal totalExperience;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class FinancialDetailsDto {
        private BigDecimal monthlyGrossIncome;
        private BigDecimal monthlyDeductions;
        private BigDecimal monthlyEmi;
        // monthlyNetIncome is calculated automatically
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class LoanDetailsDto {
        // Common loan fields
        @NotNull(message = "Amount requested is required")
        @Positive(message = "Amount must be positive")
        private BigDecimal amountRequested;

        @NotNull(message = "Repayment period is required")
        @Positive(message = "Repayment period must be positive")
        private Integer repaymentPeriod;

        // Vehicle loan specific
        private VehicleLoanDetailsDto vehicleLoanDetails;

        // Education loan specific
        private EducationLoanDetailsDto educationLoanDetails;

        // Home loan specific
        private HomeLoanDetailsDto homeLoanDetails;

        // Loan against property specific
        private LoanAgainstPropertyDetailsDto loanAgainstPropertyDetails;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class VehicleLoanDetailsDto {
        private String vehicleType;
        private String make;
        private String model;
        private BigDecimal exShowroomPrice;
        private BigDecimal insuranceCost;
        private BigDecimal roadTax;
        private BigDecimal accessoriesOtherCost;

        @Valid
        private DealerDetailsDto dealerDetails;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DealerDetailsDto {
        private String dealerName;
        private String addressLine1;
        private String addressLine2;
        private String addressLine3;
        private String country;
        private String state;
        private String city;
        private String pincode;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class EducationLoanDetailsDto {
        private String courseName;
        private String institutionName;
        private String institutionCountry;
        private String institutionState;
        private String institutionCity;
        private Integer courseDurationYears;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class HomeLoanDetailsDto {
        private String propertyType;
        @Valid
        private AddressDto propertyAddress;
        private BigDecimal propertyValue;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class LoanAgainstPropertyDetailsDto {
        private String propertyType;
        @Valid
        private AddressDto propertyAddress;
        private BigDecimal propertyMarketValue;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DocumentDto {
        @NotBlank(message = "Document type is required")
        private String documentType;

        @NotBlank(message = "File name is required")
        private String fileName;

        private Long fileSize;
        private String mimeType;
        private String base64Content;
    }
}
