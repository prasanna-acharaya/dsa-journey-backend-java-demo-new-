package com.bom.dsa.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Entity for storing financial details of the loan applicant.
 * One-to-One relationship with Lead entity.
 * Monthly net income is calculated automatically.
 */
@Entity
@Table(name = "financial_details")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FinancialDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lead_id", nullable = false, unique = true)
    private Lead lead;

    @Column(name = "monthly_gross_income", precision = 15, scale = 2)
    private BigDecimal monthlyGrossIncome;

    @Column(name = "monthly_deductions", precision = 15, scale = 2)
    private BigDecimal monthlyDeductions;

    @Column(name = "monthly_emi", precision = 15, scale = 2)
    private BigDecimal monthlyEmi;

    @Column(name = "monthly_net_income", precision = 15, scale = 2)
    private BigDecimal monthlyNetIncome;

    /**
     * Calculate and return monthly net income.
     * Formula: monthlyGrossIncome - monthlyDeductions
     * 
     * @return calculated net income
     */
    public BigDecimal calculateNetIncome() {
        if (monthlyGrossIncome == null) {
            return BigDecimal.ZERO;
        }
        BigDecimal deductions = monthlyDeductions != null ? monthlyDeductions : BigDecimal.ZERO;
        return monthlyGrossIncome.subtract(deductions);
    }

    /**
     * Pre-persist/update hook to calculate net income.
     */
    @PrePersist
    @PreUpdate
    public void updateCalculatedFields() {
        this.monthlyNetIncome = calculateNetIncome();
    }
}
