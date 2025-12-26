package com.bom.dsa.controller;

import com.bom.dsa.dto.request.AuthorizeApprovalRequest;
import com.bom.dsa.dto.request.DsaRequestDto;
import com.bom.dsa.dto.response.DsaResponseDto;
import com.bom.dsa.dto.response.AuthorizeApprovalResponse;
import com.bom.dsa.dto.response.VerifyApprovalResponse;
import com.bom.dsa.dto.response.PendingApprovalResponse;
import com.bom.dsa.enums.DsaStatus;
import com.bom.dsa.service.DsaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@WebFluxTest(controllers = DsaController.class, properties = "services.approval.base-url=http://localhost:8081")
@org.springframework.test.context.ActiveProfiles("test")
@org.springframework.context.annotation.Import(com.bom.dsa.config.TestSecurityConfig.class)
class DsaControllerTest {

        @Autowired
        private WebTestClient webTestClient;

        @MockBean
        private DsaService dsaService;

        @Test
        @WithMockUser
        void createDsa_Success() {
                DsaRequestDto request = DsaRequestDto.builder()
                                .name("Test DSA")
                                .mobileNumber("9876543210")
                                .build();

                DsaResponseDto response = DsaResponseDto.builder()
                                .id(UUID.randomUUID())
                                .name("Test DSA")
                                .status(DsaStatus.PENDING)
                                .build();

                when(dsaService.createDsa(any(DsaRequestDto.class), any())).thenReturn(response);

                webTestClient.post()
                                .uri("/api/v1/dsa")
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(request)
                                .exchange()
                                .expectStatus().isOk()
                                .expectBody()
                                .jsonPath("$.name").isEqualTo("Test DSA");
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        void updateDsaStatus_Success() {
                UUID id = UUID.randomUUID();
                DsaResponseDto response = DsaResponseDto.builder()
                                .id(id)
                                .status(DsaStatus.EMPANELLED)
                                .build();

                when(dsaService.updateDsaStatus(eq(id), eq(DsaStatus.EMPANELLED))).thenReturn(response);

                webTestClient.put()
                                .uri(uriBuilder -> uriBuilder
                                                .path("/api/v1/dsa/{id}/status")
                                                .queryParam("status", DsaStatus.EMPANELLED)
                                                .build(id))
                                .exchange()
                                .expectStatus().isOk()
                                .expectBody()
                                .jsonPath("$.status").isEqualTo("EMPANELLED");
        }

        @Test
        @WithMockUser
        void authorizeProduct_Success() {
                AuthorizeApprovalRequest request = AuthorizeApprovalRequest.builder()
                                .dsaId(UUID.randomUUID().toString())
                                .productType("VEHICLE_LOAN")
                                .build();

                AuthorizeApprovalResponse response = AuthorizeApprovalResponse.builder()
                                .status("AUTHORIZED")
                                .build();

                when(dsaService.authorizeDsaProduct(any())).thenReturn(Mono.just(response));

                webTestClient.post()
                                .uri("/api/v1/dsa/approval/authorize")
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(request)
                                .exchange()
                                .expectStatus().isOk()
                                .expectBody()
                                .jsonPath("$.status").isEqualTo("AUTHORIZED");
        }

        @Test
        @WithMockUser
        void verifyApprovals_Success() {
                UUID dsaId = UUID.randomUUID();
                when(dsaService.verifyDsaApprovals(dsaId)).thenReturn(Mono.just(Collections.emptyList()));

                webTestClient.get()
                                .uri("/api/v1/dsa/approval/verify/{dsaId}", dsaId)
                                .exchange()
                                .expectStatus().isOk();
        }
}
