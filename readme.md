# **🏠 KollApp: Manage Shared Living Efficiently 💻**

Welcome to **KollApp**, a Java application developed as part of the course **IT1901 - Informatikk Prosjektarbeid I**. This app is designed to help roomates efficiently manage and share household tasks in a shared living space. Let's make communal living stress-free!

---

## **🚀 Quick Start with Eclipse Che**

[Click here to open this project in Eclipse Che](https://che.stud.ntnu.no/#https://gitlab.stud.idi.ntnu.no/it1901/groups-2024/gr2409/gr2409/-/tree/master?ref_type=heads?new)

---

## **🎯 Purpose**

**KollApp** aims to help roommates coordinate and manage household tasks, such as cleaning, dishwashing, grocery shopping, and other shared responsibilities. It enhances communication and ensures that tasks are fairly distributed and completed on time.

📖 **For a detailed description of the purpose and intended functionality, visit [Purpose and Functionality](docs/purpose.md)**

---

## **🔑 Key Feautures**

- **📋 Task Assignment**: Easily assign tasks to roomates.
- **🛒 Shared Purchases**: Track shared expenses for household items.
- **📅 Event Planning**: Plan group events and share them with your roomates.
- **🕒 Task Deadlines**: Add, modify, and manage task deadlines.
- **🔐 Secure Login**: Ensure that only authorized users can access the application.
- **👥 Group Management**: Create and search for groups to manage tasks and activities within specific sets of roommates.

📖 **For a detailed description of user stories, visit [User Stories](docs/user_stories.md)**

---

## **🗂️ Project Architecture**

The **KollApp** project is organized into several modules, each with a specific responsibility. Below is an overview of the project's structure:

**1. Core Module 📂**
  `gr2409/kollapp/core`  
  Contains the core logic of the application.

**2. UI Module 📂**
  `gr2409/kollapp/ui`  
  Contains the user interface components of the application.

**3. Persistence Module 📂**
  `gr2409/kollapp/persistence`  
  Manages the storage and retrieval of data.

📖 For an in-depth look at the modules and their dependencies, visit **[Module Structure and Dependencies](docs/module_structure_and_dependencies.md)**

📖 For an in-depth look at the project's architecture and its components, including a visual representation of module interactions, visit the **[Project Architecture Diagram]()**


---

## **⚙️ System Requirements**

To build and run **KollApp**, make sure you have the following installed:

- **Java version**: 17
- **Maven version**: 3.9.9

---

## **📦 Dependencies**

The **KollApp** application relies on the following dependencies:

- **JavaFX** (version 17.0.12) for building the user interface.
- **JUnit 5** (version 5.10.0) for writing and running unit tests.
- **Mockito** (version 5.0.0) for creating mock objects in tests.
- **TestFX** (version 4.0.16-alpha) for testing JavaFX applications.
- **Hamcrest** (version 2.2) for writing matcher objects for assertions.
- **Jackson** (version 2.18.0-rc1) for JSON processing.
- **Maven** (version 3.9.9) for project management and build automation.

Ensure Maven is installed and properly configured to handle these dependencies.

📖 Full dependency details are available in the **[pom.xml](kollapp/pom.xml)** file.

---

## **🚀 How to Run the Application**

To build and launch **KollApp**, follow these steps:

**1. Navigating to Project Directory 🛠️**
   Before running the commands, navigate to the `kollapp` directory:

   ```sh
   cd kollapp
   ```

**2. Build the Project and Install Dependencies 📦**
    To clean the project and install all required dependencies, run:

   ```sh
   mvn clean install
   ```

**3. Run the Application ▶️**
    After the build is complete, launch `kollapp` with the following command:

   ```sh
   cd ui
   mvn javafx:run
   ```

---

## **🧪 Testing and Code Quality**

Maintaining a high standard of code reliability and performance is essential to the  **KollApp** project. To achieve this, we utilize a range of comprehensive testing and code analysis tools that ensure the robustness of our codebase.

📖 For a detailed guide on how to run tests and use tools like JaCoCo, SpotBugs and Checkstyle, see **[Testing and Code Quality](docs/testing_and_code_quality.md)**

---

## 📚 Suggested Reading

- 📖 **[Release 2](/docs/release2/release2_updates.md)**: For detailed information on major updates.
- 📖 **[Development Practices and Tools](/docs/release2/development_practices_and_code_quality.md)**: For detailed information on our work habits, workflow, and code quality practices.
