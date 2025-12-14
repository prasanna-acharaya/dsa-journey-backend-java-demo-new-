package com.bom.dsa.service;

import com.bom.dsa.dto.request.CreateLeadRequest;
import com.bom.dsa.dto.request.UpdateLeadRequest;
import com.bom.dsa.dto.response.LeadResponse;
import com.bom.dsa.dto.response.LeadSummaryResponse;
import com.bom.dsa.entity.*;
import com.bom.dsa.enums.LeadStatus;
import com.bom.dsa.enums.ProductType;
import com.bom.dsa.exception.CustomExceptions;
import com.bom.dsa.repository.LeadRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service for Lead management operations.
 * Handles creation, retrieval, update, and deletion of loan leads.
 */
@Service
@Slf4j
public class LeadService {

    private final LeadRepository leadRepository;

    public LeadService(LeadRepository leadRepository) {
        this.leadRepository = leadRepository;
    }

    /**
     * Create a new lead with all details.
     * 
     * @param request   the create lead request
     * @param createdBy the username of the creator
     * @return Mono containing the created lead response
     */
    @Transactional
    public Mono<LeadResponse> createLead(CreateLeadRequest request, String createdBy) {
        log.info("Creating new lead for user: {}, productType: {}", createdBy, request.getProductType());

        return Mono.fromCallable(() -> {
            try {
                // Create lead entity
                Lead lead = Lead.builder()
                        .productType(request.getProductType())
                        .status(LeadStatus.APPLIED)
                        .createdBy(createdBy) // Manually set to context user
                        .build();

                // Map and set basic details
                if (request.getBasicDetails() != null) {
                    log.debug("Mapping basic details for lead");
                    BasicDetails basicDetails = mapBasicDetails(request.getBasicDetails());
                    lead.setBasicDetails(basicDetails);
                } else {
                    log.warn("Basic details not provided in create request");
                    throw new CustomExceptions.BusinessException("Basic details are required to create a lead");
                }

                // Map and set occupation details
                if (request.getOccupationDetails() != null) {
                    log.debug("Mapping occupation details for lead");
                    OccupationDetails occupationDetails = mapOccupationDetails(request.getOccupationDetails());
                    lead.setOccupationDetails(occupationDetails);
                }

                // Map and set financial details
                if (request.getFinancialDetails() != null) {
                    log.debug("Mapping financial details for lead");
                    FinancialDetails financialDetails = mapFinancialDetails(request.getFinancialDetails());
                    lead.setFinancialDetails(financialDetails);
                }

                // Map loan details based on product type
                if (request.getLoanDetails() != null) {
                    log.debug("Mapping loan details for productType: {}", request.getProductType());
                    mapLoanDetails(lead, request.getLoanDetails(), request.getProductType());
                } else {
                    log.warn("Loan details not provided in create request");
                    throw new CustomExceptions.BusinessException("Loan details are required to create a lead");
                }

                // Save lead
                Lead savedLead = leadRepository.save(lead);
                log.info("Successfully created lead with reference number: {}, id: {}",
                        savedLead.getApplicationReferenceNumber(), savedLead.getId());

                return toLeadResponse(savedLead);

            } catch (CustomExceptions.BusinessException e) {
                log.error("Business validation failed while creating lead: {}", e.getMessage());
                throw e;
            } catch (Exception e) {
                log.error("Unexpected error while creating lead for user: {}", createdBy, e);
                throw new CustomExceptions.BusinessException("Failed to create lead: " + e.getMessage());
            }
        }).subscribeOn(Schedulers.boundedElastic());
    }

    /**
     * Get lead by ID with all details.
     * 
     * @param leadId the lead ID
     * @return Mono containing the lead response
     */
    public Mono<LeadResponse> getLeadById(UUID leadId) {
        log.info("Fetching lead by id: {}", leadId);

        return Mono.fromCallable(() -> {
            try {
                Lead lead = leadRepository.findByIdWithDetails(leadId)
                        .orElseThrow(() -> {
                            log.warn("Lead not found with id: {}", leadId);
                            return new CustomExceptions.ResourceNotFoundException("Lead", "id", leadId);
                        });

                log.debug("Found lead with reference number: {}", lead.getApplicationReferenceNumber());
                return toLeadResponse(lead);

            } catch (CustomExceptions.ResourceNotFoundException e) {
                throw e;
            } catch (Exception e) {
                log.error("Error fetching lead by id: {}", leadId, e);
                throw new CustomExceptions.BusinessException("Failed to fetch lead: " + e.getMessage());
            }
        }).subscribeOn(Schedulers.boundedElastic());
    }

