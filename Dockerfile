# Use Java 21 slim image
FROM eclipse-temurin:21-jre

# Set working directory
WORKDIR /app

# Copy the built JAR into the container
COPY target/events-0.0.1-SNAPSHOT.jar app.jar

# Expose the application's port
EXPOSE 8088

# Run the application
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
