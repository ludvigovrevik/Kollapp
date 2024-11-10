# Project Challenges and Lessons Learned

## Challenge 1: Implementing JSON Persistence and User Management

### Description
One of the biggest challenges during our project was implementing JSON persistence and managing user data in a consistent and scalable manner. The challenge involved figuring out file pathing, modularization, and debugging the JSON-related components. We encountered significant issues with file paths—particularly with using relative paths correctly—which led to confusing errors during debugging.

### Measures Attempted
To address this challenge, we modularized our code to separate concerns and make debugging easier. Initially, persistence logic was placed in a persistence module. However, to simplify the architecture and improve cohesion, we moved the handlers directly into the API service classes. We experimented with different pathing approaches until we found one that worked consistently, using relative paths combined with environment variables. In hindsight, utilizing Maven plugins to skip tests during development would have saved time, as debugging through failing tests often slowed us down.

### Lessons Learned
- Using relative pathing effectively is crucial but not straightforward. Incorrect paths can cause hard-to-trace issues, emphasizing the importance of thorough testing and a clear structure in file management.
- We gained a better understanding of JSON as a data storage solution, finding it convenient and flexible once set up correctly.
- Modularizing the persistence logic by reorganizing it within API service classes significantly improved maintainability.
- Learning to use Maven plugins to manage our build process—including skipping unnecessary tests—proved invaluable in streamlining our workflow.

---

## Challenge 2: Understanding and Using Maven Plugins Effectively

### Description
Another significant challenge was understanding and effectively using Maven plugins. We faced difficulties in setting up dependencies and plugins due to version mismatches, complex configurations, and compatibility problems. The API module required specific versions of Spring dependencies, while the UI module needed different versions of Jackson for JSON processing, which led to runtime errors. These errors were particularly challenging to diagnose because builds would often succeed, but tests would fail with cryptic error messages, making it hard to trace the root cause.

### Measures Attempted
To overcome these difficulties, we researched Maven documentation and experimented with plugin configurations. For example, we faced version compatibility issues between Spring Boot 2.6.8 and Jackson. Initially, using Jackson version 2.18.0-rc1 caused runtime errors (`NoSuchFieldError`), which we resolved by aligning Jackson to version 2.13.x. We also integrated JaCoCo for code coverage and adjusted its arguments to avoid conflicts with JavaFX tests.

### Lessons Learned
- Understanding Maven is crucial for effective project management in Java. Properly configuring Maven plugins can save a lot of time, but it requires a solid understanding of how Maven works and how plugins integrate with it.
- Ensuring version compatibility, especially with dependencies like Jackson and Spring Boot, was critical in avoiding build issues.
- This experience improved our proficiency in managing Java projects, making our build process more predictable and reducing time spent troubleshooting.
- Documenting configuration changes provided reference points for future debugging and integration, making our process more efficient.
