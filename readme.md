# Project: KollApp

##Eclipse Che
[Press here to open this project in Eclipse Che]
https://che.stud.ntnu.no/#https://gitlab.stud.idi.ntnu.no/it1901/groups-2024/gr2409/gr2409/-/tree/master?new

## Purpose

This repository is for developing **KollApp**, a Java application created as part of the course **Informatikk Prosjektarbeid I - IT1901**.

**KollApp** is designed to help roommates in a shared flat manage and share household tasks more efficiently. The application provides an overview of shared responsibilities such as house cleaning, unloading the dishwasher, grocery shopping, and other communal tasks. It aims to improve communication and coordination among flatmates, ensuring that chores are evenly distributed and completed on time.

## **You can read more about purpose [here](docs/purpose.md).**
## Repository Structure

- **Main Application Code**:  
  `gr2409/kollapp/src/main/java/app`  
  Contains the Java files responsible for handling the application's logic and functionality.

- **Resources**:  
  `gr2409/kollapp/src/main/resources/app`  
  Contains resource files such as `.fxml` files for the user interface.

- **Tests**:  
  `gr2409/kollapp/src/test/java/app`  
  Contains unit tests and other test cases for the Java application.

## Requirements

To build and run this project, you need the following:

- **Java version**: 17
- **Maven version**: 3.9.9

All required dependencies for the project are listed in the `pom.xml` file. Some of the major dependencies include:

- **JavaFX** (version 17.0.12) for building the user interface.
- **JUnit 5** (version 5.10.0) for testing.
- **TestFX** (version 4.0.16-alpha) for testing JavaFX components.
- **Hamcrest** (version 2.2) for writing matchers in unit tests.

## How to Run the Application

1. **Navigate to the `kollapp` directory**:

   ```bash
   cd kollapp
   ```

2. **Run the application using `Maven`:**:

   ```bash
   mvn javafx:run
   ```
3. **Run JUnit tests using `Maven`:**:

   ```bash
   mvn test
   ```