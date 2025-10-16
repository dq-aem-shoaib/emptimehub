-- -----------------------------
-- SEED DATA
-- -----------------------------

-- 1. Users
INSERT INTO users (user_id, user_name, company_email, password, role)
VALUES
('11111111-1111-1111-1111-111111111111', 'Admin User', 'admin@example.com', 'adminpass', 'ADMIN'),
('22222222-2222-2222-2222-222222222222', 'Employee One', 'john.doe@company.com', '$2a$12$9/IixUv/hK2Ivb.eJDKeMejqyW18.51.tT3Dxt/sbUiblXmyU/rZe', 'EMPLOYEE'),
('33333333-3333-3333-3333-333333333333', 'Client One', 'client1@example.com', 'clientpass', 'CLIENT'),
('12333333-3333-3333-3333-333333333333', 'Employee Two', 'sandy@techcorp.com', '$2a$12$.bMRKRpAXrrXBPZC1GkPiOyUlvs4RPn/bVEz9SXPSnJz56YdtLV2m', 'EMPLOYEE'),
('77333333-3333-3333-3333-333333333333', 'Client Two', 'client2@example.com', 'client2pass', 'CLIENT');

-- 2. Address
INSERT INTO address (address_id, house_no, street_name, city, state, country, pincode)
VALUES
('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', '101', 'MG Road', 'Bangalore', 'Karnataka', 'India', '560001'),
('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', '202', 'Park Street', 'Kolkata', 'West Bengal', 'India', '700016'),
('cccccccc-cccc-cccc-cccc-cccccccccccc', '303', 'Marine Drive', 'Mumbai', 'Maharashtra', 'India', '400020');

-- 3. Clients
INSERT INTO client (client_id, user_id, company_name, contact_number, email, gst, tan_number, currency, pan_number)
VALUES
('aaaaaaaa-1111-1111-1111-aaaaaaaaaaaa', '22222222-2222-2222-2222-222222222222', 'TechCorp', '+911234567890', 'contact@techcorp.com', 'GST12345', 'TAN12345', 'INR', 'PAN12345'),
('abbaaaaa-1111-1111-1177-aaaaaaaaaaaa', '77333333-3333-3333-3333-333333333333', 'Digiquad', '+911234567780', 'digi@techcorp.com', 'GST897', 'TAN12090', 'INR', 'PAN128945');

-- 4. Client POCs
INSERT INTO client_poc (poc_id, client_id, name, email, contact_number, designation)
VALUES
('dddddddd-dddd-dddd-dddd-dddddddddddd', 'aaaaaaaa-1111-1111-1111-aaaaaaaaaaaa', 'John Doe', 'john@techcorp.com', '+911234567891', 'HR Manager'),
('eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee', 'aaaaaaaa-1111-1111-1111-aaaaaaaaaaaa', 'Jane Smith', 'jane@techcorp.com', '+911234567892', 'Finance Head');

-- 5. Bank Details
INSERT INTO bank_details (bank_account_id, account_holder_name, account_number, ifsc_code, bank_name, branch_name)
VALUES
('55555555-5555-5555-5555-555555555555', 'Employee One', '123456789012', 'IFSC001', 'Bank of America', 'Downtown'),
('55555555-2222-5555-2222-555555555555', 'Employee Two', '123456789010', 'IFSC002', 'Bank of America', 'Downtown');

-- 6. Employee
INSERT INTO employee (
    employee_id, user_id, client_id, first_name, last_name, personal_email, company_email,
    designation, date_of_birth, date_of_joining, rate_card, pan_number, available_leaves,
    aadhar_number, bank_account_id, gender, marital_status, number_of_children
)
VALUES
(
'99999999-9999-9999-9999-999999999999',
'33333333-3333-3333-3333-333333333333',
'aaaaaaaa-1111-1111-1111-aaaaaaaaaaaa',
'Ravi', 'Kumar', 'ravi.personal@gmail.com', 'ravi@techcorp.com',
'SOFTWARE_ENGINEER', '1995-05-15', '2022-01-10', 50000, 'PANEMP001', 20,
'AADHAREMP001', '55555555-5555-5555-5555-555555555555', 'MALE', 'SINGLE', 0
),
(
'12399999-9999-9999-9999-999999999999',
'12333333-3333-3333-3333-333333333333',
'aaaaaaaa-1111-1111-1111-aaaaaaaaaaaa',
'Sandy', 'Kumar', 'sandy.personal@gmail.com', 'sandy@techcorp.com',
'SOFTWARE_ENGINEER', '1995-05-15', '2022-01-10', 50000, 'PANEMP001', 20,
'AADHAREMP001', '55555555-5555-5555-5555-555555555555', 'MALE', 'SINGLE', 0
);

