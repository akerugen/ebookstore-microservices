# Kubernetes Deployment Guide

## Обзор

Этот проект развернут в Kubernetes с использованием minikube. Все сервисы работают в namespace `ebookstore`.

## Теория

### Kubernetes и Minikube

**Kubernetes** — оркестратор контейнеров. Координирует, где запустить контейнер, когда его перезапустить, как распределить трафик.

**Minikube** — локальная тестовая версия Kubernetes, которая работает на одном ПК. В minikube есть только один узел (нода), поэтому если его отключить, то весь кластер упадет. Все поды крутятся на одной единственной ноде.

### Основные концепции

- **Node (Узел)** — рабочий компьютер (сервер), который входит в состав Kubernetes кластера. В minikube одна нода содержит и Control Plane, и Worker компоненты.

- **Pod** — самая маленькая единица в Kubernetes, которую можно развернуть. В поды входят контейнеры. Все контейнеры одного пода видят друг друга по localhost. У подов всегда разные id и уникальные ip внутри кластера. Данные теряются, если не сохранены на диск.

- **Deployment** — инструкция для Kubernetes чтобы запустить несколько копий приложения, а затем управлять ими. При падении одного из подов, ReplicaSet видит это и возобновляет до указанного числа реплик.

- **Ingress** — объект для маршрутизации внешнего трафика на сервисы внутри кластера.


## Архитектура

### Сервисы

1. **Базы данных:**
   - `auth-db` (PostgreSQL) - для auth-service
   - `user-db` (PostgreSQL) - для user-service
   - `catalog-db` (PostgreSQL) - для catalog-service
   - `notification-db` (PostgreSQL) - для notification-service
   - `redis` - для кэширования токенов

2. **Бизнес-сервисы:**
   - `auth-service` (8082) - авторизация и аутентификация
   - `user-service` (8081) - управление пользователями
   - `catalog-service` (8083) - каталог книг
   - `notification-service` (8084) - уведомления

3. **Инфраструктура:**
   - `nginx-gateway` (80) - API Gateway с маршрутизацией и авторизацией
   - `frontend` (3000) - React приложение

### Маршрутизация

Все запросы идут через `nginx-gateway`, который:
- Проверяет авторизацию через `auth_request` к `auth-service`
- Маршрутизирует запросы к соответствующим сервисам
- Обрабатывает CORS

Ingress направляет:
- `/api/*` → `nginx-gateway`
- `/` → `frontend`

## Установка

### Установка Minikube и kubectl

**Windows (через Chocolatey):**
```bash
# Установка Chocolatey (PowerShell от администратора)
Set-ExecutionPolicy Bypass -Scope Process -Force; [System.Net.ServicePointManager]::SecurityProtocol = [System.Net.ServicePointManager]::SecurityProtocol -bor 3072; iex ((New-Object System.Net.WebClient).DownloadString('https://community.chocolatey.org/install.ps1'))

# Установить Minikube и kubectl
choco install minikube kubernetes-cli
```

**Linux:**
```bash
# Установка Minikube
curl -LO https://github.com/kubernetes/minikube/releases/latest/download/minikube-linux-amd64
chmod +x minikube-linux-amd64
sudo mv minikube-linux-amd64 /usr/local/bin/minikube

# Установка kubectl
curl -LO "https://dl.k8s.io/release/$(curl -L -s https://dl.k8s.io/release/stable.txt)/bin/linux/amd64/kubectl"
chmod +x kubectl
sudo mv kubectl /usr/local/bin/
```

### Запуск Minikube

```bash
# Стандартный запуск
minikube start --driver=docker

# Если памяти мало, можно ограничить
minikube start --driver=docker --memory=2200mb --cpus=2
```

### Включение Ingress аддона

```bash
minikube addons enable ingress
```

## Подготовка образов

Перед деплоем нужно собрать Docker образы и загрузить их в minikube:

