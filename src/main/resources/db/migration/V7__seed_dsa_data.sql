-- 1. Create DSAs in various statuses (Users already seeded in V2)

-- 2.1 PENDING DSA (Created by Maker/Admin, waiting for Checker)
INSERT INTO ids_dsa (id, name, unique_code, mobile_number, email, status, category, city, address_line_1, constitution, registration_date, gstin, pan, empanelment_date, agreement_date, agreement_period, zone_mapping, risk_score, created_by, created_at, updated_at)
VALUES 
('c1eebc99-9c0b-4ef8-bb6d-6bb9bd380003', 'Ritik Chauhan', 'DSA_PENDING_001', '9876543210', 'ritik@example.com', 'PENDING', 'Individual', 'Gurugram', 'SCO 56 Old Judicial Complex', 'Partnership', '2025-02-26', '22AAAAA0000A1Z5', 'BXSCP123G', '2025-02-26', '2025-02-26', '2 Years', 'North Zone', 750.0, 'ADMIN001', NOW(), NOW())
ON CONFLICT (id) DO NOTHING;

-- For dsa_products, we don't have a PK, so we use a subquery to avoid duplicates if needed, but let's just use a simple insert for now.
-- If Flyway keeps failing, we'll need a better fix.
INSERT INTO dsa_products (dsa_id, product_type)
SELECT 'c1eebc99-9c0b-4ef8-bb6d-6bb9bd380003', 'VEHICLE_LOAN'
WHERE NOT EXISTS (SELECT 1 FROM dsa_products WHERE dsa_id = 'c1eebc99-9c0b-4ef8-bb6d-6bb9bd380003' AND product_type = 'VEHICLE_LOAN');

INSERT INTO dsa_products (dsa_id, product_type)
SELECT 'c1eebc99-9c0b-4ef8-bb6d-6bb9bd380003', 'HOME_LOAN'
WHERE NOT EXISTS (SELECT 1 FROM dsa_products WHERE dsa_id = 'c1eebc99-9c0b-4ef8-bb6d-6bb9bd380003' AND product_type = 'HOME_LOAN');

INSERT INTO dsa_bank_details (id, dsa_id, account_name, account_number, ifsc_code, branch_name) VALUES 
('d1eebc99-9c0b-4ef8-bb6d-6bb9bd380004', 'c1eebc99-9c0b-4ef8-bb6d-6bb9bd380003', 'Ritik Chauhan', '4213146403', 'BOMK0004329', 'Faridabad - NIT Branch')
ON CONFLICT (id) DO NOTHING;

-- 2.2 EMPANELLED DSA (Active)
-- Using ID from V9 fix to ensure test compatibility: b0eebc99-9c0b-4ef8-bb6d-6bb9bd380005
INSERT INTO ids_dsa (id, name, unique_code, mobile_number, email, status, category, city, address_line_1, constitution, gstin, pan, empanelment_date, agreement_date, agreement_period, zone_mapping, risk_score, created_by, created_at, updated_at)
VALUES 
('b0eebc99-9c0b-4ef8-bb6d-6bb9bd380005', 'Safe Loans Pvt Ltd', 'DSA_ACTIVE_001', '9876543211', 'dsa001@example.com', 'EMPANELLED', 'Corporation', 'Mumbai', '123 Business Park', 'Private Limited', '27AAAAA0000A1Z5', 'ABCde1234F', '2024-01-15', '2024-01-15', '3 Years', 'West Zone', 820.0, 'ADMIN001', NOW(), NOW())
ON CONFLICT (id) DO UPDATE SET 
    unique_code = EXCLUDED.unique_code,
    status = EXCLUDED.status;

INSERT INTO dsa_products (dsa_id, product_type)
SELECT 'b0eebc99-9c0b-4ef8-bb6d-6bb9bd380005', 'VEHICLE_LOAN'
WHERE NOT EXISTS (SELECT 1 FROM dsa_products WHERE dsa_id = 'b0eebc99-9c0b-4ef8-bb6d-6bb9bd380005' AND product_type = 'VEHICLE_LOAN');

INSERT INTO dsa_products (dsa_id, product_type)
SELECT 'b0eebc99-9c0b-4ef8-bb6d-6bb9bd380005', 'EDUCATION_LOAN'
WHERE NOT EXISTS (SELECT 1 FROM dsa_products WHERE dsa_id = 'b0eebc99-9c0b-4ef8-bb6d-6bb9bd380005' AND product_type = 'EDUCATION_LOAN');

INSERT INTO dsa_products (dsa_id, product_type)
SELECT 'b0eebc99-9c0b-4ef8-bb6d-6bb9bd380005', 'HOME_LOAN'
WHERE NOT EXISTS (SELECT 1 FROM dsa_products WHERE dsa_id = 'b0eebc99-9c0b-4ef8-bb6d-6bb9bd380005' AND product_type = 'HOME_LOAN');

INSERT INTO dsa_products (dsa_id, product_type)
SELECT 'b0eebc99-9c0b-4ef8-bb6d-6bb9bd380005', 'LOAN_AGAINST_PROPERTY'
WHERE NOT EXISTS (SELECT 1 FROM dsa_products WHERE dsa_id = 'b0eebc99-9c0b-4ef8-bb6d-6bb9bd380005' AND product_type = 'LOAN_AGAINST_PROPERTY');

-- 2.3 REJECTED DSA
INSERT INTO ids_dsa (id, name, unique_code, mobile_number, email, status, category, city, address_line_1, pan, risk_score, created_by, created_at, updated_at)
VALUES 
('f3eebc99-9c0b-4ef8-bb6d-6bb9bd380006', 'Risky Business', 'DSA_REJECTED_001', '9876543212', 'risk@business.com', 'REJECTED', 'Individual', 'Delhi', 'Somewhere in Delhi', 'XXXXX0000X', 400.0, 'ADMIN001', NOW(), NOW())
ON CONFLICT (id) DO NOTHING;

