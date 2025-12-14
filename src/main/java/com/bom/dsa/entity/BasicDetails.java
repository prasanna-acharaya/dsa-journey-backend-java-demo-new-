package com.bom.dsa.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Entity for storing basic personal details of the loan applicant.
 * One-to-One relationship with Lead entity.
 */
@Entity
@Table(name = "basic_details")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BasicDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lead_id", nullable = false, unique = true)
    private Lead lead;

    @Column(name = "salutation", length = 10)
    private String salutation;

    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "middle_name", length = 100)
    private String middleName;

    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @Column(name = "date_of_birth", nullable = false)
    private LocalDate dateOfBirth;

    @Column(name = "gender", nullable = false, length = 10)
    private String gender;

    @Column(name = "marital_status", nullable = false, length = 20)
    private String maritalStatus;

    @Column(name = "qualification", length = 50)
    private String qualification;

    @Column(name = "mobile_number", nullable = false, length = 15)
    private String mobileNumber;

    @Column(name = "email_address", nullable = false, length = 255)
    private String emailAddress;

    // Current Address fields
    @Column(name = "current_address_line1", length = 255)
    private String currentAddressLine1;

    @Column(name = "current_address_line2", length = 255)
    private String currentAddressLine2;

    @Column(name = "current_address_line3", length = 255)
    private String currentAddressLine3;

    @Column(name = "current_country", length = 100)
    private String currentCountry;

    @Column(name = "current_state", length = 100)
    private String currentState;

    @Column(name = "current_city", length = 100)
    private String currentCity;

    @Column(name = "current_pincode", length = 10)
    private String currentPincode;

    // Permanent Address fields
    @Column(name = "permanent_address_line1", length = 255)
    private String permanentAddressLine1;

    @Column(name = "permanent_address_line2", length = 255)
    private String permanentAddressLine2;

    @Column(name = "permanent_address_line3", length = 255)
    private String permanentAddressLine3;

    @Column(name = "permanent_country", length = 100)
    private String permanentCountry;

    @Column(name = "permanent_state", length = 100)
    private String permanentState;

    @Column(name = "permanent_city", length = 100)
    private String permanentCity;

    @Column(name = "permanent_pincode", length = 10)
    private String permanentPincode;

    @Column(name = "same_as_current_address")
    private Boolean sameAsCurrentAddress;

    /**
     * Get full name of the applicant.
     * 
     * @return formatted full name
     */
    public String getFullName() {
        StringBuilder name = new StringBuilder();
        if (salutation != null && !salutation.isEmpty()) {
            name.append(salutation).append(" ");
        }
        if (firstName != null) {
            name.append(firstName);
        }
        if (middleName != null && !middleName.isEmpty()) {
            name.append(" ").append(middleName);
        }
        if (lastName != null) {
            name.append(" ").append(lastName);
        }
        return name.toString().trim();
    }
}
