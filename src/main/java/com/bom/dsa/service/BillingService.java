package com.bom.dsa.service;

import com.bom.dsa.dto.response.BillingResponse;
import com.bom.dsa.dto.response.BillingSummaryResponse;
import com.bom.dsa.entity.Billing;
import com.bom.dsa.entity.User;
import com.bom.dsa.enums.BillingStatus;
import com.bom.dsa.exception.CustomExceptions;
import com.bom.dsa.repository.BillingRepository;
import com.bom.dsa.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service for billing and commission operations.
 * Handles billing retrieval, summary, and invoice operations.
 */
@Service
@Slf4j
public class BillingService {

    private final BillingRepository billingRepository;
    private final UserRepository userRepository;

    public BillingService(BillingRepository billingRepository, UserRepository userRepository) {
        this.billingRepository = billingRepository;
        this.userRepository = userRepository;
    }

    /**
     * Get billing by ID.
     * 
     * @param billingId the billing ID
     * @return Mono containing billing response
     */
    public Mono<BillingResponse> getBillingById(UUID billingId) {
        log.info("Fetching billing by id: {}", billingId);

        return Mono.fromCallable(() -> {
            try {
                Billing billing = billingRepository.findById(billingId)
                        .orElseThrow(() -> {
                            log.warn("Billing not found with id: {}", billingId);
                            return new CustomExceptions.ResourceNotFoundException("Billing", "id", billingId);
                        });
                log.debug("Found billing: {}", billing.getInvoiceId());
                return toBillingResponse(billing);
            } catch (CustomExceptions.ResourceNotFoundException e) {
                throw e;
            } catch (Exception e) {
                log.error("Error fetching billing by id: {}", billingId, e);
                throw new CustomExceptions.BusinessException("Failed to fetch billing: " + e.getMessage());
            }
        }).subscribeOn(Schedulers.boundedElastic());
    }

    /**
     * Get billing by invoice ID.
     * 
     * @param invoiceId the invoice ID
     * @return Mono containing billing response
     */
    public Mono<BillingResponse> getBillingByInvoiceId(String invoiceId) {
        log.info("Fetching billing by invoiceId: {}", invoiceId);

        return Mono.fromCallable(() -> {
            try {
                Billing billing = billingRepository.findByInvoiceId(invoiceId)
                        .orElseThrow(() -> {
                            log.warn("Billing not found with invoiceId: {}", invoiceId);
                            return new CustomExceptions.ResourceNotFoundException("Billing", "invoiceId", invoiceId);
                        });
                log.debug("Found billing with invoice: {}", billing.getInvoiceId());
                return toBillingResponse(billing);
            } catch (CustomExceptions.ResourceNotFoundException e) {
                throw e;
            } catch (Exception e) {
                log.error("Error fetching billing by invoiceId: {}", invoiceId, e);
                throw new CustomExceptions.BusinessException("Failed to fetch billing: " + e.getMessage());
            }
        }).subscribeOn(Schedulers.boundedElastic());
    }

    /**
     * Get billings with pagination and filters.
     * 
     * @param dsaUniqueCode the DSA username
     * @param status        optional billing status filter
     * @param periodStart   optional period start filter
     * @param pageable      pagination info
     * @return Mono containing page of billing responses
     */
    public Mono<Page<BillingResponse>> getBillings(String dsaUniqueCode, BillingStatus status,
            LocalDate periodStart, Pageable pageable) {
        log.info("Fetching billings for user: {}, status: {}, periodStart: {}, page: {}",
                dsaUniqueCode, status, periodStart, pageable.getPageNumber());

        return Mono.fromCallable(() -> {
            try {
                User user = userRepository.findByDsaUniqueCode(dsaUniqueCode)
                        .orElseThrow(() -> {
                            log.warn("User not found with dsaUniqueCode: {}", dsaUniqueCode);
                            return new CustomExceptions.ResourceNotFoundException("User", "dsaUniqueCode",
                                    dsaUniqueCode);
                        });

                Page<Billing> billingPage = billingRepository.searchBillings(user.getId(), status, periodStart,
                        pageable);
                log.debug("Found {} billings, total: {}", billingPage.getNumberOfElements(),
                        billingPage.getTotalElements());

                List<BillingResponse> responses = billingPage.getContent().stream()
                        .map(this::toBillingResponse)
                        .collect(Collectors.toList());

                Page<BillingResponse> result = new PageImpl<>(responses, pageable, billingPage.getTotalElements());
                return result;

            } catch (CustomExceptions.ResourceNotFoundException e) {
                throw e;
            } catch (Exception e) {
                log.error("Error fetching billings for user: {}", dsaUniqueCode, e);
                throw new CustomExceptions.BusinessException("Failed to fetch billings: " + e.getMessage());
            }
        }).subscribeOn(Schedulers.boundedElastic());
    }

