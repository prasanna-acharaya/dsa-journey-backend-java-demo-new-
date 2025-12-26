-- Seed precisely 4 accounts with password 'password123'
-- Bcrypt Hash: $2a$10$8.UnVuG9HHgffUDAlk8q7uy5qFEVSFAh2Uhc9VC2HzmcD7uCuZqy.

-- 1. Admin User
INSERT INTO users (id, dsa_unique_code, password, full_name, email, mobile_number, role, created_by)
VALUES (
    'b0eebc99-9c0b-4ef8-bb6d-6bb9bd380001',
    'ADMIN001',
    '$2a$10$uYxZtKxy0scNyAblaHSxpe6u2zpw46raAJr/DNBZzrM4/1XpvHBe2',
    'Admin User',
    'admin@bom.com',
    '9999999001',
    'ADMIN',
    'SYSTEM'
) ON CONFLICT (dsa_unique_code) DO UPDATE SET password = EXCLUDED.password;

-- 2. Checker User
INSERT INTO users (id, dsa_unique_code, password, full_name, email, mobile_number, role, created_by)
VALUES (
    'b0eebc99-9c0b-4ef8-bb6d-6bb9bd380002',
    'CHECKER001',
    '$2a$10$uYxZtKxy0scNyAblaHSxpe6u2zpw46raAJr/DNBZzrM4/1XpvHBe2',
    'Checker User',
    'checker@bom.com',
    '9999999002',
    'CHECKER',
    'SYSTEM'
) ON CONFLICT (dsa_unique_code) DO UPDATE SET password = EXCLUDED.password;

-- 3. Bank Manager User
INSERT INTO users (id, dsa_unique_code, password, full_name, email, mobile_number, role, created_by)
VALUES (
    'b0eebc99-9c0b-4ef8-bb6d-6bb9bd380003',
    'MANAGER001',
    '$2a$10$uYxZtKxy0scNyAblaHSxpe6u2zpw46raAJr/DNBZzrM4/1XpvHBe2',
    'Bank Manager User',
    'manager@bom.com',
    '9999999003',
    'BANK_MANAGER',
    'SYSTEM'
) ON CONFLICT (dsa_unique_code) DO UPDATE SET password = EXCLUDED.password;

-- 4. DSA User
INSERT INTO users (id, dsa_unique_code, password, full_name, email, mobile_number, role, created_by)
VALUES (
    'b0eebc99-9c0b-4ef8-bb6d-6bb9bd380005',
    'DSA_ACTIVE_001',
    '$2a$10$uYxZtKxy0scNyAblaHSxpe6u2zpw46raAJr/DNBZzrM4/1XpvHBe2',
    'Safe Loans Pvt Ltd',
    'dsa001@example.com',
    '9876543211',
    'DSA',
    'SYSTEM'
) ON CONFLICT (dsa_unique_code) DO UPDATE SET password = EXCLUDED.password;
