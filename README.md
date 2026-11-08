## Getting Started

Welcome to the VS Code Java world. Here is a guideline to help you get started to write Java code in Visual Studio Code.

## Folder Structure

The workspace contains two folders by default, where:

- `src`: the folder to maintain sources
- `lib`: the folder to maintain dependencies

Meanwhile, the compiled output files will be generated in the `bin` folder by default.

> If you want to customize the folder structure, open `.vscode/settings.json` and update the related settings there.

## Dependency Management

The `JAVA PROJECTS` view allows you to manage your dependencies. More details can be found [here](https://github.com/microsoft/vscode-java-dependency#manage-dependencies).


# Advanced File System — Visitor Pattern

A production-grade Java 17 Maven implementation of an in-memory hierarchical file system using the **Visitor behavioral pattern**. This project demonstrates double dispatch, strict tree integrity rules, and extensible, decoupled operations over a composite tree structure (`FileNode` and `DirectoryNode`).

---

## 1. Features & Implementation Details

* **Element Hierarchy:** Supports arbitrary nesting of directories (`DirectoryNode`) containing files (`FileNode`).
* **Double Dispatch:** Elements delegate to overloaded `visit` methods on the `FileSystemVisitor` interface without relying on runtime type discrimination (`instanceof` or `getClass()`).
* **Tree Integrity Guardrails:** Automatically rejects negative file sizes, duplicate sibling names within the same directory, multiple parent ownership, and cyclic directory references.
* **Deterministic Output:** Uses `TreeMap` internally to maintain strict lexicographical ordering of child nodes and root-relative path representations (`/` separator).
* **Visitor Operations:**
  1. `TotalSizeVisitor`: Calculates the cumulative byte size of all files within a subtree (directory nodes contribute 0 bytes).
  2. `SearchVisitor`: Filters files matching a configurable filename substring criterion and minimum size threshold, returning sorted root-relative paths.
  3. `StatisticsVisitor`: Collects file count, directory count (including root), and maximum tree depth without modifying existing element classes or `accept` signatures.

---

## 2. Prerequisites

* **Java Development Kit (JDK):** Version 17 or higher
* **Apache Maven:** Version 3.8+

---

## 3. Getting Started

### Clone the Repository
```bash
git clone [https://github.com/your-username/file-system-visitor.git](https://github.com/your-username/file-system-visitor.git)
cd file-system-visitor

```

### Build and Run Automated Tests

Executes the JUnit 5 test suite verifying all 8 core checkpoints (T1–T8):

```bash
mvn clean test

```

### Execute Demonstration Application

Runs the deterministic executable main program to trace all three visitor operations:

```bash
mvn clean compile exec:java -Dexec.mainClass="com.filesystem.Main"

```

---

## 4. Project Structure

```text
file-system-visitor/
├── pom.xml
├── README.md
└── src/
    ├── main/
    │   └── java/
    │       └── com/
    │           └── filesystem/
    │               ├── FileSystemElement.java
    │               ├── FileNode.java
    │               ├── DirectoryNode.java
    │               ├── FileSystemVisitor.java
    │               ├── TotalSizeVisitor.java
    │               ├── SearchVisitor.java
    │               ├── StatisticsVisitor.java
    │               └── Main.java
    └── test/
        └── java/
            └── com/
                └── filesystem/
                    └── FileSystemTest.java

```

---

## 5. Automated Verification (JUnit 5)

| Check | Target Description | Status |
| --- | --- | --- |
| **T1** | Both element types reach correct overloaded visit methods via double dispatch | Passed |
| **T2** | Size accumulation across multiple nesting levels is exact | Passed |
| **T3** | Search correctly combines filename and minimum size criteria | Passed |
| **T4** | Search produces deterministic, sorted, root-relative paths | Passed |
| **T5** | Statistics correctly report file count, directory count, and max depth | Passed |
| **T6** | Empty structures and no-match search queries execute safely | Passed |
| **T7** | Repeat executions with `reset()` do not leak or multiply state | Passed |
| **T8** | Invalid tree mutations (cycles, duplicate siblings, negative size) are rejected | Passed |