```bash
# Запускаем minikube
minikube start --driver=docker

# Настраиваем docker для работы с minikube (важно!)
eval $(minikube docker-env)

# Используем скрипт для сборки всех образов
cd k8s/manifests
chmod +x build-images.sh  # На Windows можно пропустить
./build-images.sh

# Или собираем вручную:
# cd auth-service && docker build -t auth-service:latest .
# cd ../user-service && docker build -t user-service:latest .
# cd ../catalog-service && docker build -t catalog-service:latest .
# cd ../notification-service && docker build -t notification-service:latest .
# cd ../k8s/manifests && docker build -f Dockerfile.nginx -t nginx-gateway:latest .
# cd ../../frontend && docker build -t frontend:latest .
```

**Важно:** После сборки образов не закрывать терминал с `eval $(minikube docker-env)` или выполнить команду снова перед деплоем

## Деплой

Из директории `k8s/manifests`:

```bash
chmod +x deploy-all.sh
./deploy-all.sh
```

Или вручную по порядку применения манифестов:

```bash
# 1. Базовые ресурсы
kubectl apply -f namespace.yaml
kubectl apply -f configmap.yaml
kubectl apply -f secret.yaml

# 2. Инфраструктура (Redis и базы данных)
kubectl apply -f redis-deployment.yaml
kubectl apply -f auth-db-deployment.yaml
kubectl apply -f user-db-deployment.yaml
kubectl apply -f catalog-db-deployment.yaml
kubectl apply -f notification-db-deployment.yaml

# 3. Бизнес-сервисы
kubectl apply -f auth-service-deployment.yaml
kubectl apply -f user-service-deployment.yaml
kubectl apply -f catalog-service-deployment.yaml
kubectl apply -f notification-service-deployment.yaml

# 4. Шлюзы и фронтенд
kubectl apply -f nginx-gateway-deployment.yaml
kubectl apply -f frontend-deployment.yaml

# 5. Маршрутизация
kubectl apply -f nginx-ingress.yaml
```

## Проверка статуса

```bash
# Проверить все ресурсы
kubectl -n ebookstore get all

# Проверить поды
kubectl -n ebookstore get pods

# Проверить сервисы
kubectl -n ebookstore get svc

# Проверить deployments
kubectl -n ebookstore get deployment

# Проверить логи
kubectl -n ebookstore logs -f deployment/auth-service
kubectl -n ebookstore logs -f deployment/nginx-gateway
```

## Доступ к приложению

### Через Ingress (рекомендуется)

После деплоя нужно настроить доступ через Ingress:

```bash
# Проверить ingress
kubectl -n ebookstore get ingress

# Посмотреть детали ingress
kubectl -n ebookstore describe ingress ebookstore-ingress

# Использовать minikube tunnel для получения внешнего IP (в отдельном терминале)
minikube tunnel
```

После запуска `minikube tunnel` ingress получит внешний IP адрес, к которому можно обращаться.

### Через Port-Forward (для тестирования)

```bash
# Port-forward для frontend
kubectl -n ebookstore port-forward service/frontend 3000:3000

# Port-forward для nginx-gateway (если нужен прямой доступ к API)
kubectl -n ebookstore port-forward service/nginx-gateway 8080:80

# Port-forward для отдельных сервисов (для отладки)
kubectl -n ebookstore port-forward service/auth-service 8082:8082
kubectl -n ebookstore port-forward service/user-service 8081:8081
```

## Масштабирование

### Ручное масштабирование

```bash
# Увеличить число реплик до 5
kubectl -n ebookstore scale deployment auth-service --replicas=5

# Проверить количество подов
kubectl -n ebookstore get pods

# Вернуть на место
kubectl -n ebookstore scale deployment auth-service --replicas=3
```

### Перезапуск deployment

```bash
# Перезапустить все поды в deployment
kubectl -n ebookstore rollout restart deployment/auth-service

# Проверить статус rollout
kubectl -n ebookstore rollout status deployment/auth-service
```

