package com.bom.dsa.dto.response;

import com.bom.dsa.enums.LeadStatus;
import com.bom.dsa.enums.ProductType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Summary response DTO for lead list views.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeadSummaryResponse {

    private UUID leadId;
    private String applicationReferenceNumber;
    private ProductType productType;
    private LeadStatus status;
    private String customerName;
    private String mobileNumber;
    private String emailAddress;
    private BigDecimal amountRequested;
    private Instant createdAt;
    private Instant updatedAt;
}
