# build
FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /app

# Cache of depend
COPY pom.xml .
RUN mvn -q -DskipTests dependency:go-offline

# Build
COPY src ./src
RUN mvn -q -DskipTests clean package

# Run
FROM eclipse-temurin:17-jre
WORKDIR /app

# Healthboost
RUN useradd -ms /bin/bash appuser
USER appuser


# COPY of jar
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]