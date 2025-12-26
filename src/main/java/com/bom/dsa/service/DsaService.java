package com.bom.dsa.service;

import com.bom.dsa.dto.request.*;
import com.bom.dsa.dto.response.*;
import com.bom.dsa.client.ApprovalClient;
import com.bom.dsa.entity.BankAccountDetails;
import com.bom.dsa.entity.Dsa;
import com.bom.dsa.entity.DsaDocument;
import com.bom.dsa.enums.DsaStatus;
import com.bom.dsa.exception.CustomExceptions;
import com.bom.dsa.repository.DsaRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import reactor.core.publisher.Mono;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class DsaService {

    private final DsaRepository dsaRepository;
    private final ApprovalClient approvalClient;

    @Transactional
    public DsaResponseDto createDsa(DsaRequestDto request, String createdBy) {
        log.info("Creating DSA: {}", request.getName());

        // Generate unique code (Simple logic for demo)
        String uniqueCode = "DSA" + System.currentTimeMillis();

        Dsa dsa = Dsa.builder()
                .name(request.getName())
                .uniqueCode(uniqueCode)
                .mobileNumber(request.getMobileNumber())
                .email(request.getEmail())
                .status(DsaStatus.PENDING) // Default to Pending
                .category(request.getCategory())
                .city(request.getCity())
                .addressLine1(request.getAddressLine1())
                .constitution(request.getConstitution())
                .registrationDate(request.getRegistrationDate())
                .gstin(request.getGstin())
                .pan(request.getPan())
                .empanelmentDate(request.getEmpanelmentDate())
                .agreementDate(request.getAgreementDate())
                .agreementExpiryDate(request.getAgreementExpiryDate())
                .agreementPeriod(request.getAgreementPeriod())
                .zoneMapping(request.getZoneMapping())
                .riskScore(request.getRiskScore() != null ? request.getRiskScore() : 0.0)
                .products(request.getProducts() != null ? request.getProducts() : new ArrayList<>())
                .createdBy(createdBy)
                .build();

        // Bank Details
        if (request.getBankDetails() != null) {
            BankAccountDetails bank = BankAccountDetails.builder()
                    .accountName(request.getBankDetails().getAccountName())
                    .accountNumber(request.getBankDetails().getAccountNumber())
                    .ifscCode(request.getBankDetails().getIfscCode())
                    .branchName(request.getBankDetails().getBranchName())
                    .build();
            dsa.setBankAccountDetails(bank);
        }

        // Documents
        if (request.getDocuments() != null) {
            request.getDocuments().forEach(docDto -> {
                DsaDocument doc = DsaDocument.builder()
                        .documentName(docDto.getDocumentName())
                        .fileName(docDto.getFileName())
                        .filePath(docDto.getFilePath())
                        .build();
                dsa.addDocument(doc);
            });
        }

        Dsa savedDsa = dsaRepository.save(dsa);
        stageDsaProducts(savedDsa, request);
        return mapToResponse(savedDsa);
    }

    @Transactional
    public DsaResponseDto updateDsa(UUID id, DsaRequestDto request) {
        Dsa dsa = dsaRepository.findById(id)
                .orElseThrow(() -> new CustomExceptions.ResourceNotFoundException("DSA", "id", id));

        // Update fields
        dsa.setName(request.getName());
        dsa.setMobileNumber(request.getMobileNumber());
        dsa.setEmail(request.getEmail());
        dsa.setCategory(request.getCategory());
        dsa.setCity(request.getCity());
        dsa.setAddressLine1(request.getAddressLine1());
        dsa.setConstitution(request.getConstitution());
        dsa.setGstin(request.getGstin());
        dsa.setPan(request.getPan());

        // Update products
        if (request.getProducts() != null) {
            dsa.setProducts(request.getProducts());
        }

        // Update Bank (Replace logic for simplicity)
        if (request.getBankDetails() != null) {
            if (dsa.getBankAccountDetails() == null) {
                BankAccountDetails bank = BankAccountDetails.builder().build();
                dsa.setBankAccountDetails(bank);
            }
            dsa.getBankAccountDetails().setAccountName(request.getBankDetails().getAccountName());
            dsa.getBankAccountDetails().setAccountNumber(request.getBankDetails().getAccountNumber());
            dsa.getBankAccountDetails().setIfscCode(request.getBankDetails().getIfscCode());
            dsa.getBankAccountDetails().setBranchName(request.getBankDetails().getBranchName());
        }

        Dsa savedDsa = dsaRepository.save(dsa);
        stageDsaProducts(savedDsa, request);
        return mapToResponse(savedDsa);
    }

    private void stageDsaProducts(Dsa savedDsa, DsaRequestDto request) {
        if (request.getProducts() != null && !request.getProducts().isEmpty()) {
            StageApprovalRequest stageRequest = StageApprovalRequest.builder()
                    .dsaId(savedDsa.getId().toString())
                    .products(request.getProducts().stream()
                            .map(Enum::name)
                            .collect(Collectors.toList()))
                    .build();

            log.info("Triggering product staging for DSA: {} with {} products", savedDsa.getId(),
                    request.getProducts().size());
            approvalClient.stageApprovals(stageRequest).subscribe();
        }
    }

    public Mono<AuthorizeApprovalResponse> authorizeDsaProduct(AuthorizeApprovalRequest request) {
        return approvalClient.authorizeApproval(request);
    }

    public Mono<List<VerifyApprovalResponse>> verifyDsaApprovals(UUID dsaId) {
        return approvalClient.verifyApprovals(dsaId.toString());
    }

    public Mono<List<PendingApprovalResponse>> getPendingApprovalsForUser(String userId) {
        return approvalClient.getPendingApprovals(userId)
                .map(rawList -> rawList.stream()
                        .map(raw -> {
                            Dsa dsa = dsaRepository.findById(UUID.fromString(raw.getDsaId())).orElse(null);
                            return PendingApprovalResponse.builder()
                                    .id(raw.getId())
                                    .dsaId(raw.getDsaId())
                                    .dsaName(dsa != null ? dsa.getName() : "Unknown")
                                    .dsaUniqueCode(dsa != null ? dsa.getUniqueCode() : "N/A")
                                    .userId(raw.getUserId())
                                    .productType(raw.getProductType())
                                    .approvedAt(raw.getApprovedAt() != null
                                            ? java.time.LocalDateTime.parse(raw.getApprovedAt())
                                            : null)
                                    .build();
                        })
                        .collect(Collectors.toList()));
    }

    @Transactional
    public DsaResponseDto updateDsaStatus(UUID id, DsaStatus status) {
        Dsa dsa = dsaRepository.findById(id)
                .orElseThrow(() -> new CustomExceptions.ResourceNotFoundException("DSA", "id", id));
        dsa.setStatus(status);
        return mapToResponse(dsaRepository.save(dsa));
    }

    @Transactional(readOnly = true)
    public Page<DsaResponseDto> getAllDsas(String category, DsaStatus status, Pageable pageable) {
        Specification<Dsa> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (category != null && !category.isEmpty()) {
                predicates.add(cb.equal(root.get("category"), category));
            }
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<Dsa> page = dsaRepository.findAll(spec, pageable);
        List<DsaResponseDto> content = page.getContent().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        return new PageImpl<>(content, pageable, page.getTotalElements());
    }

    private DsaResponseDto mapToResponse(Dsa dsa) {
        DsaResponseDto.BankDetailsDto bankDto = null;
        if (dsa.getBankAccountDetails() != null) {
            bankDto = DsaResponseDto.BankDetailsDto.builder()
                    .accountName(dsa.getBankAccountDetails().getAccountName())
                    .accountNumber(dsa.getBankAccountDetails().getAccountNumber())
                    .ifscCode(dsa.getBankAccountDetails().getIfscCode())
                    .branchName(dsa.getBankAccountDetails().getBranchName())
                    .build();
        }

        List<DsaResponseDto.DocumentDto> docDtos = dsa.getDocuments().stream()
                .map(d -> DsaResponseDto.DocumentDto.builder()
                        .documentName(d.getDocumentName())
                        .fileName(d.getFileName())
                        .filePath(d.getFilePath())
                        .build())
                .collect(Collectors.toList());

        return DsaResponseDto.builder()
                .id(dsa.getId())
                .name(dsa.getName())
                .uniqueCode(dsa.getUniqueCode())
                .mobileNumber(dsa.getMobileNumber())
                .email(dsa.getEmail())
                .status(dsa.getStatus())
                .category(dsa.getCategory())
                .city(dsa.getCity())
                .addressLine1(dsa.getAddressLine1())
                .constitution(dsa.getConstitution())
                .registrationDate(dsa.getRegistrationDate())
                .gstin(dsa.getGstin())
                .pan(dsa.getPan())
                .empanelmentDate(dsa.getEmpanelmentDate())
                .agreementDate(dsa.getAgreementDate())
                .agreementExpiryDate(dsa.getAgreementExpiryDate())
                .agreementPeriod(dsa.getAgreementPeriod())
                .zoneMapping(dsa.getZoneMapping())
                .riskScore(dsa.getRiskScore())
                .products(dsa.getProducts())
                .bankDetails(bankDto)
                .documents(docDtos)
                .build();
    }
}
