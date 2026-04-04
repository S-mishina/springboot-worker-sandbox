# --- Build Stage ---
FROM eclipse-temurin:25-jdk AS build
WORKDIR /app

# Copy gradle wrapper and build files
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
# No settings.gradle in root? If it exists, copy it too.
COPY settings.gradle* .

# Download dependencies (cache layer)
RUN ./gradlew dependencies --no-daemon || true

# Copy source code and build
COPY src src
RUN ./gradlew bootJar --no-daemon

# --- Runtime Stage ---
FROM eclipse-temurin:25-jre
WORKDIR /app

# Create a non-root user for security
RUN groupadd -r spring && useradd -r -g spring spring
USER spring:spring

# Copy the built JAR from the build stage
COPY --from=build /app/build/libs/*.jar app.jar

# Environment variables (default values can be overridden at runtime)
ENV AWS_REGION=ap-northeast-1 \
    AWS_SQS_CONSUMER_ENABLED=true \
    JAVA_OPTS="-Xms512m -Xmx512m"

# Expose port (if applicable, though SQS workers usually don't need it)
EXPOSE 8080

# Run the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
