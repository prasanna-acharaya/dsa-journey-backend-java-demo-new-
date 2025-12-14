package com.bom.dsa.controller;

import com.bom.dsa.dto.request.CreateLeadRequest;
import com.bom.dsa.dto.request.UpdateLeadRequest;
import com.bom.dsa.dto.response.LeadResponse;
import com.bom.dsa.dto.response.LeadSummaryResponse;
import com.bom.dsa.enums.LeadStatus;
import com.bom.dsa.enums.ProductType;
import com.bom.dsa.service.LeadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * REST Controller for Lead management APIs.
 */
@RestController
@RequestMapping("/api/v1/leads")
@Slf4j
@Tag(name = "Lead Management", description = "Lead CRUD APIs")
@SecurityRequirement(name = "bearerAuth")
public class LeadController {

        private final LeadService leadService;

        public LeadController(LeadService leadService) {
                this.leadService = leadService;
        }

        @PostMapping
        @Operation(summary = "Create Lead", description = "Create a new lead with all details")
        public Mono<ResponseEntity<LeadResponse>> createLead(
                        @Valid @RequestBody CreateLeadRequest request,
                        @AuthenticationPrincipal String username) {
                log.info("Creating new lead by user: {}", username);
                return leadService.createLead(request, username)
                                .map(response -> ResponseEntity.status(HttpStatus.CREATED).body(response));
        }

        @GetMapping("/{leadId}")
        @Operation(summary = "Get Lead by ID", description = "Get lead details by ID")
        public Mono<ResponseEntity<LeadResponse>> getLeadById(
                        @Parameter(description = "Lead ID") @PathVariable UUID leadId) {
                return leadService.getLeadById(leadId)
                                .map(ResponseEntity::ok);
        }

        @GetMapping
        @Operation(summary = "Get Leads", description = "Get leads with pagination, filtering, and sorting")
        public Mono<ResponseEntity<Page<LeadSummaryResponse>>> getLeads(
                        @AuthenticationPrincipal String username,
                        @RequestParam(required = false) LeadStatus status,
                        @RequestParam(required = false) ProductType productType,
                        @RequestParam(required = false) String searchTerm,
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "12") int size,
                        @RequestParam(defaultValue = "createdAt") String sortBy,
                        @RequestParam(defaultValue = "desc") String sortDir) {

                Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending()
                                : Sort.by(sortBy).descending();
                Pageable pageable = PageRequest.of(page, size, sort);

                return leadService.getLeads(username, status, productType, searchTerm, pageable)
                                .map(ResponseEntity::ok);
        }

        @PutMapping("/{leadId}")
        @Operation(summary = "Update Lead", description = "Update existing lead (before submission)")
        public Mono<ResponseEntity<LeadResponse>> updateLead(
                        @Parameter(description = "Lead ID") @PathVariable UUID leadId,
                        @Valid @RequestBody UpdateLeadRequest request,
                        @AuthenticationPrincipal String username) {
                request.setLeadId(leadId);
                log.info("Updating lead {} by user: {}", leadId, username);
                return leadService.updateLead(request, username)
                                .map(ResponseEntity::ok);
        }

        @DeleteMapping("/{leadId}")
        @Operation(summary = "Delete Lead", description = "Soft delete lead (mark as deleted)")
        public Mono<ResponseEntity<Void>> deleteLead(
                        @Parameter(description = "Lead ID") @PathVariable UUID leadId,
                        @AuthenticationPrincipal String username) {
                log.info("Deleting lead {} by user: {}", leadId, username);
                return leadService.deleteLead(leadId, username)
                                .then(Mono.just(ResponseEntity.noContent().<Void>build()));
        }
}
