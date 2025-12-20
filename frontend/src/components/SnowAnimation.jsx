import React, { useEffect, useState } from "react";
import "./SnowAnimation.css";

export function SnowAnimation({ enabled }) {
  const [snowflakes, setSnowflakes] = useState([]);

  useEffect(() => {
    if (!enabled) {
      setSnowflakes([]);
      return;
    }

    // создаем 50 снежинок с разными параметрами для плавной анимации
    const flakes = Array.from({ length: 50 }, (_, i) => ({
      id: i,
      left: Math.random() * 100, // позиция по горизонтали (0-100%)
      delay: Math.random() * 5, // задержка начала анимации (0-5 сек)
      duration: 10 + Math.random() * 10, // длительность падения (10-20 сек)
      size: 3 + Math.random() * 3, // размер снежинки (3-6px) - небольшие снежинки
      opacity: 0.3 + Math.random() * 0.4, // непрозрачность (0.3-0.7) - не мешает
      drift: -10 + Math.random() * 20, // боковое смещение при падении (-10 до +10px)
    }));

    setSnowflakes(flakes);
  }, [enabled]);

  if (!enabled || snowflakes.length === 0) {
    return null;
  }

  return (
    <div className="snow-container">
      {snowflakes.map((flake) => (
        <div
          key={flake.id}
          className="snowflake"
          style={{
            left: `${flake.left}%`,
            animationDelay: `${flake.delay}s`,
            animationDuration: `${flake.duration}s`,
            width: `${flake.size}px`,
            height: `${flake.size}px`,
            opacity: flake.opacity,
            "--drift": `${flake.drift}px`,
          }}
        />
      ))}
    </div>
  );
}

