# Gradle Properties Template Manager (IntelliJ Plugin)

An IntelliJ IDEA plugin that simplifies managing `gradle.properties` using documented
`gradle.properties.*` template files.

The plugin provides a dedicated tool window that merges template metadata with actual
project values, allowing you to safely view, edit, preview, and save Gradle properties
without manually editing configuration files.

---

## ✨ Features

- 📄 Reads `gradle.properties` and one or more `gradle.properties.*` template files
- 🧩 Displays properties in a structured, editable table
- 📝 Supports structured documentation comments in templates
- ☑️ Type-aware editing (e.g. booleans rendered as checkboxes)
- 🔍 Unified, read-only diff preview before saving changes
- 🔄 Safe merge behavior:
  - Updates existing properties
  - Appends new properties
  - Preserves unrelated entries and comments
  - Ignores empty values
- 🪟 Integrated IntelliJ tool window with refresh and save actions

---

## 📂 Supported Template Format

Template files may include structured documentation using standard `.properties` comments.

Example `gradle.properties.template`:

```properties
# @doc Database JDBC URL
# @required
db.url=jdbc:postgresql://localhost:5432/app

# @doc Enable SQL logging
# @type boolean
db.sql.logging=false
```

## Development

This project is built using the **IntelliJ Platform Gradle Plugin (2.x)** and **Kotlin**.

### Essential Gradle Tasks

| Task | Description |
| :--- | :--- |
| `./gradlew runIde` | Launches a sandbox instance of IntelliJ IDEA with the plugin installed. |
| `./gradlew test` | Runs the integration tests (includes UI/Headless IDE tests). |
| `./gradlew buildPlugin` | Assembles the plugin distribution ZIP file in `build/distributions`. |
| `./gradlew verifyPlugin` | Validates the plugin compatibility and structure against JetBrains' rules. |
| `./gradlew patchPluginXml` | Updates versioning and metadata in the `plugin.xml` automatically. |

### Project Configuration

- **SDK**: Java 21 (managed via Foojay Toolchains)
- **Target Platform**: IntelliJ IDEA 2026.2+
- **Language**: Kotlin 2.4.20

## Installation

1. Clone the repository.
2. Open the project in IntelliJ IDEA.
3. Run the `./gradlew runIde` task to test the plugin locally.
4. To install in your primary IDE:
   - Run `./gradlew buildPlugin`.
   - In IntelliJ, go to `Settings` > `Plugins` > `⚙️` > `Install Plugin from Disk...`.
   - Select the ZIP file located in `build/distributions/`.
