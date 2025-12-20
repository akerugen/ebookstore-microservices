#!/bin/bash

set -e

echo "Начинаем деплой в Кубы"

# Цвета
GREEN='\033[0;32m'
BLUE='\033[0;34m'
NC='\033[0m'

# 1. Создаем namespace
echo -e "${BLUE}1. Создание namespace${NC}"
kubectl apply -f namespace.yaml
sleep 2

# 2. Создаем ConfigMap и Secrets
echo -e "${BLUE}2. Создание ConfigMap и Secrets${NC}"
kubectl apply -f configmap.yaml
kubectl apply -f secret.yaml
sleep 2

# 3. Разворачиваем Redis
echo -e "${BLUE}3. Развёртывание Redis${NC}"
kubectl apply -f redis-deployment.yaml
kubectl -n ebookstore wait --for=condition=available --timeout=300s \
    deployment/redis || echo "Redis timeout, продолжаем"
sleep 3

# 4. Разворачиваем БД
echo -e "${BLUE}4. Развёртывание PostgreSQL (Auth)${NC}"
kubectl apply -f auth-db-deployment.yaml
kubectl -n ebookstore wait --for=condition=available --timeout=300s \
    deployment/auth-db || echo "Auth-DB timeout, продолжаем"
sleep 2

echo -e "${BLUE}5. Развёртывание PostgreSQL (User)${NC}"
kubectl apply -f user-db-deployment.yaml
kubectl -n ebookstore wait --for=condition=available --timeout=300s \
    deployment/user-db || echo "User-DB timeout, продолжаем"
sleep 2

echo -e "${BLUE}6. Развёртывание PostgreSQL (Catalog)${NC}"
kubectl apply -f catalog-db-deployment.yaml
kubectl -n ebookstore wait --for=condition=available --timeout=300s \
    deployment/catalog-db || echo "Catalog-DB timeout, продолжаем"
sleep 2

echo -e "${BLUE}7. Развёртывание PostgreSQL (Notification)${NC}"
kubectl apply -f notification-db-deployment.yaml
kubectl -n ebookstore wait --for=condition=available --timeout=300s \
    deployment/notification-db || echo "Notification-DB timeout, продолжаем"
sleep 2

# 5. Разворачиваем сервисы
echo -e "${BLUE}8. Развёртывание Auth-Service${NC}"
kubectl apply -f auth-service-deployment.yaml
kubectl -n ebookstore wait --for=condition=available --timeout=300s \
    deployment/auth-service || echo "Auth-Service timeout, продолжаем"
sleep 2

echo -e "${BLUE}9. Развёртывание User-Service${NC}"
kubectl apply -f user-service-deployment.yaml
kubectl -n ebookstore wait --for=condition=available --timeout=300s \
    deployment/user-service || echo "User-Service timeout, продолжаем"
sleep 2

echo -e "${BLUE}10. Развёртывание Catalog-Service${NC}"
kubectl apply -f catalog-service-deployment.yaml
kubectl -n ebookstore wait --for=condition=available --timeout=300s \
    deployment/catalog-service || echo "Catalog-Service timeout, продолжаем"
sleep 2

echo -e "${BLUE}11. Развёртывание Notification-Service${NC}"
kubectl apply -f notification-service-deployment.yaml
kubectl -n ebookstore wait --for=condition=available --timeout=300s \
    deployment/notification-service || echo "Notification-Service timeout, продолжаем"
sleep 2

echo -e "${BLUE}12. Развёртывание Nginx-Gateway${NC}"
kubectl apply -f nginx-gateway-deployment.yaml
kubectl -n ebookstore wait --for=condition=available --timeout=300s \
    deployment/nginx-gateway || echo "Nginx-Gateway timeout, продолжаем"
sleep 2

echo -e "${BLUE}13. Развёртывание Frontend${NC}"
kubectl apply -f frontend-deployment.yaml
kubectl -n ebookstore wait --for=condition=available --timeout=300s \
    deployment/frontend || echo "Frontend timeout, продолжаем"
sleep 2

# 6. Ingress
echo -e "${BLUE}14. Создание Ingress${NC}"
kubectl apply -f nginx-ingress.yaml
sleep 2

echo ""
echo -e "${GREEN}Деплой - успех!${NC}"
echo ""
echo "Чекнуть статус:"
echo "kubectl -n ebookstore get all"
echo ""
echo "Чекнуть логи:"
echo "kubectl -n ebookstore logs -f deployment/auth-service"
