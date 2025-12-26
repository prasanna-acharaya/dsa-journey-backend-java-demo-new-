package com.bom.dsa.controller;

import com.bom.dsa.dto.request.CreateLeadRequest;
import com.bom.dsa.dto.request.UpdateLeadRequest;
import com.bom.dsa.dto.response.LeadResponse;
import com.bom.dsa.dto.response.LeadSummaryResponse;
import com.bom.dsa.enums.LeadStatus;
import com.bom.dsa.enums.ProductType;
import com.bom.dsa.service.LeadService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@WebFluxTest(controllers = LeadController.class, properties = "services.approval.base-url=http://localhost:8081")
@org.springframework.test.context.ActiveProfiles("test")
@org.springframework.context.annotation.Import(com.bom.dsa.config.TestSecurityConfig.class)
class LeadControllerTest {

        @Autowired
        private WebTestClient webTestClient;

        @MockBean
        private LeadService leadService;

        @Test
        @WithMockUser(username = "testuser")
        void createLead_Success() {
                CreateLeadRequest request = CreateLeadRequest.builder()
                                .productType(ProductType.VEHICLE_LOAN)
                                .basicDetails(CreateLeadRequest.BasicDetailsDto.builder()
                                                .firstName("John")
                                                .lastName("Doe")
                                                .dateOfBirth(java.time.LocalDate.of(1990, 1, 1))
                                                .gender("Male")
                                                .maritalStatus("Single")
                                                .mobileNumber("9876543210")
                                                .emailAddress("john@example.com")
                                                .build())
                                .occupationDetails(CreateLeadRequest.OccupationDetailsDto.builder()
                                                .occupationType(com.bom.dsa.enums.OccupationType.SALARIED)
                                                .build())
                                .build();
                LeadResponse response = LeadResponse.builder()
                                .leadId(UUID.randomUUID())
                                .applicationReferenceNumber("BOM123")
                                .build();

                when(leadService.createLead(any(), any())).thenReturn(Mono.just(response));

                webTestClient.post()
                                .uri("/api/v1/leads")
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(request)
                                .exchange()
                                .expectStatus().isCreated()
                                .expectBody()
                                .jsonPath("$.applicationReferenceNumber").isEqualTo("BOM123");
        }

        @Test
        @WithMockUser(username = "testuser")
        void getLeadById_Success() {
                UUID leadId = UUID.randomUUID();
                LeadResponse response = LeadResponse.builder()
                                .leadId(leadId)
                                .applicationReferenceNumber("BOM123")
                                .build();

                when(leadService.getLeadById(leadId)).thenReturn(Mono.just(response));

                webTestClient.get()
                                .uri("/api/v1/leads/{leadId}", leadId)
                                .exchange()
                                .expectStatus().isOk()
                                .expectBody()
                                .jsonPath("$.leadId").isEqualTo(leadId.toString());
        }

        @Test
        @WithMockUser(username = "testuser")
        void getLeads_Success() {
                LeadSummaryResponse summary = LeadSummaryResponse.builder()
                                .leadId(UUID.randomUUID())
                                .applicationReferenceNumber("BOM123")
                                .build();
                Page<LeadSummaryResponse> pageResponse = new PageImpl<>(Collections.singletonList(summary),
                                PageRequest.of(0, 10), 1);

                when(leadService.getLeads(any(), any(), any(), any(), any())).thenReturn(Mono.just(pageResponse));

                webTestClient.get()
                                .uri("/api/v1/leads")
                                .exchange()
                                .expectStatus().isOk()
                                .expectBody()
                                .jsonPath("$.content[0].applicationReferenceNumber").isEqualTo("BOM123");
        }

        @Test
        @WithMockUser(username = "testuser")
        void updateLead_Success() {
                UUID leadId = UUID.randomUUID();
                UpdateLeadRequest request = UpdateLeadRequest.builder()
                                .leadId(leadId)
                                .build();

                LeadResponse response = LeadResponse.builder()
                                .leadId(leadId)
                                .status(LeadStatus.DRAFT)
                                .build();

                when(leadService.updateLead(any(), any())).thenReturn(Mono.just(response));

                webTestClient.put()
                                .uri("/api/v1/leads/{leadId}", leadId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(request)
                                .exchange()
                                .expectStatus().isOk()
                                .expectBody()
                                .jsonPath("$.leadId").isEqualTo(leadId.toString());
        }

        @Test
        @WithMockUser(username = "testuser")
        void deleteLead_Success() {
                UUID leadId = UUID.randomUUID();
                when(leadService.deleteLead(any(), any())).thenReturn(Mono.empty());

                webTestClient.delete()
                                .uri("/api/v1/leads/{leadId}", leadId)
                                .exchange()
                                .expectStatus().isNoContent();
        }
}
