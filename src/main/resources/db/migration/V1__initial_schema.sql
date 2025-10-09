-- Enable extension for UUID generation
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- -----------------------------
-- Table: user
-- -----------------------------
CREATE TABLE users (
    user_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL CHECK (role IN ('ADMIN','EMPLOYEE','CLIENT')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);


-- =====================================
-- CLIENT TABLE
-- =====================================
CREATE TABLE client (
    client_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
    company_name VARCHAR(255) NOT NULL,
    contact_number VARCHAR(20),
    email VARCHAR(255),
    address TEXT,
    pan_number VARCHAR(20),
    status VARCHAR(20) DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE','INACTIVE')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
-- -----------------------------
-- Table: employee
-- -----------------------------
CREATE TABLE employee (
    employee_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
    client_id UUID NULL REFERENCES "client"(client_id) ON DELETE SET NULL,
    full_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    contact_number VARCHAR(20),
    address TEXT,
    date_of_birth DATE,
    date_of_joining DATE,
    designation VARCHAR(100),
    rate_card DECIMAL(10,2) DEFAULT 0,
    pan_number VARCHAR(20),
    available_leaves NUMERIC(5),
    aadhar_number VARCHAR(20),
    account_number VARCHAR(30),
    status VARCHAR(20) DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE','INACTIVE')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);


-- =====================================
-- LEAVE TABLE
-- =====================================
CREATE TABLE employee_leave (
    leave_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    employee_id UUID NOT NULL REFERENCES employee(employee_id) ON DELETE CASCADE,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    type VARCHAR(20) CHECK (type IN ('PAID','UNPAID','SICK','CASUAL')),
    reason TEXT,
    status VARCHAR(20) DEFAULT 'PENDING' CHECK (status IN ('PENDING','APPROVED','REJECTED')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =====================================
-- TIMESHEET TABLE
-- =====================================
CREATE TABLE timesheet (
    timesheet_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    employee_id UUID NOT NULL REFERENCES employee(employee_id) ON DELETE CASCADE,
    client_id UUID NOT NULL REFERENCES client(client_id) ON DELETE CASCADE,
    work_date DATE NOT NULL,
    hours_worked DECIMAL(5,2) NOT NULL,
    task_description TEXT,
    status VARCHAR(20) DEFAULT 'SUBMITTED' CHECK (status IN ('SUBMITTED','APPROVED','REJECTED')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =====================================
-- SALARY TABLE
-- =====================================
CREATE TABLE salary (
    salary_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    employee_id UUID NOT NULL REFERENCES employee(employee_id) ON DELETE CASCADE,
    salary_month DATE NOT NULL,
    total_hours DECIMAL(6,2) DEFAULT 0,
    gross_salary DECIMAL(10,2) DEFAULT 0,
    deductions DECIMAL(10,2) DEFAULT 0,
    net_salary DECIMAL(10,2) DEFAULT 0,
    payment_status VARCHAR(20) DEFAULT 'UNPAID' CHECK (payment_status IN ('PAID','UNPAID')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =====================================
-- INVOICE TABLE
-- =====================================
CREATE TABLE invoice (
    invoice_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    client_id UUID NOT NULL REFERENCES client(client_id) ON DELETE CASCADE,
    invoice_month DATE NOT NULL,
    total_hours DECIMAL(10,2) DEFAULT 0,
    total_amount DECIMAL(12,2) DEFAULT 0,
    status VARCHAR(20) DEFAULT 'PENDING' CHECK (status IN ('PENDING','PAID')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =====================================
-- INVOICE_DETAIL TABLE
-- =====================================
CREATE TABLE invoice_detail (
    invoice_detail_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    invoice_id UUID NOT NULL REFERENCES invoice(invoice_id) ON DELETE CASCADE,
    salary_id UUID NOT NULL REFERENCES salary(salary_id) ON DELETE CASCADE,
    employee_id UUID NOT NULL REFERENCES employee(employee_id),
    hours_worked DECIMAL(6,2),
    rate_per_hour DECIMAL(10,2),
    amount DECIMAL(12,2),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE device_sessions (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
    device_id UUID NOT NULL,
    device_name VARCHAR(100),
    ip_address VARCHAR(45),
    user_agent TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_accessed TIMESTAMP,
    login_time TIMESTAMP,
    logout_time TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE
);

CREATE TABLE refresh_tokens (
    id UUID PRIMARY KEY,
    token VARCHAR(100) UNIQUE NOT NULL,
    device_session_id UUID NOT NULL REFERENCES device_sessions(id) ON DELETE CASCADE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP NOT NULL,
    is_active BOOLEAN DEFAULT TRUE

);
CREATE TABLE admin (
    admin_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users (user_id) ON DELETE CASCADE,
    full_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    contact_number VARCHAR(20),
    address TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
