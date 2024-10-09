# 📄 Documentation of Work Habits, Workflow, and Code Quality

In this project, we have made conscious choices to ensure an efficient workflow, good work habits, and high code quality. This documentation describes our approach to version control, collaboration, and testing, as well as the tools we have used to maintain code quality.

---

## 🛠️ Work Habits

### Pair Programming and Independent Work

- We combined pair programming with independent work to balance collaboration and individual productivity. Our team held regular meetings three times a week, both online and in-person, to discuss progress, address any challenges, and review code updates. These meetings helped us stay on the same page and keep the project moving forward. Pair programming was particularly beneficial for solving difficult issues and identifying bugs early in the development process.

### Team Roles and Coordination

- In Release 2, we divided our tasks to make the most of our skills and keep things efficient. Ludvig focused on implementing task addition logic and the user login system, while Thomas worked on group management features, such as creating and joining groups. Sang contributed by categorizing tasks, separating unfinished tasks from completed ones. Kien worked on enhancing task addition logic and integrating tasks into JSON with added UI elements. We planned our timelines carefully to make sure each piece was ready for the next person to build on, keeping the workflow smooth and the features well-connected.

### Close Collaboration with Teaching Assistant

- Throughout the project, we maintained close communication with our teaching assistant, Ådne Børresen. Here we asked questions to clarify our doubts and ensure that we fully understood the project requirements. This proactive approach helped us stay on track and align our development with the expectations, making sure we were always on the same page regarding the project's goals and deliverables.

---

## 🔄 Workflow

### Version Control System and Branch Structure

- We use Git as our version control system with a structured branch strategy to ensure stability in the main code (`master` branch). When working on different issues related to predefined user stories, we always create separate feature branches. This prevents conflicts with the `master` branch and allows us to test new features without affecting the main code.

### Merge Requests and Code Reviews

- Before a merge request is sent to `master`, we always ensure that another person in the group reviews and approves the request. This practice ensures that the changes added to the main code are of high quality and follow our coding standards. Code reviews also contribute to knowledge sharing within the group and help identify potential issues early.

---

## ⚙️ Code Quality and Testing

### Approach to Testing

- Even though we developed the tests later in the project, we focused heavily on thoroughly testing all critical functions. We implemented both unit tests and UI tests to ensure that the application behaved as expected in various scenarios. During this process, we faced challenges integrating these tests into our project, as we discovered that specific dependency and Java versions were required to run them successfully, leading to some delays. This experience taught us the importance of carefully managing dependency compatibility and environment configurations early in the development process.

📖 For a quick overview of system requirements and dependencies, visit the **[Main README](../../readme.md)** 

### Tools for Code Quality

  We have used several tools to maintain and improve code quality:

- **JaCoCo** (version: 0.8.10): To measure test coverage and ensure that a large portion of the code is tested.
- **Mockito** (version: 5.0.0): For mocking dependencies in unit tests, allowing us to isolate specific functions.
- **Checkstyle** (version: 3.2.0): To follow coding standards and maintain consistency in the code.
- **SpotBugs** (version: 4.7.3.0): To identify potential errors and weaknesses in the code, allowing us to address them before they become issues in production.

---