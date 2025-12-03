# === УСТАНОВКА ===
### 1. Сначала надо установить докер (особенно если это пустая убунту под виндой):
```
curl -fsSL https://get.docker.com -o get-docker.sh && sudo sh get-docker.sh && sudo usermod -aG docker $USER && newgrp docker
```
## Если ранее не установлен кубер на винду:
### 1.1 Установка Chocolatey под винду (powershell от админа):
```
Set-ExecutionPolicy Bypass -Scope Process -Force; [System.Net.ServicePointManager]::SecurityProtocol = [System.Net.ServicePointManager]::SecurityProtocol -bor 3072; iex ((New-Object System.Net.WebClient).DownloadString('https://community.chocolatey.org/install.ps1'))
```

### 1.2 Установить Minikube и kubectl
```
choco install minikube kubernetes-cli
```
# установка на линукс:
### 1.3 Установить Minikube
```
curl -LO https://github.com/kubernetes/minikube/releases/latest/download/minikube-linux-amd64 && chmod +x minikube-linux-amd64 && sudo mv minikube-linux-amd64 /usr/local/bin/minikube
```

### 1.4 Установить kubectl
```
curl -LO "https://dl.k8s.io/release/$(curl -L -s https://dl.k8s.io/release/stable.txt)/bin/linux/amd64/kubectl" && chmod +x kubectl && sudo mv kubectl /usr/local/bin/
```

# Запуск minikube:
### 2. Запустить Minikube
```
minikube start --driver=docker
```
### 2.1. Если памяти оч мало, то можно ограничить двумя гигами:
```
minikube start --driver=docker --memory=2200mb --cpus=2
```

# === РАБОТА С ПРОГОЙ (подготовка) ===
### 1. Перейти в корень проекта через (mnt для винды):
```
cd /mnt/c/users/akeru/documents/github/ebookstore-microservices
```
### 2. дальше можно собрать Java приложения прямо в IDEA через:
```
mvn clean package
```
### 3. обратно в терминале сбилдить докер образы сервисов, например:
```
eval $(minikube docker-env)
docker build -t auth-service:latest .
docker build -t user-service:latest .
```
### 3.1 либо пересобрать для загрузки в minikube (более правильно):
```
cd auth-service && docker build -t auth-service:1.0 . && cd ..
cd user-service && docker build -t user-service:1.0 . && cd ..
```
### 3.2 Проверить образы:
```
docker images | grep -E "auth-service|user-service"
```

# === ДЕПЛОЙ ===
### 1. Запустить bash-скрипт, где все поднимается с логами:
```
chmod +x deploy-all.sh
./deploy-all.sh
```
### 1.1 /или/ Применить манифесты (руками):
```
cd k8s-manifests
kubectl apply -f 01-namespace.yaml
kubectl apply -f 02-configmap.yaml
kubectl apply -f 03-secret.yaml
kubectl apply -f 04-redis-deployment.yaml
kubectl apply -f 05-auth-db-deployment.yaml
kubectl apply -f 06-user-db-deployment.yaml
kubectl apply -f 07-auth-service-deployment.yaml
kubectl apply -f 08-user-service-deployment.yaml

kubectl apply -f 09-nginx-ingress.yaml
kubectl apply -f https://github.com/kubernetes-sigs/metrics-server/releases/latest/download/components.yaml
kubectl get deployment metrics-server -n kube-system

kubectl apply -f 10-hpa.yaml
```
### 2. Проверка подов:
```
kubectl -n ebookstore get pods
```

### 3. Проверка работы сервисов:
```
kubectl -n ebookstore get svc
```
### 4. Проверка deployments:
```
kubectl -n ebookstore get deployment
```

