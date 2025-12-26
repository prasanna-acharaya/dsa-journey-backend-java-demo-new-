package com.bom.dsa.controller;

import com.bom.dsa.dto.response.DashboardAnalyticsResponse;
import com.bom.dsa.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/dashboard")
@Slf4j
@Tag(name = "Dashboard", description = "Dashboard Analytics APIs")
@SecurityRequirement(name = "bearerAuth")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping
    @Operation(summary = "Get Dashboard Analytics", description = "Get dashboard analytics for the logged-in DSA user")
    public Mono<ResponseEntity<DashboardAnalyticsResponse>> getDashboardAnalytics(
            @AuthenticationPrincipal String username) {
        log.info("Fetching dashboard analytics for user: {}", username);
        return dashboardService.getDashboardAnalytics(username)
                .map(ResponseEntity::ok);
    }

    @GetMapping("/admin")
    @PreAuthorize("hasAnyRole('ADMIN', 'BANK_MANAGER')")
    @Operation(summary = "Get Admin Dashboard Analytics", description = "Get overall dashboard analytics (Admin only)")
    public Mono<ResponseEntity<DashboardAnalyticsResponse>> getAdminDashboardAnalytics() {
        log.info("Fetching admin dashboard analytics");
        return dashboardService.getAdminDashboardAnalytics()
                .map(ResponseEntity::ok);
    }
}