-- 3. Create Leads for Analytics
-- Linking to 'DSA_ACTIVE_001'

-- 3.1 APPLIED Lead
INSERT INTO leads (id, application_reference_number, status, product_type, created_by, created_at, updated_at)
VALUES ('a1eebc99-9c0b-4ef8-bb6d-6bb9bd380101', 'LD-APP-001', 'APPLIED', 'HOME_LOAN', 'DSA_ACTIVE_001', NOW(), NOW())
ON CONFLICT (id) DO NOTHING;

INSERT INTO basic_details (lead_id, first_name, last_name, mobile_number, email_address, date_of_birth, gender, marital_status)
SELECT 'a1eebc99-9c0b-4ef8-bb6d-6bb9bd380101', 'John', 'Doe', '9000000101', 'john@example.com', '1990-01-01', 'MALE', 'SINGLE'
WHERE NOT EXISTS (SELECT 1 FROM basic_details WHERE lead_id = 'a1eebc99-9c0b-4ef8-bb6d-6bb9bd380101');

INSERT INTO home_loan_details (lead_id, amount_requested, repayment_period, property_type, property_value)
SELECT 'a1eebc99-9c0b-4ef8-bb6d-6bb9bd380101', 5000000, 240, 'APARTMENT', 7500000
WHERE NOT EXISTS (SELECT 1 FROM home_loan_details WHERE lead_id = 'a1eebc99-9c0b-4ef8-bb6d-6bb9bd380101');

-- 3.2 SANCTIONED Lead
INSERT INTO leads (id, application_reference_number, status, product_type, created_by, created_at, updated_at)
VALUES ('a2eebc99-9c0b-4ef8-bb6d-6bb9bd380102', 'LD-SANC-002', 'SANCTIONED', 'VEHICLE_LOAN', 'DSA_ACTIVE_001', NOW() - INTERVAL '2 DAYS', NOW())
ON CONFLICT (id) DO NOTHING;

INSERT INTO basic_details (lead_id, first_name, last_name, mobile_number, email_address, date_of_birth, gender, marital_status)
SELECT 'a2eebc99-9c0b-4ef8-bb6d-6bb9bd380102', 'Jane', 'Smith', '9000000102', 'jane@example.com', '1992-05-15', 'FEMALE', 'MARRIED'
WHERE NOT EXISTS (SELECT 1 FROM basic_details WHERE lead_id = 'a2eebc99-9c0b-4ef8-bb6d-6bb9bd380102');

INSERT INTO vehicle_loan_details (lead_id, amount_requested, repayment_period, vehicle_type, make, model)
SELECT 'a2eebc99-9c0b-4ef8-bb6d-6bb9bd380102', 800000, 60, 'FOUR_WHEELER', 'Tata', 'Nexon'
WHERE NOT EXISTS (SELECT 1 FROM vehicle_loan_details WHERE lead_id = 'a2eebc99-9c0b-4ef8-bb6d-6bb9bd380102');

-- 3.3 DISBURSED Lead
INSERT INTO leads (id, application_reference_number, status, product_type, created_by, created_at, updated_at)
VALUES ('a3eebc99-9c0b-4ef8-bb6d-6bb9bd380103', 'LD-DISB-003', 'DISBURSED', 'EDUCATION_LOAN', 'DSA_ACTIVE_001', NOW() - INTERVAL '10 DAYS', NOW())
ON CONFLICT (id) DO NOTHING;

INSERT INTO basic_details (lead_id, first_name, last_name, mobile_number, email_address, date_of_birth, gender, marital_status)
SELECT 'a3eebc99-9c0b-4ef8-bb6d-6bb9bd380103', 'Student', 'One', '9000000103', 'student@example.com', '2000-08-20', 'OTHER', 'SINGLE'
WHERE NOT EXISTS (SELECT 1 FROM basic_details WHERE lead_id = 'a3eebc99-9c0b-4ef8-bb6d-6bb9bd380103');

INSERT INTO education_loan_details (lead_id, amount_requested, repayment_period, course_name, institution_name)
SELECT 'a3eebc99-9c0b-4ef8-bb6d-6bb9bd380103', 2000000, 120, 'MBA', 'IIM Ahmedabad'
WHERE NOT EXISTS (SELECT 1 FROM education_loan_details WHERE lead_id = 'a3eebc99-9c0b-4ef8-bb6d-6bb9bd380103');

-- 3.4 REJECTED Lead
INSERT INTO leads (id, application_reference_number, status, product_type, created_by, created_at, updated_at)
VALUES ('a4eebc99-9c0b-4ef8-bb6d-6bb9bd380104', 'LD-REJ-004', 'REJECTED', 'LOAN_AGAINST_PROPERTY', 'DSA_ACTIVE_001', NOW() - INTERVAL '5 DAYS', NOW())
ON CONFLICT (id) DO NOTHING;

INSERT INTO basic_details (lead_id, first_name, last_name, mobile_number, email_address, date_of_birth, gender, marital_status)
SELECT 'a4eebc99-9c0b-4ef8-bb6d-6bb9bd380104', 'Reject', 'Me', '9000000104', 'reject@example.com', '1985-12-12', 'MALE', 'DIVORCED'
WHERE NOT EXISTS (SELECT 1 FROM basic_details WHERE lead_id = 'a4eebc99-9c0b-4ef8-bb6d-6bb9bd380104');