    /**
     * Get leads with pagination and filters.
     * 
     * @param createdBy   the creator username
     * @param status      optional status filter
     * @param productType optional product type filter
     * @param searchTerm  optional search term
     * @param pageable    pagination info
     * @return Mono containing page of lead summaries
     */
    public Mono<Page<LeadSummaryResponse>> getLeads(String createdBy, LeadStatus status,
            ProductType productType, String searchTerm,
            Pageable pageable) {
        log.info("Fetching leads for user: {}, status: {}, productType: {}, searchTerm: {}, page: {}",
                createdBy, status, productType, searchTerm, pageable.getPageNumber());

        return Mono.fromCallable(() -> {
            try {
                Page<Lead> leadPage = leadRepository.searchLeads(createdBy, status, productType, searchTerm, pageable);

                log.debug("Found {} leads, total: {}", leadPage.getNumberOfElements(), leadPage.getTotalElements());

                List<LeadSummaryResponse> summaries = leadPage.getContent().stream()
                        .map(this::toLeadSummaryResponse)
                        .collect(Collectors.toList());

                Page<LeadSummaryResponse> result = new PageImpl<>(summaries, pageable, leadPage.getTotalElements());
                return result;

            } catch (Exception e) {
                log.error("Error fetching leads for user: {}", createdBy, e);
                throw new CustomExceptions.BusinessException("Failed to fetch leads: " + e.getMessage());
            }
        }).subscribeOn(Schedulers.boundedElastic());
    }

    /**
     * Get recent leads for dashboard.
     * 
     * @param createdBy the creator username
     * @param limit     maximum number of leads to return
     * @return Mono containing list of lead summaries
     */
    public Mono<List<LeadSummaryResponse>> getRecentLeads(String createdBy, int limit) {
        log.info("Fetching {} recent leads for user: {}", limit, createdBy);

        return Mono.fromCallable(() -> {
            try {
                List<Lead> leads = leadRepository.findRecentLeads(createdBy, PageRequest.of(0, limit));

                log.debug("Found {} recent leads", leads.size());

                return leads.stream()
                        .map(this::toLeadSummaryResponse)
                        .collect(Collectors.toList());

            } catch (Exception e) {
                log.error("Error fetching recent leads for user: {}", createdBy, e);
                throw new CustomExceptions.BusinessException("Failed to fetch recent leads: " + e.getMessage());
            }
        }).subscribeOn(Schedulers.boundedElastic());
    }

    /**
     * Update lead.
     * 
     * @param request   the update lead request
     * @param updatedBy the username of the updater
     * @return Mono containing the updated lead response
     */
    @Transactional
    public Mono<LeadResponse> updateLead(UpdateLeadRequest request, String updatedBy) {
        log.info("Updating lead: {} by user: {}", request.getLeadId(), updatedBy);

        return Mono.fromCallable(() -> {
            try {
                Lead lead = leadRepository.findByIdWithDetails(request.getLeadId())
                        .orElseThrow(() -> {
                            log.warn("Lead not found for update, id: {}", request.getLeadId());
                            return new CustomExceptions.ResourceNotFoundException("Lead", "id", request.getLeadId());
                        });

                // Check if lead can be updated
                if (lead.getStatus() != LeadStatus.DRAFT) {
                    log.warn("Attempted to update lead in non-DRAFT status, leadId: {}, status: {}",
                            lead.getId(), lead.getStatus());
                    throw new CustomExceptions.InvalidOperationException(
                            "Only leads in DRAFT status can be updated. Current status: " + lead.getStatus());
                }

                // Update basic details if provided
                if (request.getBasicDetails() != null) {
                    log.debug("Updating basic details for lead: {}", lead.getId());
                    BasicDetails basicDetails = mapBasicDetails(request.getBasicDetails());
                    lead.setBasicDetails(basicDetails);
                }

                // Update occupation details if provided
                if (request.getOccupationDetails() != null) {
                    log.debug("Updating occupation details for lead: {}", lead.getId());
                    OccupationDetails occupationDetails = mapOccupationDetails(request.getOccupationDetails());
                    lead.setOccupationDetails(occupationDetails);
                }

                // Update financial details if provided
                if (request.getFinancialDetails() != null) {
                    log.debug("Updating financial details for lead: {}", lead.getId());
                    FinancialDetails financialDetails = mapFinancialDetails(request.getFinancialDetails());
                    lead.setFinancialDetails(financialDetails);
                }

                Lead updatedLead = leadRepository.save(lead);
                log.info("Successfully updated lead: {}", updatedLead.getApplicationReferenceNumber());

                return toLeadResponse(updatedLead);

            } catch (CustomExceptions.ResourceNotFoundException | CustomExceptions.InvalidOperationException e) {
                throw e;
            } catch (Exception e) {
                log.error("Error updating lead: {}", request.getLeadId(), e);
                throw new CustomExceptions.BusinessException("Failed to update lead: " + e.getMessage());
            }
        }).subscribeOn(Schedulers.boundedElastic());
    }

