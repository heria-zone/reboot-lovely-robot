# libs/

Place the HZLib JAR files here to enable local dependency resolution.

Required files (matching `hzlib_version=1.0.0` in `gradle.properties`):

- `hzlib-common-1.21.1-1.0.0.jar`
- `hzlib-fabric-1.21.1-1.0.0.jar`
- `hzlib-forge-1.21.1-1.0.0.jar`
- `hzlib-neoforge-1.21.1-1.0.0.jar`

These are the same JARs used by `monsters_girls-1.21.1/libs/`.
Build HZLib first (`./gradlew build` in `sources/common/hzlib-1.21.1/`),
then copy the output JARs from `hzlib-1.21.1/*/build/libs/` here.
