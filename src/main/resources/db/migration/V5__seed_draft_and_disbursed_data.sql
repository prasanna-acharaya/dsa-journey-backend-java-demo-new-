-- Seed Draft and Disbursed Data for Interrelated Scenarios
-- UUIDs generated for integrity

-- ==========================================
-- 1. DRAFT VEHICLE LOAN
-- ==========================================
INSERT INTO leads (id, application_reference_number, status, product_type, created_by)
VALUES (
    'f5eebc99-9c0b-4ef8-bb6d-6bb9bd380f66',
    'VL-DRAFT-001',
    'DRAFT',
    'VEHICLE_LOAN',
    'DSA_ACTIVE_001'
);

INSERT INTO basic_details (lead_id, first_name, last_name, date_of_birth, gender, marital_status, mobile_number, email_address, current_city)
VALUES (
    'f5eebc99-9c0b-4ef8-bb6d-6bb9bd380f66',
    'Michael',
    'Draftsman',
    '1998-02-14',
    'MALE',
    'SINGLE',
    '9000000001',
    'michael.draft@example.com',
    'Chennai'
);

INSERT INTO vehicle_loan_details (lead_id, amount_requested, repayment_period, vehicle_type, make, model)
VALUES (
    'f5eebc99-9c0b-4ef8-bb6d-6bb9bd380f66',
    600000.00,
    36,
    'TWO_WHEELER',
    'Royal Enfield',
    'Classic 350'
);

-- ==========================================
-- 2. DISBURSED VEHICLE LOAN
-- ==========================================
INSERT INTO leads (id, application_reference_number, status, product_type, created_by)
VALUES (
    'aceebc99-9c0b-4ef8-bb6d-6bb9bd380a99',
    'VL-DISBURSED-002',
    'DISBURSED',
    'VEHICLE_LOAN',
    'DSA_ACTIVE_001'
);

INSERT INTO basic_details (lead_id, first_name, last_name, date_of_birth, gender, marital_status, mobile_number, email_address, current_city)
VALUES (
    'aceebc99-9c0b-4ef8-bb6d-6bb9bd380a99',
    'Sarah',
    'Winner',
    '1988-11-22',
    'FEMALE',
    'MARRIED',
    '9000000002',
    'sarah.winner@example.com',
    'Mumbai'
);

INSERT INTO vehicle_loan_details (lead_id, amount_requested, repayment_period, vehicle_type, make, model)
VALUES (
    'aceebc99-9c0b-4ef8-bb6d-6bb9bd380a99',
    1500000.00,
    60,
    'FOUR_WHEELER',
    'Mahindra',
    'Thar'
);

-- ==========================================
-- 3. BILLING RECORD
-- ==========================================
INSERT INTO billing (invoice_id, user_id, period_start, period_end, payout_percentage, amount, status)
VALUES (
    'INV-202312-001',
    (SELECT id FROM users WHERE dsa_unique_code = 'DSA_ACTIVE_001'),
    '2023-12-01',
    '2023-12-31',
    0.02,
    30000.00,
    'PENDING'
);

-- ==========================================
-- 4. ADDITIONAL LEADS (APPLIED, VERIFIED, APPROVED)
-- ==========================================

-- 4.1 Home Loan (Applied)
INSERT INTO leads (id, application_reference_number, status, product_type, created_by, created_at)
VALUES ('b1eebc99-9c0b-4ef8-bb6d-6bb9bd380b01', 'HL-APP-003', 'APPLIED', 'HOME_LOAN', 'DSA_ACTIVE_001', NOW() - INTERVAL '5 DAYS');

INSERT INTO basic_details (lead_id, first_name, last_name, date_of_birth, gender, marital_status, mobile_number, email_address, current_city)
VALUES ('b1eebc99-9c0b-4ef8-bb6d-6bb9bd380b01', 'Amit', 'Sharma', '1985-06-15', 'MALE', 'MARRIED', '9876543210', 'amit.sharma@example.com', 'Delhi');

