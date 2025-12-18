FROM nginx:alpine

# Устанавливаем envsubst для подстановки переменных окружения
RUN apk add --no-cache gettext

RUN rm /etc/nginx/nginx.conf

COPY nginx.conf /etc/nginx/nginx.conf.template

EXPOSE 80

# Используем envsubst для подстановки переменных окружения в nginx.conf
# Если CORS_ORIGIN не установлен, используем значение по умолчанию
CMD ["/bin/sh", "-c", "export CORS_ORIGIN=${CORS_ORIGIN:-http://localhost:3000} && envsubst '$$CORS_ORIGIN' < /etc/nginx/nginx.conf.template > /etc/nginx/nginx.conf && nginx -g 'daemon off;'"]
