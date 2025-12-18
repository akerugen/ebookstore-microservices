import React, { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { catalogApi } from "../services/catalogApi";

export function BookDetailsPage() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [book, setBook] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const load = async () => {
      try {
        setLoading(true);
        const data = await catalogApi.getBook(id);
        setBook(data);
      } catch {
        setError("не удалось загрузить книгу");
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

  if (!book) {
    return (
      <div className="page">
        <div>Книга не найдена</div>
      </div>
    );
  }

  return (
    <div className="page">
      <div style={{ display: "flex", justifyContent: "flex-start", marginBottom: "1rem" }}>
        <button className="btn secondary btn-back" onClick={() => navigate(-1)}>
          ← Назад
        </button>
      </div>
      <div className="card book-details">
        <h1>{book.title}</h1>
        {book.description && <p>{book.description}</p>}
        <div className="details-row">
          {book.authorName && (
            <span>
              Автор: <strong>{book.authorName}</strong>
            </span>
          )}
          {book.genreName && (
            <span>
              Жанр: <strong>{book.genreName}</strong>
            </span>
          )}
          {book.statusName && (
            <span>
              Статус: <strong>{book.statusName}</strong>
            </span>
          )}
        </div>
        {book.price != null && (
          <div className="price">Цена: {book.price.toFixed(2)} ₽</div>
        )}
      </div>
    </div>
  );
}



