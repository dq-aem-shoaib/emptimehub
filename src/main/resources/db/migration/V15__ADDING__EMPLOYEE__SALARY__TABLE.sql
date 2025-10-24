CREATE TABLE employee_salary (
    salary_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    employee_id UUID NOT NULL REFERENCES employee(employee_id),
    basic_pay NUMERIC(12,2) NOT NULL,
    pay_type VARCHAR(20) NOT NULL,          -- MONTHLY, HOURLY, CONTRACT
    standard_hours NUMERIC(5,2) DEFAULT 160,
    bank_account_number VARCHAR(50),
    ifsc_code VARCHAR(20),
    pay_class VARCHAR(50),
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);


CREATE TABLE employee_allowances (
    allowance_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    salary_id UUID NOT NULL REFERENCES employee_salary(salary_id),
    allowance_type VARCHAR(50) NOT NULL,   -- e.g., HRA, Transport, Medical, Special
    amount NUMERIC(12,2) NOT NULL
);


CREATE TABLE employee_deductions (
    deduction_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    salary_id UUID NOT NULL REFERENCES employee_salary(salary_id),
    deduction_type VARCHAR(50) NOT NULL,   -- e.g., PF, ESI, TDS, Loan, Penalty
    amount NUMERIC(12,2) NOT NULL
);

