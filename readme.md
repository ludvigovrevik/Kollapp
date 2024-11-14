# **🏠 KollApp: Manage Shared Living Efficiently 💻**

Welcome to **KollApp**, a Java application developed as part of the course **IT1901 - Informatikk Prosjektarbeid I**. This app is designed to help roomates efficiently manage and share household tasks in a shared living space. Let's make communal living stress-free!

---

## 🚀 Quick Start with Eclipse Che

For an example use of **KollApp**, check out these accounts:

| Username | Password |
|----------|----------|
| kien     | 12341234 |
| thomas   | 12341234 |
| sang     | 12341234 |
| ludvig   | 12341234 |

Feel free to register a new account if you'd like to explore on your own!

[Open this project in Eclipse Che](https://che.stud.ntnu.no/#https://gitlab.stud.idi.ntnu.no/it1901/groups-2024/gr2409/gr2409?new)

---

## **🎯 Purpose**

**KollApp** aims to help roommates coordinate and manage household tasks, such as cleaning, dishwashing, grocery shopping, and other shared responsibilities. It enhances communication and ensures that tasks are fairly distributed and completed on time.

📖 For a detailed description of the purpose and intended functionality, visit **[Purpose and Functionality](docs/purpose.md)**
📖 To view examples of use cases for KollApp, visit **[Gallery](./docs/release3/gallery.md)**. **Highly Recommended!**

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
  Manages the storage of data.

**4. API Module 📂**
  `gr2409/kollapp/api`  
  Handles the communication between the client and the server of the application.

📖 For an in-depth look at the modules and their dependencies, visit **[Module Structure and Dependencies](docs/release3/module_structure_and_dependencies.md)**

📖 For an in-depth look at the project's architecture and its components, including a visual representation of module interactions, visit the **[Project Architecture Diagram](/docs/release3/architecture_diagrams.md)**

---

## **⚙️ System Requirements**

To build and run **KollApp**, make sure you have the following installed:

| System Requirement   |                                                                                                  |
|----------------------|--------------------------------------------------------------------------------------------------|
| Java version: 17     | **[ Download ]( https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html )** |
| Maven version: 3.9.9 | **[ Download ]( https://maven.apache.org/download.cgi )**                                        |

---

## **📦 Dependencies**

The **KollApp** application relies on a set of essential dependencies for its functionality:

- **JavaFX** (version 17.0.12): Provides the graphical user interface. For additional JavaFX modules, visit **[Gluon’s JavaFX page](https://gluonhq.com/products/javafx/)** for downloads.
- **JUnit 5** (version 5.10.0): Used for unit testing across all modules to ensure code quality and reliability.
- **Mockito** (version 5.0.0): Facilitates mock testing, helping to simulate dependencies in test environments.
- **TestFX** (version 4.0.16-alpha): Specifically for testing JavaFX applications to maintain a consistent user experience.
- **Jackson** (version 2.18.0-rc1): Manages JSON data processing, essential for data storage and retrieval in JSON format.
- **Spring Boot** (version 2.7.3): Powers the backend REST services, allowing client-server communication.
- **Maven** (version 3.9.9): Manages project dependencies, builds, and plugin integrations.

These dependencies provide a stable foundation for development, ensuring both functionality and maintainability. For a complete list and version details, refer to the **[pom.xml](kollapp/pom.xml)** file.

📖 For an in-depth look at the modules and their dependencies, visit **[Module Structure and Dependencies](docs/release3/module_structure_and_dependencies.md)**

---

## **🚀 How to Run the Application**
  
❗️ From the `gr2409` folder, enter the following script on the terminal:

```sh
./start_app.sh
```

Alternatively, to build and launch **KollApp** without script, follow these steps:

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

**3. Run Springboot 🌱**

  To start the backend services of **KollApp**, you need to run the Spring Boot application. This will initialize the REST API and other backend functionalities.

  ```sh
  cd api
  mvn spring-boot:run
  ```

**4. Run the Application ▶️**
  After running Springboot, launch `kollapp` with the following command:

   ```sh
   cd ..
   cd ui
   mvn javafx:run
   ```

 📖 For a detailed guide on how to create a shippable product, visit **[Creating a Shippable Product](docs/release3/create_shippable_product.md)**

  ---

## **🧪 Testing and Code Quality**

Maintaining a high standard of code reliability and performance is essential to the  **KollApp** project. To achieve this, we utilize a range of comprehensive testing and code analysis tools that ensure the robustness of our codebase.

📖 For a detailed guide on how to run tests and use tools like JaCoCo, SpotBugs and Checkstyle, see **[Testing and Code Quality](docs/release2/testing_and_code_quality.md)**

---

## 📚 Suggested Reading

- 📖 **[User Stories Overview](/docs/user_stories.md)**: Understand the core user stories that guide development across releases. **Highly Recommended!**
- 📖 **[Purpose and Functionality](docs/purpose.md)**: For a detailed description of the purpose and intended functionality of **KollApp**.
- 📖 **[Gallery](./docs/release3/gallery.md)**: See examples of use cases for **KollApp**. **Highly Recommended!**

| **Release 1** | **Release 2** | **Release 3** |
|----------------------------------|----------------------------------------------------------------------------------------------------------|-----------------------------------------------------------------------------------------------|
| 📖 **[Release 1 Updates](docs/release1/release1_updates.md)** | 📖 **[Release 2 Updates](docs/release2/release2_updates.md)** | 📖 **[Release 3 Updates](/docs/release3/release3_updates.md)** |
| 📖 **[AI Tools Guide](docs/release1/ai-tools.md)** | 📖 **[Development Practices & Code Quality](/docs/release2/development_practices_and_code_quality.md)** | 📖 **[Project Challenges and Lessons Learned](docs/release3/challenges.md)** |
|  | 📖 **[Testing & Code Quality Standards](docs/release2/testing_and_code_quality.md)** | 📖 **[Testing & Code Quality Standards](docs/release3/testing_and_code_quality.md)** |
|  | 📖 **[Project Architecture Diagrams](docs/release2/architecture_diagrams.md)** | 📖 **[Project Architecture Diagrams](docs/release3/architecture_diagrams.md)** |
|  | 📖 **[JSON Save Format Specifications](/docs/release2/json_format.md)** | 📖 **[JSON Save Format Specifications](docs/release3/json_format.md)** |
|  | 📖 **[Module Structure & Dependencies](docs/release2/module_structure_and_dependencies.md)** | 📖 **[Module Structure & Dependencies](docs/release3/module_structure_and_dependencies.md)** |
|  | 📖 **[AI Usage Declaration](docs/release2/ai_declaration.md)** | 📖 **[AI Usage Declaration](docs/release3/ai_declaration.md)** |
|  |  | 📖 **[Contribution Guidelines](docs/release3/contribution.md)** |
|  |  | 📖 **[Sustainability Reflection on KollApp](docs/release3/sustainability.md)** |
|  |  | 📖 **[Creating a Shippable Product](docs/release3/create_shippable_product.md)** |
|  |  | 📖 **[REST Service Documentation](docs/release3/rest_service.md)** |
