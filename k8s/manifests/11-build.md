```
chmod +x deploy-all.sh
./deploy-all.sh
```

# Все поды запущены?
```
kubectl -n ebookstore get pods
```

# Сервисы работают?
```
kubectl -n ebookstore get svc
```

# Deployments здоровы?
```
kubectl -n ebookstore get deployment
```

# Логи приложения
```
kubectl -n ebookstore logs -f deployment/auth-service
```

# Описание пода (если проблемы)
```
kubectl -n ebookstore describe pod <pod-name>
```