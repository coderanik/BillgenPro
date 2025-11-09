# Multi-stage build for Spring Boot application
# Build stage - Maven with Temurin JDK 17 (supports ARM64)
FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /app

# Copy pom.xml and download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code and build
COPY src ./src
RUN mvn clean package -DskipTests

# Runtime stage - Use standard JRE (better ARM64 support than Alpine)
FROM eclipse-temurin:17-jre

WORKDIR /app

# Install curl for healthcheck (lighter than wget, better availability)
RUN apt-get update && \
    apt-get install -y --no-install-recommends curl && \
    rm -rf /var/lib/apt/lists/* && \
    apt-get clean

# Create non-root user for security
RUN groupadd -r spring && useradd -r -g spring spring
USER spring:spring

# Copy jar from build stage
COPY --from=build /app/target/*.jar app.jar

# Expose port (Spring Boot will use PORT env var or default to 5000)
EXPOSE 5000

# Health check using curl
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD curl -f http://localhost:5000/login || exit 1

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]

