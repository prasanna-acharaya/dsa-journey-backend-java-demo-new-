-- DSA Loan Management System - Initial Schema
-- Version: V1
-- Description: Creates all tables for the DSA Loan Management System

-- Enable UUID extension (if using PostgreSQL)
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- =====================================
-- Users table (for authentication)
-- =====================================
CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    dsa_unique_code VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    mobile_number VARCHAR(15) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'DSA',
    is_active BOOLEAN DEFAULT true,
    is_locked BOOLEAN DEFAULT false,
    failed_login_attempts INTEGER DEFAULT 0,
    last_login_at TIMESTAMP,
    created_by VARCHAR(100),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(100),
    updated_at TIMESTAMP
);

-- =====================================
-- Leads table (main table)
-- =====================================
CREATE TABLE leads (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    application_reference_number VARCHAR(50) UNIQUE NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'APPLIED',
    product_type VARCHAR(50) NOT NULL,
    
    -- Branch Assignment
    assigned_branch_name VARCHAR(255),
    assigned_branch_address TEXT,
    
    -- Soft delete fields
    is_deleted BOOLEAN DEFAULT false,
    deleted_at TIMESTAMP,
    deleted_by VARCHAR(100),
    
    -- Audit fields
    created_by VARCHAR(100) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(100),
    updated_at TIMESTAMP
);

-- =====================================
-- Basic Details table (OneToOne with leads)
-- =====================================
CREATE TABLE basic_details (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    lead_id UUID NOT NULL UNIQUE,
    
    -- Personal Information
    salutation VARCHAR(10),
    first_name VARCHAR(100) NOT NULL,
    middle_name VARCHAR(100),
    last_name VARCHAR(100) NOT NULL,
    date_of_birth DATE NOT NULL,
    gender VARCHAR(10) NOT NULL,
    marital_status VARCHAR(20) NOT NULL,
    qualification VARCHAR(50),
    mobile_number VARCHAR(15) NOT NULL,
    email_address VARCHAR(255) NOT NULL,
    
    -- Current Address
    current_address_line1 VARCHAR(255),
    current_address_line2 VARCHAR(255),
    current_address_line3 VARCHAR(255),
    current_country VARCHAR(100),
    current_state VARCHAR(100),
    current_city VARCHAR(100),
    current_pincode VARCHAR(10),
    
    -- Permanent Address
    permanent_address_line1 VARCHAR(255),
    permanent_address_line2 VARCHAR(255),
    permanent_address_line3 VARCHAR(255),
    permanent_country VARCHAR(100),
    permanent_state VARCHAR(100),
    permanent_city VARCHAR(100),
    permanent_pincode VARCHAR(10),
    same_as_current_address BOOLEAN DEFAULT false,
    
    FOREIGN KEY (lead_id) REFERENCES leads(id) ON DELETE CASCADE
);

-- =====================================
-- Occupation Details table (OneToOne with leads)
-- =====================================
CREATE TABLE occupation_details (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    lead_id UUID NOT NULL UNIQUE,
    occupation_type VARCHAR(20) NOT NULL,
    company_type VARCHAR(50),
    employer_name VARCHAR(255),
    designation VARCHAR(100),
    total_experience DECIMAL(4,2),
    FOREIGN KEY (lead_id) REFERENCES leads(id) ON DELETE CASCADE
);

-- =====================================
-- Financial Details table (OneToOne with leads)
-- =====================================
CREATE TABLE financial_details (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    lead_id UUID NOT NULL UNIQUE,
    monthly_gross_income DECIMAL(15,2),
    monthly_deductions DECIMAL(15,2),
    monthly_emi DECIMAL(15,2),
    monthly_net_income DECIMAL(15,2),
    FOREIGN KEY (lead_id) REFERENCES leads(id) ON DELETE CASCADE
);

-- =====================================
-- Vehicle Loan Details table (OneToOne with leads)
-- =====================================
CREATE TABLE vehicle_loan_details (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    lead_id UUID NOT NULL UNIQUE,
    amount_requested DECIMAL(15,2) NOT NULL,
    repayment_period INTEGER NOT NULL,
    vehicle_type VARCHAR(20) NOT NULL,
    make VARCHAR(100),
    model VARCHAR(100),
    ex_showroom_price DECIMAL(15,2),
    insurance_cost DECIMAL(15,2),
    road_tax DECIMAL(15,2),
    accessories_other_cost DECIMAL(15,2),
    total_cost_of_vehicle DECIMAL(15,2),
    
    -- Dealer Details
    dealer_name VARCHAR(255),
    dealer_address_line1 VARCHAR(255),
    dealer_address_line2 VARCHAR(255),
    dealer_address_line3 VARCHAR(255),
    dealer_country VARCHAR(100),
    dealer_state VARCHAR(100),
    dealer_city VARCHAR(100),
    dealer_pincode VARCHAR(10),
    
    FOREIGN KEY (lead_id) REFERENCES leads(id) ON DELETE CASCADE
);

