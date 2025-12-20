FROM node:20-alpine

WORKDIR /app

# сначала копируем только манифесты, чтобы кешировать установку зависимостей
COPY package*.json ./

# установка зависимостей
RUN npm install

# далее копируем исходники
COPY . .

# production-сборка react-приложения
RUN npm run build

# простой nodejs-сервер для раздачи статики
RUN npm install -g serve

EXPOSE 3000

# поднимаем nodejs сервер внутри контейнера
CMD ["serve", "-s", "dist", "-l", "3000"]



