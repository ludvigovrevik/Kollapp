# 📦 Creating a Shippable Product

To create a shippable product for **KollApp**, follow these steps:

## 1. Navigate to the Project Directory

Navigate to the `kollapp`folder:

```sh
cd kollapp
```

## 2. Clean and Install Dependencies

Clean the project and install all required dependencies:

```sh
mvn clean install
```

## 3. Package the Application

Use the `jpackage` plugin to create a shippable package:

```sh
mvn -pl ui jpackage:jpackage
```

## 4. Download Installation Packages

You can also find the installation packages here

| Operating System |      |
|------------------|------|
| Windows          | [Download KollApp 1.0.0 for Windows](docs/release3/executable_files_shippable_product/windows/KollApp-1.0.0.exe) |
| Mac              | [Download KollApp 1.0.0 for Mac](docs/release3/executable_files_shippable_product/mac/KollApp-1.0.0.dmg) |
| Linux            | [Download KollApp 1.0.0 for Linux](docs/release3/executable_files_shippable_product/KollApp-1.0.0.deb) |

---

📖 Return to the **[Main README](../../readme.md)** for additional information and project overview.
