# Template Mod - Quick Project Setup

This is a complete multi-loader Minecraft mod template for version 1.21.1 supporting Fabric, Forge, and NeoForge.

## Quick Start

1. **Copy this template** to your new project directory
2. **Update `gradle.properties`** with your mod information:
   ```properties
   mod_id=yourmod
   mod_name=Your Mod Name
   mod_group=com.yourname.yourmod
   mod_authors=Your Name
   ```
3. **Rename files and folders** to match your mod:
   - Rename `template.mixins.json` files to `${mod_id}.mixins.json`
   - Update package structure from `net.heriazone.template` to your `${mod_group}`
   - Rename entry point classes from `Template*` to `${mod_name}*`
4. **Build and test**: `./gradlew build`

## Template Variables

The template uses Gradle property expansion for easy customization:

| Variable | Usage | Example |
|----------|-------|---------|
| `${mod_id}` | Mod identifier | `yourmod` |
| `${mod_name}` | Display name | `Your Mod Name` |
| `${mod_group}` | Java package | `com.yourname.yourmod` |
| `${mod_authors}` | Author name | `Your Name` |
| `${mod_version}` | Version | `1.0.0` |
| `${mod_description}` | Description | `Your mod description` |

## File Structure

```
template-mod-1.21.1/
├── Common/                 # Shared code across loaders
│   └── src/main/java/
│       └── ${mod_group}/
├── Fabric/                 # Fabric-specific code
│   └── src/main/java/
│       └── ${mod_group}/
├── Forge/                  # Forge-specific code
│   └── src/main/java/
│       └── ${mod_group}/
├── NeoForge/              # NeoForge-specific code
│   └── src/main/java/
│       └── ${mod_group}/
└── gradle.properties      # Configuration (EDIT THIS)
```

## Library Dependencies

The template supports optional library dependencies:

- **HZ Lib**: General utilities (`use_hzlib=true`)
- **Lovely Lib**: Robot-specific functionality (`use_lovelylib=true`)
- **GeckoLib**: Animation library (automatically included with Lovely Lib)

## Build Commands

- **Build all**: `./gradlew build`
- **Run Fabric client**: `./gradlew :Fabric:runClient`
- **Run Forge client**: `./gradlew :Forge:runClient`
- **Run NeoForge client**: `./gradlew :NeoForge:runClient`

## What's Included

✅ **Multi-loader support** - Fabric, Forge, NeoForge  
✅ **Proper build system** - Two-tier Gradle configuration  
✅ **Entry point classes** - Ready-to-use mod initialization  
✅ **Mixin support** - Pre-configured mixin files  
✅ **Template variables** - Easy customization via gradle.properties  
✅ **Library integration** - Optional HZ Lib and Lovely Lib support  
✅ **Documentation** - Comprehensive JavaDoc comments  

## Customization Guide

### 1. Basic Setup
Edit `gradle.properties` with your mod details.

### 2. Rename Files
- `template.mixins.json` → `${mod_id}.mixins.json` (all loaders)
- Entry point classes: `Template*` → `${mod_name}*`

### 3. Update Package Structure
- Create your package: `src/main/java/${mod_group}/`
- Move classes to new package
- Update package declarations in Java files

### 4. Customize Entry Points
- Update class names in `fabric.mod.json`
- Ensure `@Mod` annotations use correct mod ID
- Update import statements

### 5. Add Your Code
- Implement your mod logic in the Common module
- Add loader-specific code in respective modules
- Create mixins in `${mod_group}.mixins` package

## Template Features

- **Zero configuration** - Works out of the box
- **Template variables** - Single point of configuration
- **Best practices** - Follows project coding standards
- **Documentation** - Comprehensive JavaDoc comments
- **Testing ready** - Includes test framework setup
- **CI/CD ready** - Gradle build system compatible

## Support

This template follows the same patterns as HZ Lib and Lovely Lib. For examples and advanced usage, refer to those libraries in the same project structure.