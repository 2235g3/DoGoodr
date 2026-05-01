-- DoGoodr testing seed data for the deployment database.
-- Password for all seeded accounts: Password123!
-- The password value below is a BCrypt hash generated with Spring Security BCryptPasswordEncoder.

BEGIN;

-- One seeded admin account.
INSERT INTO users (user_id, email, password, secondary_email, phone_number, created_at, last_login, role)
VALUES (
    '00000000-0000-4000-8000-000000000001',
    'admin@dogoodr.test',
    '$2a$10$9fJSwKPqXiar5BjtlzhzSOSz.OiWv/Bfv4WOfziK6mx04PuoAShje',
    NULL,
    NULL,
    NOW(),
    NULL,
    'ADMIN'
)
ON CONFLICT (email) DO UPDATE
SET password = EXCLUDED.password,
    role = 'ADMIN';

-- Minimal organisation accounts/profiles required to own the seeded opportunities.
INSERT INTO users (user_id, email, password, secondary_email, phone_number, created_at, last_login, role)
VALUES
    ('00000000-0000-4000-8000-000000000101', 'kitchen@dogoodr.test', '$2a$10$9fJSwKPqXiar5BjtlzhzSOSz.OiWv/Bfv4WOfziK6mx04PuoAShje', NULL, NULL, NOW(), NULL, 'ORGANISATION'),
    ('00000000-0000-4000-8000-000000000102', 'greenstreets@dogoodr.test', '$2a$10$9fJSwKPqXiar5BjtlzhzSOSz.OiWv/Bfv4WOfziK6mx04PuoAShje', NULL, NULL, NOW(), NULL, 'ORGANISATION'),
    ('00000000-0000-4000-8000-000000000103', 'learning@dogoodr.test', '$2a$10$9fJSwKPqXiar5BjtlzhzSOSz.OiWv/Bfv4WOfziK6mx04PuoAShje', NULL, NULL, NOW(), NULL, 'ORGANISATION'),
    ('00000000-0000-4000-8000-000000000104', 'wellbeing@dogoodr.test', '$2a$10$9fJSwKPqXiar5BjtlzhzSOSz.OiWv/Bfv4WOfziK6mx04PuoAShje', NULL, NULL, NOW(), NULL, 'ORGANISATION')
ON CONFLICT (email) DO UPDATE
SET password = EXCLUDED.password,
    role = 'ORGANISATION';

INSERT INTO organisation_profiles (
    v_profile_id,
    user_id,
    display_name,
    profile_picture_url,
    account_type,
    description,
    contact_email,
    location,
    website_url,
    last_updated,
    verified
)
VALUES
    -- account_type is stored as the AccountType enum ordinal in the current production schema:
    -- PERSONAL=0, CHARITY=1, NGO=2, GOVERNMENT=3, COMMUNITY_GROUP=4, OTHER=5.
    ('00000000-0000-4000-8000-000000000201', '00000000-0000-4000-8000-000000000101', 'Surrey Community Kitchen', NULL, 1, 'Community meals, pantry support, and practical help for residents facing food insecurity.', 'kitchen@dogoodr.test', 'Guildford, Surrey', 'https://example.org/community-kitchen', NOW(), true),
    ('00000000-0000-4000-8000-000000000202', '00000000-0000-4000-8000-000000000102', 'Green Streets Guildford', NULL, 4, 'Local environmental projects focused on cleaner streets, biodiversity, and low-carbon neighbourhoods.', 'greenstreets@dogoodr.test', 'Guildford, Surrey', 'https://example.org/green-streets', NOW(), true),
    ('00000000-0000-4000-8000-000000000203', '00000000-0000-4000-8000-000000000103', 'Bright Futures Learning', NULL, 1, 'Tutoring, digital inclusion, and mentoring programmes for young people and adult learners.', 'learning@dogoodr.test', 'Woking, Surrey', 'https://example.org/bright-futures', NOW(), true),
    ('00000000-0000-4000-8000-000000000204', '00000000-0000-4000-8000-000000000104', 'Wellbeing Connect Surrey', NULL, 2, 'Social connection, mental wellbeing, and befriending support for isolated residents.', 'wellbeing@dogoodr.test', 'Surrey', 'https://example.org/wellbeing-connect', NOW(), true)
