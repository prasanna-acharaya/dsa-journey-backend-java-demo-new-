package com.bom.dsa.service;

import com.bom.dsa.dto.response.BillingResponse;
import com.bom.dsa.dto.response.BillingSummaryResponse;
import com.bom.dsa.entity.Billing;
import com.bom.dsa.entity.User;
import com.bom.dsa.enums.BillingStatus;
import com.bom.dsa.repository.BillingRepository;
import com.bom.dsa.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BillingServiceTest {

    @Mock
    private BillingRepository billingRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private BillingService billingService;

    private User testUser;
    private Billing testBilling;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(UUID.randomUUID())
                .dsaUniqueCode("DSA123")
                .fullName("Test DSA")
                .build();

        testBilling = Billing.builder()
                .id(UUID.randomUUID())
                .invoiceId("INV-001")
                .amount(BigDecimal.valueOf(5000))
                .status(BillingStatus.PENDING)
                .user(testUser)
                .periodStart(LocalDate.now().minusMonths(1))
                .periodEnd(LocalDate.now())
                .build();
    }

    @Test
    void getBillingById_Success() {
        when(billingRepository.findById(testBilling.getId())).thenReturn(Optional.of(testBilling));

        StepVerifier.create(billingService.getBillingById(testBilling.getId()))
                .expectNextMatches(response -> response.getInvoiceId().equals("INV-001"))
                .verifyComplete();
    }

    @Test
    void getBillingByInvoiceId_Success() {
        when(billingRepository.findByInvoiceId("INV-001")).thenReturn(Optional.of(testBilling));

        StepVerifier.create(billingService.getBillingByInvoiceId("INV-001"))
                .expectNextMatches(response -> response.getInvoiceId().equals("INV-001"))
                .verifyComplete();
    }

    @Test
    void getBillings_Success() {
        when(userRepository.findByDsaUniqueCode("DSA123")).thenReturn(Optional.of(testUser));
        Page<Billing> page = new PageImpl<>(Collections.singletonList(testBilling));
        when(billingRepository.searchBillings(any(), any(), any(), any())).thenReturn(page);

        StepVerifier.create(billingService.getBillings("DSA123", null, null, PageRequest.of(0, 10)))
                .expectNextMatches(p -> p.getTotalElements() == 1)
                .verifyComplete();
    }

    @Test
    void getBillingSummary_Success() {
        when(userRepository.findByDsaUniqueCode("DSA123")).thenReturn(Optional.of(testUser));
        when(billingRepository.sumTotalAmountByUserId(testUser.getId())).thenReturn(BigDecimal.valueOf(10000));
        when(billingRepository.sumAmountByUserIdAndStatus(testUser.getId(), BillingStatus.PENDING))
                .thenReturn(BigDecimal.valueOf(5000));
        when(billingRepository.sumAmountByUserIdAndStatus(testUser.getId(), BillingStatus.PAYMENT_SUCCESSFUL))
                .thenReturn(BigDecimal.valueOf(5000));
        when(billingRepository.countByUserIdAndStatus(testUser.getId(), BillingStatus.PENDING)).thenReturn(1L);
        when(billingRepository.countByUserIdAndStatus(testUser.getId(), BillingStatus.PAYMENT_SUCCESSFUL))
                .thenReturn(1L);

        StepVerifier.create(billingService.getBillingSummary("DSA123"))
                .expectNextMatches(summary -> summary.getTotalEarned().compareTo(BigDecimal.valueOf(10000)) == 0 &&
                        summary.getPendingInvoices() == 1 &&
                        summary.getPaidInvoices() == 1)
                .verifyComplete();
    }
}
