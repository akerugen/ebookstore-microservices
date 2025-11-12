CREATE TABLE IF NOT EXISTS credentials (
                                           id BIGSERIAL PRIMARY KEY,
                                           user_id BIGINT NOT NULL UNIQUE,
                                           username VARCHAR(50) NOT NULL UNIQUE,
                                           email VARCHAR(100) NOT NULL UNIQUE,
                                           password VARCHAR(255) NOT NULL,
                                           created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                           updated_at TIMESTAMP,
                                           is_active BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE INDEX idx_credentials_username ON credentials(username);
CREATE INDEX idx_credentials_email ON credentials(email);
CREATE INDEX idx_credentials_user_id ON credentials(user_id);
