# 1) build stage con Java 21
FROM maven:3.9.0-eclipse-temurin-21 AS builder
WORKDIR /app
COPY pom.xml mvnw ./
COPY .mvn .mvn
RUN chmod +x mvnw
RUN ./mvnw dependency:go-offline -B
COPY src src
RUN ./mvnw package -DskipTests -B

# 2) run stage con JRE 21
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
ENV PORT=${PORT}
ENTRYPOINT ["java","-jar","app.jar"]

