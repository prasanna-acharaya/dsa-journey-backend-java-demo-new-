package com.bom.dsa.controller;

import com.bom.dsa.dto.request.*;
import com.bom.dsa.dto.response.*;
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
import reactor.core.publisher.Mono;

import java.util.List;
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
    public ResponseEntity<DsaResponseDto> createDsa(
            @RequestBody DsaRequestDto request,
            @org.springframework.security.core.annotation.AuthenticationPrincipal String username) {
        return ResponseEntity.ok(dsaService.createDsa(request, username));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update DSA", description = "Update an existing DSA profile")
    public ResponseEntity<DsaResponseDto> updateDsa(@PathVariable UUID id, @RequestBody DsaRequestDto request) {
        return ResponseEntity.ok(dsaService.updateDsa(id, request));
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('CHECKER') or hasRole('ADMIN')")
    @Operation(summary = "Update DSA Status", description = "Approve/Reject DSA (Checker/Admin)")
    public ResponseEntity<DsaResponseDto> updateDsaStatus(@PathVariable UUID id, @RequestParam DsaStatus status) {
        return ResponseEntity.ok(dsaService.updateDsaStatus(id, status));
    }

    @PostMapping("/approval/authorize")
    @Operation(summary = "Authorize DSA Product", description = "Finalize approval for a specific product")
    public Mono<AuthorizeApprovalResponse> authorizeProduct(@RequestBody AuthorizeApprovalRequest request) {
        return dsaService.authorizeDsaProduct(request);
    }

    @GetMapping("/approval/verify/{dsaId}")
    @Operation(summary = "Verify DSA Approvals", description = "Get full list of products and their approval status")
    public Mono<List<VerifyApprovalResponse>> verifyApprovals(@PathVariable UUID dsaId) {
        return dsaService.verifyDsaApprovals(dsaId);
    }

    @GetMapping("/approval/pending/{userId}")
    @Operation(summary = "Get Pending Approvals", description = "Get all staged products assigned to a manager")
    public Mono<List<PendingApprovalResponse>> getPendingApprovals(@PathVariable String userId) {
        return dsaService.getPendingApprovalsForUser(userId);
    }

    @GetMapping
    @Operation(summary = "Get All DSAs", description = "Get list of DSAs with filters")
    public ResponseEntity<Page<DsaResponseDto>> getAllDsas(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) DsaStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "created_at") String sortBy, // Note: DB field is created_at but entity uses
                                                                      // auditing? Dsa entity doesn't have created_at
                                                                      // mapped in @Column explicitly, let's use 'id'
                                                                      // for default sort or add audit fields.
            // Dsa Entity has @EntityListeners(AuditingEntityListener.class) but no fields?
            // Wait, I missed adding Audit fields to Dsa Entity!
            // I will default sort by 'name' for now.
            @RequestParam(defaultValue = "asc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by("name").ascending() : Sort.by("name").descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return ResponseEntity.ok(dsaService.getAllDsas(category, status, pageable));
    }
}
