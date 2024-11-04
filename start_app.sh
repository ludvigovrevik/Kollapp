#!/bin/bash
# Script to start the application
# Clean and install the project without running tests
echo "Running 'mvn clean install -DskipTests'..."
mvn -f ./kollapp clean install -DskipTests

# Start the Spring Boot application
echo "Starting Spring Boot application..."
mvn -f ./kollapp/api spring-boot:run &
SPRING_PID=$!

# Wait for Spring Boot to initialize
echo "Waiting for Spring Boot to start..."
sleep 5

# Start the JavaFX application
echo "Starting JavaFX application..."
mvn -f ./kollapp/ui javafx:run

# Stop the Spring Boot process after JavaFX application closes
kill $SPRING_PID