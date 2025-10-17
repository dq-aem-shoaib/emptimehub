-- -----------------------------
-- SEED DATA
-- -----------------------------

-- 1. Users
INSERT INTO users (user_id, user_name, company_email, password, role)
VALUES
('11111111-1111-1111-1111-111111111111', 'AdminUser', 'admin@company.com',
 '$2a$12$t1Gb08wuTExFudX1GmOz3OBnlXliq8F.U.xIcqiFY/k41jjxD6P2O', 'ADMIN'),
('22222222-2222-2222-2222-222222222222', 'ClientUser', 'client@company.com',
 '$2a$12$t1Gb08wuTExFudX1GmOz3OBnlXliq8F.U.xIcqiFY/k41jjxD6P2O', 'CLIENT'),
('33333333-3333-3333-3333-333333333333', 'EmployeeUser', 'employee@company.com',
 '$2a$12$t1Gb08wuTExFudX1GmOz3OBnlXliq8F.U.xIcqiFY/k41jjxD6P2O', 'EMPLOYEE');

-- 2. Address
INSERT INTO address (address_id, house_no, street_name, city, state, country, pincode)
VALUES
('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', '101', 'MG Road', 'Bangalore', 'Karnataka', 'India', '560001'),
('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', '202', 'Park Street', 'Kolkata', 'West Bengal', 'India', '700016'),
('cccccccc-cccc-cccc-cccc-cccccccccccc', '303', 'Marine Drive', 'Mumbai', 'Maharashtra', 'India', '400020');

-- 3. Clients
INSERT INTO client (client_id, user_id, company_name, contact_number, email, gst, tan_number, currency, pan_number, status)
VALUES
('aaaaaaaa-1111-1111-1111-aaaaaaaaaaaa', '22222222-2222-2222-2222-222222222222', 'TechCorp', '+911234567890', 'contact@techcorp.com', 'GST12345', 'TAN12345', 'INR', 'PAN12345', 'ACTIVE');

-- 4. Client POCs
INSERT INTO client_poc (poc_id, client_id, name, email, contact_number, designation, status)
VALUES
('dddddddd-dddd-dddd-dddd-dddddddddddd', 'aaaaaaaa-1111-1111-1111-aaaaaaaaaaaa', 'John Doe', 'john@techcorp.com', '+911234567891', 'HR Manager', 'ACTIVE'),
('eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee', 'aaaaaaaa-1111-1111-1111-aaaaaaaaaaaa', 'Jane Smith', 'jane@techcorp.com', '+911234567892', 'Finance Head', 'ACTIVE');

-- 5. Bank Details
INSERT INTO bank_details (bank_account_id, account_holder_name, account_number, ifsc_code, bank_name, branch_name)
VALUES
('ffffffff-ffff-ffff-ffff-ffffffffffff', 'Employee User', '1234567890', 'IFSC0001', 'State Bank', 'Main Branch');

-- Seed for Employee table with employment_type
INSERT INTO employee (
    employee_id, user_id, client_id, first_name, last_name, personal_email, company_email,
    designation, employment_type, date_of_birth, date_of_joining, rate_card, pan_number, available_leaves,
    aadhar_number, bank_account_id, gender, marital_status, number_of_children,
    employee_photo_url, pan_card_url, aadhar_card_url, bank_passbook_url,
    tenth_cft_url, inter_cft_url, degree_cft_url, post_graduation_cft_url, status
)
VALUES
-- Employee 1
(
    '99999999-9999-9999-9999-999999999999',
    '33333333-3333-3333-3333-333333333333',
    'aaaaaaaa-1111-1111-1111-aaaaaaaaaaaa',
    'Ravi', 'Kumar', 'ravi.personal@gmail.com', 'ravi@techcorp.com',
    'SOFTWARE_ENGINEER', 'FULLTIME',
    '1995-05-15', '2022-01-10', 50000, 'PANEMP001', 20,
    'AADHAREMP001', 'ffffffff-ffff-ffff-ffff-ffffffffffff', 'MALE', 'SINGLE', 0,
    NULL, NULL, NULL, NULL,
    NULL, NULL, NULL, NULL, 'ACTIVE'
);



-- 7. Employee Address (linking table)
INSERT INTO employee_address (employee_id, address_id, address_type)
VALUES
('99999999-9999-9999-9999-999999999999', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'PERMANENT');

-- 8. Admin
INSERT INTO admin (admin_id, user_id, full_name, email, contact_number, address_id)
VALUES
('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaa0001', '11111111-1111-1111-1111-111111111111', 'Admin One', 'admin1@company.com', '+911112223334', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa');

-- 9. Projects
INSERT INTO projects (project_id, client_id, employee_id, project_name, start_date, end_date)
VALUES
('aaaaaaaa-aaaa-aaaa-aaaa-000000000001', 'aaaaaaaa-1111-1111-1111-aaaaaaaaaaaa', '99999999-9999-9999-9999-999999999999', 'Project Alpha', '2025-01-01', '2025-12-31');

-- 10. Employee Leave
INSERT INTO employee_leave (leave_id, employee_id, approval_id, leave_type, from_date, to_date, subject, context, status, admin_comment, working_days, holidays)
VALUES
('eeeeeeee-eeee-eeee-eeee-000000000001', '99999999-9999-9999-9999-999999999999', NULL, 'ANNUAL', '2025-12-20', '2025-12-25', 'Vacation', 'Going on leave', 'PENDING', NULL, 5, 0);

-- 11. Holidays
INSERT INTO holidays (holiday_id, holiday_date, name, type, description)
VALUES
('ffffffff-ffff-ffff-ffff-000000000001', '2025-01-26', 'Republic Day', 'NATIONAL', 'National holiday in India');

-- 12. Timesheets
INSERT INTO timesheets (timesheet_id, employee_id, client_id, work_date, hours_worked, task_name, task_description, status)
VALUES
('aaaaaaaa-aaaa-aaaa-aaaa-000000000002', '99999999-9999-9999-9999-999999999999', 'aaaaaaaa-1111-1111-1111-aaaaaaaaaaaa', '2025-10-10', 8, 'Development', 'Worked on module X', 'SUBMITTED');

-- 13. Salary
INSERT INTO salary (salary_id, employee_id, salary_month, total_hours, gross_salary, deductions, net_salary, payment_status)
VALUES
('bbbbbbbb-bbbb-bbbb-bbbb-000000000003', '99999999-9999-9999-9999-999999999999', '2025-09-01', 160, 50000, 5000, 45000, 'UNPAID');

-- 14. Invoice
INSERT INTO invoice (invoice_id, client_id, invoice_month, total_hours, total_amount, status)
VALUES
('cccccccc-cccc-cccc-cccc-000000000004', 'aaaaaaaa-1111-1111-1111-aaaaaaaaaaaa', '2025-09-01', 160, 45000, 'PENDING');

-- 15. Invoice Detail
INSERT INTO invoice_detail (invoice_detail_id, invoice_id, salary_id, employee_id, hours_worked, rate_per_hour, amount)
VALUES
('dddddddd-dddd-dddd-dddd-000000000005', 'cccccccc-cccc-cccc-cccc-000000000004', 'bbbbbbbb-bbbb-bbbb-bbbb-000000000003', '99999999-9999-9999-9999-999999999999', 160, 281.25, 45000);
