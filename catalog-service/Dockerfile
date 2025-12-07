FROM eclipse-temurin:17

WORKDIR /app

COPY target/catalog-service-0.0.1-SNAPSHOT.jar /app/catalog-service.jar

EXPOSE 8083

ENTRYPOINT ["java", "-jar", "/app/catalog-service.jar"]