ON CONFLICT (user_id) DO UPDATE
SET display_name = EXCLUDED.display_name,
    account_type = EXCLUDED.account_type,
    description = EXCLUDED.description,
    contact_email = EXCLUDED.contact_email,
    location = EXCLUDED.location,
    website_url = EXCLUDED.website_url,
    last_updated = NOW(),
    verified = true;

WITH seed_tags(name) AS (
    VALUES
        ('community support'),
        ('food security'),
        ('homelessness support'),
        ('environment'),
        ('biodiversity'),
        ('sustainability'),
        ('education'),
        ('digital inclusion'),
        ('youth development'),
        ('health and wellbeing'),
        ('mental health'),
        ('older people'),
        ('accessibility'),
        ('advocacy'),
        ('events and fundraising'),
        ('languages')
)
INSERT INTO semantic_tags (name)
SELECT seed_tags.name
FROM seed_tags
WHERE NOT EXISTS (
    SELECT 1 FROM semantic_tags existing_tag
    WHERE LOWER(existing_tag.name) = LOWER(seed_tags.name)
);

WITH seed_labels(name, tag_name, required, type) AS (
    VALUES
        ('Food preparation', 'food security', false, 'SKILL'),
        ('Food bank support', 'food security', false, 'CAUSE'),
        ('Community meals', 'food security', false, 'CAUSE'),
        ('Homelessness outreach', 'homelessness support', false, 'CAUSE'),
        ('Kitchen hygiene', 'food security', true, 'SKILL'),
        ('Litter picking', 'environment', false, 'INTEREST'),
        ('Tree planting', 'biodiversity', false, 'INTEREST'),
        ('Wildlife conservation', 'biodiversity', false, 'CAUSE'),
        ('Climate action', 'sustainability', false, 'CAUSE'),
        ('Gardening', 'biodiversity', false, 'SKILL'),
        ('Tutoring', 'education', false, 'SKILL'),
        ('Mentoring', 'youth development', false, 'SKILL'),
        ('Youth work', 'youth development', false, 'CAUSE'),
        ('Digital skills', 'digital inclusion', false, 'SKILL'),
        ('Web design', 'digital inclusion', false, 'SKILL'),
        ('CV support', 'digital inclusion', false, 'SKILL'),
        ('Befriending', 'older people', false, 'SKILL'),
        ('Mental health support', 'mental health', false, 'CAUSE'),
        ('Active listening', 'mental health', false, 'SKILL'),
        ('First aid', 'health and wellbeing', true, 'SKILL'),
        ('Accessibility support', 'accessibility', false, 'CAUSE'),
        ('Event support', 'events and fundraising', false, 'SKILL'),
        ('Fundraising', 'events and fundraising', false, 'SKILL'),
        ('Campaigning', 'advocacy', false, 'SKILL'),
        ('Research', 'advocacy', false, 'SKILL'),
        ('English', 'languages', false, 'LANGUAGE'),
        ('Arabic', 'languages', false, 'LANGUAGE'),
        ('Spanish', 'languages', false, 'LANGUAGE'),
        ('Enhanced DBS', 'youth development', true, 'OTHER'),
        ('Safeguarding training', 'youth development', true, 'EDUCATION')
)
INSERT INTO label (name, semantic_tag_id, required, type)
SELECT seed_labels.name, semantic_tags.id, seed_labels.required, seed_labels.type
FROM seed_labels
JOIN semantic_tags ON LOWER(semantic_tags.name) = LOWER(seed_labels.tag_name)
WHERE NOT EXISTS (
    SELECT 1 FROM label existing_label
    WHERE LOWER(existing_label.name) = LOWER(seed_labels.name)
);

