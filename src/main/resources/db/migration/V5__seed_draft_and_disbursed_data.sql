-- Seed Draft and Disbursed Data for Interrelated Scenarios
-- UUIDs generated for integrity

-- ==========================================
-- 1. DRAFT VEHICLE LOAN (For Update API Testing)
-- ==========================================
INSERT INTO leads (id, application_reference_number, status, product_type, created_by)
VALUES (
    'f5eebc99-9c0b-4ef8-bb6d-6bb9bd380f66',
    'VL-DRAFT-001',
    'DRAFT',
    'VEHICLE_LOAN',
    'DSA12K93431'
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
-- 2. DISBURSED VEHICLE LOAN (For Billing Context)
-- ==========================================
INSERT INTO leads (id, application_reference_number, status, product_type, created_by)
VALUES (
    'aceebc99-9c0b-4ef8-bb6d-6bb9bd380a99',
    'VL-DISBURSED-002',
    'DISBURSED',
    'VEHICLE_LOAN',
    'DSA12K93431'
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
-- 3. BILLING RECORD (Payout for Disbursed Loan)
-- ==========================================
INSERT INTO billing (invoice_id, user_id, period_start, period_end, payout_percentage, amount, status)
VALUES (
    'INV-202312-001',
    (SELECT id FROM users WHERE dsa_unique_code = 'DSA12K93431'), -- Link to DSA User
    '2023-12-01',
    '2023-12-31',
    0.02, -- 2% Payout
    30000.00, -- 2% of 15,00,000
    'PENDING'
);
