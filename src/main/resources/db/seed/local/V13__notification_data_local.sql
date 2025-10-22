-- Manager: Shoaib (11111111-1111-1111-1111-111111111111)
-- Employee: Manoj (33333333-3333-3333-3333-333333333333)

INSERT INTO notifications (id, user_id, message, reference_id, read_status, created_at)
VALUES
('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', '11111111-1111-1111-1111-111111111111',
 'Manoj applied leave from 2025-10-25 to 2025-10-26 (2 days). Category: CASUAL',
 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', FALSE, NOW()),

('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', '33333333-3333-3333-3333-333333333333',
 'Your leave from 2025-10-25 to 2025-10-26 has been APPROVED by Shoaib.',
 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', FALSE, NOW());