    /**
     * Soft delete lead.
     * 
     * @param leadId    the lead ID
     * @param deletedBy the username of the deleter
     * @return Mono<Void>
     */
    @Transactional
    public Mono<Void> deleteLead(UUID leadId, String deletedBy) {
        log.info("Deleting lead: {} by user: {}", leadId, deletedBy);

        return Mono.fromCallable(() -> {
            try {
                Lead lead = leadRepository.findById(leadId)
                        .orElseThrow(() -> {
                            log.warn("Lead not found for deletion, id: {}", leadId);
                            return new CustomExceptions.ResourceNotFoundException("Lead", "id", leadId);
                        });

                // Check if lead can be deleted
                if (lead.getStatus() != LeadStatus.DRAFT) {
                    log.warn("Attempted to delete lead in non-DRAFT status, leadId: {}, status: {}",
                            lead.getId(), lead.getStatus());
                    throw new CustomExceptions.InvalidOperationException(
                            "Only leads in DRAFT status can be deleted. Current status: " + lead.getStatus());
                }

                lead.softDelete(deletedBy);
                leadRepository.save(lead);

                log.info("Successfully soft deleted lead: {}", lead.getApplicationReferenceNumber());
                return null;

            } catch (CustomExceptions.ResourceNotFoundException | CustomExceptions.InvalidOperationException e) {
                throw e;
            } catch (Exception e) {
                log.error("Error deleting lead: {}", leadId, e);
                throw new CustomExceptions.BusinessException("Failed to delete lead: " + e.getMessage());
            }
        }).subscribeOn(Schedulers.boundedElastic()).then();
    }

    // ========== Private Helper Methods ==========

    /**
     * Map BasicDetailsDto to BasicDetails entity.
     */
    private BasicDetails mapBasicDetails(CreateLeadRequest.BasicDetailsDto dto) {
        BasicDetails bd = new BasicDetails();
        bd.setSalutation(dto.getSalutation());
        bd.setFirstName(dto.getFirstName());
        bd.setMiddleName(dto.getMiddleName());
        bd.setLastName(dto.getLastName());
        bd.setDateOfBirth(dto.getDateOfBirth());
        bd.setGender(dto.getGender());
        bd.setMaritalStatus(dto.getMaritalStatus());
        bd.setQualification(dto.getQualification());
        bd.setMobileNumber(dto.getMobileNumber());
        bd.setEmailAddress(dto.getEmailAddress());
        bd.setSameAsCurrentAddress(dto.getSameAsCurrentAddress());

        if (dto.getCurrentAddress() != null) {
            bd.setCurrentAddressLine1(dto.getCurrentAddress().getAddressLine1());
            bd.setCurrentAddressLine2(dto.getCurrentAddress().getAddressLine2());
            bd.setCurrentAddressLine3(dto.getCurrentAddress().getAddressLine3());
            bd.setCurrentCountry(dto.getCurrentAddress().getCountry());
            bd.setCurrentState(dto.getCurrentAddress().getState());
            bd.setCurrentCity(dto.getCurrentAddress().getCity());
            bd.setCurrentPincode(dto.getCurrentAddress().getPincode());
        }

        if (dto.getPermanentAddress() != null) {
            bd.setPermanentAddressLine1(dto.getPermanentAddress().getAddressLine1());
            bd.setPermanentAddressLine2(dto.getPermanentAddress().getAddressLine2());
            bd.setPermanentAddressLine3(dto.getPermanentAddress().getAddressLine3());
            bd.setPermanentCountry(dto.getPermanentAddress().getCountry());
            bd.setPermanentState(dto.getPermanentAddress().getState());
            bd.setPermanentCity(dto.getPermanentAddress().getCity());
            bd.setPermanentPincode(dto.getPermanentAddress().getPincode());
        }

        return bd;
    }

