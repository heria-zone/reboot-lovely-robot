# Library Template 1.21.1

This is a template for creating multi-loader Minecraft libraries for version 1.21.1 with support for Forge, Fabric, and NeoForge.

## Setup Instructions

1. **Copy this template** to your new library directory
2. **Customize gradle.properties**:
   - Change `lib_id`, `lib_name`, `lib_version`
   - Update `lib_group`, `lib_authors`, `lib_description`
   - Set your repository URLs
   - Configure dependencies (`depends_on_hzlib`, `needs_geckolib`)

3. **Update package structure**:
   - Rename packages from `com.yourname.yourlib` to your actual package
   - Update package-info.java files with your library information

4. **Configure dependencies**:
   - Set `depends_on_hzlib=true` if your library builds on HZ Lib utilities
   - Set `needs_geckolib=true` if your library provides GeckoLib abstractions
   - Update build.gradle files to include/exclude dependencies as needed

5. **Update library metadata**:
   - Edit `fabric.mod.json` in Fabric module
   - Edit `mods.toml` in Forge module  
   - Edit `neoforge.mods.toml` in NeoForge module

## Library Design Principles

### API Design
- **Common Module**: Contains all public APIs and shared logic
- **Loader Modules**: Contain only loader-specific implementations
- **Interface Segregation**: Split large interfaces into focused contracts
- **Dependency Inversion**: Depend on abstractions, not implementations

### Versioning Strategy
- **Semantic Versioning**: MAJOR.MINOR.PATCH
- **API Compatibility**: Maintain backward compatibility within major versions
- **Breaking Changes**: Only in major version increments
- **Documentation**: Document all breaking changes in CHANGELOG.md

### Testing Strategy
- **Unit Tests**: Test individual components in isolation
- **Integration Tests**: Test cross-loader compatibility
- **API Tests**: Validate public API contracts
- **Performance Tests**: Ensure acceptable performance characteristics

## Project Structure

```
your-library-1.21.1/
├── Common/                    # Shared library code
│   ├── src/main/java/         # Public APIs and shared logic
│   └── src/test/java/         # Unit tests
├── Fabric/                    # Fabric-specific implementations
│   ├── src/main/java/         # Fabric implementations
│   └── src/test/java/         # Fabric-specific tests
├── Forge/                     # Forge-specific implementations
│   ├── src/main/java/         # Forge implementations
│   └── src/test/java/         # Forge-specific tests
├── NeoForge/                  # NeoForge-specific implementations
│   ├── src/main/java/         # NeoForge implementations
│   └── src/test/java/         # NeoForge-specific tests
├── buildSrc/                  # Build configuration
├── build.gradle               # Root build configuration
├── settings.gradle            # Project settings
└── gradle.properties          # Project properties
```

## Development Workflow

### 1. Design Phase
- Define public APIs in Common module
- Create interfaces for loader-specific functionality
- Document expected behavior and contracts

### 2. Implementation Phase
- Implement shared logic in Common module
- Create loader-specific implementations
- Write comprehensive tests

### 3. Testing Phase
- Run unit tests: `./gradlew test`
- Run integration tests: `./gradlew integrationTest`
- Test with example mods to validate APIs

### 4. Publishing Phase
- Update version in gradle.properties
- Update CHANGELOG.md with changes
- Publish to Maven: `./gradlew publish`

## Building and Testing

```bash
# Build all modules
./gradlew build

# Run all tests
./gradlew test

# Build and test specific loader
./gradlew :Fabric:build :Fabric:test

# Publish to local Maven repository
./gradlew publishToMavenLocal

# Publish to remote Maven repository
./gradlew publish
```

## API Documentation

The template includes JavaDoc generation:

```bash
# Generate API documentation
./gradlew javadoc

# Documentation will be in build/docs/javadoc/
```

## Example Usage

After customizing the template, your library can be used like this:

### In a mod's build.gradle:
```gradle
dependencies {
    // For Fabric
    modImplementation "com.yourname.yourlib:yourlib-fabric:1.21.1-1.0.0"
    
    // For Forge
    implementation "com.yourname.yourlib:yourlib-forge:1.21.1-1.0.0"
    
    // For NeoForge
    implementation "com.yourname.yourlib:yourlib-neoforge:1.21.1-1.0.0"
}
```

### In mod metadata:
```toml
# For Forge/NeoForge mods.toml
[[dependencies.yourmod]]
modId="yourlib"
mandatory=true
versionRange="[1.0.0,)"
```

```json
// For Fabric fabric.mod.json
"depends": {
    "yourlib": ">=1.0.0"
}
```

## Best Practices

### Code Organization
- Keep Common module free of loader-specific code
- Use factory patterns for loader-specific object creation
- Minimize public API surface area
- Document all public APIs with JavaDoc

### Dependency Management
- Minimize external dependencies
- Use `api` for dependencies that consumers need
- Use `implementation` for internal dependencies
- Document all dependency requirements

### Testing
- Test all public APIs
- Test cross-loader compatibility
- Include performance benchmarks for critical paths
- Mock external dependencies in unit tests

---

**Note**: This template follows the architectural patterns established in the HZ Lib and Lovely Lib libraries, providing a proven foundation for creating maintainable multi-loader libraries.