MERGE INTO users (
    user_id,
    email,
    password,
    secondary_email,
    phone_number,
    created_at,
    last_login,
    role
) KEY (email) VALUES (
    '00000000-0000-0000-0000-000000000001',
    'admin@example.com',
    '$2a$10$pXlBsH5mRgr8KlL8aEOg3eLtB7BOIjlb5LfPpcO0cFg3fzL7sgFcK',
    NULL,
    NULL,
    CURRENT_TIMESTAMP,
    NULL,
    'ADMIN'
);
