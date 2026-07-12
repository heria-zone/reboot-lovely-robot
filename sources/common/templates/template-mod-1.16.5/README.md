# Template Mod — Minecraft 1.16.5

Multiloader mod template targeting MC 1.16.5 with support for **Fabric** and **Forge**.

> NeoForge did not exist for Minecraft 1.16.5 — this template is Fabric + Forge only.

## Version Stack

| Component | Version |
|---|---|
| Minecraft | 1.16.5 |
| Java | 8 |
| Gradle wrapper | **7.2** (hard requirement — ForgeGradle 5 does not support Gradle 8+) |
| Forge | 36.2.42 (ForgeGradle `5.1.+`) |
| NeoForge | — (not available for 1.16.5) |
| fabric-loom | 0.10-SNAPSHOT |
| fabric-loader | 0.11.3 |
| fabric-api | 0.34.2+1.16 |
| GeckoLib (Forge) | 3.0.106 |
| GeckoLib (Fabric) | 3.0.107 |
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
   hzlib_project_path=../../common/hzlib-1.16.5
   lovelylib_project_path=../../common/lovelylib-1.16.5
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
template-mod-1.16.5/
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

### Logging in Common

MC 1.16.5 does not include SLF4J (added in MC 1.17). Use **Log4j 2** instead:
```java
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public static final Logger LOGGER = LogManager.getLogger(MOD_NAME);
```
The `Common/build.gradle` declares `log4j-api:2.15.0` as `compileOnly` so it is available at compile time. At runtime it is provided by Minecraft itself — do not bundle it in your JAR.

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

Set `needs_geckolib=true` in `gradle.properties`. GeckoLib is resolved from the Cloudsmith Maven repository, which is already declared in the build. Note that GeckoLib 3.x is used for 1.16.5 (not 4.x).

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

- Java 8+ (compile target is Java 8; any JDK 8–17 works)
- **Gradle 7.2 exactly** — ForgeGradle 5.1.+ hard-caps at Gradle 7.x and will refuse Gradle 8+. The wrapper is pinned to `gradle-7.2-all.zip`. Do not upgrade it.

## Important Notes

- **Do not upgrade the Gradle wrapper** beyond 7.x for this template. ForgeGradle 5 is incompatible with Gradle 8+. Use the 1.20.1 or 1.21.1 template if you need Gradle 8+.
- **Do not mix `toolchain.languageVersion` with `sourceCompatibility`** — Gradle 7.2 and loom 0.10 reject the combination. Java version is set via `sourceCompatibility = JavaVersion.VERSION_1_8` only.
- **VanillaGradle is not used** — it now requires Gradle 9.2+ and is incompatible with this setup. The Common module compiles without a direct Minecraft dependency; MC classes are provided by each loader at compile time through the `multiloader-loader` wiring.
- **`copyIdeResources`** is not available in ForgeGradle 5 (it's an FG6 feature). Run configs from the Gradle panel work correctly; launching directly from the IntelliJ Run button may not find resources until you run `processResources` manually.
- **`exclusiveContent` / `includeGroupAndSubgroups`** are Gradle 7.3+ APIs — not available here. `settings.gradle` uses plain `maven {}` declarations instead.
- **Mixin `compatibilityLevel`** must be `JAVA_8` in both `Fabric/src/main/resources/templatemod.mixins.json` and `Forge/src/main/resources/templatemod.mixins.json`. The Mixin version bundled with 1.16.5 loaders does not know `JAVA_17` or `JAVA_21`.
- **NeoForge is not available** for MC 1.16.5. Do not add a NeoForge subproject.
- **fabric-api** is declared as `suggests` (not `depends`) in `fabric.mod.json` — move it to `depends` if your mod genuinely requires it.
- **`logoFile`** in `mods.toml` must be a plain filename with no subdirectory path (e.g. `icon.png`, not `assets/yourmod/icon.png`). FG5 / 1.16.5 Forge rejects paths with `/` in root resource declarations.