    /**
     * Map OccupationDetailsDto to OccupationDetails entity.
     */
    private OccupationDetails mapOccupationDetails(CreateLeadRequest.OccupationDetailsDto dto) {
        return OccupationDetails.builder()
                .occupationType(dto.getOccupationType())
                .companyType(dto.getCompanyType())
                .employerName(dto.getEmployerName())
                .designation(dto.getDesignation())
                .totalExperience(dto.getTotalExperience())
                .build();
    }

    /**
     * Map FinancialDetailsDto to FinancialDetails entity.
     */
    private FinancialDetails mapFinancialDetails(CreateLeadRequest.FinancialDetailsDto dto) {
        return FinancialDetails.builder()
                .monthlyGrossIncome(dto.getMonthlyGrossIncome())
                .monthlyDeductions(dto.getMonthlyDeductions())
                .monthlyEmi(dto.getMonthlyEmi())
                .build();
    }

    /**
     * Map LoanDetailsDto to appropriate loan entity based on product type.
     */
    private void mapLoanDetails(Lead lead, CreateLeadRequest.LoanDetailsDto dto, ProductType productType) {
        log.debug("Mapping loan details for product type: {}", productType);

        switch (productType) {
            case VEHICLE_LOAN:
                if (dto.getVehicleLoanDetails() != null) {
                    VehicleLoanDetails vld = VehicleLoanDetails.builder()
                            .amountRequested(dto.getAmountRequested())
                            .repaymentPeriod(dto.getRepaymentPeriod())
                            .vehicleType(dto.getVehicleLoanDetails().getVehicleType())
                            .make(dto.getVehicleLoanDetails().getMake())
                            .model(dto.getVehicleLoanDetails().getModel())
                            .exShowroomPrice(dto.getVehicleLoanDetails().getExShowroomPrice())
                            .insuranceCost(dto.getVehicleLoanDetails().getInsuranceCost())
                            .roadTax(dto.getVehicleLoanDetails().getRoadTax())
                            .accessoriesOtherCost(dto.getVehicleLoanDetails().getAccessoriesOtherCost())
                            .build();

                    if (dto.getVehicleLoanDetails().getDealerDetails() != null) {
                        var dealer = dto.getVehicleLoanDetails().getDealerDetails();
                        vld.setDealerName(dealer.getDealerName());
                        vld.setDealerAddressLine1(dealer.getAddressLine1());
                        vld.setDealerAddressLine2(dealer.getAddressLine2());
                        vld.setDealerAddressLine3(dealer.getAddressLine3());
                        vld.setDealerCountry(dealer.getCountry());
                        vld.setDealerState(dealer.getState());
                        vld.setDealerCity(dealer.getCity());
                        vld.setDealerPincode(dealer.getPincode());
                    }
                    lead.setVehicleLoanDetails(vld);
                    log.debug("Vehicle loan details mapped successfully");
                } else {
                    log.warn("Vehicle loan details not provided for VEHICLE_LOAN product type");
                }
                break;

            case EDUCATION_LOAN:
                if (dto.getEducationLoanDetails() != null) {
                    EducationLoanDetails eld = EducationLoanDetails.builder()
                            .amountRequested(dto.getAmountRequested())
                            .repaymentPeriod(dto.getRepaymentPeriod())
                            .courseName(dto.getEducationLoanDetails().getCourseName())
                            .institutionName(dto.getEducationLoanDetails().getInstitutionName())
                            .institutionCountry(dto.getEducationLoanDetails().getInstitutionCountry())
                            .institutionState(dto.getEducationLoanDetails().getInstitutionState())
                            .institutionCity(dto.getEducationLoanDetails().getInstitutionCity())
                            .courseDurationYears(dto.getEducationLoanDetails().getCourseDurationYears())
                            .build();
                    lead.setEducationLoanDetails(eld);
                    log.debug("Education loan details mapped successfully");
                } else {
                    log.warn("Education loan details not provided for EDUCATION_LOAN product type");
                }
                break;

            case HOME_LOAN:
                if (dto.getHomeLoanDetails() != null) {
                    HomeLoanDetails hld = HomeLoanDetails.builder()
                            .amountRequested(dto.getAmountRequested())
                            .repaymentPeriod(dto.getRepaymentPeriod())
                            .propertyType(dto.getHomeLoanDetails().getPropertyType())
                            .propertyValue(dto.getHomeLoanDetails().getPropertyValue())
                            .build();

                    if (dto.getHomeLoanDetails().getPropertyAddress() != null) {
                        var addr = dto.getHomeLoanDetails().getPropertyAddress();
                        hld.setPropertyAddressLine1(addr.getAddressLine1());
                        hld.setPropertyAddressLine2(addr.getAddressLine2());
                        hld.setPropertyAddressLine3(addr.getAddressLine3());
                        hld.setPropertyCountry(addr.getCountry());
                        hld.setPropertyState(addr.getState());
                        hld.setPropertyCity(addr.getCity());
                        hld.setPropertyPincode(addr.getPincode());
                    }
                    lead.setHomeLoanDetails(hld);
                    log.debug("Home loan details mapped successfully");
                } else {
                    log.warn("Home loan details not provided for HOME_LOAN product type");
                }
                break;

            case LOAN_AGAINST_PROPERTY:
                if (dto.getLoanAgainstPropertyDetails() != null) {
                    LoanAgainstPropertyDetails lapd = LoanAgainstPropertyDetails.builder()
                            .amountRequested(dto.getAmountRequested())
                            .repaymentPeriod(dto.getRepaymentPeriod())
                            .propertyType(dto.getLoanAgainstPropertyDetails().getPropertyType())
                            .propertyMarketValue(dto.getLoanAgainstPropertyDetails().getPropertyMarketValue())
                            .build();

                    if (dto.getLoanAgainstPropertyDetails().getPropertyAddress() != null) {
                        var addr = dto.getLoanAgainstPropertyDetails().getPropertyAddress();
                        lapd.setPropertyAddressLine1(addr.getAddressLine1());
                        lapd.setPropertyAddressLine2(addr.getAddressLine2());
                        lapd.setPropertyAddressLine3(addr.getAddressLine3());
                        lapd.setPropertyCountry(addr.getCountry());
                        lapd.setPropertyState(addr.getState());
                        lapd.setPropertyCity(addr.getCity());
                        lapd.setPropertyPincode(addr.getPincode());
                    }
                    lead.setLoanAgainstPropertyDetails(lapd);
                    log.debug("Loan against property details mapped successfully");
                } else {
                    log.warn("Loan against property details not provided for LOAN_AGAINST_PROPERTY product type");
                }
                break;

            default:
                log.error("Unknown product type: {}", productType);
                throw new CustomExceptions.BusinessException("Unknown product type: " + productType);
        }
    }

