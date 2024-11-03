#!/bin/bash
#script to start the application
# Clean and install the project without running tests
echo "Running 'mvn clean install -DskipTests'..."
mvn -f ./kollapp clean install -DskipTests

# Start the Spring Boot application
# Update "springboot-module" to the name of your Spring Boot module
echo "Starting Spring Boot application..."
mvn -f ./kollapp/api spring-boot:run &
SPRING_PID=$!

# Wait for Spring Boot to initialize
# Adjust the sleep duration if needed for your Spring Boot startup time
echo "Waiting for Spring Boot to start..."
sleep 10

# Start the JavaFX application
# Update "javafx-module" to the name of your JavaFX module
echo "Starting JavaFX application..."
mvn -f ./kollapp/ui javafx:run

# Stop the Spring Boot process after JavaFX application closes
kill $SPRING_PID
