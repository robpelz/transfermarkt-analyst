# ============================================
# BACKEND DOCKERFILE
# ============================================

# Stage 1: Build mit Maven
FROM maven:3.9.9-eclipse-temurin-22 AS builder
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Runtime
FROM openjdk:22-jdk-slim
WORKDIR /app
COPY --from=builder /app/target/transfermarkt-analyst-*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]