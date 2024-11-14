# 📦 Module Structure and Dependencies

This document provides an overview of the module structure and dependencies used in the **KollApp** project. It outlines the organization of the project into different modules and the specific dependencies required for each module.

---

## 📁 Module Structure

The **KollApp** project is organized into the following modules:

### 1. Core Module 🛠️

- **Path**: `gr2409/kollapp/core`
- **Description**: Contains the core logic of the application. This module is independent of any user interface and focuses on the core functionality.
- **Dependencies**:
  - **JUnit 5**: For writing and running unit tests.
  - **Mockito**: For creating mock objects in tests.
  - **Jackson**: For JSON processing.

### 2. UI Module 🎨

- **Path**: `gr2409/kollapp/ui`
- **Description**: Contains the user interface components of the application. This module uses JavaFX to create the graphical user interface.
- **Dependencies**:
  - **JavaFX**: For building the user interface.
  - **JUnit 5**: For writing and running unit tests.
  - **TestFX**: For testing JavaFX applications.
  - **Mockito**: For creating mock objects in tests.

### 3. Persistence Module 💾

- **Path**: `gr2409/kollapp/persistence`
- **Description**: Manages the storage and retrieval of data by storing it in JSON files. This module ensures that user data, to-do lists, group information, and other relevant data are saved efficiently.
- **Dependencies**:
  - **JUnit 5**: For writing and running unit tests.
  - **Mockito**: For creating mock objects in tests.
  - **Jackson**: For JSON processing.

### 4. API Module 🌐

- **Path**: `gr2409/kollapp/api`
- **Description**: Provides REST web services for the application. This module handles the communication between the client and the server and writes JSON files to the persistence module.
- **Dependencies**:
  - **Spring Boot**: For building the backend services.
  - **JUnit 5**: For writing and running unit tests.
  - **Mockito**: For creating mock objects in tests.
  - **Jackson**: For JSON processing.

---

## 📁 Storage Strategy

### Document Metaphor (Desktop) vs. Implicit Storage (App)

**KollApp** uses an implicit storage strategy, similar to modern applications, where data is automatically saved without requiring explicit user actions. This approach ensures a seamless user experience, reducing the risk of data loss.

📖 For detailed information about the JSON save format used for users, to-do lists, and groups, see **[JSON Save Format](json_format.md)**

---

📖 Return to the **[Main README](../../readme.md)** for additional information and project overview.