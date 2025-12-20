CREATE TABLE IF NOT EXISTS notifications
(
    id         BIGSERIAL PRIMARY KEY,
    user_id    BIGINT       NOT NULL,
    type       VARCHAR(50)  NOT NULL, -- BOOK_CREATED, BOOK_UPDATED, BOOK_DELETED, PRICE_CHANGED
    title      VARCHAR(255) NOT NULL,
    message    TEXT         NOT NULL,
    book_id    BIGINT,                -- может быть null, если книга удалена
    is_read    BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_notifications_user_id ON notifications (user_id);
CREATE INDEX idx_notifications_user_id_is_read ON notifications (user_id, is_read);
CREATE INDEX idx_notifications_created_at ON notifications (created_at DESC);

COMMENT ON TABLE notifications IS 'Уведомления пользователей о изменениях в каталоге книг';
COMMENT ON COLUMN notifications.user_id IS 'ID пользователя (userId из JWT токена)';
COMMENT ON COLUMN notifications.type IS 'Тип уведомления: BOOK_CREATED, BOOK_UPDATED, BOOK_DELETED, PRICE_CHANGED';
COMMENT ON COLUMN notifications.title IS 'Заголовок уведомления';
COMMENT ON COLUMN notifications.message IS 'Текст уведомления';
COMMENT ON COLUMN notifications.book_id IS 'ID книги (может быть null, если книга удалена)';
COMMENT ON COLUMN notifications.is_read IS 'Флаг прочитанности уведомления';
COMMENT ON COLUMN notifications.created_at IS 'Дата создания уведомления';

