# Multi-stage Dockerfile for Spring Boot services
# WHY MULTI-STAGE BUILDS?
# - Stage 1 (builder): Full JDK + Maven for compilation (~500MB image)
# - Stage 2 (runtime): Minimal JRE only for running the app (~150MB image)
# - Final image is 70% smaller — faster deploys, smaller attack surface

# ─── Stage 1: Build ──────────────────────────────────────────────────────────
FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /app

# Copy dependency files first (Docker layer caching optimization)
# If pom.xml doesn't change, Maven doesn't re-download dependencies
COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .

# Download dependencies (cached layer — only invalidated on pom.xml change)
RUN chmod +x ./mvnw && ./mvnw dependency:go-offline -B

# Copy source code (changes frequently — separate layer)
COPY src ./src

# Build, skipping tests (tests run in CI pipeline, not Docker build)
RUN ./mvnw clean package -DskipTests -B

# ─── Stage 2: Runtime ─────────────────────────────────────────────────────────
FROM eclipse-temurin:21-jre-alpine AS runtime

# Security: Run as non-root user
# NEVER run production containers as root!
RUN addgroup -S shortify && adduser -S shortify -G shortify
USER shortify

WORKDIR /app

# Copy only the built JAR (not source code, not Maven, not JDK)
COPY --from=builder /app/target/*.jar app.jar

# Expose service port (documentation only — actual binding via docker-compose)
EXPOSE 8080

# JVM tuning for containerized environments:
# -XX:+UseContainerSupport: JVM respects Docker memory limits (Java 11+)
# -XX:MaxRAMPercentage=75.0: Use 75% of container memory for heap
# -XX:+UseG1GC: G1 garbage collector — balanced throughput and latency
# -Djava.security.egd: Faster random number generation in containers
ENTRYPOINT ["java", \
  "-XX:+UseContainerSupport", \
  "-XX:MaxRAMPercentage=75.0", \
  "-XX:+UseG1GC", \
  "-Djava.security.egd=file:/dev/./urandom", \
  "-jar", "app.jar"]