    /**
     * Convert Lead entity to LeadResponse DTO.
     */
    private LeadResponse toLeadResponse(Lead lead) {
        log.debug("Converting lead to response: {}", lead.getId());

        LeadResponse.LeadResponseBuilder builder = LeadResponse.builder()
                .leadId(lead.getId())
                .applicationReferenceNumber(lead.getApplicationReferenceNumber())
                .status(lead.getStatus())
                .productType(lead.getProductType());

        // Assigned branch
        if (lead.getAssignedBranchName() != null) {
            builder.assignedBranch(LeadResponse.AssignedBranchDto.builder()
                    .name(lead.getAssignedBranchName())
                    .address(lead.getAssignedBranchAddress())
                    .build());
        }

        // Basic details
        if (lead.getBasicDetails() != null) {
            BasicDetails bd = lead.getBasicDetails();
            builder.basicDetails(LeadResponse.BasicDetailsDto.builder()
                    .salutation(bd.getSalutation())
                    .firstName(bd.getFirstName())
                    .middleName(bd.getMiddleName())
                    .lastName(bd.getLastName())
                    .fullName(bd.getFullName())
                    .dateOfBirth(bd.getDateOfBirth())
                    .gender(bd.getGender())
                    .maritalStatus(bd.getMaritalStatus())
                    .qualification(bd.getQualification())
                    .mobileNumber(bd.getMobileNumber())
                    .emailAddress(bd.getEmailAddress())
                    .sameAsCurrentAddress(bd.getSameAsCurrentAddress())
                    .currentAddress(LeadResponse.AddressDto.builder()
                            .addressLine1(bd.getCurrentAddressLine1())
                            .addressLine2(bd.getCurrentAddressLine2())
                            .addressLine3(bd.getCurrentAddressLine3())
                            .country(bd.getCurrentCountry())
                            .state(bd.getCurrentState())
                            .city(bd.getCurrentCity())
                            .pincode(bd.getCurrentPincode())
                            .build())
                    .permanentAddress(LeadResponse.AddressDto.builder()
                            .addressLine1(bd.getPermanentAddressLine1())
                            .addressLine2(bd.getPermanentAddressLine2())
                            .addressLine3(bd.getPermanentAddressLine3())
                            .country(bd.getPermanentCountry())
                            .state(bd.getPermanentState())
                            .city(bd.getPermanentCity())
                            .pincode(bd.getPermanentPincode())
                            .build())
                    .build());
        }

        // Occupation details
        if (lead.getOccupationDetails() != null) {
            OccupationDetails od = lead.getOccupationDetails();
            builder.occupationDetails(LeadResponse.OccupationDetailsDto.builder()
                    .occupationType(od.getOccupationType())
                    .companyType(od.getCompanyType())
                    .employerName(od.getEmployerName())
                    .designation(od.getDesignation())
                    .totalExperience(od.getTotalExperience())
                    .build());
        }

        // Financial details
        if (lead.getFinancialDetails() != null) {
            FinancialDetails fd = lead.getFinancialDetails();
            builder.financialDetails(LeadResponse.FinancialDetailsDto.builder()
                    .monthlyGrossIncome(fd.getMonthlyGrossIncome())
                    .monthlyDeductions(fd.getMonthlyDeductions())
                    .monthlyEmi(fd.getMonthlyEmi())
                    .monthlyNetIncome(fd.getMonthlyNetIncome())
                    .build());
        }

        // Loan details
        builder.loanDetails(mapLoanDetails(lead));

        // Audit info
        builder.auditInfo(LeadResponse.AuditInfoDto.builder()
                .createdBy(lead.getCreatedBy())
                .createdAt(lead.getCreatedAt())
                .updatedBy(lead.getUpdatedBy())
                .updatedAt(lead.getUpdatedAt())
                .build());

        return builder.build();
    }

