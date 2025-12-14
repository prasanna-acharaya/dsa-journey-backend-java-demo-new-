package com.bom.dsa.entity;

import com.bom.dsa.enums.OccupationType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Entity for storing occupation/employment details of the loan applicant.
 * One-to-One relationship with Lead entity.
 */
@Entity
@Table(name = "occupation_details")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OccupationDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lead_id", nullable = false, unique = true)
    private Lead lead;

    @Enumerated(EnumType.STRING)
    @Column(name = "occupation_type", nullable = false, length = 20)
    private OccupationType occupationType;

    @Column(name = "company_type", length = 50)
    private String companyType;

    @Column(name = "employer_name", length = 255)
    private String employerName;

    @Column(name = "designation", length = 100)
    private String designation;

    @Column(name = "total_experience", precision = 4, scale = 2)
    private BigDecimal totalExperience;
}
