# 🚀 Release 3 Updates

In this release, we have implemented several new features and enhanced the UI from Release 2. Here are the key updates and enhancements in Release 3:

---

## 🌟 Major Updates

### 🎨  UI and UI Logic enhancements

- Added colors and animations to provide a better user experience.
- Updated the previous implementation of grid view to a table view for displaying tasks related information.
- Changed a lot in terms of backend loagic for adding  items to the table view.

### 💬 Group Chat

- Implemented group chat functionality to facilitate communication and collaboration among group members.
- Users can send and receive messages within their groups and view message history.

### 💰 Shared Expense Management

- Added shared expense management for groups, allowing users to log and track shared expenses.
- Users can view a summary of all shared expenses and balances within their groups.

### 🔐 Password Hashing

- Implemented password hashing for user accounts to enhance security. User passwords are now stored as hashed values instead of plain string in `kollapp/persistence/src/main/java/persistence/users`.

### 🔐 Logout Functionality

- Introduced a logout feature to enhance user session management and security.

### 🌐 REST API and API Module

- Developed a REST API to handle communication between the client and server.
- Created an API module (`gr2409/kollapp/api`) to provide REST web services for the application.
- Utilized Spring Boot for building backend services and ensured maintainability and scalability.

### 🧪 Code Quality and Testing

- Updated and added more tests for the application to ensure reliability and stability.
- Included tests for the new REST services to verify their functionality and performance.

### 📖 Documentation

- Created reflection notes about sustainability using the SuSAF framework.
- Created class, package and sequence diagrams for the final iteration of the project.
- Created two new user stories for group chat and shared expense functionality.
- Updated existing mark down files to reflect the final release.
  
---

## ❌ Scrapped Features

### 🛠️ Task Update Functionality

- Initially planned to reintroduce task update functionality from Release 1, but this feature was more challenging than expected and has been scrapped.

### 📅 Calendar Integration

- Planned to add a calendar feature for visualizing upcoming events and to-dos, but this idea was scrapped in favor of implementing group chat and shared expense management.

---

## 📚 Suggested Reading

- 📖 **[Main README](../../readme.md)**: For an overview of the project, including system requirements and setup instructions.
- 📖 **[Development Practices and Tools](docs/release2/development_practices_and_code_quality.md)**: For detailed information on our work habits, workflow, and code quality practices.
- 📖 **[Testing and Code Quality](docs/release2/testing_and_code_quality.md)**: For a detailed guide on how to run tests and use tools like JaCoCo, SpotBugs, and Checkstyle.
- 📖 **[Project Architecture Diagrams](docs/release2/architecture_diagrams.md)**: For an overview showcasing different diagrams of the project's architecture.
- 📖 **[User Stories](docs/user_stories.md)**: For an updated overview of user stories.
- 📖 **[JSON Save Format](docs/release2/json_format.md)**: For more details about our implementation of JSON.