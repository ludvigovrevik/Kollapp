# **🚀 Getting Started with Testing and Code Quality**

To maintain a high standard of code reliability and performance in the **KollApp** project, we utilize a range of comprehensive testing and code analysis tools. Follow these steps to get started.

---

## 1. Navigate to the `kollapp` directory 📂

Change your working directory to the `kollapp` folder:

```sh
cd kollapp
```

## 2. Clean the Project (Optional) 🧹

Clean the project and remove the `/target` folders to ensure a fresh start:

```sh
mvn clean
```

## 3. Install Dependencies 📦

Download all required plugins and dependencies for the project:

```sh
mvn clean install
```

## 4. Run Unit Tests 🧪

Execute all JUnit tests, including those utilizing Mockito for mock objects:

```sh
mvn clean test
```

## 5. Code Coverage Analysis with JaCoCo 📊

Run JaCoCo to generate a detailed code coverage report:

```sh
mvn clean verify
```

📄 The JaCoCo report will be available at:

- `kollapp/report-aggregate/target/site/jacoco-aggregate/index.html`

## 6. Run SpotBugs Analysis 🔍

Perform static code analysis using SpotBugs to detect potential issues:

```sh
mvn spotbugs:spotbugs
```

📄 SpotBugs reports will be generated at:

- `kollapp/core/target/spotbugsXml.xml`
- `kollapp/persistence/target/spotbugsXml.xml`
- `kollapp/ui/target/spotbugsXml.xml`

## 7. SpotBugs Check (Fail Build if Bugs Detected) 🚫

Run SpotBugs and stop the build if any bugs are detected:

```sh
mvn spotbugs:check
```

## 8. Code Style Analysis with Checkstyle 📝

Generate Checkstyle reports

```sh
mvn checkstyle:checkstyle
```

📄 Checkstyle reports will be generated at:

- `kollapp/core/target/checkstyle-result.xml`
- `kollapp/persistence/target/checkstyle-result.xml`
- `kollapp/ui/target/checkstyle-result.xml`

## 9. Checkstyle Check (Fail Build if Style Issues Detected) 🚫

Run Checkstyle and stop the build if code style issues are found:

```sh
mvn checkstyle:check
```

## 10. Launch the Application 🚀

To run the **KollApp** application using JavaFX, switch to the `ui` directory and run:

```sh
cd ui
mvn javafx:run
```

---

By following these steps, you can effectively manage testing and code quality for the **KollApp** project, ensuring a reliable and maintainable codebase throughout development.

---

📖 Return to the **[Main README](../readme.md)** for additional information and project overview.
