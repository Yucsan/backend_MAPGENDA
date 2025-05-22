# 1) build stage
FROM maven:3.9.0-eclipse-temurin-17 AS builder
WORKDIR /app
COPY pom.xml mvnw ./
COPY .mvn .mvn
RUN mvnw dependency:go-offline -B

COPY src src
RUN mvnw package -DskipTests -B

# 2) run stage
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
ENV PORT=${PORT}
ENTRYPOINT ["java","-jar","app.jar"]
