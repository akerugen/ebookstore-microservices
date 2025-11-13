CREATE TABLE IF NOT EXISTS users
(
    id            BIGSERIAL PRIMARY KEY,
    username      VARCHAR(50)  NOT NULL UNIQUE, -- бизнес-ключ (как в credentials в auth-service)
    email         VARCHAR(100) NOT NULL UNIQUE, -- бизнес-ключ (как в credentials в auth-service)
    first_name    VARCHAR(50),
    last_name     VARCHAR(50),
    date_of_birth DATE,
    created_at    TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at    TIMESTAMP
);

CREATE INDEX idx_users_username ON users (username);
CREATE INDEX idx_users_email ON users (email);

COMMENT ON TABLE users IS 'Профиль пользователя - бизнес-данные';
COMMENT ON COLUMN users.username IS 'Уникальное имя пользователя (бизнес-ключ)';
COMMENT ON COLUMN users.email IS 'Email пользователя (бизнес-ключ)';
COMMENT ON COLUMN users.first_name IS 'Имя пользователя';
COMMENT ON COLUMN users.last_name IS 'Фамилия пользователя';
COMMENT ON COLUMN users.date_of_birth IS 'Дата рождения пользователя';
COMMENT ON COLUMN users.created_at IS 'Дата создания профиля';
COMMENT ON COLUMN users.updated_at IS 'Дата изменения профиля';