    /**
     * Map loan details from entity to response DTO.
     */
    private LeadResponse.LoanDetailsDto mapLoanDetails(Lead lead) {
        LeadResponse.LoanDetailsDto.LoanDetailsDtoBuilder loanBuilder = LeadResponse.LoanDetailsDto.builder();
        boolean hasLoanDetails = false;

        if (lead.getVehicleLoanDetails() != null) {
            VehicleLoanDetails vld = lead.getVehicleLoanDetails();
            loanBuilder.amountRequested(vld.getAmountRequested())
                    .repaymentPeriod(vld.getRepaymentPeriod())
                    .vehicleLoanDetails(LeadResponse.VehicleLoanDetailsDto.builder()
                            .vehicleType(vld.getVehicleType())
                            .make(vld.getMake())
                            .model(vld.getModel())
                            .exShowroomPrice(vld.getExShowroomPrice())
                            .insuranceCost(vld.getInsuranceCost())
                            .roadTax(vld.getRoadTax())
                            .accessoriesOtherCost(vld.getAccessoriesOtherCost())
                            .totalCostOfVehicle(vld.getTotalCostOfVehicle())
                            .dealerDetails(LeadResponse.DealerDetailsDto.builder()
                                    .dealerName(vld.getDealerName())
                                    .addressLine1(vld.getDealerAddressLine1())
                                    .addressLine2(vld.getDealerAddressLine2())
                                    .addressLine3(vld.getDealerAddressLine3())
                                    .country(vld.getDealerCountry())
                                    .state(vld.getDealerState())
                                    .city(vld.getDealerCity())
                                    .pincode(vld.getDealerPincode())
                                    .build())
                            .build());
            hasLoanDetails = true;
        }

        if (lead.getEducationLoanDetails() != null) {
            EducationLoanDetails eld = lead.getEducationLoanDetails();
            loanBuilder.amountRequested(eld.getAmountRequested())
                    .repaymentPeriod(eld.getRepaymentPeriod())
                    .educationLoanDetails(LeadResponse.EducationLoanDetailsDto.builder()
                            .courseName(eld.getCourseName())
                            .institutionName(eld.getInstitutionName())
                            .institutionCountry(eld.getInstitutionCountry())
                            .institutionState(eld.getInstitutionState())
                            .institutionCity(eld.getInstitutionCity())
                            .courseDurationYears(eld.getCourseDurationYears())
                            .build());
            hasLoanDetails = true;
        }

        if (lead.getHomeLoanDetails() != null) {
            HomeLoanDetails hld = lead.getHomeLoanDetails();
            loanBuilder.amountRequested(hld.getAmountRequested())
                    .repaymentPeriod(hld.getRepaymentPeriod())
                    .homeLoanDetails(LeadResponse.HomeLoanDetailsDto.builder()
                            .propertyType(hld.getPropertyType())
                            .propertyValue(hld.getPropertyValue())
                            .propertyAddress(LeadResponse.AddressDto.builder()
                                    .addressLine1(hld.getPropertyAddressLine1())
                                    .addressLine2(hld.getPropertyAddressLine2())
                                    .addressLine3(hld.getPropertyAddressLine3())
                                    .country(hld.getPropertyCountry())
                                    .state(hld.getPropertyState())
                                    .city(hld.getPropertyCity())
                                    .pincode(hld.getPropertyPincode())
                                    .build())
                            .build());
            hasLoanDetails = true;
        }

        if (lead.getLoanAgainstPropertyDetails() != null) {
            LoanAgainstPropertyDetails lapd = lead.getLoanAgainstPropertyDetails();
            loanBuilder.amountRequested(lapd.getAmountRequested())
                    .repaymentPeriod(lapd.getRepaymentPeriod())
                    .loanAgainstPropertyDetails(LeadResponse.LoanAgainstPropertyDetailsDto.builder()
                            .propertyType(lapd.getPropertyType())
                            .propertyMarketValue(lapd.getPropertyMarketValue())
                            .propertyAddress(LeadResponse.AddressDto.builder()
                                    .addressLine1(lapd.getPropertyAddressLine1())
                                    .addressLine2(lapd.getPropertyAddressLine2())
                                    .addressLine3(lapd.getPropertyAddressLine3())
                                    .country(lapd.getPropertyCountry())
                                    .state(lapd.getPropertyState())
                                    .city(lapd.getPropertyCity())
                                    .pincode(lapd.getPropertyPincode())
                                    .build())
                            .build());
            hasLoanDetails = true;
        }

        if (hasLoanDetails) {
            return loanBuilder.build();
        }
        return null;
    }

