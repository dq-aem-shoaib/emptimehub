-- 1. Drop the old leave_duration column (if it exists)
ALTER TABLE employee_leave
DROP COLUMN leave_duration;

-- 2. Rename working_days → leave_duration
ALTER TABLE employee_leave
RENAME COLUMN working_days TO leave_duration;