    /**
     * Get billing summary for user.
     * 
     * @param dsaUniqueCode the DSA username
     * @return Mono containing billing summary
     */
    public Mono<BillingSummaryResponse> getBillingSummary(String dsaUniqueCode) {
        log.info("Fetching billing summary for user: {}", dsaUniqueCode);

        return Mono.fromCallable(() -> {
            try {
                User user = userRepository.findByDsaUniqueCode(dsaUniqueCode)
                        .orElseThrow(() -> {
                            log.warn("User not found with dsaUniqueCode: {}", dsaUniqueCode);
                            return new CustomExceptions.ResourceNotFoundException("User", "dsaUniqueCode",
                                    dsaUniqueCode);
                        });

                BigDecimal totalEarned = safeAmount(billingRepository.sumTotalAmountByUserId(user.getId()));
                BigDecimal pendingAmount = safeAmount(
                        billingRepository.sumAmountByUserIdAndStatus(user.getId(), BillingStatus.PENDING));
                BigDecimal paidAmount = safeAmount(
                        billingRepository.sumAmountByUserIdAndStatus(user.getId(), BillingStatus.PAYMENT_SUCCESSFUL));
                Long pendingCount = safeCount(
                        billingRepository.countByUserIdAndStatus(user.getId(), BillingStatus.PENDING));
                Long paidCount = safeCount(
                        billingRepository.countByUserIdAndStatus(user.getId(), BillingStatus.PAYMENT_SUCCESSFUL));

                log.debug("Billing summary - totalEarned: {}, pending: {}, paid: {}, pendingCount: {}, paidCount: {}",
                        totalEarned, pendingAmount, paidAmount, pendingCount, paidCount);

                return BillingSummaryResponse.builder()
                        .totalEarned(totalEarned)
                        .pendingAmount(pendingAmount)
                        .paidAmount(paidAmount)
                        .totalInvoices(pendingCount + paidCount)
                        .pendingInvoices(pendingCount)
                        .paidInvoices(paidCount)
                        .build();

            } catch (CustomExceptions.ResourceNotFoundException e) {
                throw e;
            } catch (Exception e) {
                log.error("Error fetching billing summary for user: {}", dsaUniqueCode, e);
                throw new CustomExceptions.BusinessException("Failed to fetch billing summary: " + e.getMessage());
            }
        }).subscribeOn(Schedulers.boundedElastic());
    }

    /**
     * Convert Billing entity to BillingResponse DTO.
     */
    private BillingResponse toBillingResponse(Billing billing) {
        log.debug("Converting billing to response: {}", billing.getInvoiceId());

        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));

        return BillingResponse.builder()
                .id(billing.getId())
                .invoiceId(billing.getInvoiceId())
                .period(billing.getFormattedPeriod())
                .periodStart(billing.getPeriodStart())
                .periodEnd(billing.getPeriodEnd())
                .payoutPercentage(billing.getPayoutPercentage())
                .amount(billing.getAmount())
                .formattedAmount(currencyFormat.format(billing.getAmount()))
                .status(billing.getStatus())
                .generatedAt(billing.getGeneratedAt())
                .paidAt(billing.getPaidAt())
                .dsaUniqueCode(billing.getUser().getDsaUniqueCode())
                .dsaName(billing.getUser().getFullName())
                .build();
    }

    /**
     * Safe amount helper - returns ZERO if null.
     */
    private BigDecimal safeAmount(BigDecimal value) {
        return value != null ? value : BigDecimal.ZERO;
    }

    /**
     * Safe count helper - returns 0 if null.
     */
    private Long safeCount(Long value) {
        return value != null ? value : 0L;
    }
}
