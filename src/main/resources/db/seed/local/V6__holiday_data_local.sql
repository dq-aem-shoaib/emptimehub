----------------------------------------------------------
--  Holiday_Calendar
----------------------------------------------------------
INSERT INTO holiday_calendar
(holiday_id, holiday_name, holiday_date, holiday_type, location_region, description, recurrence_rule, country_code, active_status, created_by)
VALUES
('abbbbbbb-1122-bbbb-1122-bbbbbbbb1122', 'Republic Day', '2025-01-26', 'PUBLIC', 'India', 'National holiday celebrating the Constitution of India.', 'ANNUAL', 'IN', TRUE, 'eeeeeeee-5555-eeee-5555-eeeeeeee5555'),
('abbbbbbb-1123-bbbb-1123-bbbbbbbb1123', 'Independence Day', '2025-08-15', 'PUBLIC', 'India', 'Commemorates India’s independence from British rule.', 'ANNUAL', 'IN', TRUE, 'eeeeeeee-5555-eeee-5555-eeeeeeee5555'),
('abbbbbbb-1124-bbbb-1124-bbbbbbbb1124', 'Labor Day', '2025-05-01', 'PUBLIC', 'Global', 'International Workers’ Day celebrated worldwide.', 'ANNUAL', NULL, TRUE, 'eeeeeeee-5555-eeee-5555-eeeeeeee5555'),
('abbbbbbb-1125-bbbb-1125-bbbbbbbb1125', 'Thanksgiving Day', '2025-11-27', 'REGIONAL', 'United States', 'Annual celebration of gratitude and harvest.', 'ANNUAL', 'US', TRUE, 'eeeeeeee-5555-eeee-5555-eeeeeeee5555'),
('abbbbbbb-1126-bbbb-1126-bbbbbbbb1126', 'Company Foundation Day', '2025-06-10', 'COMPANY_SPECIFIC', 'Global', 'Marks the anniversary of company founding.', 'ANNUAL', NULL, TRUE, 'eeeeeeee-5555-eeee-5555-eeeeeeee5555');

----------------------------------------------------------
--  Holiday_Scheme
----------------------------------------------------------
INSERT INTO holiday_scheme
(scheme_id, scheme_name, description, country_code, state, city, active_status, created_by)
VALUES
('aabbbbbb-1122-bbbb-1122-bbbbbbbb1122', 'India Holidays 2025', 'Official holidays observed across India.', 'IN', 'Karnataka', 'Bangalore', TRUE, 'eeeeeeee-5555-eeee-5555-eeeeeeee5555'),
('aabbbbbb-1122-bbbb-1122-bbbbbbbb1123', 'US Holidays 2025', 'Official holidays observed across the United States.', 'US', 'California', 'San Francisco', TRUE, 'eeeeeeee-5555-eeee-5555-eeeeeeee5555'),
('aabbbbbb-1122-bbbb-1122-bbbbbbbb1124', 'China Holidays 2025', 'Official holidays observed across China.', 'CN', 'Guangdong', 'Shenzhen', TRUE, 'eeeeeeee-5555-eeee-5555-eeeeeeee5555'),
('aabbbbbb-1122-bbbb-1122-bbbbbbbb1125', 'Company Global Holidays 2025', 'Company-wide holidays observed globally.', NULL, NULL, NULL, TRUE, 'eeeeeeee-5555-eeee-5555-eeeeeeee5555'),
('aabbbbbb-1122-bbbb-1122-bbbbbbbb1126', 'Europe Holidays 2025', 'Holidays observed across the European Union region.', 'EU', NULL, NULL, TRUE, 'eeeeeeee-5555-eeee-5555-eeeeeeee5555');



INSERT INTO holiday_scheme_mapping (id, scheme_id, holiday_id)
VALUES
('aaabbbbb-1122-bbbb-1122-bbbbbbbb1121', 'aabbbbbb-1122-bbbb-1122-bbbbbbbb1122', 'abbbbbbb-1122-bbbb-1122-bbbbbbbb1122'),
('aaabbbbb-1122-bbbb-1122-bbbbbbbb1122', 'aabbbbbb-1122-bbbb-1122-bbbbbbbb1122', 'abbbbbbb-1123-bbbb-1123-bbbbbbbb1123'),
('aaabbbbb-1122-bbbb-1122-bbbbbbbb1123', 'aabbbbbb-1122-bbbb-1122-bbbbbbbb1123', 'abbbbbbb-1125-bbbb-1125-bbbbbbbb1125'),
('aaabbbbb-1122-bbbb-1122-bbbbbbbb1124', 'aabbbbbb-1122-bbbb-1122-bbbbbbbb1125', 'abbbbbbb-1124-bbbb-1124-bbbbbbbb1124'),
('aaabbbbb-1122-bbbb-1122-bbbbbbbb1125', 'aabbbbbb-1122-bbbb-1122-bbbbbbbb1125', 'abbbbbbb-1126-bbbb-1126-bbbbbbbb1126');

INSERT INTO holiday_assignment
(assignment_id, scheme_id, employee_id, assigned_by, inheritance_flag)
VALUES
('aaaabbbb-1122-bbbb-1122-bbbbbbbb1121', 'aabbbbbb-1122-bbbb-1122-bbbbbbbb1122', '99999999-9999-9999-9999-999999999999', 'eeeeeeee-5555-eeee-5555-eeeeeeee5555', FALSE),
('aaaabbbb-1122-bbbb-1122-bbbbbbbb1122', 'aabbbbbb-1122-bbbb-1122-bbbbbbbb1125', '99999999-9999-9999-9999-999999999999', 'eeeeeeee-5555-eeee-5555-eeeeeeee5555', FALSE);

