-- =====================================
-- SEED DATA
-- =====================================

-- -----------------------------
-- Users
-- -----------------------------
INSERT INTO users (user_id, user_name, company_email, password, role)
VALUES
('11111111-1111-1111-1111-111111111111', 'Admin User', 'admin@example.com', 'adminpass', 'ADMIN'),
('22222222-2222-2222-2222-222222222222', 'Employee One', 'emp1@example.com', '$2a$12$dhiS8HyhfkOqWMOnAeeVA.VRMdvtUjvDKtUdgc5PRemcdfRbFHw8K', 'EMPLOYEE'),
('33333333-3333-3333-3333-333333333333', 'Client One', 'client1@example.com', 'clientpass', 'CLIENT');

-- -----------------------------
-- Address
-- -----------------------------
INSERT INTO address (address_id, house_no, street_name, city, state, country, pincode)
VALUES
('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', '101', 'Main Street', 'New York', 'NY', 'USA', '10001'),
('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', '202', 'Second Street', 'Los Angeles', 'CA', 'USA', '90001'),
('cccccccc-cccc-cccc-cccc-cccccccccccc', '303', 'Third Street', 'Chicago', 'IL', 'USA', '60601');

-- -----------------------------
-- Client
-- -----------------------------
INSERT INTO client (client_id, user_id, company_name, contact_number, email, address_id, gst, currency, pan_number, status)
VALUES
('44444444-4444-4444-4444-444444444444', '33333333-3333-3333-3333-333333333333', 'Client Company', '1234567890', 'contact@client.com', 'cccccccc-cccc-cccc-cccc-cccccccccccc', 'GST12345', 'USD', 'PAN12345', 'ACTIVE');

-- -----------------------------
-- Bank Details
-- -----------------------------
INSERT INTO bank_details (bank_account_id, account_holder_name, account_number, ifsc_code, bank_name, branch_name)
VALUES
('55555555-5555-5555-5555-555555555555', 'Employee One', '123456789012', 'IFSC001', 'Bank of America', 'Downtown');

-- -----------------------------
-- Employee
-- -----------------------------
INSERT INTO employee (employee_id, user_id, client_id, first_name, last_name, personal_email, company_email, contact_number, address_id, currency, date_of_birth, date_of_joining, designation, rate_card, pan_number, available_leaves, aadhar_number, bank_account_id, status)
VALUES
('66666666-6666-6666-6666-666666666666', '22222222-2222-2222-2222-222222222222', '44444444-4444-4444-4444-444444444444', 'John', 'Doe', 'john.doe@gmail.com', 'john.doe@company.com', '9876543210', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'USD', '1990-01-01', '2020-01-01', 'Developer', 50.00, 'PANEMP001', 20, 'AADHAREMP001', '55555555-5555-5555-5555-555555555555', 'ACTIVE');

-- -----------------------------
-- Employee Leave
-- -----------------------------
INSERT INTO employee_leave (leave_id, employee_id, start_date, end_date, type, reason, status)
VALUES
('77777777-7777-7777-7777-777777777777', '66666666-6666-6666-6666-666666666666', '2025-10-01', '2025-10-05', 'PAID', 'Vacation', 'APPROVED');

-- -----------------------------
-- Timesheet
-- -----------------------------
INSERT INTO timesheet (timesheet_id, employee_id, client_id, work_date, hours_worked, task_description, status)
VALUES
('88888888-8888-8888-8888-888888888888', '66666666-6666-6666-6666-666666666666', '44444444-4444-4444-4444-444444444444', '2025-10-08', 8.00, 'Worked on project X', 'SUBMITTED');

-- -----------------------------
-- Salary
-- -----------------------------
INSERT INTO salary (salary_id, employee_id, salary_month, total_hours, gross_salary, deductions, net_salary, payment_status)
VALUES
('99999999-9999-9999-9999-999999999999', '66666666-6666-6666-6666-666666666666', '2025-09-01', 160, 8000, 500, 7500, 'UNPAID');

-- -----------------------------
-- Invoice
-- -----------------------------
INSERT INTO invoice (invoice_id, client_id, invoice_month, total_hours, total_amount, status)
VALUES
('aaaaaaaa-1111-aaaa-1111-aaaaaaaa1111', '44444444-4444-4444-4444-444444444444', '2025-09-01', 160, 8000, 'PENDING');

-- -----------------------------
-- Invoice Detail
-- -----------------------------
INSERT INTO invoice_detail (invoice_detail_id, invoice_id, salary_id, employee_id, hours_worked, rate_per_hour, amount)
VALUES
('bbbbbbbb-2222-bbbb-2222-bbbbbbbb2222', 'aaaaaaaa-1111-aaaa-1111-aaaaaaaa1111', '99999999-9999-9999-9999-999999999999', '66666666-6666-6666-6666-666666666666', 160, 50, 8000);

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
('eeeeeeee-5555-eeee-5555-eeeeeeee5555', '11111111-1111-1111-1111-111111111111', 'Admin User', 'admin@example.com', '1234567890', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa');
-- ----------------------------
-- TIMESHEET
-- ----------------------------
INSERT INTO timesheet (timesheet_id,employee_id,client_id,work_date,hours_worked,task_name,task_description,status)
VALUES
  ('a1b2c3d4-e5f6-7890-1234-56789abcdef0','66666666-6666-6666-6666-666666666666','44444444-4444-4444-4444-444444444444','2025-10-09',8.00,'Fix bugs','Worked on frontend bug fixes and API integration','SUBMITTED'),
  ('123e4567-e89b-12d3-a456-426614174000','66666666-6666-6666-6666-666666666666','44444444-4444-4444-4444-444444444444','2025-10-10',7.50,'Check code quality','Worked on backend bug fixes and code quality','APPROVED');
