CREATE TABLE employee_employment_details (
    employment_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    employee_id UUID NOT NULL REFERENCES employee(employee_id) ON DELETE CASCADE,
    notice_period_duration VARCHAR(20),           -- e.g., '60 Days'
    probation_applicable BOOLEAN DEFAULT FALSE,
    probation_duration VARCHAR(20),               -- e.g., '3 months'
    probation_notice_period VARCHAR(20),
    bond_applicable BOOLEAN DEFAULT FALSE,
    bond_duration VARCHAR(20),                    -- e.g., '2 years'
    working_model VARCHAR(20),                    -- ONSITE / HYBRID / REMOTE
    shift_timing VARCHAR(50),                     -- GENERAL / US SHIFT
    department VARCHAR(50),
    date_of_confirmation DATE,
    location VARCHAR(100),
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE employee_statutory_details (
    statutory_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    employee_id UUID NOT NULL REFERENCES employee(employee_id) ON DELETE CASCADE,
    passport_number VARCHAR(20),
    tax_regime VARCHAR(10),                -- OLD / NEW
    pf_uan_number VARCHAR(50),
    esi_number VARCHAR(50),
    ssn_number VARCHAR(50),                -- for international hires
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE employee_insurance_details (
    insurance_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    employee_id UUID NOT NULL REFERENCES employee(employee_id) ON DELETE CASCADE,
    policy_number VARCHAR(50),
    provider_name VARCHAR(100),
    coverage_start DATE,
    coverage_end DATE,
    nominee_name VARCHAR(100),
    nominee_relation VARCHAR(50),
    nominee_contact VARCHAR(20),
    group_insurance BOOLEAN DEFAULT FALSE,
    other_benefits JSONB,                     -- e.g., {"Food Coupons": "Yes", "LTA": "Yes"}
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE employee_additional_details (
    hr_admin_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    employee_id UUID NOT NULL REFERENCES employee(employee_id) ON DELETE CASCADE,
    offer_letter_url VARCHAR(255),
    contract_url VARCHAR(255),
    tax_declaration_form_url VARCHAR(255),
    work_permit_url VARCHAR(255),
    background_check_status VARCHAR(50) DEFAULT 'PENDING', -- CLEARED / PENDING
    remarks TEXT,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE employee_equipment (
    equipment_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    employee_id UUID NOT NULL REFERENCES employee(employee_id) ON DELETE CASCADE,
    equipment_type VARCHAR(50),                 -- Laptop, Phone
    serial_number VARCHAR(100),
    issued_date DATE,
    returned_date DATE
);



