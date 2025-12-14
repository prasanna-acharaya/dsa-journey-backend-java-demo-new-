package com.bom.dsa.entity;

import com.bom.dsa.enums.LeadStatus;
import com.bom.dsa.enums.ProductType;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Main Lead entity representing a loan application.
 * Uses @OneToOne relationships for all detail entities.
 * Supports multiple loan types through polymorphic relationships.
 */
@Entity
@Table(name = "leads")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Lead {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "application_reference_number", unique = true, nullable = false, length = 50)
    private String applicationReferenceNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private LeadStatus status = LeadStatus.APPLIED;

    @Enumerated(EnumType.STRING)
    @Column(name = "product_type", nullable = false, length = 50)
    private ProductType productType;

    // OneToOne relationship with BasicDetails (owning side is BasicDetails)
    @OneToOne(mappedBy = "lead", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private BasicDetails basicDetails;

    // OneToOne relationship with OccupationDetails (owning side is
    // OccupationDetails)
    @OneToOne(mappedBy = "lead", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private OccupationDetails occupationDetails;

    // OneToOne relationship with FinancialDetails (owning side is FinancialDetails)
    @OneToOne(mappedBy = "lead", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private FinancialDetails financialDetails;

    // Branch Assignment
    @Column(name = "assigned_branch_name", length = 255)
    private String assignedBranchName;

    @Column(name = "assigned_branch_address", columnDefinition = "TEXT")
    private String assignedBranchAddress;

    // Loan-specific details (One-to-One based on product type)
    @OneToOne(mappedBy = "lead", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private VehicleLoanDetails vehicleLoanDetails;

    @OneToOne(mappedBy = "lead", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private EducationLoanDetails educationLoanDetails;

    @OneToOne(mappedBy = "lead", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private HomeLoanDetails homeLoanDetails;

    @OneToOne(mappedBy = "lead", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private LoanAgainstPropertyDetails loanAgainstPropertyDetails;

    // Documents (One-to-Many)
    @OneToMany(mappedBy = "lead", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Document> documents = new ArrayList<>();

    // Soft Delete fields
    @Column(name = "is_deleted")
    @Builder.Default
    private Boolean isDeleted = false;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    @Column(name = "deleted_by", length = 100)
    private String deletedBy;

    // Audit fields
    @CreatedBy
    @Column(name = "created_by", nullable = false, updatable = false, length = 100)
    private String createdBy;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedBy
    @Column(name = "updated_by", length = 100)
    private String updatedBy;

    @LastModifiedDate
    @Column(name = "updated_at")
    private Instant updatedAt;

    /**
     * Generate application reference number before persisting.
     * Format: BOM + 7 random digits (e.g., BOM3617389)
     */
    @PrePersist
    public void prePersist() {
        if (applicationReferenceNumber == null) {
            applicationReferenceNumber = "BOM" + (1000000 + (int) (Math.random() * 9000000));
        }
    }

    /**
     * Soft delete the lead.
     * 
     * @param deletedByUser the user who deleted the lead
     */
    public void softDelete(String deletedByUser) {
        this.isDeleted = true;
        this.deletedAt = Instant.now();
        this.deletedBy = deletedByUser;
    }

    /**
     * Set basic details with bidirectional relationship.
     * 
     * @param details the basic details to set
     */
    public void setBasicDetails(BasicDetails details) {
        if (details != null) {
            details.setLead(this);
        }
        this.basicDetails = details;
    }

    /**
     * Set occupation details with bidirectional relationship.
     * 
     * @param details the occupation details to set
     */
    public void setOccupationDetails(OccupationDetails details) {
        if (details != null) {
            details.setLead(this);
        }
        this.occupationDetails = details;
    }

    /**
     * Set financial details with bidirectional relationship.
     * 
     * @param details the financial details to set
     */
    public void setFinancialDetails(FinancialDetails details) {
        if (details != null) {
            details.setLead(this);
        }
        this.financialDetails = details;
    }

    /**
     * Set vehicle loan details with bidirectional relationship.
     * 
     * @param details the vehicle loan details to set
     */
    public void setVehicleLoanDetails(VehicleLoanDetails details) {
        if (details != null) {
            details.setLead(this);
        }
        this.vehicleLoanDetails = details;
    }

    /**
     * Set education loan details with bidirectional relationship.
     * 
     * @param details the education loan details to set
     */
    public void setEducationLoanDetails(EducationLoanDetails details) {
        if (details != null) {
            details.setLead(this);
        }
        this.educationLoanDetails = details;
    }

    /**
     * Set home loan details with bidirectional relationship.
     * 
     * @param details the home loan details to set
     */
    public void setHomeLoanDetails(HomeLoanDetails details) {
        if (details != null) {
            details.setLead(this);
        }
        this.homeLoanDetails = details;
    }

    /**
     * Set loan against property details with bidirectional relationship.
     * 
     * @param details the loan against property details to set
     */
    public void setLoanAgainstPropertyDetails(LoanAgainstPropertyDetails details) {
        if (details != null) {
            details.setLead(this);
        }
        this.loanAgainstPropertyDetails = details;
    }

    /**
     * Add a document to the lead.
     * 
     * @param document the document to add
     */
    public void addDocument(Document document) {
        documents.add(document);
        document.setLead(this);
    }

    /**
     * Remove a document from the lead.
     * 
     * @param document the document to remove
     */
    public void removeDocument(Document document) {
        documents.remove(document);
        document.setLead(null);
    }
}
