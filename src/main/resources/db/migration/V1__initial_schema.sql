-- Enable extension for UUID generation
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- -----------------------------
-- Table: users
-- -----------------------------
CREATE TABLE users (
    user_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_name VARCHAR(255) NOT NULL,
    company_email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL, -- was ENUM, now VARCHAR
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- -----------------------------
-- Table: address
-- -----------------------------
CREATE TABLE address (
    address_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    house_no VARCHAR(20) NOT NULL,
    street_name VARCHAR(100) NOT NULL,
    city VARCHAR(50) NOT NULL,
    state VARCHAR(50) NOT NULL,
    country VARCHAR(50) NOT NULL,
    pincode VARCHAR(20) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- -----------------------------
-- Table: client
-- -----------------------------
CREATE TABLE client (
    client_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
    company_name VARCHAR(255) NOT NULL,
    contact_number VARCHAR(20),
    email VARCHAR(255),
    gst VARCHAR(30),
    currency VARCHAR(10),
    tan_number VARCHAR(20),
    pan_number VARCHAR(20),
    status VARCHAR(20) DEFAULT 'ACTIVE', -- was ENUM, now VARCHAR
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- -----------------------------
-- Table: client_poc
-- -----------------------------
CREATE TABLE client_poc (
    poc_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    client_id UUID NOT NULL REFERENCES client(client_id) ON DELETE CASCADE,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    contact_number VARCHAR(20),
    designation VARCHAR(100),
    status VARCHAR(20) DEFAULT 'ACTIVE', -- was ENUM, now VARCHAR
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- -----------------------------
-- Table: bank_details
-- -----------------------------
CREATE TABLE bank_details (
    bank_account_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    account_holder_name VARCHAR(100) NOT NULL,
    account_number VARCHAR(30) NOT NULL UNIQUE,
    ifsc_code VARCHAR(20) NOT NULL,
    bank_name VARCHAR(100),
    branch_name VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- -----------------------------
-- EMPLOYEE TABLE
-- -----------------------------
CREATE TABLE employee (
    employee_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
    client_id UUID NULL REFERENCES client(client_id) ON DELETE SET NULL,
    -- Basic Info
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    personal_email VARCHAR(100) NOT NULL,
    company_email VARCHAR(100) NOT NULL,
    contact_number VARCHAR(20),
    alternate_contact_number VARCHAR(20),

    -- Employment Info
    designation VARCHAR(50), -- was ENUM
    employment_type VARCHAR(50) CHECK (employment_type IN ('CONTRACTOR','FREELANCER','FULLTIME')),
    reporting_manager_id UUID NULL REFERENCES employee(employee_id) ON DELETE SET NULL,
    date_of_birth DATE,
    date_of_joining DATE,
    rate_card DECIMAL(10,2) DEFAULT 0,
    pan_number VARCHAR(20),
    available_leaves NUMERIC(5),
    aadhar_number VARCHAR(20),
    bank_account_id UUID NULL REFERENCES bank_details(bank_account_id),

    -- Personal Info
    gender VARCHAR(10), -- was ENUM
    marital_status VARCHAR(20), -- was ENUM
    number_of_children INT DEFAULT 0 CHECK (number_of_children >= 0),
    employee_photo_url VARCHAR(255),

    -- Documents
    pan_card_url VARCHAR(255),
    aadhar_card_url VARCHAR(255),
    bank_passbook_url VARCHAR(255),
    tenth_cft_url VARCHAR(255),
    inter_cft_url VARCHAR(255),
    degree_cft_url VARCHAR(255),
    post_graduation_cft_url VARCHAR(255),

    -- Status
    status VARCHAR(20) DEFAULT 'ACTIVE', -- was ENUM

    -- Tracking
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- -----------------------------
-- EMPLOYEE_ADDRESS LINKING TABLE
-- -----------------------------
CREATE TABLE employee_address (
    employee_address_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    employee_id UUID NOT NULL REFERENCES employee(employee_id) ON DELETE CASCADE,
    address_id UUID NOT NULL REFERENCES address(address_id) ON DELETE CASCADE,
    address_type VARCHAR(20) NOT NULL-- was ENUM
);

-- -----------------------------
-- CLIENT_ADDRESS LINKING TABLE
-- -----------------------------
CREATE TABLE client_address (
    client_address_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    client_id UUID NOT NULL REFERENCES client(client_id) ON DELETE CASCADE,
    address_id UUID NOT NULL REFERENCES address(address_id) ON DELETE CASCADE,
    address_type VARCHAR(20) NOT NULL-- was ENUM
);

-- -----------------------------
-- Table: timesheet
-- -----------------------------
CREATE TABLE timesheets (
    timesheet_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    employee_id UUID NOT NULL REFERENCES employee(employee_id) ON DELETE CASCADE,
    client_id UUID NOT NULL REFERENCES client(client_id) ON DELETE CASCADE,
    work_date DATE NOT NULL,
    hours_worked DECIMAL(5,2) NOT NULL,
    task_name TEXT,
    task_description TEXT,
    status VARCHAR(20) DEFAULT 'SUBMITTED', -- was ENUM
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- -----------------------------
-- Table: salary
-- -----------------------------
CREATE TABLE salary (
    salary_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    employee_id UUID NOT NULL REFERENCES employee(employee_id) ON DELETE CASCADE,
    salary_month DATE NOT NULL,
    total_hours DECIMAL(6,2) DEFAULT 0,
    gross_salary DECIMAL(10,2) DEFAULT 0,
    deductions DECIMAL(10,2) DEFAULT 0,
    net_salary DECIMAL(10,2) DEFAULT 0,
    payment_status VARCHAR(20) DEFAULT 'UNPAID', -- was ENUM
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- -----------------------------
-- Table: invoice
-- -----------------------------
CREATE TABLE invoice (
    invoice_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    client_id UUID NOT NULL REFERENCES client(client_id) ON DELETE CASCADE,
    invoice_month DATE NOT NULL,
    total_hours DECIMAL(10,2) DEFAULT 0,
    total_amount DECIMAL(12,2) DEFAULT 0,
    status VARCHAR(20) DEFAULT 'PENDING', -- was ENUM
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- -----------------------------
-- Table: invoice_detail
-- -----------------------------
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

-- -----------------------------
-- Table: device_sessions
-- -----------------------------
CREATE TABLE device_sessions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
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

-- -----------------------------
-- Table: refresh_tokens
-- -----------------------------
CREATE TABLE refresh_tokens (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    token VARCHAR(100) UNIQUE NOT NULL,
    device_session_id UUID NOT NULL REFERENCES device_sessions(id) ON DELETE CASCADE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP NOT NULL,
    is_active BOOLEAN DEFAULT TRUE
);

-- -----------------------------
-- Table: admin
-- -----------------------------
CREATE TABLE admin (
    admin_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
    full_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    contact_number VARCHAR(20),
    address_id UUID NOT NULL REFERENCES address(address_id) ON DELETE CASCADE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- -----------------------------
-- Table: employee_leave
-- -----------------------------
CREATE TABLE employee_leave (
    leave_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    employee_id UUID NOT NULL REFERENCES employee(employee_id) ON DELETE CASCADE,
    approval_id UUID REFERENCES employee(employee_id) ON DELETE SET NULL,
    leave_type VARCHAR(50) NOT NULL,
    from_date DATE NOT NULL,
    to_date DATE NOT NULL,
    subject VARCHAR(255),
    context TEXT,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    manager_comment TEXT,
    working_days NUMERIC(2),
    holidays NUMERIC(2),
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW()
);

-- ----------------------------
-- Holidays
-- ----------------------------
-- -----------------------------
-- Table: holidays
-- -----------------------------
CREATE TABLE holidays (
    holiday_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    holiday_date DATE NOT NULL,
    name VARCHAR(100) NOT NULL,
    type VARCHAR(50) NOT NULL,
    description TEXT,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW()
);

-- ----------------------------
-- Projects
-- ----------------------------
CREATE TABLE projects (
    project_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    client_id UUID NOT NULL REFERENCES client(client_id) ON DELETE CASCADE,
    employee_id UUID NOT NULL REFERENCES employee(employee_id) ON DELETE CASCADE,
    project_name VARCHAR(200) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL
);
