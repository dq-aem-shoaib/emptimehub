

-- 2. Add partial_day flag
ALTER TABLE employee_leave
ADD COLUMN partial_day BOOLEAN DEFAULT FALSE;

-- 3. Add leave_duration (auto-calculated)
ALTER TABLE employee_leave
ADD COLUMN leave_duration NUMERIC(4,1);

-- 4. Add attachment_url as mandatory VARCHAR
ALTER TABLE employee_leave
ADD COLUMN attachment_url VARCHAR(255);

-- 5. Add withdrawn flag (employee withdraws leave)
ALTER TABLE employee_leave
ADD COLUMN withdrawn BOOLEAN DEFAULT FALSE;

-- 6. Add policy violation fields
ALTER TABLE employee_leave
ADD COLUMN policy_violation BOOLEAN DEFAULT FALSE,
ADD COLUMN violation_reason TEXT;

ALTER TABLE employee_leave
ADD COLUMN notice_period_violation BOOLEAN DEFAULT FALSE;