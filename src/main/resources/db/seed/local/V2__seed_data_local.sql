-- =====================================
-- Sample Seed Data
-- =====================================

-- -----------------------------
-- USERS
-- -----------------------------
INSERT INTO users (user_id, user_name, email, password, role)
VALUES
  ('11111111-1111-1111-1111-111111111111','Admin User','admin@example.com','adminpass','ADMIN'),
  ('22222222-2222-2222-2222-222222222222','Employee One','emp1@example.com','emp1pass','EMPLOYEE'),
  ('33333333-3333-3333-3333-333333333333','Employee Two','emp2@example.com','emp2pass','EMPLOYEE'),
  ('44444444-4444-4444-4444-444444444444','Client A','clienta@example.com','clientpass','CLIENT');

-- -----------------------------
-- CLIENTS
-- -----------------------------
INSERT INTO client (client_id, user_id, company_name, contact_number, email, address, pan_number)
VALUES
  ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa','44444444-4444-4444-4444-444444444444','Client A Pvt Ltd','9999999999','clienta@example.com','123, Business St, City','PANCLIENTA');

-- -----------------------------
-- EMPLOYEES
-- -----------------------------
INSERT INTO employee (employee_id, user_id, client_id, full_name, email, contact_number, address, date_of_birth, date_of_joining, designation, rate_card, pan_number, available_leaves, aadhar_number, account_number, status)
VALUES
  ('eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee','22222222-2222-2222-2222-222222222222','aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa','John Doe','emp1@example.com','9876543210','12, Employee St, City','1990-05-15','2022-01-01','Developer',500,'PANEMP1',20,'AADHAREMP1','ACC001','ACTIVE'),
  ('ffffffff-ffff-ffff-ffff-ffffffffffff','33333333-3333-3333-3333-333333333333','aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa','Priya Sharma','emp2@example.com','9876543211','34, Employee St, City','1992-07-20','2022-02-01','Designer',400,'PANEMP2',18,'AADHAREMP2','ACC002','ACTIVE');

-- -----------------------------
-- SALARY
-- -----------------------------
INSERT INTO salary (salary_id, employee_id, salary_month, total_hours, gross_salary, deductions, net_salary, payment_status)
VALUES
  ('10101010-1010-1010-1010-101010101010','eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee','2025-09-01',160,80000,0,80000,'UNPAID'),
  ('20202020-2020-2020-2020-202020202020','ffffffff-ffff-ffff-ffff-ffffffffffff','2025-09-01',150,60000,0,60000,'UNPAID');

-- -----------------------------
-- INVOICE
-- -----------------------------
INSERT INTO invoice (invoice_id, client_id, invoice_month, total_hours, total_amount, status)
VALUES
  ('55555555-5555-5555-5555-555555555555','aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa','2025-09-01',310,140000,'PENDING');

-- -----------------------------
-- INVOICE_DETAIL
-- -----------------------------
INSERT INTO invoice_detail (invoice_detail_id, invoice_id, salary_id, employee_id, hours_worked, rate_per_hour, amount)
VALUES
  ('aaaa1111-aaaa-1111-aaaa-1111aaaa1111','55555555-5555-5555-5555-555555555555','10101010-1010-1010-1010-101010101010','eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee',160,500,80000),
  ('bbbb2222-bbbb-2222-bbbb-2222bbbb2222','55555555-5555-5555-5555-555555555555','20202020-2020-2020-2020-202020202020','ffffffff-ffff-ffff-ffff-ffffffffffff',150,400,60000);

