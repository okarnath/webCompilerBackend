# Use a base image with Java 21 installed
FROM openjdk:21-jdk-slim

# Install Python
RUN apt-get update && apt-get install -y python3

# Set the working directory inside the container
WORKDIR /app

# Copy your Spring Boot JAR file into the container
COPY target/your-app.jar your-app.jar

# Expose the port that the app runs on
EXPOSE 8080

# Command to run the Spring Boot app