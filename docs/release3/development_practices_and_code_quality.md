# 🛠️ Development Practices and Code Quality - Release 3

In Release 3, we continued to follow the good work habits and workflow established in Release 2. Our focus remained on maintaining high code quality and ensuring a smooth development process. Here are some key practices and tools we used:

---

## Good Work Habits

### Consistent Coding Standards

We adhered to consistent coding standards across the project to ensure readability and maintainability. This included following naming conventions, code formatting, and documentation practices.

### Code Reviews

Regular code reviews were conducted to catch potential issues early and ensure that all code met our quality standards. This collaborative process helped improve the overall quality of the codebase.

### Version Control

We used Git for version control, ensuring that all changes were tracked and could be easily reverted if necessary. Branching and merging strategies were employed to manage feature development and integration.

### Collaboration with Teaching Assistant

Our close collaboration with our teaching assistant, Ådne Børresen, remained invaluable in Release 3. We consulted with Ådne to clarify best practices for secure data handling, especially regarding our new features for release 3. The feedbacks from Ådne was helpful, as it helped us catch and address potential security issues before they could impact the project.

---

## Workflow

### Agile Methodology

We followed an Agile methodology, breaking down the project into manageable sprints. This allowed us to focus on delivering incremental improvements and adapting to changes as needed.

### Version Control System and Branch Structure

We maintained our structured Git branching strategy, using a `master` branch for stable code and creating feature branches for each new functionality. This approach minimized conflicts and kept our main codebase stable.

### Merge Requests and Code Reviews

We continued the practice of requiring peer reviews before merging any changes into `master`. This practice proved particularly useful for ensuring code quality and catching potential issues early.

### Continuous Integration

Continuous Integration (CI) was set up to automatically build and test the project whenever changes were pushed to the repository. This helped catch issues early and ensured that the codebase remained stable.

### Issue Tracking

We used GitLab's issue tracking system to manage tasks, bugs, and feature requests. This helped us stay organized and prioritize work effectively.

---

## Code Quality

### Unit Testing

Extensive unit tests were written using JUnit 5 to ensure that individual components worked as expected. This helped catch bugs early and provided a safety net for refactoring.

### Integration Testing

Integration tests were conducted to verify that different components of the application worked together correctly. This ensured that the system as a whole functioned as intended. We also used Postman to test the API during development, which helped us identify and fix issues early in the process.

### Code Coverage

We used JaCoCo to measure code coverage and ensure that our tests covered a significant portion of the codebase. This helped identify untested areas and improve overall test coverage.

### Static Code Analysis

Tools like Checkstyle and SpotBugs were used to analyze the code for potential issues and enforce coding standards. This helped maintain a high level of code quality and catch potential bugs early.

### Mock Testing

Mockito was used for creating mock objects in tests, allowing us to isolate components and test them independently. This helped ensure that our tests were reliable and focused on specific functionalities.

### UI Testing

TestFX was used for testing JavaFX applications in a headless environment, ensuring that the user interface behaved as expected. This helped maintain a consistent user experience and catch UI-related issues early.

### Readable Assertions

Hamcrest was used to make assertions more readable and expressive, improving the clarity of our tests.

### Test Execution

The Maven Surefire Plugin was used to run the tests defined in the project, ensuring that all tests were executed consistently.

---

📖 Return to the **[Main README](../../readme.md)** for additional information and project overview.
