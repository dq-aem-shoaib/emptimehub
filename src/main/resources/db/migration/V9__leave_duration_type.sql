-- Alter column to allow decimals (e.g., up to 5 digits, 2 decimals)
ALTER TABLE employee_leave
ALTER COLUMN leave_duration TYPE NUMERIC(5,2);
