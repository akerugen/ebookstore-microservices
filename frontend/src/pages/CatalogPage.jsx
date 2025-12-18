import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { catalogApi } from "../services/catalogApi";
import { useAuth } from "../auth/AuthContext";
import { hasRole } from "../utils/jwt";

export function CatalogPage() {
  const [books, setBooks] = useState([]);
  const [authors, setAuthors] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [showBookModal, setShowBookModal] = useState(false);
  const [editingBook, setEditingBook] = useState(null);
  const [showAuthorModal, setShowAuthorModal] = useState(false);
  const [editingAuthor, setEditingAuthor] = useState(null);

  const { userInfo } = useAuth();
  const navigate = useNavigate();

  const isAdmin = hasRole(userInfo, ["ROLE_ADMIN", "ROLE_SUPER_USER"]);

  useEffect(() => {
    // загружаем книги и авторов
    const load = async () => {
      try {
        setLoading(true);
        const [booksData, authorsData] = await Promise.all([
          catalogApi.getBooks(),
          catalogApi.getAuthors()
        ]);
        // Проверяем, что данные являются массивами
        setBooks(Array.isArray(booksData) ? booksData : []);
        setAuthors(Array.isArray(authorsData) ? authorsData : []);
      } catch (e) {
        console.error("Failed to load catalog:", e);
        setError("Не удалось загрузить каталог");
        setBooks([]);
        setAuthors([]);
      } finally {
        setLoading(false);
      }
    };
    load();
  }, []);

  const openCreateBook = () => {
    setEditingBook(null);
    setShowBookModal(true);
  };

  const openEditBook = (book) => {
    setEditingBook(book);
    setShowBookModal(true);
  };

  const openCreateAuthor = () => {
    setEditingAuthor(null);
    setShowAuthorModal(true);
  };

  const openEditAuthor = (author) => {
    setEditingAuthor(author);
    setShowAuthorModal(true);
  };

  const onDeleteBook = async (book) => {
    if (!window.confirm(`Удалить книгу "${book.title}"?`)) return;
    try {
      await catalogApi.deleteBook(book.id);
      setBooks((prev) => prev.filter((b) => b.id !== book.id));
    } catch {
      alert("Не удалось удалить книгу");
    }
  };

  const onDeleteAuthor = async (author) => {
    if (!window.confirm(`Удалить автора "${author.firstName} ${author.lastName}"?`)) {
      return;
    }
    try {
      await catalogApi.deleteAuthor(author.id);
      setAuthors((prev) => prev.filter((a) => a.id !== author.id));
    } catch {
      alert("Не удалось удалить автора");
    }
  };

  const onBookSaved = (saved) => {
    setShowBookModal(false);
    setEditingBook(null);
    setBooks((prev) => {
      const exists = prev.find((b) => b.id === saved.id);
      if (exists) {
        return prev.map((b) => (b.id === saved.id ? saved : b));
      }
      return [...prev, saved];
    });
  };

  const onAuthorSaved = (saved) => {
    setShowAuthorModal(false);
    setEditingAuthor(null);
    setAuthors((prev) => {
      const exists = prev.find((a) => a.id === saved.id);
      if (exists) {
        return prev.map((a) => (a.id === saved.id ? saved : a));
      }
      return [...prev, saved];
    });
  };

  if (loading) {
    return <div className="page"><div>Загрузка каталога...</div></div>;
  }

  if (error) {
    return <div className="page error-text">{error}</div>;
  }

  return (
    <div className="page">
      <div className="page-header">
        <h1>Каталог книг</h1>
        {isAdmin && (
          <button className="btn primary" onClick={openCreateBook}>
            Добавить книгу
          </button>
        )}
      </div>

      <div className="grid">
        {books.map((book) => (
          <div
            key={book.id}
            className="card book-card"
            onClick={() => navigate(`/books/${book.id}`)}
          >
            <h2>{book.title}</h2>
            <div className="muted">{book.description}</div>
            <div className="tags">
              {book.genreName && <span className="tag">{book.genreName}</span>}
              {book.statusName && (
                <span className="tag secondary">{book.statusName}</span>
              )}
            </div>
            {isAdmin && (
              <div
                className="card-actions"
                onClick={(e) => {
                  // не пускаем клик наверх (на страницу книги)
                  e.stopPropagation();
                }}
              >
                <button
                  className="btn secondary small"
                  onClick={() => openEditBook(book)}
                >
                  Редактировать
                </button>
                <button
                  className="btn danger small"
                  onClick={() => onDeleteBook(book)}
                >
                  Удалить
                </button>
              </div>
            )}
          </div>
        ))}
      </div>

      <div className="page-header">
        <h2>Авторы</h2>
        {isAdmin && (
          <button className="btn secondary" onClick={openCreateAuthor}>
            Добавить автора
          </button>
        )}
      </div>

      <div className="grid authors-grid">
        {authors.map((author) => (
          <div
            key={author.id}
            className="card author-card"
            onClick={() => navigate(`/authors/${author.id}`)}
          >
            <h3>
              {author.firstName} {author.lastName}
            </h3>
            {author.biography && (
              <div className="muted">{author.biography}</div>
            )}
            {isAdmin && (
              <div
                className="card-actions"
                onClick={(e) => {
                  e.stopPropagation();
                }}
              >
                <button
                  className="btn secondary small"
                  onClick={() => openEditAuthor(author)}
                >
                  Редактировать
                </button>
                <button
                  className="btn danger small"
                  onClick={() => onDeleteAuthor(author)}
                >
                  Удалить
                </button>
              </div>
            )}
          </div>
        ))}
      </div>

      {showBookModal && (
        <BookModal
          book={editingBook}
          onClose={() => {
            setShowBookModal(false);
            setEditingBook(null);
          }}
          onSaved={onBookSaved}
        />
      )}

      {showAuthorModal && (
        <AuthorModal
          author={editingAuthor}
          onClose={() => {
            setShowAuthorModal(false);
            setEditingAuthor(null);
          }}
          onSaved={onAuthorSaved}
        />
      )}
    </div>
  );
}

