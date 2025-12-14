package com.bom.dsa.dto.request;

import com.bom.dsa.enums.LeadStatus;
import com.bom.dsa.enums.ProductType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Request DTO for updating a lead.
 * Only allowed when lead is in DRAFT status.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateLeadRequest {

    @NotNull(message = "Lead ID is required")
    private UUID leadId;

    private LeadStatus status;
    private ProductType productType;

    @Valid
    private CreateLeadRequest.BasicDetailsDto basicDetails;

    @Valid
    private CreateLeadRequest.OccupationDetailsDto occupationDetails;

    @Valid
    private CreateLeadRequest.FinancialDetailsDto financialDetails;

    @Valid
    private CreateLeadRequest.LoanDetailsDto loanDetails;
}
