# Template Mod - Minecraft 1.21.1

A template for creating new Minecraft mods with multiloader support (Fabric, Forge, NeoForge).

## Quick Start

1. **Copy this template** to your new project directory
2. **Update `gradle.properties`** with your mod details:
   ```properties
   # Mod Properties - CUSTOMIZE THESE FOR YOUR MOD
   mod_id=yourmodid
   mod_name=Your Mod Name
   mod_version=1.0.0
   mod_group=com.yourname.yourmod
   mod_authors=Your Name
   mod_description=Your mod description here
   mod_homepage=https://github.com/yourusername/your-mod
   ```

3. **Update package names** in Java files:
   - Replace `net.heriazone.templatemod` with your package name
   - Rename classes from `Template`/`TemplateMod` to your mod names

4. **Build and test**:
   ```bash
   ./gradlew build
   ./gradlew :Fabric:runClient
   ./gradlew :Forge:runClient
   ./gradlew :NeoForge:runClient
   ```

## Project Structure

```
template-mod-1.21.1/
├── Common/          # Shared code across all loaders
├── Fabric/          # Fabric-specific code
├── Forge/           # Forge-specific code
├── NeoForge/        # NeoForge-specific code
├── gradle.properties # Main configuration file
└── build.gradle     # Build configuration
```

## Key Features

- **Multiloader Support**: Works with Fabric, Forge, and NeoForge
- **Variable-Driven**: All configuration uses gradle.properties variables
- **Modern Architecture**: Java 21, latest loader versions
- **Clean Structure**: Follows established patterns from working mods

## Customization

### Dependencies

Edit `gradle.properties` to enable/disable dependencies:

```properties
# Set to true if your mod depends on HZ Lib
depends_on_hzlib=false

# Set to true if your mod depends on Lovely Lib
depends_on_lovelylib=false

# Set to true if your mod needs GeckoLib
needs_geckolib=false
```

### Adding Features

1. Add common code to `Common/src/main/java/`
2. Add loader-specific code to respective loader directories
3. Update configuration files as needed
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

## Testing

```bash
# Run client for each loader
./gradlew :Fabric:runClient
./gradlew :Forge:runClient
./gradlew :NeoForge:runClient
```

## Requirements

- Java 21
- Gradle 8.10+
- Minecraft 1.21.1

## License

Customize the license in `gradle.properties` and add your LICENSE file.