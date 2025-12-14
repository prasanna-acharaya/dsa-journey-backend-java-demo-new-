package com.bom.dsa.dto.response;

import com.bom.dsa.enums.BillingStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Response DTO for billing details.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BillingResponse {

    private UUID id;
    private String invoiceId;
    private String period;
    private LocalDate periodStart;
    private LocalDate periodEnd;
    private BigDecimal payoutPercentage;
    private BigDecimal amount;
    private String formattedAmount;
    private BillingStatus status;
    private Instant generatedAt;
    private Instant paidAt;

    // DSA Information
    private String dsaUniqueCode;
    private String dsaName;
}
