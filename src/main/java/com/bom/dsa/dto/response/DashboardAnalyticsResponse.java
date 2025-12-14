package com.bom.dsa.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Response DTO for dashboard analytics.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardAnalyticsResponse {

    // Lead counts
    private Long totalLeads;
    private Long appliedLeads;
    private Long underProcessLeads;
    private Long sanctionedLeads;
    private Long disbursedLeads;
    private Long rejectedLeads;

    // Amount statistics
    private BigDecimal totalAmountRequested;
    private BigDecimal totalAmountDisbursed;

    // Distribution maps
    private Map<String, Long> leadsByProductType;
    private Map<String, Long> leadsByStatus;

    // Conversion rate
    private String conversionRate;
}