    /**
     * Convert Lead entity to LeadSummaryResponse DTO.
     */
    private LeadSummaryResponse toLeadSummaryResponse(Lead lead) {
        BigDecimal amountRequested = getAmountRequested(lead);

        String customerName = lead.getBasicDetails() != null ? lead.getBasicDetails().getFullName() : "";
        String mobileNumber = lead.getBasicDetails() != null ? lead.getBasicDetails().getMobileNumber() : "";
        String emailAddress = lead.getBasicDetails() != null ? lead.getBasicDetails().getEmailAddress() : "";

        return LeadSummaryResponse.builder()
                .leadId(lead.getId())
                .applicationReferenceNumber(lead.getApplicationReferenceNumber())
                .productType(lead.getProductType())
                .status(lead.getStatus())
                .customerName(customerName)
                .mobileNumber(mobileNumber)
                .emailAddress(emailAddress)
                .amountRequested(amountRequested)
                .createdAt(lead.getCreatedAt())
                .updatedAt(lead.getUpdatedAt())
                .build();
    }

    /**
     * Get amount requested from appropriate loan details based on product type.
     */
    private BigDecimal getAmountRequested(Lead lead) {
        if (lead.getVehicleLoanDetails() != null) {
            return lead.getVehicleLoanDetails().getAmountRequested();
        } else if (lead.getEducationLoanDetails() != null) {
            return lead.getEducationLoanDetails().getAmountRequested();
        } else if (lead.getHomeLoanDetails() != null) {
            return lead.getHomeLoanDetails().getAmountRequested();
        } else if (lead.getLoanAgainstPropertyDetails() != null) {
            return lead.getLoanAgainstPropertyDetails().getAmountRequested();
        }
        return null;
    }
}
