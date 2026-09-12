# Multi-stage Dockerfile for Wallet & P2P Transfer Service
# Stage 1: Build
FROM eclipse-temurin:17-jdk AS builder

WORKDIR /build

# Copy Maven wrapper and POM first for efficient Docker layer caching
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN chmod +x mvnw && ./mvnw dependency:go-offline -B

# Copy source code and build production artifact
COPY src/ src/
RUN ./mvnw clean package -DskipTests

# Stage 2: Minimal, secure runtime
FROM eclipse-temurin:17-jre AS runtime

# Install wget for HEALTHCHECK
RUN apt-get update && apt-get install -y wget && rm -rf /var/lib/apt/lists/*

# Create non-root system user and group
RUN addgroup --system appgroup && adduser --system --ingroup appgroup appuser

WORKDIR /app

# Copy executable jar from builder stage
COPY --from=builder /build/target/*.jar /app/app.jar

# Set correct ownership
RUN chown -R appuser:appgroup /app

# Switch to non-root user
USER appuser

EXPOSE 8080

# Health check probing Spring Boot Actuator
HEALTHCHECK --interval=15s --timeout=4s --start-period=25s --retries=3 \
  CMD wget -qO- http://localhost:8080/actuator/health | grep -q '"status":"UP"' || exit 1

ENTRYPOINT ["sh", "-c", "exec java -jar /app/app.jar $APP_OPTS"]