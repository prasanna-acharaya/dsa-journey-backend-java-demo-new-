package com.bom.dsa.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Entity for Vehicle Loan specific details.
 * One-to-one relationship with Lead entity.
 */
@Entity
@Table(name = "vehicle_loan_details")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleLoanDetails {

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

    @Column(name = "vehicle_type", nullable = false, length = 20)
    private String vehicleType;

    @Column(name = "make", length = 100)
    private String make;

    @Column(name = "model", length = 100)
    private String model;

    @Column(name = "ex_showroom_price", precision = 15, scale = 2)
    private BigDecimal exShowroomPrice;

    @Column(name = "insurance_cost", precision = 15, scale = 2)
    private BigDecimal insuranceCost;

    @Column(name = "road_tax", precision = 15, scale = 2)
    private BigDecimal roadTax;

    @Column(name = "accessories_other_cost", precision = 15, scale = 2)
    private BigDecimal accessoriesOtherCost;

    @Column(name = "total_cost_of_vehicle", precision = 15, scale = 2)
    private BigDecimal totalCostOfVehicle;

    // Dealer Details
    @Column(name = "dealer_name", length = 255)
    private String dealerName;

    @Column(name = "dealer_address_line1", length = 255)
    private String dealerAddressLine1;

    @Column(name = "dealer_address_line2", length = 255)
    private String dealerAddressLine2;

    @Column(name = "dealer_address_line3", length = 255)
    private String dealerAddressLine3;

    @Column(name = "dealer_country", length = 100)
    private String dealerCountry;

    @Column(name = "dealer_state", length = 100)
    private String dealerState;

    @Column(name = "dealer_city", length = 100)
    private String dealerCity;

    @Column(name = "dealer_pincode", length = 10)
    private String dealerPincode;

    /**
     * Calculate total cost of vehicle before persisting.
     */
    @PrePersist
    @PreUpdate
    public void calculateTotalCost() {
        BigDecimal total = BigDecimal.ZERO;
        if (exShowroomPrice != null)
            total = total.add(exShowroomPrice);
        if (insuranceCost != null)
            total = total.add(insuranceCost);
        if (roadTax != null)
            total = total.add(roadTax);
        if (accessoriesOtherCost != null)
            total = total.add(accessoriesOtherCost);
        this.totalCostOfVehicle = total;
    }
}
