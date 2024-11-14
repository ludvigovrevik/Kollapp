# 🛠️ Release 3 Development Practices and Code Quality

In Release 3, we continued to follow the good work habits and workflow established in Release 2. Our focus remained on maintaining high code quality and ensuring a smooth development process. Here are some key practices and tools we used:

---

## Good Work Habits

### Consistent Coding Standards

We made sure to keep things consistent across the project with clear coding standards. This meant sticking to naming conventions, formatting, and documenting things as we went, which made the code more readable and easier to maintain.

### Code Reviews

We used regular code reviews, often meeting on Discord or in person, to catch any issues early and uphold our quality standards. This collaborative process helped improve the overall codebase and allowed us to share insights as a team.

### Version Control

We used Git for version control, which made it easy to track changes and roll things back if needed. With branching and merging strategies, we managed feature development and kept things organized.

### Collaboration with Teaching Assistant

Working closely with our teaching assistant, Ådne Børresen. We checked in with Ådne on best practices, especially for security around new features. The feedback was super helpful, letting us tackle potential issues before they could impact the project.

---

## Workflow

### Agile Methodology

Following Agile principles, we broke the project into sprints. This kept us focused on incremental improvements and ready to adapt as needed.

### Version Control System and Branch Structure

We kept our Git branching structure going strong, with a stable `master` branch and feature branches for new stuff. This approach minimized conflicts and kept the main codebase stable.

### Merge Requests and Code Reviews

We continued the practice of requiring peer reviews before merging any changes into `master`. This practice proved particularly useful for ensuring code quality and catching potential issues early.

### Continuous Integration

We set up Continuous Integration (CI) to build and test the project automatically when new code was pushed. This kept things stable and helped catch issues early on.

### Issue Tracking

We used GitLab’s issue tracker to stay on top of tasks, bugs, and new features. It kept us organized and helped us prioritize effectively.

---

## Code Quality

### Unit Testing

We wrote a ton of unit tests using JUnit 5 to make sure individual components worked as expected. These tests helped us catch bugs early and made refactoring less risky.

### Integration Testing

Integration tests were used to verify that different components of the application worked together correctly. This ensured that the system as a whole functioned as intended. We also used Postman to test the API during development, which helped us identify and fix issues early in the process.

### Code Coverage

We used JaCoCo for measuring code coverage. It showed us which parts of the code needed more testing, helping us improve our overall test coverage.

### Static Code Analysis

We used Checkstyle and SpotBugs to scan the code for potential issues and enforce coding standards. This helped us catch bugs early and keep the code clean.

### Mock Testing

Mockito were also used to test components in isolation and keep tests focused on specific functionalities.

### UI Testing

TestFX allowed us to test the JavaFX user interface in a headless environment. This helped us keep the user experience consistent and catch UI issues early.

### Readable Assertions

Hamcrest made our assertions more readable and expressive, making our tests clearer and easier to understand.

### Test Execution

We used the Maven Surefire Plugin to run all the tests, making sure everything was executed consistently.

## 📚 Suggested Reading

- 📖 **[Testing and Code Quality](testing_and_code_quality.md)**: For a detailed guide on how to run tests and use tools like JaCoCo, SpotBugs and Checkstyle.
- 📖 **[Module Structure and Dependencies](module_structure_and_dependencies.md)**: For an in-depth look at the modules and their dependencies for **KollApp**

---

📖 Return to the **[Main README](../../readme.md)** for additional information and project overview.
