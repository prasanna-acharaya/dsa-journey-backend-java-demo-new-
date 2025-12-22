package com.bom.dsa.service;

import com.bom.dsa.dto.response.DashboardAnalyticsResponse;
import com.bom.dsa.enums.LeadStatus;
import com.bom.dsa.repository.DsaRepository;
import com.bom.dsa.repository.LeadRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@org.mockito.junit.jupiter.MockitoSettings(strictness = org.mockito.quality.Strictness.LENIENT)
class DashboardServiceTest {

        @Mock
        private LeadRepository leadRepository;

        @Mock
        private DsaRepository dsaRepository;

        @InjectMocks
        private DashboardService dashboardService;

        private String testUser = "testUser";

        @Test
        void getDashboardAnalytics_Success() {
                when(leadRepository.countByCreatedByAndIsDeletedFalse(testUser)).thenReturn(10L);
                when(leadRepository.countByCreatedByAndStatusAndIsDeletedFalse(eq(testUser), eq(LeadStatus.APPLIED)))
                                .thenReturn(5L);
                when(leadRepository.countByCreatedByAndStatusAndIsDeletedFalse(eq(testUser), eq(LeadStatus.DISBURSED)))
                                .thenReturn(2L);

                List<Object[]> productStats = new ArrayList<>();
                productStats.add(new Object[] { "HOME_LOAN", 5L });
                when(leadRepository.countByCreatedByGroupByProductType(testUser)).thenReturn(productStats);

                StepVerifier.create(dashboardService.getDashboardAnalytics(testUser))
                                .expectNextMatches(response -> response.getTotalLeads() == 10L &&
                                                response.getDisbursedLeads() == 2L &&
                                                response.getConversionRate().equals("20.0%"))
                                .verifyComplete();
        }

        @Test
        void getAdminDashboardAnalytics_Success() {
                when(leadRepository.count()).thenReturn(100L);
                when(leadRepository.countByStatusAndIsDeletedFalse(LeadStatus.DISBURSED)).thenReturn(10L);
                when(dsaRepository.count()).thenReturn(50L);

                StepVerifier.create(dashboardService.getAdminDashboardAnalytics())
                                .expectNextMatches(response -> response.getTotalLeads() == 100L &&
                                                response.getTotalDsaCount() == 50L &&
                                                response.getConversionRate().equals("10.0%"))
                                .verifyComplete();
        }
}
