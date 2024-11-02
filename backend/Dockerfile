# Use an OpenJDK image as the base
FROM openjdk:17-jdk-alpine

# Set the working directory
WORKDIR /app

# Copy the JAR file into the container
COPY revpay-1.0.jar app.jar

# Expose the application port (replace 8080 with your actual app port if different)
EXPOSE 8080

# Define the command to run the JAR file
CMD ["java", "-jar", "app.jar"]
