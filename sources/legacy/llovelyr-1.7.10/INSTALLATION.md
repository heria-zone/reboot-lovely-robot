
## Gradle JVM (for running Gradle itself)
**Location:** Settings → Build, Execution, Deployment → Build Tools → Gradle → "Gradle JVM"

**Set to:** **Java 21** (or newer)

This is what runs the Gradle build process itself. This MUST be Java 21+ due to the RetroFuturaGradle plugin requirement.

---

## Project Structure Settings

**Location:** File → Project Structure (Ctrl+Alt+Shift+S)

### 1. SDK (Project SDK)
**Set to:** **Java 8** (1.8)

This is the Java version used for compiling your mod code. Minecraft 1.7.10 runs on Java 8, so your mod must be compiled with Java 8 compatibility.

### 2. Language Level
**Set to:** **8 - Lambdas, type annotations, etc.**

Or if you want to use modern Java syntax (thanks to `enableModernJavaSyntax = true`), you can set it to a higher level like **17** or **21**, and the build will still produce Java 8-compatible bytecode.

---

## Summary Table

| Setting | Location | Value | Purpose |
|---------|----------|-------|---------|
| **Gradle JVM** | Settings → Gradle | **Java 21** | Runs the Gradle build tool |
| **Project SDK** | Project Structure → Project | **Java 8 (1.8)** | Target JVM for your mod |
| **Language Level** | Project Structure → Project | **8** (or higher if using modern syntax) | Java language features you can use |

---

## Visual Guide

1. **Gradle JVM:**
   ```
   Settings → Build, Execution, Deployment → Build Tools → Gradle
   └─ Gradle JVM: [Select Java 21]
   ```

2. **Project Structure:**
   ```
   File → Project Structure → Project
   ├─ SDK: [1.8 (Java version 1.8.x)]
   └─ Language level: [8 - Lambdas, type annotations etc.]
   ```