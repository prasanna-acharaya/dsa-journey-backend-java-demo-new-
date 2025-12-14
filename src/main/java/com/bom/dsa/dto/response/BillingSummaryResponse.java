package com.bom.dsa.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Response DTO for billing summary.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BillingSummaryResponse {

    private BigDecimal totalEarned;
    private BigDecimal pendingAmount;
    private BigDecimal paidAmount;
    private Long totalInvoices;
    private Long pendingInvoices;
    private Long paidInvoices;
}
