package com.bom.dsa.service;

import com.bom.dsa.dto.request.CreateLeadRequest;
import com.bom.dsa.dto.response.LeadResponse;
import com.bom.dsa.entity.Lead;
import com.bom.dsa.enums.LeadStatus;
import com.bom.dsa.enums.ProductType;
import com.bom.dsa.repository.LeadRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LeadServiceTest {

        @Mock
        private LeadRepository leadRepository;

        private LeadService leadService;

        @BeforeEach
        void setUp() {
                leadService = new LeadService(leadRepository);
        }

        @Test
        void createLead_Success() {
                CreateLeadRequest request = CreateLeadRequest.builder()
                                .productType(ProductType.VEHICLE_LOAN)
                                .basicDetails(CreateLeadRequest.BasicDetailsDto.builder()
                                                .firstName("John")
                                                .lastName("Doe")
                                                .emailAddress("john@example.com")
                                                .mobileNumber("9876543210")
                                                .build())
                                .loanDetails(CreateLeadRequest.LoanDetailsDto.builder()
                                                .amountRequested(BigDecimal.valueOf(100000))
                                                .repaymentPeriod(12)
                                                .vehicleLoanDetails(CreateLeadRequest.VehicleLoanDetailsDto.builder()
                                                                .vehicleType("Car")
                                                                .build())
                                                .build())
                                .build();

                Lead savedLead = Lead.builder()
                                .id(UUID.randomUUID())
                                .applicationReferenceNumber("BOM1234567")
                                .productType(ProductType.VEHICLE_LOAN)
                                .status(LeadStatus.APPLIED)
                                .build();

                // Mocking the mapper behavior which sets bidirectional relationships
                // Ideally we should use a real mapper or spy, but for unit checking pure
                // service logic,
                // we assume repository save returns the populated object.
                when(leadRepository.save(any(Lead.class))).thenReturn(savedLead);

                Mono<LeadResponse> result = leadService.createLead(request, "testUser");

                StepVerifier.create(result)
                                .expectNextMatches(response -> response.getApplicationReferenceNumber()
                                                .equals("BOM1234567"))
                                .verifyComplete();
        }

        @Test
        void getLeads_Success() {
                Lead lead = Lead.builder()
                                .id(UUID.randomUUID())
                                .applicationReferenceNumber("BOM1234567")
                                .productType(ProductType.VEHICLE_LOAN)
                                .status(LeadStatus.APPLIED)
                                .createdBy("testUser")
                                .build();

                Page<Lead> page = new PageImpl<>(Collections.singletonList(lead));

                when(leadRepository.searchLeads(any(), any(), any(), any(), any(Pageable.class)))
                                .thenReturn(page);

                // We can pass nulls for optional filters
                var result = leadService.getLeads("testUser", null, null, null, PageRequest.of(0, 10));

                StepVerifier.create(result)
                                .expectNextMatches(p -> p.getTotalElements() == 1)
                                .verifyComplete();
        }
}