WITH seed_labels(name, tag_name, required, type) AS (
    VALUES
        ('Food preparation', 'food security', false, 'SKILL'),
        ('Food bank support', 'food security', false, 'CAUSE'),
        ('Community meals', 'food security', false, 'CAUSE'),
        ('Homelessness outreach', 'homelessness support', false, 'CAUSE'),
        ('Kitchen hygiene', 'food security', true, 'SKILL'),
        ('Litter picking', 'environment', false, 'INTEREST'),
        ('Tree planting', 'biodiversity', false, 'INTEREST'),
        ('Wildlife conservation', 'biodiversity', false, 'CAUSE'),
        ('Climate action', 'sustainability', false, 'CAUSE'),
        ('Gardening', 'biodiversity', false, 'SKILL'),
        ('Tutoring', 'education', false, 'SKILL'),
        ('Mentoring', 'youth development', false, 'SKILL'),
        ('Youth work', 'youth development', false, 'CAUSE'),
        ('Digital skills', 'digital inclusion', false, 'SKILL'),
        ('Web design', 'digital inclusion', false, 'SKILL'),
        ('CV support', 'digital inclusion', false, 'SKILL'),
        ('Befriending', 'older people', false, 'SKILL'),
        ('Mental health support', 'mental health', false, 'CAUSE'),
        ('Active listening', 'mental health', false, 'SKILL'),
        ('First aid', 'health and wellbeing', true, 'SKILL'),
        ('Accessibility support', 'accessibility', false, 'CAUSE'),
        ('Event support', 'events and fundraising', false, 'SKILL'),
        ('Fundraising', 'events and fundraising', false, 'SKILL'),
        ('Campaigning', 'advocacy', false, 'SKILL'),
        ('Research', 'advocacy', false, 'SKILL'),
        ('English', 'languages', false, 'LANGUAGE'),
        ('Arabic', 'languages', false, 'LANGUAGE'),
        ('Spanish', 'languages', false, 'LANGUAGE'),
        ('Enhanced DBS', 'youth development', true, 'OTHER'),
        ('Safeguarding training', 'youth development', true, 'EDUCATION')
)
UPDATE label
SET semantic_tag_id = semantic_tags.id,
    required = seed_labels.required,
    type = seed_labels.type
FROM seed_labels
JOIN semantic_tags ON LOWER(semantic_tags.name) = LOWER(seed_labels.tag_name)
WHERE LOWER(label.name) = LOWER(seed_labels.name);

WITH seed_links(tag_one, tag_two, weight) AS (
    VALUES
        ('community support', 'food security', 0.90),
        ('community support', 'homelessness support', 0.88),
        ('food security', 'homelessness support', 0.82),
        ('environment', 'biodiversity', 0.90),
        ('environment', 'sustainability', 0.86),
        ('biodiversity', 'sustainability', 0.78),
        ('education', 'digital inclusion', 0.84),
        ('education', 'youth development', 0.86),
        ('digital inclusion', 'accessibility', 0.72),
        ('health and wellbeing', 'mental health', 0.92),
        ('health and wellbeing', 'older people', 0.76),
        ('mental health', 'older people', 0.70),
        ('advocacy', 'events and fundraising', 0.68),
        ('community support', 'health and wellbeing', 0.74),
        ('languages', 'community support', 0.58)
)
INSERT INTO semantic_links (semantic_tag_one_id, semantic_tag_two_id, weight)
SELECT LEAST(tag_one.id, tag_two.id), GREATEST(tag_one.id, tag_two.id), seed_links.weight
FROM seed_links
JOIN semantic_tags tag_one ON LOWER(tag_one.name) = LOWER(seed_links.tag_one)
JOIN semantic_tags tag_two ON LOWER(tag_two.name) = LOWER(seed_links.tag_two)
ON CONFLICT (semantic_tag_one_id, semantic_tag_two_id) DO UPDATE
SET weight = EXCLUDED.weight;

