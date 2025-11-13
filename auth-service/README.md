# auth-service guideline:


### 1. Регистрация пользователя:
```
curl -X POST http://localhost:9092/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "ivan_test",
    "email": "ivan@test.com",
    "password": "SecurePass123!",
    "confirmPassword": "SecurePass123!"
  }'
```

### 2. Вход пользователя:
```
curl -X POST http://localhost:9092/api/auth/login \
   -H "Content-Type: application/json" \
   -d '{
   "usernameOrEmail": "ivan_test",
   "password": "SecurePass123!"
   }'
```

### 4. Проверка токена:
```
curl -X POST "http://localhost:9092/api/auth/validate?token=YOUR_ACCESS_TOKEN"
```

### 5. Рефреш токена:
```
curl -X POST http://localhost:9092/api/auth/refresh \
  -H "Content-Type: application/json" \
  -d '{
    "refreshToken": "YOUR_REFRESH_TOKEN"
  }'
```

### 6. Выход из учетки:
```
curl -X POST "http://localhost:9092/api/auth/logout?refreshToken=YOUR_REFRESH_TOKEN"
```

### 7. Логин под суперюзером:
```
curl -s -X POST http://localhost:9092/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "usernameOrEmail": "superuser",
    "password": "SuperuserPass123!"
  }'
```