## Мониторинг (опционально)

Для мониторинга кластера можно установить Prometheus и Grafana:

### Установка через Helm

```bash
# Установить Helm
curl https://raw.githubusercontent.com/helm/helm/main/scripts/get-helm-3 | bash
helm version

# Добавить репозиторий Prometheus
helm repo add prometheus-community https://prometheus-community.github.io/helm-charts
helm repo update

# Создать namespace и установить Prometheus stack
kubectl create namespace monitoring

helm install prometheus prometheus-community/kube-prometheus-stack \
  --namespace monitoring \
  --set prometheus.prometheusSpec.serviceMonitorSelectorNilUsesHelmValues=false \
  --set grafana.adminPassword=admin123

# Проверить поды
kubectl -n monitoring get pods
```

### Доступ к Grafana и Prometheus

```bash
# Port-forward для Grafana (в отдельном терминале)
kubectl -n monitoring port-forward svc/prometheus-grafana 3000:80

# Port-forward для Prometheus (в отдельном терминале)
kubectl -n monitoring port-forward svc/prometheus-kube-prometheus-prometheus 9090:9090
```

Открыть в браузере:
- Grafana: `http://localhost:3000` (логин: `admin`, пароль: `admin123`)
- Prometheus: `http://localhost:9090`

В Grafana добавить Prometheus как data source:
- URL: `http://prometheus-kube-prometheus-prometheus.monitoring.svc.cluster.local:9090`

Импортировать готовые дашборды:
- `6417` - Kubernetes Cluster Monitoring (главный)
- `1860` - Node Exporter Metrics
- `12114` - Kubernetes Deployment Metrics

## Важно

1. **Образы должны быть собраны с `imagePullPolicy: Never`** - они загружаются локально в minikube
2. **Nginx конфигурация для k8s** использует имена сервисов Kubernetes (например, `auth-service:8082` вместо docker-compose имен)
3. **CORS в k8s** настроен на `*` для простоты, но можно изменить через переменную окружения `CORS_ORIGIN` в deployment nginx-gateway
4. **Frontend в k8s** использует относительный путь `/api` для запросов, так как все идет через Ingress на одном домене

## Удаление

### Удаление namespace (обычный способ)

```bash
kubectl delete namespace ebookstore
```

### Мгновенное удаление namespace (только в экстренных случаях)

Если namespace "завис" и не удаляется обычным способом:

```bash
kubectl get namespace ebookstore -o json \
  | tr -d "\n" \
  | sed "s/\"kubernetes\"//g" \
  | kubectl replace --raw /api/v1/namespaces/ebookstore/finalize -f -
```

### Удаление отдельного ресурса

```bash
# Удалить deployment
kubectl -n ebookstore delete deployment auth-service

# Удалить под (будет автоматически пересоздан deployment'ом)
kubectl -n ebookstore delete pod <pod-name>

# Удалить все поды в deployment (будут пересозданы)
kubectl -n ebookstore delete pods -l app=auth-service
```

## Остановка и перезапуск Minikube

### Остановка

```bash
minikube stop
```

Поды исчезнут, т.к. они живут только на ноде.

### Запуск после остановки

```bash
minikube start --driver=docker

# Проверить статус кластера
kubectl cluster-info

# Проверить поды (они автоматически восстановятся)
kubectl -n ebookstore get pods
```

## Симуляция отказа узла

Полезно для тестирования отказоустойчивости:

```bash
# Посмотреть ноды
kubectl get nodes

# Заблокировать узел (новые поды туда не пойдут)
kubectl cordon minikube

# Проверить статус узла (должно быть SchedulingDisabled)
kubectl get nodes

# Принудительное пересоздание подов
kubectl -n ebookstore rollout restart deployment/auth-service

# Поды будут в статусе Pending
kubectl -n ebookstore get pods

# Разблокировать узел
kubectl uncordon minikube

# Поды запустятся
kubectl -n ebookstore get pods -w
```

