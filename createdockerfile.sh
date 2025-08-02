# Use official JDK image
FROM openjdk:21-jdk-slim

# Set working directory
WORKDIR /app

# Copy the built jar into the container
COPY target/prik2go-backend.jar app.jar

# Expose port 8080
EXPOSE 8080

# Set environment variables (can be overridden via .env or docker-compose)
ENV SPRING_PROFILES_ACTIVE=default

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
