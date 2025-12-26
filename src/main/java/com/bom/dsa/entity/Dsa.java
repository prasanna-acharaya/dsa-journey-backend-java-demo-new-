package com.bom.dsa.entity;

import com.bom.dsa.enums.DsaStatus;
import com.bom.dsa.enums.ProductType;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Entity for DSA Profile details.
 * Linked to User via unique code or ID.
 */
@Entity
@Table(name = "ids_dsa")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Dsa {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "name", length = 100)
    private String name;

    @Column(name = "unique_code", unique = true, nullable = false, length = 50)
    private String uniqueCode;

    @Column(name = "mobile_number", length = 15)
    private String mobileNumber;

    @Column(name = "email", length = 255)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private DsaStatus status;

    @Column(name = "category", length = 50)
    private String category;

    @Column(name = "city", length = 100)
    private String city;

    @Column(name = "empanelment_date")
    private LocalDate empanelmentDate;

    @Column(name = "agreement_date")
    private LocalDate agreementDate;

    @Column(name = "agreement_expiry_date")
    private LocalDate agreementExpiryDate;

    @Column(name = "gstin", length = 20)
    private String gstin;

    @Column(name = "pan", length = 15)
    private String pan;

    @Column(name = "constitution", length = 50)
    private String constitution;

    @Column(name = "registration_date")
    private LocalDate registrationDate;

    @Column(name = "zone_mapping", length = 100)
    private String zoneMapping;

    @Column(name = "agreement_period", length = 50)
    private String agreementPeriod;

    @Column(name = "risk_score", nullable = false)
    @Builder.Default
    private Double riskScore = 0.0;

    @Column(name = "address_line_1", length = 255)
    private String addressLine1;

    @ElementCollection(targetClass = ProductType.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "dsa_products", joinColumns = @JoinColumn(name = "dsa_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "product_type")
    @Builder.Default
    private List<ProductType> products = new ArrayList<>();

    @OneToOne(mappedBy = "dsa", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private BankAccountDetails bankAccountDetails;

    @OneToMany(mappedBy = "dsa", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<DsaDocument> documents = new ArrayList<>();

    // Helper method to add document
    public void addDocument(DsaDocument document) {
        documents.add(document);
        document.setDsa(this);
    }

    // Helper method to set bank account
    public void setBankAccountDetails(BankAccountDetails details) {
        this.bankAccountDetails = details;
        if (details != null) {
            details.setDsa(this);
        }
    }

    // Audit fields
    @Column(name = "created_by", length = 100)
    private String createdBy;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_by", length = 100)
    private String updatedBy;

    @LastModifiedDate
    @Column(name = "updated_at")
    private Instant updatedAt;
}
