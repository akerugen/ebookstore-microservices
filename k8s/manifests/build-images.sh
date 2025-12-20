#!/bin/bash

set -e

echo "Сборка Docker образов для Kubernetes"

# проверяем, что мы в правильной директории
if [ ! -f "nginx-k8s.conf" ]; then
    echo "Ошибка: nginx-k8s.conf не найден. Надо запустить скрипт из директории k8s/manifests"
    exit 1
fi

# получаем корневую директорию проекта (на два уровня выше)
PROJECT_ROOT="$(cd "$(dirname "$0")/../.." && pwd)"

echo "Корневая директория проекта: $PROJECT_ROOT"

# настраиваем docker для работы с minikube (если используется)
if command -v minikube &> /dev/null; then
    echo "Настройка docker для minikube..."
    eval $(minikube docker-env)
fi

# собираем образы сервисов
echo "Сборка auth-service..."
cd "$PROJECT_ROOT/auth-service"
docker build -t auth-service:latest .

echo "Сборка user-service..."
cd "$PROJECT_ROOT/user-service"
docker build -t user-service:latest .

echo "Сборка catalog-service..."
cd "$PROJECT_ROOT/catalog-service"
docker build -t catalog-service:latest .

echo "Сборка notification-service..."
cd "$PROJECT_ROOT/notification-service"
docker build -t notification-service:latest .

# собираем nginx-gateway (build context - k8s/manifests)
echo "Сборка nginx-gateway..."
cd "$PROJECT_ROOT/k8s/manifests"
docker build -f Dockerfile.nginx -t nginx-gateway:latest .

# собираем frontend
echo "Сборка frontend..."
cd "$PROJECT_ROOT/frontend"
docker build -t frontend:latest .

echo ""
echo "Все образы собраны успешно"
echo ""
echo "Для проверки образов:"
echo "docker images | grep -E '(auth-service|user-service|catalog-service|notification-service|nginx-gateway|frontend)'"

