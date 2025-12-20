import React, { createContext, useContext, useState, useEffect } from "react";

const SnowContext = createContext();

const SNOW_ENABLED_KEY = "snowAnimationEnabled";

export function SnowProvider({ children }) {
  const [enabled, setEnabled] = useState(() => {
    // при первой загрузке проверяем localStorage
    const saved = localStorage.getItem(SNOW_ENABLED_KEY);
    return saved === "true";
  });

  useEffect(() => {
    // сохраняем состояние в localStorage при изменении
    localStorage.setItem(SNOW_ENABLED_KEY, enabled.toString());
  }, [enabled]);

  const toggle = () => {
    setEnabled((prev) => !prev);
  };

  return (
    <SnowContext.Provider value={{ enabled, toggle }}>
      {children}
    </SnowContext.Provider>
  );
}

export function useSnowContext() {
  const context = useContext(SnowContext);
  if (!context) {
    throw new Error("useSnowContext must be used within SnowProvider");
  }
  return context;
}

