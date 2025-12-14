package com.bom.dsa.repository;

import com.bom.dsa.entity.Billing;
import com.bom.dsa.enums.BillingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for Billing entity operations.
 */
@Repository
public interface BillingRepository extends JpaRepository<Billing, UUID> {

        @Query("SELECT b FROM Billing b JOIN FETCH b.user WHERE b.invoiceId = :invoiceId")
        Optional<Billing> findByInvoiceId(@Param("invoiceId") String invoiceId);

        Page<Billing> findByUserId(UUID userId, Pageable pageable);

        Page<Billing> findByStatus(BillingStatus status, Pageable pageable);

        Page<Billing> findByUserIdAndStatus(UUID userId, BillingStatus status, Pageable pageable);

        /**
         * Search billings with filters.
         */
        @Query("SELECT b FROM Billing b WHERE b.user.id = :userId " +
                        "AND (:status IS NULL OR b.status = :status) " +
                        "AND (:periodStart IS NULL OR b.periodStart >= :periodStart)")
        Page<Billing> searchBillings(
                        @Param("userId") UUID userId,
                        @Param("status") BillingStatus status,
                        @Param("periodStart") LocalDate periodStart,
                        Pageable pageable);

        /**
         * Sum total amount by user and status.
         */
        @Query("SELECT SUM(b.amount) FROM Billing b WHERE b.user.id = :userId AND b.status = :status")
        BigDecimal sumAmountByUserIdAndStatus(@Param("userId") UUID userId, @Param("status") BillingStatus status);

        /**
         * Sum total amount by user.
         */
        @Query("SELECT SUM(b.amount) FROM Billing b WHERE b.user.id = :userId")
        BigDecimal sumTotalAmountByUserId(@Param("userId") UUID userId);

        /**
         * Count by user and status.
         */
        Long countByUserIdAndStatus(UUID userId, BillingStatus status);
}
