package com.bom.dsa.controller;

import com.bom.dsa.dto.request.DsaRequestDto;
import com.bom.dsa.dto.response.DsaResponseDto;
import com.bom.dsa.enums.DsaStatus;
import com.bom.dsa.service.DsaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/dsa")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "DSA Management", description = "APIs for managing DSA profiles and workflow")
public class DsaController {

        private final DsaService dsaService;

        @PostMapping
        @Operation(summary = "Create DSA", description = "Create a new DSA profile (Maker)")
        public reactor.core.publisher.Mono<ResponseEntity<DsaResponseDto>> createDsa(
                        @RequestBody DsaRequestDto request,
                        @org.springframework.security.core.annotation.AuthenticationPrincipal String username) {
                return dsaService.createDsa(request, username)
                                .map(ResponseEntity::ok);
        }

        @PutMapping("/{id}")
        @Operation(summary = "Update DSA", description = "Update an existing DSA profile")
        public reactor.core.publisher.Mono<ResponseEntity<DsaResponseDto>> updateDsa(@PathVariable UUID id,
                        @RequestBody DsaRequestDto request) {
                return dsaService.updateDsa(id, request)
                                .map(ResponseEntity::ok);
        }

        @GetMapping("/{id}")
        @Operation(summary = "Get DSA by ID", description = "Get a single DSA profile by its UUID")
        public reactor.core.publisher.Mono<ResponseEntity<DsaResponseDto>> getDsaById(@PathVariable UUID id) {
                return dsaService.getDsaById(id)
                                .map(ResponseEntity::ok);
        }

        @PutMapping("/{id}/status")
        @PreAuthorize("hasRole('CHECKER') or hasRole('ADMIN')")
        @Operation(summary = "Update DSA Status", description = "Approve/Reject DSA (Checker/Admin)")
        public reactor.core.publisher.Mono<ResponseEntity<DsaResponseDto>> updateDsaStatus(
                        @PathVariable UUID id,
                        @RequestParam DsaStatus status,
                        @io.swagger.v3.oas.annotations.Parameter(description = "Optional remarks for status update (can be null or empty)") @RequestParam(required = false) String remarks) {
                return dsaService.updateDsaStatus(id, status, remarks)
                                .map(ResponseEntity::ok);
        }

        @GetMapping
        @Operation(summary = "Get All DSAs", description = "Get list of DSAs with filters")
        public reactor.core.publisher.Mono<ResponseEntity<Page<DsaResponseDto>>> getAllDsas(
                        @RequestParam(required = false) String category,
                        @RequestParam(required = false) DsaStatus status,
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "10") int size,
                        @RequestParam(defaultValue = "id") String sortBy,
                        @RequestParam(defaultValue = "asc") String sortDir) {

                Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending()
                                : Sort.by(sortBy).descending();
                Pageable pageable = PageRequest.of(page, size, sort);
                return dsaService.getAllDsas(category, status, pageable)
                                .map(ResponseEntity::ok);
        }
}
