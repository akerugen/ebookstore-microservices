# сгенерировать сложный пароль:
```
openssl rand -base64 32
```

# применить через kubectl create secret
```
kubectl create secret generic app-secret \
--from-literal=JWT_SECRET=$(openssl rand -base64 32) \
-n ebookstore
```