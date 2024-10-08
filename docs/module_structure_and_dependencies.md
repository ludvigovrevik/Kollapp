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
- **Description**: Manages the storage and retrieval of data. This module handles the persistence of application data to various storage systems.
- **Dependencies**:
  - **JUnit 5**: For writing and running unit tests.
  - **Mockito**: For creating mock objects in tests.
  - **Jackson**: For JSON processing.

By following this architecture, **KollApp** ensures a maintainable, scalable, and robust application structure.  

---

## 📁 Storage Strategy

### Document Metaphor (Desktop) vs. Implicit Storage (App)

**KollApp** uses an implicit storage strategy, similar to modern applications, where data is automatically saved without requiring explicit user actions. This approach ensures a seamless user experience, reducing the risk of data loss.

📖 For detailed information about the JSON save format used for users, to-do lists, and groups, see **[JSON Save Format](json_format.md)**.

---

📖 Return to the **[Main README](../README.md)** for additional information and project overview.