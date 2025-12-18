import React, { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { catalogApi } from "../services/catalogApi";

export function AuthorDetailsPage() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [author, setAuthor] = useState(null);
  const [books, setBooks] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const load = async () => {
      try {
        setLoading(true);
        const [authorData, booksData] = await Promise.all([
          catalogApi.getAuthor(id),
          catalogApi.getBooksByAuthor(id)
        ]);
        setAuthor(authorData);
        setBooks(booksData);
      } catch {
        setError("Не удалось загрузить автора");
      } finally {
        setLoading(false);
      }
    };
    load();
  }, [id]);

  if (loading) {
    return <div className="page">Загрузка...</div>;
  }

  if (error) {
    return (
      <div className="page">
        <div className="error-text">{error}</div>
      </div>
    );
  }

  if (!author) {
    return (
      <div className="page">
        <div>Автор не найден</div>
      </div>
    );
  }

  return (
    <div className="page">
      <button className="btn secondary" onClick={() => navigate(-1)}>
        Назад
      </button>
      <div className="card author-details">
        <h1>
          {author.firstName} {author.lastName}
          {author.patronymic && ` ${author.patronymic}`}
        </h1>
        {author.birthDate && (
          <div className="details-row">
            <span>
              Дата рождения: <strong>{author.birthDate}</strong>
            </span>
          </div>
        )}
        {author.biography && <p>{author.biography}</p>}
      </div>

      {books.length > 0 && (
        <div>
          <h2>Книги автора</h2>
          <div className="grid">
            {books.map((book) => (
              <div
                key={book.id}
                className="card book-card"
                onClick={() => navigate(`/books/${book.id}`)}
              >
                <h3>{book.title}</h3>
                {book.description && (
                  <div className="muted">{book.description}</div>
                )}
                {book.price != null && (
                  <div className="price">Цена: {book.price.toFixed(2)} ₽</div>
                )}
              </div>
            ))}
          </div>
        </div>
      )}
    </div>
  );
}

