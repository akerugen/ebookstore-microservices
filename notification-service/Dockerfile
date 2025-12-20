FROM eclipse-temurin:17

WORKDIR /app

COPY target/notification-service-0.0.1-SNAPSHOT.jar /app/notification-service.jar

EXPOSE 8084

ENTRYPOINT ["java", "-jar", "/app/notification-service.jar"]

