# 📦 Module Structure and Dependencies

This document provides an overview of the module structure, dependencies, and plugins used in the **KollApp** project. Each module is organized based on its responsibilities and includes specific plugins and dependencies to support its functionality.

---

## 📁 Module Structure

The **KollApp** project is organized into the following modules:

### 1. Core Module 🛠️

- **Path**: `gr2409/kollapp/core`
- **Description**: Contains the core logic of the application, independent of any user interface. Focuses on core functionality.
- **Dependencies**:
  - **JUnit 5**: [`org.junit.jupiter:junit-jupiter-api`](https://junit.org/junit5/), [`org.junit.jupiter:junit-jupiter-engine`](https://junit.org/junit5/), [`org.junit.jupiter:junit-jupiter-params`](https://junit.org/junit5/) for writing and running unit tests.
  - **Mockito**: [`org.mockito:mockito-core`](https://site.mockito.org/), [`org.mockito:mockito-junit-jupiter`](https://site.mockito.org/), with **Byte Buddy** ([`net.bytebuddy:byte-buddy`](https://bytebuddy.net/), [`net.bytebuddy:byte-buddy-agent`](https://bytebuddy.net/)) for creating mock objects in tests.
  - **Jackson**: [`com.fasterxml.jackson.core:jackson-core`](https://github.com/FasterXML/jackson-core), [`com.fasterxml.jackson.core:jackson-databind`](https://github.com/FasterXML/jackson-databind), [`com.fasterxml.jackson.datatype:jackson-datatype-jsr310`](https://github.com/FasterXML/jackson-modules-java8) for JSON processing.
- **Plugins**:
  - **Maven Compiler Plugin**: Ensures compilation to the appropriate Java version.
  - **JaCoCo Maven Plugin**: Generates code coverage reports for core logic.
  - **Surefire Plugin**: Executes tests during the build process.

---

### 2. UI Module 🎨

- **Path**: `gr2409/kollapp/ui`
- **Description**: Contains the user interface components built with JavaFX.
- **Dependencies**:
  - **JavaFX**: [`org.openjfx:javafx-base`](https://openjfx.io/), [`org.openjfx:javafx-controls`](https://openjfx.io/), [`org.openjfx:javafx-fxml`](https://openjfx.io/), [`org.openjfx:javafx-graphics`](https://openjfx.io/) for building the UI.
    - **Important Note**: JavaFX dependencies are included, but if you are using an operating system that does not bundle JavaFX by default, you may need to download the JavaFX modules (jmods) manually. Visit **[Gluon’s JavaFX page](https://gluonhq.com/products/javafx/)** to download these modules as needed.
  - **JUnit 5**: For unit testing.
  - **TestFX**: [`org.testfx:testfx-core`](https://testfx.github.io/TestFX/), [`org.testfx:testfx-junit5`](https://testfx.github.io/TestFX/), [`org.testfx:openjfx-monocle`](https://testfx.github.io/TestFX/) (for headless testing) to test JavaFX applications.
  - **Mockito**: For mocking objects in UI tests.
  - **Hamcrest**: [`org.hamcrest:hamcrest`](http://hamcrest.org/) for assertions in tests.
- **Plugins**:
  - **JavaFX Maven Plugin**: Supports building and running JavaFX applications.
  - **Checkstyle Plugin**: Enforces code style and ensures readability.
  - **Antrun Plugin**: Manages any file operations needed during packaging.
  - **JPackage Maven Plugin**: Creates platform-specific installers (e.g., EXE, DMG, DEB) with profiles:
    - **Windows Profile**: Configures `JPackage` to create an EXE package.
    - **MacOS Profile**: Configures `JPackage` to create a DMG package.
    - **Linux Profile**: Configures `JPackage` to create a DEB package.

---

### 3. Persistence Module 💾

- **Path**: `gr2409/kollapp/persistence`
- **Description**: Manages data storage and retrieval using JSON files, handling user data, to-do lists, and group information.
- **Dependencies**:
  - **JUnit 5**: For writing and running unit tests.
  - **Mockito**: For mocking objects in persistence tests.
  - **Jackson**: For handling JSON storage format, with data binding support for JSON serialization/deserialization.
- **Plugins**:
  - **Surefire Plugin**: Runs unit tests during builds.
  - **JaCoCo Maven Plugin**: Reports code coverage to validate persistence logic.

---

### 4. API Module 🌐

- **Path**: `gr2409/kollapp/api`
- **Description**: Provides REST web services and manages server-client communication. Interfaces with the persistence module to store and retrieve data.
- **Dependencies**:
  - **Spring Boot**: [`org.springframework.boot:spring-boot-starter-web`](https://spring.io/projects/spring-boot) for backend services, [`org.springframework:spring-security-crypto`](https://spring.io/projects/spring-security) for secure data handling.
  - **JUnit 5**: For unit testing.
  - **Mockito**: For creating mock objects in API tests.
  - **Jackson**: For JSON processing between client and server.
- **Plugins**:
  - **Spring Boot Maven Plugin**: Supports building and running the Spring Boot application.
  - **SpotBugs Plugin**: Analyzes code for potential bugs and security vulnerabilities.
  - **JaCoCo Maven Plugin**: Tracks code coverage to ensure API reliability.
  - **Surefire Plugin**: Executes unit tests during the build process.

---

## 📁 Storage Strategy

### Document Metaphor (Desktop) vs. Implicit Storage (App)

**KollApp** employs an implicit storage strategy, where data is saved automatically without explicit user actions, enhancing user experience and reducing the risk of data loss.

📖 For more on JSON format, see **[JSON Save Format](json_format.md)**.

---

📖 Return to the **[Main README](../../readme.md)** for a complete project overview.
