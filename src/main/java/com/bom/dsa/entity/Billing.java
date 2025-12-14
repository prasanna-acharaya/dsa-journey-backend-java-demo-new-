package com.bom.dsa.entity;

import com.bom.dsa.enums.BillingStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Entity for DSA billing/commission tracking.
 * Tracks payout details for DSA users based on disbursed loans.
 */
@Entity
@Table(name = "billing")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Billing {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "invoice_id", unique = true, nullable = false, length = 50)
    private String invoiceId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "period_start", nullable = false)
    private LocalDate periodStart;

    @Column(name = "period_end", nullable = false)
    private LocalDate periodEnd;

    @Column(name = "payout_percentage", precision = 5, scale = 4, nullable = false)
    private BigDecimal payoutPercentage;

    @Column(name = "amount", precision = 15, scale = 2, nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    @Builder.Default
    private BillingStatus status = BillingStatus.PENDING;

    @Column(name = "generated_at", nullable = false)
    private Instant generatedAt;

    @Column(name = "paid_at")
    private Instant paidAt;

    @PrePersist
    public void prePersist() {
        if (invoiceId == null) {
            invoiceId = "BOMI" + String.format("%08d", (int) (Math.random() * 100000000));
        }
        if (generatedAt == null) {
            generatedAt = Instant.now();
        }
    }

    /**
     * Mark billing as paid.
     */
    public void markAsPaid() {
        this.status = BillingStatus.PAID;
        this.paidAt = Instant.now();
    }

    /**
     * Get formatted period string.
     */
    public String getFormattedPeriod() {
        return periodStart.getDayOfMonth() + " " + periodStart.getMonth() + " - " +
                periodEnd.getDayOfMonth() + " " + periodEnd.getMonth() + " " + periodEnd.getYear();
    }
}
