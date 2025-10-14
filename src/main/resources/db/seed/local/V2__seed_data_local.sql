-- -----------------------------
-- SEED DATA
-- -----------------------------

-- 1. Users
INSERT INTO users (user_id, user_name, company_email, password, role)
VALUES
('11111111-1111-1111-1111-111111111111', 'AdminUser', 'admin@company.com', 'adminpass', 'ADMIN'),
('22222222-2222-2222-2222-222222222222', 'ClientUser', 'client@company.com', 'clientpass', 'CLIENT'),
('33333333-3333-3333-3333-333333333333', 'EmployeeUser', 'employee@company.com', 'employeepass', 'EMPLOYEE');

-- 2. Address
INSERT INTO address (address_id, house_no, street_name, city, state, country, pincode)
VALUES
('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', '101', 'MG Road', 'Bangalore', 'Karnataka', 'India', '560001'),
('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', '202', 'Park Street', 'Kolkata', 'West Bengal', 'India', '700016'),
('cccccccc-cccc-cccc-cccc-cccccccccccc', '303', 'Marine Drive', 'Mumbai', 'Maharashtra', 'India', '400020');

-- 3. Clients
INSERT INTO client (client_id, user_id, company_name, contact_number, email, gst, tan_number, currency, pan_number)
VALUES
('aaaaaaaa-1111-1111-1111-aaaaaaaaaaaa', '22222222-2222-2222-2222-222222222222', 'TechCorp', '+911234567890', 'contact@techcorp.com', 'GST12345', 'TAN12345', 'INR', 'PAN12345');

-- 4. Client POCs
INSERT INTO client_poc (poc_id, client_id, name, email, contact_number, designation)
VALUES
('dddddddd-dddd-dddd-dddd-dddddddddddd', 'aaaaaaaa-1111-1111-1111-aaaaaaaaaaaa', 'John Doe', 'john@techcorp.com', '+911234567891', 'HR Manager'),
('eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee', 'aaaaaaaa-1111-1111-1111-aaaaaaaaaaaa', 'Jane Smith', 'jane@techcorp.com', '+911234567892', 'Finance Head');

-- 5. Bank Details
INSERT INTO bank_details (bank_account_id, account_holder_name, account_number, ifsc_code, bank_name, branch_name)
VALUES
('ffffffff-ffff-ffff-ffff-ffffffffffff', 'Employee User', '1234567890', 'IFSC0001', 'State Bank', 'Main Branch');

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
'AADHAREMP001', 'ffffffff-ffff-ffff-ffff-ffffffffffff', 'MALE', 'SINGLE', 0
);

-- 7. Admin
INSERT INTO admin (admin_id, user_id, full_name, email, contact_number, address_id)
VALUES
('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaa0001', '11111111-1111-1111-1111-111111111111', 'Admin One', 'admin1@company.com', '+911112223334', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa');

-- 8. Projects
INSERT INTO projects (project_id, client_id, employee_id, project_name, start_date, end_date)
VALUES
('aaaaaaaa-aaaa-aaaa-aaaa-000000000001', 'aaaaaaaa-1111-1111-1111-aaaaaaaaaaaa', '99999999-9999-9999-9999-999999999999', 'Project Alpha', '2025-01-01', '2025-12-31');
