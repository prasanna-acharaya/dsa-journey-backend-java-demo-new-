package com.bom.dsa.enums;

/**
 * Enum representing the status of a loan lead in the system.
 * Follows the loan application lifecycle from draft to disbursement.
 */
public enum LeadStatus {
    DRAFT, // Not yet submitted
    APPLIED, // Submitted by DSA
    UNDER_PROCESS, // Bank reviewing
    SANCTIONED, // Approved by bank
    DISBURSED, // Loan disbursed
    REJECTED // Application rejected
}
