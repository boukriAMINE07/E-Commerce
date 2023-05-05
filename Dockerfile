FROM khipu/openjdk17-alpine

# Set the working directory to /app
WORKDIR /app

# Copy the application jar file to the container
COPY target/*.jar ecommerce.jar

# Expose port 8080 for the container
EXPOSE 8080
# Run the jar file when the container launches
ENTRYPOINT ["java", "-jar", "ecommerce.jar"]
