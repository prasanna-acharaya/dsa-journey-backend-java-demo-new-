package com.bom.dsa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

/**
 * Embeddable Address class for reuse across entities.
 * Used for current address, permanent address, dealer address, property address
 * etc.
 */
@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Address {

    @Column(name = "address_line1", length = 255)
    private String addressLine1;

    @Column(name = "address_line2", length = 255)
    private String addressLine2;

    @Column(name = "address_line3", length = 255)
    private String addressLine3;

    @Column(name = "country", length = 100)
    private String country;

    @Column(name = "state", length = 100)
    private String state;

    @Column(name = "city", length = 100)
    private String city;

    @Column(name = "pincode", length = 10)
    private String pincode;
}
