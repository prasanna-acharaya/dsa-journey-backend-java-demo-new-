-- Seed default users if they don't exist

-- Admin User
INSERT INTO users (dsa_unique_code, password, full_name, email, mobile_number, role, created_by)
VALUES (
    'ADMIN001',
    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcXQQzZIE.2m5rVZt0TCXV.Rmr6',
    'System Administrator',
    'admin@bom.com',
    '9999999999',
    'ADMIN',
    'SYSTEM'
) ON CONFLICT (dsa_unique_code) DO NOTHING;

-- DSA User
INSERT INTO users (dsa_unique_code, password, full_name, email, mobile_number, role, created_by)
VALUES (
    'DSA12K93431',
    '$2a$10$pHv4XCMBLiYYOBx77Jd1ruFn.hPxoT0v8yGNsNsJvvNH5.JNvXfZm',
    'DSA User One',
    'dsa001@example.com',
    '9876543210',
    'DSA',
    'SYSTEM'
) ON CONFLICT (dsa_unique_code) DO NOTHING;
