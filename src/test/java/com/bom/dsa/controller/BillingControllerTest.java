package com.bom.dsa.controller;

import com.bom.dsa.dto.response.BillingResponse;
import com.bom.dsa.dto.response.BillingSummaryResponse;
import com.bom.dsa.service.BillingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@WebFluxTest(controllers = BillingController.class)
@org.springframework.context.annotation.Import(com.bom.dsa.config.TestSecurityConfig.class)
class BillingControllerTest {

        @Autowired
        private WebTestClient webTestClient;

        @MockBean
        private BillingService billingService;

        @Test
        @WithMockUser(username = "testuser")
        void getBillingById_Success() {
                UUID billingId = UUID.randomUUID();
                BillingResponse response = BillingResponse.builder()
                                .id(billingId)
                                .invoiceId("INV-001")
                                .build();

                when(billingService.getBillingById(any())).thenReturn(Mono.just(response));

                webTestClient.get()
                                .uri("/api/v1/billing/{billingId}", billingId)
                                .exchange()
                                .expectStatus().isOk()
                                .expectBody()
                                .jsonPath("$.invoiceId").isEqualTo("INV-001");
        }

        @Test
        @WithMockUser(username = "testuser")
        void getBillings_Success() {
                BillingResponse response = BillingResponse.builder()
                                .invoiceId("INV-001")
                                .build();
                Page<BillingResponse> pageResponse = new PageImpl<>(Collections.singletonList(response),
                                PageRequest.of(0, 10), 1);

                when(billingService.getBillings(any(), any(), any(), any())).thenReturn(Mono.just(pageResponse));

                webTestClient.get()
                                .uri("/api/v1/billing")
                                .exchange()
                                .expectStatus().isOk()
                                .expectBody()
                                .jsonPath("$.content[0].invoiceId").isEqualTo("INV-001");
        }

        @Test
        @WithMockUser(username = "testuser")
        void getBillingSummary_Success() {
                BillingSummaryResponse response = BillingSummaryResponse.builder()
                                .totalEarned(BigDecimal.valueOf(1000L))
                                .build();

                when(billingService.getBillingSummary(any())).thenReturn(Mono.just(response));

                webTestClient.get()
                                .uri("/api/v1/billing/summary")
                                .exchange()
                                .expectStatus().isOk()
                                .expectBody()
                                .jsonPath("$.totalEarned").isEqualTo(1000);
        }
}
