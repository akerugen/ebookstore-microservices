FROM eclipse-temurin:17

WORKDIR /app

COPY target/auth-service-0.0.1-SNAPSHOT.jar /app/auth-service.jar

# like server.port
EXPOSE 8082

ENTRYPOINT ["java", "-jar", "/app/auth-service.jar"]