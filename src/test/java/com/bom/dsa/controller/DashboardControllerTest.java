package com.bom.dsa.controller;

import com.bom.dsa.dto.response.DashboardAnalyticsResponse;
import com.bom.dsa.service.DashboardService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@WebFluxTest(controllers = DashboardController.class)
@org.springframework.context.annotation.Import(com.bom.dsa.config.TestSecurityConfig.class)
class DashboardControllerTest {

        @Autowired
        private WebTestClient webTestClient;

        @MockBean
        private DashboardService dashboardService;

        @Test
        @WithMockUser(username = "testuser")
        void getDashboardAnalytics_Success() {
                DashboardAnalyticsResponse response = DashboardAnalyticsResponse.builder()
                                .totalLeads(10L)
                                .build();

                when(dashboardService.getDashboardAnalytics(any())).thenReturn(Mono.just(response));

                webTestClient.get()
                                .uri("/api/v1/dashboard")
                                .exchange()
                                .expectStatus().isOk()
                                .expectBody()
                                .jsonPath("$.totalLeads").isEqualTo(10);
        }

        @Test
        @WithMockUser(username = "admin", roles = { "ADMIN" })
        void getAdminDashboardAnalytics_Success() {
                DashboardAnalyticsResponse response = DashboardAnalyticsResponse.builder()
                                .totalLeads(100L)
                                .build();

                when(dashboardService.getAdminDashboardAnalytics()).thenReturn(Mono.just(response));

                webTestClient.get()
                                .uri("/api/v1/dashboard/admin")
                                .exchange()
                                .expectStatus().isOk()
                                .expectBody()
                                .jsonPath("$.totalLeads").isEqualTo(100);
        }

}