# === ПРИМЕНЕНИЕ МИГРАЦИЙ ===
### 1. Заходим в под с базой данных:
```
kubectl -n ebookstore exec -it pod/user-db-76c955cc7-xjfs9 -- psql -U postgres -d user_service_db
kubectl -n ebookstore exec -it pod/auth-db-79cf57989d-bx8jb -- psql -U postgres -d auth_service_db

```
### 2. Применить скрипт миграции:
```
CREATE TABLE IF NOT EXISTS users
(
    id            BIGSERIAL PRIMARY KEY,
    username      VARCHAR(50)  NOT NULL UNIQUE, -- бизнес-ключ (как в credentials в auth-service)
    email         VARCHAR(100) NOT NULL UNIQUE, -- бизнес-ключ (как в credentials в auth-service)
    first_name    VARCHAR(50),
    last_name     VARCHAR(50),
    date_of_birth DATE,
    created_at    TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at    TIMESTAMP
);

CREATE INDEX idx_users_username ON users (username);
CREATE INDEX idx_users_email ON users (email);

COMMENT ON TABLE users IS 'Профиль пользователя - бизнес-данные';
COMMENT ON COLUMN users.username IS 'Уникальное имя пользователя (бизнес-ключ)';
COMMENT ON COLUMN users.email IS 'Email пользователя (бизнес-ключ)';
COMMENT ON COLUMN users.first_name IS 'Имя пользователя';
COMMENT ON COLUMN users.last_name IS 'Фамилия пользователя';
COMMENT ON COLUMN users.date_of_birth IS 'Дата рождения пользователя';
COMMENT ON COLUMN users.created_at IS 'Дата создания профиля';
COMMENT ON COLUMN users.updated_at IS 'Дата изменения профиля';


CREATE INDEX IF NOT EXISTS idx_credentials_username ON credentials (username);
CREATE INDEX IF NOT EXISTS idx_credentials_email ON credentials (email);
CREATE INDEX IF NOT EXISTS idx_credentials_role ON credentials (role);

COMMENT ON TABLE credentials IS 'Учётные данные для аутентификации';
COMMENT ON COLUMN credentials.username IS 'Уникальное имя пользователя (бизнес-ключ)';
COMMENT ON COLUMN credentials.email IS 'Email пользователя (бизнес-ключ)';
COMMENT ON COLUMN credentials.password IS 'BCrypt хеш пароля';
COMMENT ON COLUMN credentials.role IS 'Роль: USER, ADMIN, SUPER_USER';
COMMENT ON COLUMN credentials.is_active IS 'Статус пользователя: активен или неактивен';
COMMENT ON COLUMN credentials.failed_login_attempts IS 'Количество неудачных попыток входа';
COMMENT ON COLUMN credentials.last_login IS 'Последний вход пользователя';
COMMENT ON COLUMN credentials.created_at IS 'Дата создания учетных данных';
COMMENT ON COLUMN credentials.updated_at IS 'Дата изменения учетных данных';

INSERT INTO credentials (username, email, password, role, is_active, failed_login_attempts, created_at)
VALUES (
           'superuser',
           'superuser@ebookstore.system',
           '$2y$10$0ce55AhseUUslVuCMb9sqeCleEhTGzF6z4FngXTjxXzAXWuoxiLZC',  -- BCrypt хеш "SuperuserPass123!"
           'SUPER_USER',
           true,
           0,
           NOW()
       )
ON CONFLICT (username) DO NOTHING;  -- если уже существует, не вставляем

SELECT * FROM credentials WHERE username = 'superuser';

```
### 3. Проверить таблицу/таблицы:
```
\dt
\d users
\d+ credentials

\q
```


# === ТЕСТИРОВАНИЕ ===
### 1. Пробросить порты. В первом терминале пробросить auth-service:
```
kubectl -n ebookstore port-forward svc/auth-service 8082:8082
```
### 2. Во втором терминале пробросить user-service:
```
kubectl -n ebookstore port-forward svc/user-service 8081:8081
```
### 2.1 Можно включить аддон-ingress и обойтись без пробрасывания портов (+ проверить статус ingress):
```
minikube addons enable ingress
kubectl -n ebookstore get ingress
```

### 3. Открыть входную дверь в кластер (она же ingress). Сама по себе она не сработает, поэтому надо сделать туннель для полчения внешнего ip:
```
minikube tunnel
```

### 4. Посмотрить ip у ingress и проверить конфиг ingress:
```
kubectl -n ebookstore get ingress ebookstore-ingress
kubectl -n ebookstore describe ingress ebookstore-ingress
```
### 4.1 Если не сработает, то пробросить все же порт на ingress контроллер (может не работать из-за wsl2 + docker desktop):
```
kubectl -n ingress-nginx port-forward service/ingress-nginx-controller 8080:80

```
### 4. В postman закинуть курл на добытый выше айпишник:
```
тут курлы
```

# === УДАЛЕНИЕ ===
### Мгновенное удаление namespace (не рекомендуется, только в экстренных случаях или когда все подвисло):
```
kubectl get namespace ebookstore -o json \
  | tr -d "\n" \
  | sed "s/\"kubernetes\"//g" \
  | kubectl replace --raw /api/v1/namespaces/ebookstore/finalize -f -

```