function BookModal({ book, onClose, onSaved }) {
  const isEdit = Boolean(book);
  const [form, setForm] = useState({
    title: book?.title || "",
    description: book?.description || "",
    price: book?.price || "",
    authorId: book?.authorId || "",
    genreId: book?.genreId || "",
    bookStatusId: book?.bookStatusId || book?.statusId || ""
  });
  const [authors, setAuthors] = useState([]);
  const [genres, setGenres] = useState([]);
  const [statuses, setStatuses] = useState([]);
  const [error, setError] = useState(null);
  const [loading, setLoading] = useState(false);
  const [loadingData, setLoadingData] = useState(true);

  // загружаем списки авторов, жанров и статусов при открытии модалки
  useEffect(() => {
    const load = async () => {
      try {
        setLoadingData(true);
        const [authorsData, genresData, statusesData] = await Promise.all([
          catalogApi.getAuthors(),
          catalogApi.getGenres(),
          catalogApi.getBookStatuses()
        ]);
        // Проверяем, что данные являются массивами
        setAuthors(Array.isArray(authorsData) ? authorsData : []);
        setGenres(Array.isArray(genresData) ? genresData : []);
        setStatuses(Array.isArray(statusesData) ? statusesData : []);
      } catch (err) {
        console.error("Failed to load book form data:", err);
        setError("Не удалось загрузить данные для формы");
        setAuthors([]);
        setGenres([]);
        setStatuses([]);
      } finally {
        setLoadingData(false);
      }
    };
    load();
  }, []);

  const onChange = (e) => {
    setForm((prev) => ({ ...prev, [e.target.name]: e.target.value }));
  };

  const onSubmit = async (e) => {
    e.preventDefault();
    setError(null);
    setLoading(true);
    try {
      // валидация обязательных полей
      if (!form.title || !form.price || !form.authorId || !form.genreId || !form.bookStatusId) {
        setError("Заполните все обязательные поля");
        setLoading(false);
        return;
      }

      const payload = {
        title: form.title.trim(),
        description: form.description?.trim() || null,
        price: Number(form.price),
        authorId: Number(form.authorId),
        genreId: Number(form.genreId),
        bookStatusId: Number(form.bookStatusId)
      };

      const saved = isEdit
        ? await catalogApi.updateBook(book.id, payload)
        : await catalogApi.createBook(payload);
      onSaved(saved);
    } catch (err) {
      console.error("Book save error:", err);
      setError("Не удалось сохранить книгу");
    } finally {
      setLoading(false);
    }
  };

  if (loadingData) {
    return (
      <div className="modal-backdrop">
        <div className="modal">
          <div>Загрузка данных...</div>
        </div>
      </div>
    );
  }

  return (
    <div className="modal-backdrop">
      <div className="modal">
        <h2>{isEdit ? "Редактировать книгу" : "Новая книга"}</h2>
        <form onSubmit={onSubmit} className="form">
          <label>
            Название
            <input
              name="title"
              value={form.title}
              onChange={onChange}
              required
            />
          </label>
          <label>
            Описание
            <textarea
              name="description"
              value={form.description}
              onChange={onChange}
            />
          </label>
          <label>
            Цена
            <input
              type="number"
              name="price"
              value={form.price}
              onChange={onChange}
              min="0.01"
              step="0.01"
              required
            />
          </label>
          <label>
            Автор
            <select
              name="authorId"
              value={form.authorId}
              onChange={onChange}
              required
              disabled={loadingData}
            >
              <option value="">Выберите автора</option>
              {authors.map((author) => (
                <option key={author.id} value={author.id}>
                  {author.firstName} {author.lastName}
                  {author.patronymic ? ` ${author.patronymic}` : ""}
                </option>
              ))}
            </select>
          </label>
          <label>
            Жанр
            <select
              name="genreId"
              value={form.genreId}
              onChange={onChange}
              required
              disabled={loadingData}
            >
              <option value="">Выберите жанр</option>
              {genres.map((genre) => (
                <option key={genre.id} value={genre.id}>
                  {genre.name}
                </option>
              ))}
            </select>
          </label>
          <label>
            Статус
            <select
              name="bookStatusId"
              value={form.bookStatusId}
              onChange={onChange}
              required
              disabled={loadingData}
            >
              <option value="">Выберите статус</option>
              {statuses.map((status) => (
                <option key={status.id} value={status.id}>
                  {status.name}
                </option>
              ))}
            </select>
          </label>
          {error && <div className="error-text">{error}</div>}
          <div className="modal-actions">
            <button
              type="button"
              className="btn secondary"
              onClick={onClose}
            >
              Отмена
            </button>
            <button className="btn primary" type="submit" disabled={loading}>
              {loading ? "Сохраняем..." : "Сохранить"}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}

function AuthorModal({ author, onClose, onSaved }) {
  const isEdit = Boolean(author);
  const [form, setForm] = useState({
    firstName: author?.firstName || "",
    lastName: author?.lastName || "",
    biography: author?.biography || ""
  });
  const [error, setError] = useState(null);
  const [loading, setLoading] = useState(false);

  const onChange = (e) => {
    setForm((prev) => ({ ...prev, [e.target.name]: e.target.value }));
  };

  const onSubmit = async (e) => {
    e.preventDefault();
    setError(null);
    setLoading(true);
    try {
      const payload = { ...form };
      const saved = isEdit
        ? await catalogApi.updateAuthor(author.id, payload)
        : await catalogApi.createAuthor(payload);
      onSaved(saved);
    } catch {
      setError("Не удалось сохранить автора");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="modal-backdrop">
      <div className="modal">
        <h2>{isEdit ? "Редактировать автора" : "Новый автор"}</h2>
        <form onSubmit={onSubmit} className="form">
          <label>
            Имя
            <input
              name="firstName"
              value={form.firstName}
              onChange={onChange}
              required
            />
          </label>
          <label>
            Фамилия
            <input
              name="lastName"
              value={form.lastName}
              onChange={onChange}
              required
            />
          </label>
          <label>
            Биография
            <textarea
              name="biography"
              value={form.biography}
              onChange={onChange}
            />
          </label>
          {error && <div className="error-text">{error}</div>}
          <div className="modal-actions">
            <button
              type="button"
              className="btn secondary"
              onClick={onClose}
            >
              Отмена
            </button>
            <button className="btn primary" type="submit" disabled={loading}>
              {loading ? "Сохраняем..." : "Сохранить"}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}



