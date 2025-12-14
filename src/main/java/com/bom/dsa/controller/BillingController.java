package com.bom.dsa.controller;

import com.bom.dsa.dto.response.BillingResponse;
import com.bom.dsa.dto.response.BillingSummaryResponse;
import com.bom.dsa.enums.BillingStatus;
import com.bom.dsa.service.BillingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.UUID;

/**
 * REST Controller for Billing/Payment APIs.
 */
@RestController
@RequestMapping("/api/v1/billing")
@Slf4j
@Tag(name = "Billing", description = "Billing and Payment APIs")
@SecurityRequirement(name = "bearerAuth")
public class BillingController {

        private final BillingService billingService;

        public BillingController(BillingService billingService) {
                this.billingService = billingService;
        }

        @GetMapping("/{billingId}")
        @Operation(summary = "Get Billing by ID", description = "Get billing details by ID")
        public Mono<ResponseEntity<BillingResponse>> getBillingById(
                        @Parameter(description = "Billing ID") @PathVariable UUID billingId) {
                return billingService.getBillingById(billingId)
                                .map(ResponseEntity::ok);
        }

        @GetMapping("/invoice/{invoiceId}")
        @Operation(summary = "Get Billing by Invoice ID", description = "Get billing details by invoice ID for download")
        public Mono<ResponseEntity<BillingResponse>> getBillingByInvoiceId(
                        @Parameter(description = "Invoice ID") @PathVariable String invoiceId) {
                return billingService.getBillingByInvoiceId(invoiceId)
                                .map(ResponseEntity::ok);
        }

        @GetMapping
        @Operation(summary = "Get Billings", description = "Get billings with pagination and filters")
        public Mono<ResponseEntity<Page<BillingResponse>>> getBillings(
                        @AuthenticationPrincipal String username,
                        @RequestParam(required = false) BillingStatus status,
                        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodStart,
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "12") int size,
                        @RequestParam(defaultValue = "generatedAt") String sortBy,
                        @RequestParam(defaultValue = "desc") String sortDir) {

                Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending()
                                : Sort.by(sortBy).descending();
                Pageable pageable = PageRequest.of(page, size, sort);

                return billingService.getBillings(username, status, periodStart, pageable)
                                .map(ResponseEntity::ok);
        }

        @GetMapping("/summary")
        @Operation(summary = "Get Billing Summary", description = "Get billing summary (total earned, pending, paid)")
        public Mono<ResponseEntity<BillingSummaryResponse>> getBillingSummary(
                        @AuthenticationPrincipal String username) {
                return billingService.getBillingSummary(username)
                                .map(ResponseEntity::ok);
        }
}