INSERT INTO opportunities (
    opportunity_id,
    title,
    description,
    location,
    longitude,
    latitude,
    remote,
    status,
    min_age,
    start_date,
    end_date,
    recurring,
    availability,
    required_hours,
    capacity,
    date_created,
    last_updated,
    organisation_profile_id
)
VALUES
    ('10000000-0000-4000-8000-000000000001', 'Community Meal Prep Evening', 'Prepare and portion nutritious evening meals for residents using the community kitchen and local pantry network.', 'Guildford Community Centre', -0.5700, 51.2362, false, 'OPEN', 16, CURRENT_DATE + INTERVAL '3 days', CURRENT_DATE + INTERVAL '90 days', true, 'Weekday evenings', 3, 12, NOW(), NOW(), '00000000-0000-4000-8000-000000000201'),
    ('10000000-0000-4000-8000-000000000002', 'Weekend Food Bank Team', 'Sort donated goods, prepare parcels, and support friendly collection sessions for households referred to the food bank.', 'Guildford Food Hub', -0.5735, 51.2358, false, 'OPEN', 16, CURRENT_DATE + INTERVAL '5 days', CURRENT_DATE + INTERVAL '120 days', true, 'Saturday mornings', 4, 10, NOW(), NOW(), '00000000-0000-4000-8000-000000000201'),
    ('10000000-0000-4000-8000-000000000003', 'Street Support Outreach Pack Builder', 'Pack warm clothing, hygiene supplies, and food items for outreach teams supporting people experiencing homelessness.', 'Guildford', -0.5709, 51.2365, false, 'OPEN', 18, CURRENT_DATE + INTERVAL '7 days', CURRENT_DATE + INTERVAL '70 days', true, 'Tuesday afternoons', 2, 8, NOW(), NOW(), '00000000-0000-4000-8000-000000000201'),
    ('10000000-0000-4000-8000-000000000004', 'River Wey Clean-Up Crew', 'Join a supervised litter pick along the river path, recording waste hotspots and improving shared public spaces.', 'River Wey, Guildford', -0.5748, 51.2399, false, 'OPEN', 14, CURRENT_DATE + INTERVAL '10 days', CURRENT_DATE + INTERVAL '10 days', false, 'One-off weekend morning', 3, 25, NOW(), NOW(), '00000000-0000-4000-8000-000000000202'),
    ('10000000-0000-4000-8000-000000000005', 'Tree Planting and Habitat Care', 'Help plant native trees, mulch young saplings, and maintain wildlife-friendly areas in a neighbourhood green space.', 'Stoke Park, Guildford', -0.5655, 51.2494, false, 'OPEN', 16, CURRENT_DATE + INTERVAL '14 days', CURRENT_DATE + INTERVAL '60 days', true, 'Sunday mornings', 4, 20, NOW(), NOW(), '00000000-0000-4000-8000-000000000202'),
    ('10000000-0000-4000-8000-000000000006', 'Sustainable Living Event Steward', 'Support a community event with visitor welcome, stall setup, survey collection, and simple climate action signposting.', 'Guildford High Street', -0.5707, 51.2350, false, 'OPEN', 16, CURRENT_DATE + INTERVAL '21 days', CURRENT_DATE + INTERVAL '21 days', false, 'One-off Saturday', 5, 16, NOW(), NOW(), '00000000-0000-4000-8000-000000000202'),
    ('10000000-0000-4000-8000-000000000007', 'GCSE Maths Tutoring', 'Provide weekly small-group tutoring for learners who need calm, practical help building confidence before exams.', 'Woking Library', -0.5595, 51.3200, false, 'OPEN', 18, CURRENT_DATE + INTERVAL '4 days', CURRENT_DATE + INTERVAL '150 days', true, 'After school weekdays', 2, 6, NOW(), NOW(), '00000000-0000-4000-8000-000000000203'),
    ('10000000-0000-4000-8000-000000000008', 'Digital Skills Drop-In', 'Help residents use email, online forms, video calls, and basic device settings in an accessible weekly drop-in.', 'Remote and Woking', -0.5595, 51.3200, true, 'OPEN', 18, CURRENT_DATE + INTERVAL '6 days', CURRENT_DATE + INTERVAL '180 days', true, 'Weekday daytime or remote', 2, 10, NOW(), NOW(), '00000000-0000-4000-8000-000000000203'),
    ('10000000-0000-4000-8000-000000000009', 'Youth Career Mentor', 'Mentor young people exploring careers, applications, and interview confidence through structured monthly sessions.', 'Woking Youth Centre', -0.5580, 51.3190, false, 'OPEN', 21, CURRENT_DATE + INTERVAL '12 days', CURRENT_DATE + INTERVAL '180 days', true, 'Monthly evenings', 2, 8, NOW(), NOW(), '00000000-0000-4000-8000-000000000203'),
    ('10000000-0000-4000-8000-000000000010', 'Telephone Befriending Volunteer', 'Make regular friendly calls to isolated adults, helping people feel heard and connected to local support.', 'Remote', NULL, NULL, true, 'OPEN', 18, CURRENT_DATE + INTERVAL '2 days', CURRENT_DATE + INTERVAL '180 days', true, 'Flexible weekly', 1, 18, NOW(), NOW(), '00000000-0000-4000-8000-000000000204'),
    ('10000000-0000-4000-8000-000000000011', 'Wellbeing Walk Leader Assistant', 'Support gentle group walks by welcoming participants, encouraging conversation, and helping routes run smoothly.', 'Surrey Hills', -0.4380, 51.1900, false, 'OPEN', 18, CURRENT_DATE + INTERVAL '9 days', CURRENT_DATE + INTERVAL '120 days', true, 'Friday mornings', 2, 12, NOW(), NOW(), '00000000-0000-4000-8000-000000000204'),
    ('10000000-0000-4000-8000-000000000012', 'Community Mental Health Event Support', 'Help run a local wellbeing information evening, including check-in, room setup, signposting, and feedback collection.', 'Guildford', -0.5709, 51.2365, false, 'OPEN', 18, CURRENT_DATE + INTERVAL '28 days', CURRENT_DATE + INTERVAL '28 days', false, 'One-off evening', 4, 14, NOW(), NOW(), '00000000-0000-4000-8000-000000000204')