-- =====================================
-- Education Loan Details table (OneToOne with leads)
-- =====================================
CREATE TABLE education_loan_details (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    lead_id UUID NOT NULL UNIQUE,
    amount_requested DECIMAL(15,2) NOT NULL,
    repayment_period INTEGER NOT NULL,
    course_name VARCHAR(255),
    institution_name VARCHAR(255),
    institution_country VARCHAR(100),
    institution_state VARCHAR(100),
    institution_city VARCHAR(100),
    course_duration_years INTEGER,
    FOREIGN KEY (lead_id) REFERENCES leads(id) ON DELETE CASCADE
);

-- =====================================
-- Home Loan Details table (OneToOne with leads)
-- =====================================
CREATE TABLE home_loan_details (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    lead_id UUID NOT NULL UNIQUE,
    amount_requested DECIMAL(15,2) NOT NULL,
    repayment_period INTEGER NOT NULL,
    property_type VARCHAR(50),
    property_address_line1 VARCHAR(255),
    property_address_line2 VARCHAR(255),
    property_address_line3 VARCHAR(255),
    property_country VARCHAR(100),
    property_state VARCHAR(100),
    property_city VARCHAR(100),
    property_pincode VARCHAR(10),
    property_value DECIMAL(15,2),
    FOREIGN KEY (lead_id) REFERENCES leads(id) ON DELETE CASCADE
);

-- =====================================
-- Loan Against Property Details table (OneToOne with leads)
-- =====================================
CREATE TABLE loan_against_property_details (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    lead_id UUID NOT NULL UNIQUE,
    amount_requested DECIMAL(15,2) NOT NULL,
    repayment_period INTEGER NOT NULL,
    property_type VARCHAR(50),
    property_address_line1 VARCHAR(255),
    property_address_line2 VARCHAR(255),
    property_address_line3 VARCHAR(255),
    property_country VARCHAR(100),
    property_state VARCHAR(100),
    property_city VARCHAR(100),
    property_pincode VARCHAR(10),
    property_market_value DECIMAL(15,2),
    FOREIGN KEY (lead_id) REFERENCES leads(id) ON DELETE CASCADE
);

-- =====================================
-- Documents table (ManyToOne with leads)
-- =====================================
CREATE TABLE documents (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    lead_id UUID NOT NULL,
    document_type VARCHAR(50) NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    file_path VARCHAR(500) NOT NULL,
    file_size BIGINT NOT NULL,
    mime_type VARCHAR(100) NOT NULL,
    uploaded_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    uploaded_by VARCHAR(100) NOT NULL,
    FOREIGN KEY (lead_id) REFERENCES leads(id) ON DELETE CASCADE
);

-- =====================================
-- Billing table (ManyToOne with users)
-- =====================================
CREATE TABLE billing (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    invoice_id VARCHAR(50) UNIQUE NOT NULL,
    user_id UUID NOT NULL,
    period_start DATE NOT NULL,
    period_end DATE NOT NULL,
    payout_percentage DECIMAL(5,4) NOT NULL,
    amount DECIMAL(15,2) NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'PENDING',
    generated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    paid_at TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- =====================================
-- Create indexes for better query performance
-- =====================================
CREATE INDEX idx_leads_status ON leads(status);
CREATE INDEX idx_leads_product_type ON leads(product_type);
CREATE INDEX idx_leads_created_by ON leads(created_by);
CREATE INDEX idx_leads_created_at ON leads(created_at);
CREATE INDEX idx_leads_app_ref_number ON leads(application_reference_number);
CREATE INDEX idx_leads_is_deleted ON leads(is_deleted);

CREATE INDEX idx_users_dsa_code ON users(dsa_unique_code);
CREATE INDEX idx_users_email ON users(email);

CREATE INDEX idx_basic_details_lead_id ON basic_details(lead_id);
CREATE INDEX idx_basic_details_mobile ON basic_details(mobile_number);

CREATE INDEX idx_occupation_details_lead_id ON occupation_details(lead_id);
CREATE INDEX idx_financial_details_lead_id ON financial_details(lead_id);

CREATE INDEX idx_documents_lead_id ON documents(lead_id);
CREATE INDEX idx_documents_type ON documents(document_type);

CREATE INDEX idx_billing_user_id ON billing(user_id);
CREATE INDEX idx_billing_status ON billing(status);
CREATE INDEX idx_billing_period ON billing(period_start, period_end);

-- =====================================
-- Insert default admin user (password: Admin@123 - bcrypt encoded)
-- =====================================
INSERT INTO users (dsa_unique_code, password, full_name, email, mobile_number, role, created_by)
VALUES (
    'ADMIN001',
    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcXQQzZIE.2m5rVZt0TCXV.Rmr6',
    'System Administrator',
    'admin@bom.com',
    '9999999999',
    'ADMIN',
    'SYSTEM'
);

-- Insert sample DSA user (password: dsa123 - bcrypt encoded)
INSERT INTO users (dsa_unique_code, password, full_name, email, mobile_number, role, created_by)
VALUES (
    'DSA12K93431',
    '$2a$10$pHv4XCMBLiYYOBx77Jd1ruFn.hPxoT0v8yGNsNsJvvNH5.JNvXfZm',
    'DSA User One',
    'dsa001@example.com',
    '9876543210',
    'DSA',
    'SYSTEM'
);
