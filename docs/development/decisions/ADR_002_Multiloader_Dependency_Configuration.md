# ADR 002: Multiloader Dependency Configuration Fix

**Status**: Accepted
**Date**: 2025-01-12
**Decision Makers**: AI Agent Analysis
**Consulted**: Legacy 1.21.1 working implementation

## Context

The Lovely Lib multiloader library fails to build with compilation errors indicating that the Common module classes cannot be found by the loader-specific modules (Fabric, Forge, NeoForge). The error occurs because:

```
error: cannot find symbol
import net.heriazone.lovelylib.Common;
                              ^
  symbol:   class Common
  location: package net.heriazone.lovelylib
```

Analysis of the working Legacy 1.21.1 implementation reveals the correct multiloader plugin configuration pattern.

## Problem Analysis

### Current Broken Configuration (LovelyLib)
- **NeoForge/build.gradle**: Uses `id 'multiloader-common'` plugin
- **Fabric/build.gradle**: Uses `id 'multiloader-common'` plugin  
- **Forge/build.gradle**: Uses `id 'multiloader-common'` plugin
- **Common dependency**: Commented out or missing

### Working Configuration (Legacy)
- **NeoForge/build.gradle**: Uses `id 'multiloader-loader'` plugin
- **Fabric/build.gradle**: Uses `id 'multiloader-loader'` plugin
- **Forge/build.gradle**: Uses `id 'multiloader-loader'` plugin
- **Common dependency**: Automatically configured by multiloader-loader plugin

## Decision

Fix the multiloader configuration by:

1. **Change Plugin Usage**: Replace `multiloader-common` with `multiloader-loader` in all loader-specific modules
2. **Remove Manual Dependencies**: Let the multiloader-loader plugin handle Common module dependencies automatically
3. **Maintain Common Module**: Keep `multiloader-common` plugin only in the Common module itself

## Implementation Plan

### Files to Modify
- `sources/common/lovelylib-1.21.1/NeoForge/build.gradle`
- `sources/common/lovelylib-1.21.1/Fabric/build.gradle`
- `sources/common/lovelylib-1.21.1/Forge/build.gradle`

### Changes Required
1. Replace `id 'multiloader-common'` with `id 'multiloader-loader'`
2. Remove commented Common module dependencies
3. Ensure proper plugin ordering
4. Update NeoForge to use correct gradle plugin version

## Consequences

### Positive
- **Build Success**: Resolves compilation errors
- **Proper Architecture**: Establishes correct Common module as dependency
- **Consistency**: Matches working Legacy implementation pattern
- **Maintainability**: Uses established multiloader plugin system

### Negative
- **Plugin Dependency**: Relies on custom multiloader plugins in buildSrc
- **Configuration Complexity**: Requires understanding of multiloader plugin system

### Risks
- **Plugin Compatibility**: Must ensure buildSrc plugins are compatible
- **Version Alignment**: Need to verify gradle plugin versions match

## Validation

### Success Criteria
- [ ] `./gradlew build` completes successfully
- [ ] All loader modules can import Common classes
- [ ] No compilation errors in any module
- [ ] Generated JARs contain expected classes

### Test Commands
```bash
cd sources/common/lovelylib-1.21.1
./gradlew clean build --stacktrace
```

## Related Decisions
- Links to ADR_001_Library_Architecture_Strategy.md
- Future: ADR for Common module API design

## Implementation Notes

The multiloader-loader.gradle plugin in buildSrc automatically:
- Sets up Common module as compileOnly dependency
- Configures commonJava and commonResources configurations  
- Includes Common sources in compilation and packaging
- Handles capability requirements for proper dependency resolution

This is the established pattern used successfully in Legacy 1.21.1.