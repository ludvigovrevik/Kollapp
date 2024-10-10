# **🏠 KollApp: Manage Shared Living Efficiently 💻**

Welcome to **KollApp**, a Java application developed as part of the course **Informatikk Prosjektarbeid I - IT1901**. This app is designed to help roomates efficiently manage and share household tasks in a shared living space. Let's make communal living stress-free!

---

## **🚀 Get Started Quickly with Eclipse Che**


[Click here to open this project in Eclipse Che](https://gitlab.stud.idi.ntnu.no/it1901/groups-2024/gr2409/gr2409/-/tree/master?ref_type=heads?new)

---

## **🎯 Purpose of KollApp**

This repository is for developing **KollApp**, a Java application created as part of the course **Informatikk Prosjektarbeid I - IT1901**.

**KollApp** is designed to help roommates in a shared flat manage and share household tasks more efficiently. The application provides an overview of shared responsibilities such as house cleaning, unloading the dishwasher, grocery shopping, and other communal tasks. It aims to improve communication and coordination among flatmates, ensuring that chores are evenly distributed and completed on time.

📖 **More details about the app's purpose can be found [here](docs/purpose.md).**

---

## **🗂️ Repository Structure**

Here's an overview of the project's structure:

- **Main Application Code 📂**
  `gr2409/kollapp/src/main/java/app`  

  Contains the Java files responsible for handling the application's logic and functionality.

- **Resources 📂**
  `gr2409/kollapp/src/main/resources/app`  

  Contains resource files such as `.fxml` files for the user interface.

- **Tests 🧪**
  `gr2409/kollapp/src/test/java/app`  

  Contains unit tests to ensure the app work as expected.

---

## **⚙️ Requirements**

To build and run **KollApp**, make sure you have the following installed:

- **Java version**: 17
- **Maven version**: 3.9.9

---

## **📦 Dependencies**

The **KollApp** application has the following dependencies:

- **JavaFX** (version 17.0.12) for building the user interface.
- **JUnit 5** (version 5.10.0) for writing and running unit tests.
- **Maven** (version 2.2) for project management and build automation.

These dependencies are specified in the [pom.xml](../kollapp/pom.xml) file. Ensure Maven is installed and properly configured to handle these dependencies.

---

## **🚀 How to Run the Application**

Follow these steps to build and run **KollApp**:

**1. Build the Project 🛠️** 
   Navigate to the `kollapp` directory and run the following Maven command to build the project:

   ```sh
   mvn clean install
   ```

**2. Run the Application ▶️**
    After the build is complete, run the following command to launch the application:

   ```sh
   mvn javafx:run
   ```

**3. Run Unit Tests 🧪**
    To run all the JUnit tests for the application, use the following command:

   ```sh
   mvn test
   ```

---

## **🎯 Key Feautures of KollApp**

- **📋 Task Assignment**: Easily assign tasks to roomates.
- **🛒 Shared Purchases**: Track shared expenses for household items.
- **📅 Event Planning**: Plan group events and share them with your roomates.
- **🕒 Task Deadlines**: Add, modify, and manage task deadlines.

---