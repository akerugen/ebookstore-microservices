INSERT INTO credentials (username, email, password, role, is_active, failed_login_attempts, created_at)
VALUES
    (
        'admin',
        'admin@ebookstore.system',
        '$2y$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcg7b3XeKeUxWdeS86E36DRj2De',  -- password123
        'ADMIN',
        true,
        0,
        NOW()
    ),
    (
        'admin2',
        'admin2@ebookstore.system',
        '$2y$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcg7b3XeKeUxWdeS86E36DRj2De',  -- password123
        'ADMIN',
        true,
        0,
        NOW()
    ),
    (
        'user',
        'user@ebookstore.system',
        '$2y$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcg7b3XeKeUxWdeS86E36DRj2De',  -- password123
        'USER',
        true,
        0,
        NOW()
    )
ON CONFLICT (username) DO NOTHING;