-- ----------------------------
-- Salary
-- ----------------------------
INSERT INTO salary (salary_id, employee_id, salary_month, total_hours, gross_salary, deductions, net_salary, payment_status)
VALUES
('bbbbbbbb-bbbb-bbbb-bbbb-000000000003', '99999999-9999-9999-9999-999999999999', '2025-09-01', 160, 50000, 5000, 45000, 'UNPAID');

-- -----------------------------
-- Invoice
-- -----------------------------
INSERT INTO invoice (invoice_id, client_id, invoice_month, total_hours, total_amount, status)
VALUES
('aaaaaaaa-1111-aaaa-1111-aaaaaaaa1111', 'aaaaaaaa-1111-1111-1111-aaaaaaaaaaaa', '2024-09-01', 160, 8000, 'PENDING');

-- -----------------------------
-- Invoice Detail
-- -----------------------------
INSERT INTO invoice_detail (invoice_detail_id, invoice_id, salary_id, employee_id, hours_worked, rate_per_hour, amount)
VALUES
('bbbbbbbb-2222-bbbb-2222-bbbbbbbb2222', 'aaaaaaaa-1111-aaaa-1111-aaaaaaaa1111', 'bbbbbbbb-bbbb-bbbb-bbbb-000000000003', '99999999-9999-9999-9999-999999999999', 160, 50, 8000);

-- -----------------------------
-- Device Sessions
-- -----------------------------
INSERT INTO device_sessions (id, user_id, device_id, device_name, ip_address, user_agent, is_active)
VALUES
('cccccccc-3333-cccc-3333-cccccccc3333', '22222222-2222-2222-2222-222222222222', gen_random_uuid(), 'MacBook Pro', '192.168.1.10', 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7)', TRUE);

-- -----------------------------
-- Refresh Tokens
-- -----------------------------
INSERT INTO refresh_tokens (id, token, device_session_id, expires_at, is_active)
VALUES
('dddddddd-4444-dddd-4444-dddddddd4444', 'sample_refresh_token', 'cccccccc-3333-cccc-3333-cccccccc3333', '2025-12-31 23:59:59', TRUE);

-- -----------------------------
-- Admin
-- -----------------------------
INSERT INTO admin (admin_id, user_id, full_name, email, contact_number, address_id)
VALUES
('eeeeeeee-5555-eeee-5555-eeeeeeee5555', '11111111-1111-1111-1111-111111111111', 'Admin One', 'admin1@company.com', '+911112223334', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa');

-- ----------------------------
-- TIMESHEET
-- ----------------------------
INSERT INTO timesheets (timesheet_id, employee_id, client_id,work_date, hours_worked,task_name, task_description, status)
VALUES
('a1b2c3d4-e5f6-7890-1234-56789abcdef0','99999999-9999-9999-9999-999999999999','aaaaaaaa-1111-1111-1111-aaaaaaaaaaaa','2021-05-09',8.00,'Fix bugs','Worked on frontend bug fixes and API integration','SUBMITTED'),
('123e4567-e89b-12d3-a456-426614174000','99999999-9999-9999-9999-999999999999','aaaaaaaa-1111-1111-1111-aaaaaaaaaaaa','2021-05-10',7.50,'Check code quality','Worked on backend bug fixes and code quality','APPROVED');

-- ----------------------------
-- Projects
-- ----------------------------
INSERT INTO projects (project_id, client_id, employee_id, project_name, start_date, end_date)
VALUES
('123e4567-e89b-12d3-a126-126614000001','aaaaaaaa-1111-1111-1111-aaaaaaaaaaaa','99999999-9999-9999-9999-999999999999','ICICI Bank Project','2022-11-12','2023-03-16'),
('123e4567-e89b-12d3-a126-126614000007','aaaaaaaa-1111-1111-1111-aaaaaaaaaaaa','99999999-9999-9999-9999-999999999999','RBI Bank Project','2023-07-23','2023-12-11');
