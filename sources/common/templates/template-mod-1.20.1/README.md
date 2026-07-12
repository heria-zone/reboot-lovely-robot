# Template Mod — Minecraft 1.20.1

Multiloader mod template targeting MC 1.20.1 with support for **Fabric** and **Forge**.

> NeoForge did not exist for Minecraft 1.20.1 — this template is Fabric + Forge only.

## Version Stack

| Component | Version |
|---|---|
| Minecraft | 1.20.1 |
| Java | 17 |
| Gradle wrapper | 8.8 |
| Forge | 47.4.0 (ForgeGradle `[6.0,6.2)` via `gradle_version`) |
| NeoForge | — (not available for 1.20.1) |
| fabric-loom | 1.1-SNAPSHOT |
| fabric-loader | 0.17.3 |
| fabric-api | 0.92.6+1.20.1 |
| GeckoLib (Forge) | 4.2.2 |
| GeckoLib (Fabric) | 4.2.2 |
| Mappings | Official Mojang (both loaders) |

## Quick Start

1. **Copy this template** to your new project directory
2. **Update `gradle.properties`** with your mod details:
   ```properties
   mod_id=yourmodid
   mod_name=Your Mod Name
   mod_version=1.0.0
   mod_group=com.yourname.yourmod
   mod_authors=Your Name
   mod_description=Your mod description here
   mod_homepage=https://github.com/yourusername/your-mod
   mod_source=https://github.com/yourusername/your-mod
   mod_issues=https://github.com/yourusername/your-mod/issues
   ```
3. **Enable dependencies** you need (all off by default):
   ```properties
   depends_on_hzlib=true       # HZ Lib
   depends_on_lovelylib=true   # Lovely Lib (includes HZLib)
   needs_geckolib=true         # GeckoLib
   ```
4. **Update local dev paths** if using HZLib or LovelyLib from source:
   ```properties
   hzlib_project_path=../../common/hzlib-1.20.1
   lovelylib_project_path=../../common/lovelylib-1.20.1
   ```
5. **Rename packages** in all Java files — replace `net.heriazone.templatemod` with your package and rename `Template`/`TemplateMod` classes
6. **Build and test**:
   ```bash
   ./gradlew build
   ./gradlew :Fabric:runClient
   ./gradlew :Forge:runClient
   ```

## Project Structure

```
template-mod-1.20.1/
├── buildSrc/
│   └── src/main/groovy/
│       ├── multiloader-common.gradle   # Convention plugin: shared config for all subprojects
│       └── multiloader-loader.gradle   # Convention plugin: wires Common sources into each loader
├── Common/          # Shared code and resources — compiled into every loader JAR
├── Fabric/          # Fabric-specific entry point and code
├── Forge/           # Forge-specific entry point and code
├── build.gradle     # Root build — declares plugins, shared repositories, refreshDependencies task
├── settings.gradle  # Subproject includes with CI-aware TARGET_LOADER env var support
└── gradle.properties
```

### How Common Works

Common sources are **not shipped as a separate JAR**. The `multiloader-loader` convention plugin pulls `Common/src/main/java` and `Common/src/main/resources` directly into each loader subproject at compile time via the `commonJava` / `commonResources` configurations. The final Fabric and Forge JARs each contain all of Common's classes and assets merged in.

## Dependencies

### HZLib / LovelyLib (local development)

When `depends_on_hzlib=true` or `depends_on_lovelylib=true`, the build resolves JARs from two places in order:
1. `libs/` folder in the project root (production, pre-built JARs)
2. The sibling project's `*/build/libs/` directories (development, built from source)

Run this to build and copy the dependency JARs into `libs/`:
```bash
./gradlew refreshDependencies
```

### GeckoLib

Set `needs_geckolib=true` in `gradle.properties`. GeckoLib is resolved from the Cloudsmith Maven repository, which is already declared in the build.

## Building

```bash
# Build all loaders
./gradlew build

# Build a specific loader only
./gradlew :Fabric:build
./gradlew :Forge:build
```

## Running

```bash
./gradlew :Fabric:runClient
./gradlew :Forge:runClient

# With refreshed local library dependencies first
./gradlew buildWithDeps
```

## CI — Building a Single Loader

Set the `TARGET_LOADER` environment variable to `Fabric` or `Forge` before running Gradle. When `CI=true` and `TARGET_LOADER` is set, only that loader subproject is included in the build.

## Requirements

- Java 17+
- Gradle 8.8 (wrapper pinned — ForgeGradle 6 requires Gradle 8+, do not downgrade)

## Important Notes

- **ForgeGradle version** is controlled by the `gradle_version` property in `gradle.properties`, not hardcoded in `Forge/build.gradle`. Keep it at `[6.0,6.2)` for 1.20.1.
- **NeoForge is not available** for MC 1.20.1. Do not add a NeoForge subproject.
- **fabric-api** is a compile dependency here but declared as `suggests` (not `depends`) in `fabric.mod.json` — move it to `depends` if your mod genuinely requires it.
