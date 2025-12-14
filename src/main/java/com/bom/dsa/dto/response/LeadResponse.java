package com.bom.dsa.dto.response;

import com.bom.dsa.enums.LeadStatus;
import com.bom.dsa.enums.OccupationType;
import com.bom.dsa.enums.ProductType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Response DTO for lead details.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeadResponse {

    private UUID leadId;
    private String applicationReferenceNumber;
    private LeadStatus status;
    private ProductType productType;

    private AssignedBranchDto assignedBranch;
    private BasicDetailsDto basicDetails;
    private OccupationDetailsDto occupationDetails;
    private FinancialDetailsDto financialDetails;
    private LoanDetailsDto loanDetails;
    private List<DocumentDto> documents;
    private AuditInfoDto auditInfo;

    // ======== Nested DTOs ========

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AssignedBranchDto {
        private String name;
        private String address;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class BasicDetailsDto {
        private String salutation;
        private String firstName;
        private String middleName;
        private String lastName;
        private String fullName;
        private LocalDate dateOfBirth;
        private String gender;
        private String maritalStatus;
        private String qualification;
        private String mobileNumber;
        private String emailAddress;
        private AddressDto currentAddress;
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
        private BigDecimal monthlyNetIncome;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class LoanDetailsDto {
        private BigDecimal amountRequested;
        private Integer repaymentPeriod;

        // Product-specific details
        private VehicleLoanDetailsDto vehicleLoanDetails;
        private EducationLoanDetailsDto educationLoanDetails;
        private HomeLoanDetailsDto homeLoanDetails;
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
        private BigDecimal totalCostOfVehicle;
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
        private AddressDto propertyAddress;
        private BigDecimal propertyValue;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class LoanAgainstPropertyDetailsDto {
        private String propertyType;
        private AddressDto propertyAddress;
        private BigDecimal propertyMarketValue;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DocumentDto {
        private UUID id;
        private String documentType;
        private String fileName;
        private Long fileSize;
        private String mimeType;
        private Instant uploadedAt;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AuditInfoDto {
        private String createdBy;
        private Instant createdAt;
        private String updatedBy;
        private Instant updatedAt;
    }
}
