# Local Dependencies Directory

This directory contains local JAR dependencies for development purposes.

## Required JARs

### HZLib (`hzlib_version=1.0.0`)

| Loader   | Expected filename                      |
|----------|----------------------------------------|
| Common   | `hzlib-Common-1.21.1-1.0.0.jar`       |
| Fabric   | `hzlib-fabric-1.21.1-1.0.0.jar`       |
| Forge    | `hzlib-forge-1.21.1-1.0.0.jar`        |
| NeoForge | `hzlib-neoforge-1.21.1-1.0.0.jar`     |

## Build Order

HZLib must be built before LovelyLib:

```
1. Build HZLib:     cd sources/common/hzlib-1.21.1    && ./gradlew build
2. Build LovelyLib: cd sources/common/lovelylib-1.21.1 && ./gradlew build
```

## Automatic Resolution

LovelyLib is configured to resolve HZLib JARs **directly from the HZLib build output**
via `hzlib_project_path=../hzlib-1.21.1` in `gradle.properties`. No manual copy needed.

The resolution order is:
1. `libs/` folder (stable/release JARs)
2. `../hzlib-1.21.1/{Loader}/build/libs/` (live build output — used during development)

As long as HZLib has been built at least once, LovelyLib will find the JARs automatically.

## Note

JARs in this `libs/` folder are optional during development — the build output fallback
handles it. Copy JARs here only for stable/release builds.
