import React, { useEffect, useState } from "react";
import "./Toast.css";

export function Toast({ message, onClose, duration = 3000 }) {
  const [isVisible, setIsVisible] = useState(false);
  const [isExiting, setIsExiting] = useState(false);

  useEffect(() => {
    // показываем toast с небольшой задержкой для плавной анимации появления
    const showTimer = setTimeout(() => {
      setIsVisible(true);
    }, 10);

    // автоматически скрываем через duration
    const hideTimer = setTimeout(() => {
      setIsExiting(true);
      setTimeout(() => {
        onClose();
      }, 300); // время для анимации исчезновения
    }, duration);

    return () => {
      clearTimeout(showTimer);
      clearTimeout(hideTimer);
    };
  }, [duration, onClose]);

  const handleClose = () => {
    setIsExiting(true);
    setTimeout(() => {
      onClose();
    }, 300);
  };

  return (
    <div
      className={`toast ${isVisible ? "toast-visible" : ""} ${isExiting ? "toast-exiting" : ""}`}
      onClick={handleClose}
    >
      <div className="toast-content">
        <span className="toast-icon">✓</span>
        <span className="toast-message">{message}</span>
      </div>
    </div>
  );
}

export function ToastContainer({ toasts, onClose }) {
  return (
    <div className="toast-container">
      {toasts.map((toast) => (
        <Toast
          key={toast.id}
          message={toast.message}
          onClose={() => onClose(toast.id)}
          duration={toast.duration}
        />
      ))}
    </div>
  );
}

