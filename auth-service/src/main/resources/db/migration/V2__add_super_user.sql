INSERT INTO credentials (username, email, password, role, is_active, failed_login_attempts, created_at)
VALUES (
           'superuser',
           'superuser@ebookstore.system',
           '$2y$10$0ce55AhseUUslVuCMb9sqeCleEhTGzF6z4FngXTjxXzAXWuoxiLZC',  -- BCrypt хеш "SuperuserPass123!"
           'SUPER_USER',
           true,
           0,
           NOW()
       )
ON CONFLICT (username) DO NOTHING;  -- если уже существует, не вставляем

SELECT * FROM credentials WHERE username = 'superuser';