ON CONFLICT (opportunity_id) DO UPDATE
SET title = EXCLUDED.title,
    description = EXCLUDED.description,
    location = EXCLUDED.location,
    longitude = EXCLUDED.longitude,
    latitude = EXCLUDED.latitude,
    remote = EXCLUDED.remote,
    status = EXCLUDED.status,
    min_age = EXCLUDED.min_age,
    start_date = EXCLUDED.start_date,
    end_date = EXCLUDED.end_date,
    recurring = EXCLUDED.recurring,
    availability = EXCLUDED.availability,
    required_hours = EXCLUDED.required_hours,
    capacity = EXCLUDED.capacity,
    last_updated = NOW(),
    organisation_profile_id = EXCLUDED.organisation_profile_id;

DELETE FROM opportunity_label_links
WHERE opportunity_id IN (
    '10000000-0000-4000-8000-000000000001',
    '10000000-0000-4000-8000-000000000002',
    '10000000-0000-4000-8000-000000000003',
    '10000000-0000-4000-8000-000000000004',
    '10000000-0000-4000-8000-000000000005',
    '10000000-0000-4000-8000-000000000006',
    '10000000-0000-4000-8000-000000000007',
    '10000000-0000-4000-8000-000000000008',
    '10000000-0000-4000-8000-000000000009',
    '10000000-0000-4000-8000-000000000010',
    '10000000-0000-4000-8000-000000000011',
    '10000000-0000-4000-8000-000000000012'
);

