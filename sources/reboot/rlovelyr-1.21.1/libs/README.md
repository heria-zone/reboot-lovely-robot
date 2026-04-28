# Local Dependencies Directory

This directory contains local JAR dependencies for development purposes.

## Required JARs

### LovelyLib (`lovelylib_version=1.0.0`)

| Loader   | Expected filename                          |
|----------|--------------------------------------------|
| Common   | `lovelylib-Common-1.21.1-1.0.0.jar`       |
| Fabric   | `lovelylib-fabric-1.21.1-1.0.0.jar`       |
| Forge    | `lovelylib-forge-1.21.1-1.0.0.jar`        |
| NeoForge | `lovelylib-neoforge-1.21.1-1.0.0.jar`     |

### HZLib (`hzlib_version=1.0.0`)

HZLib is now a **separate library** from LovelyLib and must be added explicitly.

| Loader   | Expected filename                      |
|----------|----------------------------------------|
| Common   | `hzlib-Common-1.21.1-1.0.0.jar`       |
| Fabric   | `hzlib-fabric-1.21.1-1.0.0.jar`       |
| Forge    | `hzlib-forge-1.21.1-1.0.0.jar`        |
| NeoForge | `hzlib-neoforge-1.21.1-1.0.0.jar`     |

## Build Order

HZLib must be built before LovelyLib, and LovelyLib before Reboot:

```
1. Build HZLib:     cd sources/common/hzlib-1.21.1     && ./gradlew build
2. Build LovelyLib: cd sources/common/lovelylib-1.21.1  && ./gradlew build
3. Copy JARs to this libs/ folder (or use refreshDependencies below)
4. Build Reboot:    cd sources/reboot/rlovelyr-1.21.1   && ./gradlew build
```

## Automated Refresh

Run from the Reboot project root to build both libraries and copy all JARs automatically:

```bash
./gradlew refreshDependencies
```

Then build and test:

```bash
./gradlew buildWithDeps
./gradlew runClientWithDeps
```

## Note

JARs are resolved from the build output folders directly (no copy needed during development).
Copy to this `libs/` folder only for stable/release builds.
