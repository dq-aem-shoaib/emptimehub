ALTER TABLE employee_leave
ADD COLUMN approver_name VARCHAR(255);
ALTER TABLE employee_leave
RENAME COLUMN manager_comment TO approver_comment;
