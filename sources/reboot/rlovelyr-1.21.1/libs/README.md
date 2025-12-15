# Local Dependencies Directory

This directory contains local JAR dependencies for development purposes.

## Contents

This directory will contain:
- `lovelylib-common-1.21.1-1.0.0-dev.jar` - LovelyLib common functionality
- `lovelylib-forge-1.21.1-1.0.0-dev.jar` - LovelyLib Forge implementation
- `lovelylib-neoforge-1.21.1-1.0.0-dev.jar` - LovelyLib NeoForge implementation
- `lovelylib-fabric-1.21.1-1.0.0-dev.jar` - LovelyLib Fabric implementation

## Usage

To refresh dependencies from LovelyLib:

```bash
# From the tribute project root
./gradlew refreshDependencies

# To build with fresh dependencies
./gradlew buildWithDeps

# To run client with fresh dependencies
./gradlew runClientWithDeps
```

## Development Workflow

1. Make changes to LovelyLib
2. Run `./gradlew refreshDependencies` to copy latest JARs
3. Build and test Tribute with updated dependencies
4. Iterate as needed

## Note

These JARs are for local development only. When LovelyLib is published to Maven, this directory will be removed and dependencies will be resolved from the Maven repository.