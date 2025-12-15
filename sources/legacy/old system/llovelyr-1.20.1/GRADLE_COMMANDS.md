# Gradle Commands Reference - Legacy 1.20.1

## Issue Resolution
The Gradle wrapper JAR was corrupted. Fixed by running:
```bash
gradle wrapper --gradle-version 8.8
```

## Running Minecraft Client

### Fabric Client
```bash
./gradlew :Fabric:runClient
```

### Forge Client
```bash
./gradlew :Forge:runClient
```

## Building

### Build Both Loaders
```bash
./gradlew build
```

### Build Fabric Only
```bash
./gradlew :Fabric:build
```

### Build Forge Only
```bash
./gradlew :Forge:build
```

## Other Useful Commands

### List All Tasks
```bash
./gradlew tasks
```

### Clean Build
```bash
./gradlew clean
```

### Generate IDE Configurations
```bash
./gradlew genIntellijRuns  # For IntelliJ IDEA
./gradlew genEclipseRuns   # For Eclipse
./gradlew vscode           # For VSCode
```

### Generate Sources (Decompiled Minecraft)
```bash
./gradlew :Fabric:genSources
./gradlew :Forge:genSources
```

## Notes
- Both Fabric and Forge use Java 17
- Gradle version: 8.8
- Minecraft version: 1.20.1
- The project uses a multi-loader structure with separate Fabric and Forge subprojects
