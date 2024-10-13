# 🚀 Release 2 Updates

In this release, we have made significant improvements and additions to the **KollApp** project. In Release 1, we began with a basic to-do list application as a foundation, implementing simple functionality such as adding tasks with due dates and saving them in a text file. This initial step was part of our broader vision to evolve the project into a more comprehensive application. Here are the key updates and enhancements in Release 2:

---

## 🌟 Major Updates

### 🎨 Enhanced UI and Logic

- Completely revamped the user interface for a more user-friendly experience.
- Improved the underlying logic to support more complex operations and interactions.

### 📦 Modularized Project Structure

- Organized the project into a modular structure with separate modules for `core`, `ui`, and `persistence`.
- This modularization enhances maintainability and scalability.
- Relevant code paths: `kollapp/core/`, `kollapp/ui/`, `kollapp/persistence/`

### 📂 JSON Data Persistence

- Switched from text file storage to JSON format for data persistence.
- Utilized the Jackson library for efficient JSON processing.
- 📖 For more details about our implementation of JSON, visit **[JSON Save Format](json_format.md)**

### ✅ Task Management

- Added support for task priorities and completion status.
- Enhanced task management features, including the ability to add and complete tasks.

### 👥 Group Functionality

- Introduced the concept of user groups, allowing users to create and add users to groups.
- Each group has its own to-do list, enabling collaborative task management.

### 🔐 Login Feature

- Introduced a login feature to enhance user authentication and session management.
- Relevant code paths:
  - `kollapp/ui/src/main/resources/ui/LoginScreen.fxml`
  - `kollapp/ui/src/main/resources/ui/RegisterScreen.fxml`
  - `kollapp/ui/src/main/java/ui/LoginController.java`
  - `kollapp/ui/src/main/java/ui/RegisterController.java`

---

## 🛠️ Tools and Libraries

### 📊 JaCoCo

- Version 0.8.10
- Integrated JaCoCo for code coverage analysis.
- Ensured that our tests cover a significant portion of the codebase.

### 🧪 Mockito

- Version 5.0.0
- Utilized Mockito for creating mock objects in tests.
- Enhanced the robustness of our unit tests by isolating dependencies.

### 🐞 SpotBugs

- Version 4.7.3.0
- Incorporated SpotBugs for static code analysis.
- Identified and fixed potential bugs.

### 🛡️ Checkstyle

- Version 3.2.0
- Implemented Checkstyle for code style and formatting checks.
- Ensured consistent coding standards across the project.

📖 For more related information, visit **[Module Structure and Dependencies](module_structure_and_dependencies.md)** documentation.

---

## 🧪 Testing and Code Quality

- Implemented unit and UI tests for all major functionalities.

📖 For more details about getting started with testing and its respective tools, see **[Testing and Code Quality](testing_and_code_quality.md)** documentation.

---

## 🔮 Future Plans

In the next release, we plan to implement the following features:

### 🔐 Logout Feature

- Adding a logout feature to enhance user session management and security.

### 🛠️ Task Update Functionality

- Reintroducing the ability to update tasks, which was present in Release 1.
- Completely redesigning the logic behind updating tasks to improve efficiency and usability.

### 💰 Shared Cost Tracking

- Implementing shared cost tracking to allow users to manage and split expenses within groups.

### 📅 Calendar Integration

- Adding a calendar feature to visualize upcoming events and to-dos, helping users keep track of their schedules.

---

## 📚 Suggested Reading

- 📖 **[Main README](../../readme.md)**: For an overview of the project, including system requirements and setup instructions.
- 📖 **[Development Practices and Tools](development_practices_and_code_quality.md)**: For detailed information on our work habits, workflow, and code quality practices.
- 📖 **[Project Architecture Diagrams](architecture_diagrams.md)**: For an overview showcasing different diagrams of the project's architecture.
- 📖 **[User Stories](../user_stories.md)**: For an updated overview of user stories.
- 📖 **[JSON Save Format](json_format.md)**: For more details about our implementation of JSON.
- 📖 **[Testing and Code Quality](testing_and_code_quality.md)**: For a detailed guide on how to run tests and use tools like JaCoCo, SpotBugs and Checkstyle.
- 📖 **[AI declaration](ai_declaration.md)**: For an overview about AI tools utilized in the project.

---

📖 Return to the **[Main README](../../readme.md)** for additional information and project overview.