INSERT INTO occupation_details (lead_id, occupation_type, company_type, employer_name, designation, total_experience)
VALUES ('b1eebc99-9c0b-4ef8-bb6d-6bb9bd380b01', 'SALARIED', 'MNC', 'TCS', 'Senior Consultant', 8.5);

INSERT INTO financial_details (lead_id, monthly_gross_income, monthly_deductions, monthly_emi, monthly_net_income)
VALUES ('b1eebc99-9c0b-4ef8-bb6d-6bb9bd380b01', 150000.00, 20000.00, 15000.00, 115000.00);

INSERT INTO home_loan_details (lead_id, amount_requested, repayment_period, property_type, property_value)
VALUES ('b1eebc99-9c0b-4ef8-bb6d-6bb9bd380b01', 5000000.00, 240, 'APARTMENT', 7000000.00);

-- 4.2 Education Loan (Verified)
INSERT INTO leads (id, application_reference_number, status, product_type, created_by, created_at)
VALUES ('b2eebc99-9c0b-4ef8-bb6d-6bb9bd380b02', 'EL-VER-004', 'VERIFIED', 'EDUCATION_LOAN', 'DSA_ACTIVE_001', NOW() - INTERVAL '3 DAYS');

INSERT INTO basic_details (lead_id, first_name, last_name, date_of_birth, gender, marital_status, mobile_number, email_address, current_city)
VALUES ('b2eebc99-9c0b-4ef8-bb6d-6bb9bd380b02', 'Priya', 'Verma', '2001-09-10', 'FEMALE', 'SINGLE', '9876543212', 'priya.verma@example.com', 'Bangalore');

INSERT INTO education_loan_details (lead_id, amount_requested, repayment_period, course_name, institution_name, institution_city)
VALUES ('b2eebc99-9c0b-4ef8-bb6d-6bb9bd380b02', 2500000.00, 120, 'MS in CS', 'Stanford University', 'California');

-- 4.3 Loan Against Property (Approved)
INSERT INTO leads (id, application_reference_number, status, product_type, created_by, created_at)
VALUES ('b3eebc99-9c0b-4ef8-bb6d-6bb9bd380b03', 'LAP-APR-005', 'APPROVED', 'LOAN_AGAINST_PROPERTY', 'DSA_ACTIVE_001', NOW() - INTERVAL '1 DAY');

INSERT INTO basic_details (lead_id, first_name, last_name, date_of_birth, gender, marital_status, mobile_number, email_address, current_city)
VALUES ('b3eebc99-9c0b-4ef8-bb6d-6bb9bd380b03', 'Rajesh', 'Gupta', '1975-03-20', 'MALE', 'MARRIED', '9876543213', 'rajesh.gupta@example.com', 'Pune');

INSERT INTO occupation_details (lead_id, occupation_type, company_type, total_experience)
VALUES ('b3eebc99-9c0b-4ef8-bb6d-6bb9bd380b03', 'SELF_EMPLOYED', 'Private Limited', 15.0);

INSERT INTO loan_against_property_details (lead_id, amount_requested, repayment_period, property_type, property_market_value)
VALUES ('b3eebc99-9c0b-4ef8-bb6d-6bb9bd380b03', 10000000.00, 180, 'COMMERCIAL', 20000000.00);


-- 4.4 Vehicle Loan (Rejected) - High Risk
INSERT INTO leads (id, application_reference_number, status, product_type, created_by, created_at)
VALUES ('b4eebc99-9c0b-4ef8-bb6d-6bb9bd380b04', 'VL-REJ-006', 'REJECTED', 'VEHICLE_LOAN', 'DSA_ACTIVE_001', NOW() - INTERVAL '10 DAYS');

