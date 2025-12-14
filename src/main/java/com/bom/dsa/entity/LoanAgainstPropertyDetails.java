package com.bom.dsa.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Entity for Loan Against Property specific details.
 * One-to-one relationship with Lead entity.
 */
@Entity
@Table(name = "loan_against_property_details")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoanAgainstPropertyDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lead_id", nullable = false, unique = true)
    private Lead lead;

    @Column(name = "amount_requested", precision = 15, scale = 2, nullable = false)
    private BigDecimal amountRequested;

    @Column(name = "repayment_period", nullable = false)
    private Integer repaymentPeriod;

    @Column(name = "property_type", length = 50)
    private String propertyType;

    @Column(name = "property_address_line1", length = 255)
    private String propertyAddressLine1;

    @Column(name = "property_address_line2", length = 255)
    private String propertyAddressLine2;

    @Column(name = "property_address_line3", length = 255)
    private String propertyAddressLine3;

    @Column(name = "property_country", length = 100)
    private String propertyCountry;

    @Column(name = "property_state", length = 100)
    private String propertyState;

    @Column(name = "property_city", length = 100)
    private String propertyCity;

    @Column(name = "property_pincode", length = 10)
    private String propertyPincode;

    @Column(name = "property_market_value", precision = 15, scale = 2)
    private BigDecimal propertyMarketValue;
}
