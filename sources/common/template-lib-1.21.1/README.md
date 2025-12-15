# Template Lib - Minecraft 1.21.1

A template for creating new Minecraft libraries with multiloader support (Fabric, Forge, NeoForge).

## Quick Start

1. **Copy this template** to your new project directory
2. **Update `gradle.properties`** with your library details:
   ```properties
   # Library Properties - CUSTOMIZE THESE FOR YOUR LIBRARY
   lib_id=yourlibid
   lib_name=Your Lib Name
   lib_version=1.0.0
   lib_group=com.yourname.yourlib
   lib_authors=Your Name
   lib_description=Your library description here
   lib_homepage=https://github.com/yourusername/your-lib
   ```

3. **Update package names** in Java files:
   - Replace `net.heriazone.templatelib` with your package name
   - Rename classes from `Template`/`TemplateLib` to your library names

4. **Build and test**:
   ```bash
   ./gradlew build
   ./gradlew :Fabric:runClient
   ./gradlew :Forge:runClient
   ./gradlew :NeoForge:runClient
   ```

## Project Structure

```
template-lib-1.21.1/
├── Common/          # Shared library code across all loaders
├── Fabric/          # Fabric-specific library code
├── Forge/           # Forge-specific library code
├── NeoForge/        # NeoForge-specific library code
├── gradle.properties # Main configuration file
└── build.gradle     # Build configuration
```

## Key Features

- **Multiloader Support**: Works with Fabric, Forge, and NeoForge
- **Variable-Driven**: All configuration uses gradle.properties variables
- **Modern Architecture**: Java 21, latest loader versions
- **Library Pattern**: Designed to be used as a dependency by other mods

## Customization

### Dependencies

Edit `gradle.properties` to enable/disable dependencies:

```properties
# Set to true if your library depends on HZ Lib
depends_on_hzlib=false

# Set to true if your library needs GeckoLib
needs_geckolib=false
```

### Adding Library Features

1. Add common library code to `Common/src/main/java/`
2. Add loader-specific implementations to respective loader directories
3. Export your library's API through the main class
4. Test on all loaders

## Building

```bash
# Build all loaders
./gradlew build

# Build specific loader
./gradlew :Fabric:build
./gradlew :Forge:build
./gradlew :NeoForge:build
```

## Publishing

The built JARs can be published to:
- Maven repositories
- CurseForge/Modrinth (if applicable)
- GitHub Releases

## Using This Library

Other mods can depend on your library by adding it to their dependencies:

### Fabric (fabric.mod.json)
```json
{
  "depends": {
    "yourlibid": "*"
  }
}
```

### Forge (mods.toml)
```toml
[[dependencies.yourmod]]
    modId="yourlibid"
    mandatory=true
    versionRange="[1.0.0,)"
```

### NeoForge (neoforge.mods.toml)
```toml
[[dependencies.yourmod]]
modId="yourlibid"
type="required"
versionRange="[1.0.0,)"
```

## Requirements

- Java 21
- Gradle 8.10+
- Minecraft 1.21.1

## License

Customize the license in `gradle.properties` and add your LICENSE file.