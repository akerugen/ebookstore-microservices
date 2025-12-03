#!/bin/bash

set -e

echo "Начинаем деплой в Кубы"

# Цвета
GREEN='\033[0;32m'
BLUE='\033[0;34m'
NC='\033[0m'

# 1. Создаем namespace
echo -e "${BLUE}1. Создание namespace${NC}"
kubectl apply -f 01-namespace.yaml
sleep 2

# 2. Создаем ConfigMap и Secrets
echo -e "${BLUE}2. Создание ConfigMap и Secrets${NC}"
kubectl apply -f 02-configmap.yaml
kubectl apply -f 03-secret.yaml
sleep 2

# 3. Разворачиваем Redis
echo -e "${BLUE}3. Развёртывание Redis${NC}"
kubectl apply -f 04-redis-deployment.yaml
kubectl -n ebookstore wait --for=condition=available --timeout=300s \
    deployment/redis || echo "Redis timeout, продолжаем"
sleep 3

# 4. Разворачиваем БД
echo -e "${BLUE}4. Развёртывание PostgreSQL (Auth)${NC}"
kubectl apply -f 05-auth-db-deployment.yaml
kubectl -n ebookstore wait --for=condition=available --timeout=300s \
    deployment/auth-db || echo "Auth-DB timeout, продолжаем"
sleep 2

echo -e "${BLUE}5. Развёртывание PostgreSQL (User)${NC}"
kubectl apply -f 06-user-db-deployment.yaml
kubectl -n ebookstore wait --for=condition=available --timeout=300s \
    deployment/user-db || echo "User-DB timeout, продолжаем"
sleep 2

# 5. Разворачиваем сервисы
echo -e "${BLUE}6. Развёртывание Auth-Service${NC}"
kubectl apply -f 07-auth-service-deployment.yaml
kubectl -n ebookstore wait --for=condition=available --timeout=300s \
    deployment/auth-service || echo "Auth-Service timeout, продолжаем"
sleep 2

echo -e "${BLUE}7. Развёртывание User-Service${NC}"
kubectl apply -f 08-user-service-deployment.yaml
kubectl -n ebookstore wait --for=condition=available --timeout=300s \
    deployment/user-service || echo "User-Service timeout, продолжаем"
sleep 2

# 6. Ingress
echo -e "${BLUE}8. Создание Ingress${NC}"
kubectl apply -f 09-nginx-ingress.yaml
sleep 2

# 7. HPA
echo -e "${BLUE}9. Создание HorizontalPodAutoscaler${NC}"
kubectl apply -f 10-hpa.yaml || echo "HPA может потребовать metrics-server"

echo ""
echo -e "${GREEN}Деплой - успех!${NC}"
echo ""
echo "Чекнуть статус:"
echo "kubectl -n ebookstore get all"
echo ""
echo "Чекнуть логи:"
echo "kubectl -n ebookstore logs -f deployment/auth-service"
