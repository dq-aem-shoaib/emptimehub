-- 1. Rename existing leave_type to financial_type (PAID/UNPAID)
ALTER TABLE employee_leave
RENAME COLUMN leave_type TO financial_type;

-- 2. Ensure financial_type is NOT NULL
ALTER TABLE employee_leave
ALTER COLUMN financial_type SET NOT NULL;

-- 3. Add employee-selectable leave category
ALTER TABLE employee_leave
ADD COLUMN leave_category VARCHAR(50) NOT NULL DEFAULT 'CASUAL';

-- 4. Add check constraint to enforce allowed leave categories
ALTER TABLE employee_leave
ADD CONSTRAINT chk_leave_category
CHECK (leave_category IN ('SICK', 'CASUAL', 'PLANNED', 'UNPLANNED'));
