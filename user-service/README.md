# user-service guideline:


### 1. Создание пользователя:
```
curl -X POST http://localhost:9091/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "username": "test_user",
    "email": "test@example.com",
    "firstName": "Test",
    "lastName": "User"
  }'
```


### 2. Получить всех пользователей:
```
curl -X GET http://localhost:9091/api/users \
  -H "Content-Type: application/json"
```

### 3. Получить пользователя по ID:
```
curl -X GET http://localhost:9091/api/users/1 \
  -H "Content-Type: application/json"
```

### 4. Получить пользователя по username
```
curl -X GET http://localhost:9091/api/users/username/test_user \
  -H "Content-Type: application/json"
```

### 5. Проверить существование пользователя (существует)
```
curl -X GET "http://localhost:9091/api/users/check?username=test_user&email=test@example.com" \
  -H "Content-Type: application/json"
```

### 6. Обновить профиль пользователя
```
curl -X PATCH http://localhost:9091/api/users/1 \
  -H "Content-Type: application/json" \
  -d '{
    "username": "test_user123",
    "email": "duplicate@example.com"
  }'
```
