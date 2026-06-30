# ── Stage 1: Build ──
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# Copy only pom.xml first to leverage Docker layer caching for dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Now copy the rest of the source and build
COPY src ./src
RUN mvn clean package -DskipTests -B

# ── Stage 2: Run ──
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copy the built jar from the build stage
COPY --from=build /app/target/*.jar app.jar

# Render/Railway set PORT env var dynamically; Spring Boot reads server.port
ENV PORT=8080
EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java -jar app.jar --server.port=${PORT}"]