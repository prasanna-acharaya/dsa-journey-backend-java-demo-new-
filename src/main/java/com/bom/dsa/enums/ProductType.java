package com.bom.dsa.enums;

/**
 * Enum representing the types of loan products offered.
 */
public enum ProductType {
    VEHICLE_LOAN("Vehicle Loan"),
    EDUCATION_LOAN("Education Loan"),
    LOAN_AGAINST_PROPERTY("Loan Against Property"),
    HOME_LOAN("Home Loan");

    private final String displayName;

    ProductType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
