-- -----------------------------
-- Seed Users
-- -----------------------------
INSERT INTO "user" (user_id, name, email, password, role)
VALUES
(gen_random_uuid(), 'Admin User', 'admin@example.com', 'admin123', 'ADMIN'),
(gen_random_uuid(), 'Client One', 'client1@example.com', 'client123', 'CLIENT'),
(gen_random_uuid(), 'Client Two', 'client2@example.com', 'client123', 'CLIENT'),
(gen_random_uuid(), 'Employee One', 'employee1@example.com', 'emp123', 'EMPLOYEE'),
(gen_random_uuid(), 'Employee Two', 'employee2@example.com', 'emp123', 'EMPLOYEE');

-- -----------------------------
-- Seed Employees
-- Using UUIDs dynamically from the user table
-- -----------------------------
-- Employee One
INSERT INTO employee (
    employee_id, user_id, client_id, full_name, email, contact_number, address, date_of_birth, date_of_joining, designation, rate_per_hour, pan_number, aadhar_number, status
)
SELECT
    gen_random_uuid() as employee_id,
    u.user_id as user_id,
    c.user_id as client_id,
    'Employee One',
    'employee1@example.com',
    '9876543210',
    '123 Street, City',
    '1995-05-15',
    '2023-01-10',
    'Developer',
    500,
    'ABCDE1234F',
    '123456789012',
    'ACTIVE'
FROM "user" u
JOIN "user" c ON c.role='CLIENT'
WHERE u.role='EMPLOYEE'
LIMIT 1;

-- Employee Two
INSERT INTO employee (
    employee_id, user_id, client_id, full_name, email, contact_number, address, date_of_birth, date_of_joining, designation, rate_per_hour, pan_number, aadhar_number, status
)
SELECT
    gen_random_uuid() as employee_id,
    u.user_id as user_id,
    c.user_id as client_id,
    'Employee Two',
    'employee2@example.com',
    '9876543211',
    '456 Avenue, City',
    '1993-09-20',
    '2023-02-15',
    'Tester',
    400,
    'XYZAB5678K',
    '987654321098',
    'ACTIVE'
FROM "user" u
JOIN "user" c ON c.role='CLIENT'
WHERE u.role='EMPLOYEE'
LIMIT 1 OFFSET 1;
