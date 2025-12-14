-- Seed All Loan Types Data
-- UUIDs generated for integrity

-- ==========================================
-- 1. VEHICLE LOAN (Applied)
-- ==========================================
INSERT INTO leads (id, application_reference_number, status, product_type, created_by)
VALUES (
    'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11',
    'VL-202312-001',
    'APPLIED',
    'VEHICLE_LOAN',
    'ADMIN001'
);

INSERT INTO basic_details (lead_id, first_name, last_name, date_of_birth, gender, marital_status, mobile_number, email_address, current_city)
VALUES (
    'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11',
    'John',
    'Doe',
    '1990-01-15',
    'MALE',
    'SINGLE',
    '9876543210',
    'john.doe@example.com',
    'Mumbai'
);

INSERT INTO vehicle_loan_details (lead_id, amount_requested, repayment_period, vehicle_type, make, model)
VALUES (
    'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11',
    850000.00,
    60,
    'FOUR_WHEELER',
    'Tata',
    'Nexon'
);

-- ==========================================
-- 2. EDUCATION LOAN (Under Process)
-- ==========================================
INSERT INTO leads (id, application_reference_number, status, product_type, created_by)
VALUES (
    'c2eebc99-9c0b-4ef8-bb6d-6bb9bd380c33',
    'EL-202312-003',
    'UNDER_PROCESS',
    'EDUCATION_LOAN',
    'DSA12K93431'
);

INSERT INTO basic_details (lead_id, first_name, last_name, date_of_birth, gender, marital_status, mobile_number, email_address, current_city)
VALUES (
    'c2eebc99-9c0b-4ef8-bb6d-6bb9bd380c33',
    'Alice',
    'Wonderland',
    '2002-08-10',
    'FEMALE',
    'SINGLE',
    '9123456780',
    'alice.w@example.com',
    'Bangalore'
);

INSERT INTO education_loan_details (lead_id, amount_requested, repayment_period, course_name, institution_name, institution_city, course_duration_years)
VALUES (
    'c2eebc99-9c0b-4ef8-bb6d-6bb9bd380c33',
    2500000.00,
    84,
    'Master of Data Science',
    'IIT Bangalore',
    'Bangalore',
    2
);

-- ==========================================
-- 3. HOME LOAN (Sanctioned)
-- ==========================================
INSERT INTO leads (id, application_reference_number, status, product_type, created_by)
VALUES (
    'd3eebc99-9c0b-4ef8-bb6d-6bb9bd380d44',
    'HL-202312-004',
    'SANCTIONED',
    'HOME_LOAN',
    'ADMIN001'
);

INSERT INTO basic_details (lead_id, first_name, last_name, date_of_birth, gender, marital_status, mobile_number, email_address, current_city)
VALUES (
    'd3eebc99-9c0b-4ef8-bb6d-6bb9bd380d44',
    'Robert',
    'Brown',
    '1985-03-22',
    'MALE',
    'MARRIED',
    '9988007766',
    'robert.brown@example.com',
    'Pune'
);

INSERT INTO home_loan_details (lead_id, amount_requested, repayment_period, property_type, property_city, property_value)
VALUES (
    'd3eebc99-9c0b-4ef8-bb6d-6bb9bd380d44',
    5500000.00,
    240,
    'APARTMENT',
    'Pune',
    6500000.00
);

-- ==========================================
-- 4. LOAN AGAINST PROPERTY (Rejected)
-- ==========================================
INSERT INTO leads (id, application_reference_number, status, product_type, created_by)
VALUES (
    'e4eebc99-9c0b-4ef8-bb6d-6bb9bd380e55',
    'LAP-202312-005',
    'REJECTED',
    'LOAN_AGAINST_PROPERTY',
    'DSA12K93431'
);

INSERT INTO basic_details (lead_id, first_name, last_name, date_of_birth, gender, marital_status, mobile_number, email_address, current_city)
VALUES (
    'e4eebc99-9c0b-4ef8-bb6d-6bb9bd380e55',
    'Sarah',
    'Connor',
    '1978-11-05',
    'FEMALE',
    'WIDOWED',
    '9765432109',
    'sarah.connor@example.com',
    'Hyderabad'
);

INSERT INTO loan_against_property_details (lead_id, amount_requested, repayment_period, property_type, property_city, property_market_value)
VALUES (
    'e4eebc99-9c0b-4ef8-bb6d-6bb9bd380e55',
    15000000.00,
    180,
    'COMMERCIAL',
    'Hyderabad',
    20000000.00
);
