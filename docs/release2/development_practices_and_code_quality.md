# 📄 Documentation of Work Habits, Workflow, and Code Quality

In this project, we have made conscious choices to ensure an efficient workflow, good work habits, and high code quality. This documentation describes our approach to version control, collaboration, and testing, as well as the tools we have used to maintain code quality.

---

## 🛠️ Work Habits

- **Pair Programming and Independent Work**:
  We have combined pair programming and independent work to leverage the collective knowledge of the group while maintaining productivity when working individually. Pair programming has been particularly useful for solving complex problems, discussing functionalities, and identifying bugs early in the process.

- **Regular Meetings**:
  The group has had three regular meetings per week where we discussed the project's status, addressed any challenges, and assigned tasks. This has ensured good coordination, progress, and continuous communication among all members.

- **Task Distribution and User Stories**:
  We have divided tasks based on predefined user stories. This has given us a clear picture of what needs to be prioritized and which functionalities should be implemented first, making the development process more focused and structured.

---

## 🔄 Workflow

- **Version Control System and Branch Structure**:
  We use Git as our version control system with a structured branch strategy to ensure stability in the main code (`master` branch). When working on different issues related to predefined user stories, we always create separate feature branches. This prevents conflicts with the `master` branch and allows us to test new features without affecting the main code.

- **Merge Requests and Code Reviews**:
  Before a merge request is sent to `master`, we always ensure that another person in the group reviews and approves the request. This practice ensures that the changes added to the main code are of high quality and follow our coding standards. Code reviews also contribute to knowledge sharing within the group and help identify potential issues early.

---

## ⚙️ Code Quality and Testing

- **Approach to Testing**:
  Although we developed the tests towards the end of the project, we placed great emphasis on thoroughly testing all critical functions. We used both unit tests and integration tests to ensure that the application works as expected under various scenarios.

- **Tools for Code Quality**:
  We have used several tools to maintain and improve code quality:
  - **JaCoCo**: To measure test coverage and ensure that a large portion of the code is tested.
  - **Mockito**: For mocking dependencies in unit tests, allowing us to isolate specific functions.
  - **Checkstyle**: To follow coding standards and maintain consistency in the code.
  - **SpotBugs**: To identify potential errors and weaknesses in the code, allowing us to address them before they become issues in production.

- **Settings and Configuration**:
  - **JaCoCo** is configured to generate detailed reports on test coverage after each project build.
  - **Checkstyle** is set up with rules that follow standard Java coding conventions to make the code more readable and maintainable.
  - **SpotBugs** is integrated into our CI pipeline, automatically running and analyzing the code with each pull request.

---

## 🤝 Collaboration and Process Improvement

- We have worked in short iterations (sprints), which have allowed us to deliver improvements continuously and quickly adapt to any changes in the project.
- After each iteration, we have held retrospective meetings to evaluate what worked well and what could be improved in our workflow.
- All progress and decisions have been thoroughly documented to ensure that the project has been well-organized and transparent.

---

## 📈 Summary

Our structured workflow, along with the use of effective tools such as **JaCoCo**, **Mockito**, **Checkstyle**, and **SpotBugs**, has enabled us to maintain high code quality and efficiency in the development process. Our approach to using branches and code reviews has contributed to a stable and well-functioning project that all group members have benefited from.