# Usa una imagen con JDK 21 y Maven ya configurados
FROM maven:3.9.4-eclipse-temurin-21 AS builder

WORKDIR /app

# Copia pom.xml y descarga dependencias primero (cache optimizada)
COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .
RUN ./mvnw dependency:go-offline -B

# Copia el resto del código
COPY src ./src

# Compila el proyecto (sin tests)
RUN ./mvnw package -DskipTests -B


# Etapa final: crea imagen liviana con solo el JAR
FROM eclipse-temurin:21-jdk
WORKDIR /app
COPY --from=builder /app/target/api-0.0.1-SNAPSHOT.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]