WITH assignment(opportunity_id, label_name, weight) AS (
    VALUES
        ('10000000-0000-4000-8000-000000000001'::uuid, 'Food preparation', 1.00),
        ('10000000-0000-4000-8000-000000000001'::uuid, 'Community meals', 0.95),
        ('10000000-0000-4000-8000-000000000001'::uuid, 'Kitchen hygiene', 0.90),
        ('10000000-0000-4000-8000-000000000002'::uuid, 'Food bank support', 1.00),
        ('10000000-0000-4000-8000-000000000002'::uuid, 'Community meals', 0.75),
        ('10000000-0000-4000-8000-000000000002'::uuid, 'Event support', 0.45),
        ('10000000-0000-4000-8000-000000000003'::uuid, 'Homelessness outreach', 1.00),
        ('10000000-0000-4000-8000-000000000003'::uuid, 'Food bank support', 0.70),
        ('10000000-0000-4000-8000-000000000003'::uuid, 'Community meals', 0.65),
        ('10000000-0000-4000-8000-000000000004'::uuid, 'Litter picking', 1.00),
        ('10000000-0000-4000-8000-000000000004'::uuid, 'Climate action', 0.75),
        ('10000000-0000-4000-8000-000000000004'::uuid, 'Event support', 0.40),
        ('10000000-0000-4000-8000-000000000005'::uuid, 'Tree planting', 1.00),
        ('10000000-0000-4000-8000-000000000005'::uuid, 'Wildlife conservation', 0.90),
        ('10000000-0000-4000-8000-000000000005'::uuid, 'Gardening', 0.80),
        ('10000000-0000-4000-8000-000000000006'::uuid, 'Climate action', 1.00),
        ('10000000-0000-4000-8000-000000000006'::uuid, 'Event support', 0.95),
        ('10000000-0000-4000-8000-000000000006'::uuid, 'Campaigning', 0.60),
        ('10000000-0000-4000-8000-000000000007'::uuid, 'Tutoring', 1.00),
        ('10000000-0000-4000-8000-000000000007'::uuid, 'Mentoring', 0.75),
        ('10000000-0000-4000-8000-000000000007'::uuid, 'Safeguarding training', 0.90),
        ('10000000-0000-4000-8000-000000000007'::uuid, 'Enhanced DBS', 0.95),
        ('10000000-0000-4000-8000-000000000008'::uuid, 'Digital skills', 1.00),
        ('10000000-0000-4000-8000-000000000008'::uuid, 'Accessibility support', 0.80),
        ('10000000-0000-4000-8000-000000000008'::uuid, 'CV support', 0.55),
        ('10000000-0000-4000-8000-000000000009'::uuid, 'Mentoring', 1.00),
        ('10000000-0000-4000-8000-000000000009'::uuid, 'Youth work', 0.95),
        ('10000000-0000-4000-8000-000000000009'::uuid, 'CV support', 0.65),
        ('10000000-0000-4000-8000-000000000009'::uuid, 'Enhanced DBS', 0.95),
        ('10000000-0000-4000-8000-000000000010'::uuid, 'Befriending', 1.00),
        ('10000000-0000-4000-8000-000000000010'::uuid, 'Active listening', 0.90),
        ('10000000-0000-4000-8000-000000000010'::uuid, 'Mental health support', 0.70),
        ('10000000-0000-4000-8000-000000000011'::uuid, 'Mental health support', 0.80),
        ('10000000-0000-4000-8000-000000000011'::uuid, 'Befriending', 0.70),
        ('10000000-0000-4000-8000-000000000011'::uuid, 'First aid', 0.55),
        ('10000000-0000-4000-8000-000000000012'::uuid, 'Mental health support', 1.00),
        ('10000000-0000-4000-8000-000000000012'::uuid, 'Event support', 0.85),
        ('10000000-0000-4000-8000-000000000012'::uuid, 'Active listening', 0.75),
        ('10000000-0000-4000-8000-000000000012'::uuid, 'Accessibility support', 0.60)
),
chosen_labels AS (
    SELECT LOWER(name) AS label_name, MIN(id) AS label_id
    FROM label
    GROUP BY LOWER(name)
)
INSERT INTO opportunity_label_links (opportunity_id, label_id, weight)
SELECT assignment.opportunity_id, chosen_labels.label_id, assignment.weight
FROM assignment
JOIN chosen_labels ON chosen_labels.label_name = LOWER(assignment.label_name)
ON CONFLICT (opportunity_id, label_id) DO UPDATE
SET weight = EXCLUDED.weight;

SELECT setval(pg_get_serial_sequence('semantic_tags', 'id'), COALESCE((SELECT MAX(id) FROM semantic_tags), 1), true);
SELECT setval(pg_get_serial_sequence('label', 'id'), COALESCE((SELECT MAX(id) FROM label), 1), true);

COMMIT;