INSERT INTO basic_details (lead_id, first_name, last_name, date_of_birth, gender, marital_status, mobile_number, email_address, current_city)
VALUES ('b4eebc99-9c0b-4ef8-bb6d-6bb9bd380b04', 'Vikram', 'Singh', '1995-07-22', 'MALE', 'SINGLE', '9876543214', 'vikram.singh@example.com', 'Jaipur');

INSERT INTO vehicle_loan_details (lead_id, amount_requested, repayment_period, vehicle_type, make, model)
VALUES ('b4eebc99-9c0b-4ef8-bb6d-6bb9bd380b04', 1200000.00, 48, 'FOUR_WHEELER', 'Hyundai', 'Creta');

-- ==========================================
-- 5. MORE LEADS (Requested Update)
-- ==========================================

-- 5.1 Home Loan (Sanctioned)
INSERT INTO leads (id, application_reference_number, status, product_type, created_by, created_at)
VALUES ('c1eebc99-9c0b-4ef8-bb6d-6bb9bd380c01', 'HL-SANC-007', 'SANCTIONED', 'HOME_LOAN', 'DSA_ACTIVE_001', NOW() - INTERVAL '4 DAYS');

INSERT INTO basic_details (lead_id, first_name, last_name, date_of_birth, gender, marital_status, mobile_number, email_address, current_city)
VALUES ('c1eebc99-9c0b-4ef8-bb6d-6bb9bd380c01', 'Karan', 'Johar', '1980-05-10', 'MALE', 'SINGLE', '9876543215', 'karan.johar@example.com', 'Mumbai');

INSERT INTO home_loan_details (lead_id, amount_requested, repayment_period, property_type, property_value)
VALUES ('c1eebc99-9c0b-4ef8-bb6d-6bb9bd380c01', 12000000.00, 240, 'VILLA', 15000000.00);

-- 5.2 Education Loan (Draft)
INSERT INTO leads (id, application_reference_number, status, product_type, created_by, created_at)
VALUES ('c2eebc99-9c0b-4ef8-bb6d-6bb9bd380c02', 'EL-DRAFT-008', 'DRAFT', 'EDUCATION_LOAN', 'DSA_ACTIVE_001', NOW() - INTERVAL '1 DAY');

INSERT INTO basic_details (lead_id, first_name, last_name, date_of_birth, gender, marital_status, mobile_number, email_address, current_city)
VALUES ('c2eebc99-9c0b-4ef8-bb6d-6bb9bd380c02', 'Anjali', 'Mehta', '2002-11-30', 'FEMALE', 'SINGLE', '9876543216', 'anjali.mehta@example.com', 'Pune');

INSERT INTO education_loan_details (lead_id, amount_requested, repayment_period, course_name, institution_name, institution_city)
VALUES ('c2eebc99-9c0b-4ef8-bb6d-6bb9bd380c02', 3000000.00, 120, 'MBBS', 'AIIMS', 'Delhi');

-- 5.3 Loan Against Property (Verified)
INSERT INTO leads (id, application_reference_number, status, product_type, created_by, created_at)
VALUES ('c3eebc99-9c0b-4ef8-bb6d-6bb9bd380c03', 'LAP-VER-009', 'VERIFIED', 'LOAN_AGAINST_PROPERTY', 'DSA_ACTIVE_001', NOW() - INTERVAL '2 DAYS');

INSERT INTO basic_details (lead_id, first_name, last_name, date_of_birth, gender, marital_status, mobile_number, email_address, current_city)
VALUES ('c3eebc99-9c0b-4ef8-bb6d-6bb9bd380c03', 'Suresh', 'Raina', '1987-11-27', 'MALE', 'MARRIED', '9876543217', 'suresh.raina@example.com', 'Chennai');

INSERT INTO loan_against_property_details (lead_id, amount_requested, repayment_period, property_type, property_market_value)
VALUES ('c3eebc99-9c0b-4ef8-bb6d-6bb9bd380c03', 5000000.00, 120, 'RESIDENTIAL', 8000000.00);
