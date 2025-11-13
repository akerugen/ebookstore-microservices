CREATE TABLE IF NOT EXISTS credentials
(
    id                    BIGSERIAL PRIMARY KEY,
    username              VARCHAR(50)  NOT NULL UNIQUE,
    email                 VARCHAR(100) NOT NULL UNIQUE,
    password        VARCHAR(255) NOT NULL,
    role                  VARCHAR(50)  NOT NULL DEFAULT 'USER',
    is_active             BOOLEAN      NOT NULL DEFAULT TRUE,
    failed_login_attempts INT                   DEFAULT 0,
    last_login            TIMESTAMP,
    created_at            TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at            TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_credentials_username ON credentials (username);
CREATE INDEX IF NOT EXISTS idx_credentials_email ON credentials (email);
CREATE INDEX IF NOT EXISTS idx_credentials_role ON credentials (role);

COMMENT ON TABLE credentials IS 'Учётные данные для аутентификации';
COMMENT ON COLUMN credentials.username IS 'Уникальное имя пользователя (бизнес-ключ)';
COMMENT ON COLUMN credentials.email IS 'Email пользователя (бизнес-ключ)';
COMMENT ON COLUMN credentials.password IS 'BCrypt хеш пароля';
COMMENT ON COLUMN credentials.role IS 'Роль: USER, ADMIN, SUPER_USER';
COMMENT ON COLUMN credentials.is_active IS 'Статус пользователя: активен или неактивен';
COMMENT ON COLUMN credentials.failed_login_attempts IS 'Количество неудачных попыток входа';
COMMENT ON COLUMN credentials.last_login IS 'Последний вход пользователя';
COMMENT ON COLUMN credentials.created_at IS 'Дата создания учетных данных';
COMMENT ON COLUMN credentials.updated_at IS 'Дата изменения учетных данных';
