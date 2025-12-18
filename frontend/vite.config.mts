import { defineConfig } from "vite";
import react from "@vitejs/plugin-react-swc";

export default defineConfig({
  plugins: [react()],
  server: {
    port: 3000,
    proxy: {
      "/api": {
        // в docker-сети фронт ходит напрямую к nginx-гейтвею
        target: "http://nginx-gateway",
        changeOrigin: true
      }
    }
  }
});


