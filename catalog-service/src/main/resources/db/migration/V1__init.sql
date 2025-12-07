CREATE TABLE IF NOT EXISTS authors (
                                       id BIGSERIAL PRIMARY KEY,
                                       first_name VARCHAR(100) NOT NULL,
                                       last_name VARCHAR(100) NOT NULL,
                                       patronymic VARCHAR(100),
                                       birth_date DATE,
                                       created_at TIMESTAMP NOT NULL DEFAULT NOW(),
                                       updated_at TIMESTAMP
);

CREATE INDEX idx_authors_first_name ON authors (first_name);
CREATE INDEX idx_authors_last_name ON authors (last_name);

COMMENT ON TABLE authors IS 'Авторы книг';
COMMENT ON COLUMN authors.first_name IS 'Имя автора';
COMMENT ON COLUMN authors.last_name IS 'Фамилия автора';
COMMENT ON COLUMN authors.patronymic IS 'Отчество автора (РФ)';
COMMENT ON COLUMN authors.birth_date IS 'Дата рождения автора';
COMMENT ON COLUMN authors.created_at IS 'Дата создания записи';
COMMENT ON COLUMN authors.updated_at IS 'Дата обновления записи';

CREATE TABLE IF NOT EXISTS genres (
                                      id BIGSERIAL PRIMARY KEY,
                                      name VARCHAR(100) NOT NULL UNIQUE,
                                      created_at TIMESTAMP NOT NULL DEFAULT NOW(),
                                      updated_at TIMESTAMP
);

CREATE INDEX idx_genres_name ON genres (name);

COMMENT ON TABLE genres IS 'Жанры книг';
COMMENT ON COLUMN genres.name IS 'Название жанра (уникальное)';
COMMENT ON COLUMN genres.created_at IS 'Дата создания записи';
COMMENT ON COLUMN genres.updated_at IS 'Дата обновления записи';

CREATE TABLE IF NOT EXISTS book_statuses (
                                             id BIGSERIAL PRIMARY KEY,
                                             name VARCHAR(50) NOT NULL UNIQUE,
                                             created_at TIMESTAMP NOT NULL DEFAULT NOW(),
                                             updated_at TIMESTAMP
);

CREATE INDEX idx_book_statuses_name ON book_statuses (name);

COMMENT ON TABLE book_statuses IS 'Статусы книг (Available, Out of Stock, Coming Soon, etc.)';
COMMENT ON COLUMN book_statuses.name IS 'Название статуса (уникальное)';
COMMENT ON COLUMN book_statuses.created_at IS 'Дата создания записи';
COMMENT ON COLUMN book_statuses.updated_at IS 'Дата обновления записи';

CREATE TABLE IF NOT EXISTS books (
                                     id BIGSERIAL PRIMARY KEY,
                                     title VARCHAR(255) NOT NULL,
                                     price NUMERIC(10, 2) NOT NULL,
                                     description TEXT,
                                     author_id BIGINT NOT NULL REFERENCES authors(id) ON DELETE CASCADE,
                                     genre_id BIGINT NOT NULL REFERENCES genres(id) ON DELETE CASCADE,
                                     book_status_id BIGINT NOT NULL REFERENCES book_statuses(id) ON DELETE CASCADE,
                                     created_at TIMESTAMP NOT NULL DEFAULT NOW(),
                                     updated_at TIMESTAMP
);

CREATE INDEX idx_books_title ON books (title);
CREATE INDEX idx_books_author_id ON books (author_id);
CREATE INDEX idx_books_genre_id ON books (genre_id);
CREATE INDEX idx_books_book_status_id ON books (book_status_id);

COMMENT ON TABLE books IS 'Каталог электронных книг';
COMMENT ON COLUMN books.title IS 'Название книги';
COMMENT ON COLUMN books.price IS 'Цена книги';
COMMENT ON COLUMN books.description IS 'Описание книги';
COMMENT ON COLUMN books.author_id IS 'Foreign key на автора';
COMMENT ON COLUMN books.genre_id IS 'Foreign key на жанр';
COMMENT ON COLUMN books.book_status_id IS 'Foreign key на статус книги';
COMMENT ON COLUMN books.created_at IS 'Дата добавления в каталог';
COMMENT ON COLUMN books.updated_at IS 'Дата последнего обновления';

INSERT INTO book_statuses (name, created_at) VALUES ('Available', NOW());
INSERT INTO book_statuses (name, created_at) VALUES ('Out of Stock', NOW());
INSERT INTO book_statuses (name, created_at) VALUES ('Coming Soon', NOW());
INSERT INTO book_statuses (name, created_at) VALUES ('Discontinued', NOW());

INSERT INTO genres (name, created_at) VALUES ('Исторический роман', NOW());
INSERT INTO genres (name, created_at) VALUES ('Фантастика', NOW());
INSERT INTO genres (name, created_at) VALUES ('Детектив', NOW());
INSERT INTO genres (name, created_at) VALUES ('Приключения', NOW());
INSERT INTO genres (name, created_at) VALUES ('Литература', NOW());
INSERT INTO genres (name, created_at) VALUES ('Научная фантастика', NOW());

INSERT INTO authors (first_name, last_name, patronymic, birth_date, created_at)
VALUES ('Лев', 'Толстой', 'Николаевич', '1828-09-09', NOW());

INSERT INTO authors (first_name, last_name, patronymic, birth_date, created_at)
VALUES ('Федор', 'Достоевский', 'Михайлович', '1821-11-11', NOW());

INSERT INTO authors (first_name, last_name, patronymic, birth_date, created_at)
VALUES ('Иван', 'Тургенев', 'Сергеевич', '1818-11-09', NOW());