# GeckoLib Dependency Implementation Process

**Status**: Complete
**Date**: 2025-12-12
**Project**: Lovely Lib 1.21.1 Multiloader
**Purpose**: Document the step-by-step process for implementing GeckoLib dependency in multiloader Minecraft mod projects

## Overview

This document provides a complete implementation guide for adding GeckoLib animation library dependency to multiloader Minecraft mod projects supporting Fabric, Forge, and NeoForge loaders.

## Prerequisites

- Multiloader project structure with separate loader modules
- Gradle build system
- GeckoLib repository already configured in main build.gradle
- Understanding of conditional dependencies in Gradle

## Implementation Steps

### Step 1: Configure Version Properties

**File**: `gradle.properties`

Add GeckoLib version properties for each loader:

```properties
# Set to true if your library needs GeckoLib
needs_geckolib=true

# GeckoLib versions per loader
neoforge_geckolib=geckolib-neoforge-1.21.1:4.7.3
forge_geckolib=geckolib-forge-1.21.1:4.7.3
fabric_geckolib=geckolib-fabric-1.21.1:4.7.3
```

**Key Points**:
- Use `needs_geckolib` flag to enable/disable GeckoLib across all loaders
- Version format: `geckolib-[loader]-[mc_version]:[geckolib_version]`
- Keep versions consistent across loaders when possible

### Step 2: Configure Repository Access

**File**: `build.gradle` (root)

Ensure GeckoLib repository is configured:

```groovy
repositories {
    mavenCentral()
    
    maven {
        name = 'GeckoLib'
        url = 'https://dl.cloudsmith.io/public/geckolib3/geckolib/maven/'
    }
}
```

**Note**: This should already be configured in most projects.

### Step 3: Configure Fabric Loader

**File**: `Fabric/build.gradle`

Add conditional GeckoLib dependency:

```groovy
dependencies {
    minecraft "com.mojang:minecraft:${minecraft_version}"
    mappings "net.fabricmc:yarn:${fabric_yarn_mappings}:v2"
    modImplementation "net.fabricmc:fabric-loader:${fabric_loader_version}"
    modApi "net.fabricmc.fabric-api:fabric-api:${fabric_api_version}"
    
    // GeckoLib for Fabric (conditional)
    if (project.hasProperty('needs_geckolib') && needs_geckolib.toBoolean()) {
        modImplementation "software.bernie.geckolib:${fabric_geckolib}"
    }
}
```

**Key Points**:
- Use `modImplementation` for Fabric
- Full dependency format: `software.bernie.geckolib:${fabric_geckolib}`
- Conditional based on `needs_geckolib` property

### Step 4: Configure Forge Loader

**File**: `Forge/build.gradle`

Add conditional GeckoLib dependency:

```groovy
dependencies {
    minecraft "net.minecraftforge:forge:${minecraft_version}-${forge_version}"

    // GeckoLib dependency (conditional)
    if (project.hasProperty('needs_geckolib') && needs_geckolib.toBoolean()) {
        implementation fg.deobf("software.bernie.geckolib:${forge_geckolib}")
    }
}
```

**Key Points**:
- Use `implementation fg.deobf()` for Forge
- Full dependency format: `software.bernie.geckolib:${forge_geckolib}`
- Conditional based on `needs_geckolib` property

### Step 5: Configure NeoForge Loader

**File**: `NeoForge/build.gradle`

Add conditional GeckoLib dependency:

```groovy
dependencies {
    implementation "net.neoforged:neoforge:${neoforge_version}"
    
    // GeckoLib for NeoForge (conditional)
    if (project.hasProperty('needs_geckolib') && needs_geckolib.toBoolean()) {
        implementation "software.bernie.geckolib:${neoforge_geckolib}"
    }
}
```

**Key Points**:
- Use `implementation` for NeoForge
- Full dependency format: `software.bernie.geckolib:${neoforge_geckolib}`
- Conditional based on `needs_geckolib` property

## Testing Process

### Step 1: Clean Build Test

```bash
./gradlew clean build
```

**Expected Result**: Build completes successfully without dependency resolution errors.

### Step 2: Runtime Testing

Test each loader individually:

```bash
# Test Fabric
./gradlew :Fabric:runClient

# Test Forge  
./gradlew :Forge:runClient

# Test NeoForge
./gradlew :NeoForge:runClient
```

**Expected Results**:
- Minecraft client launches successfully
- GeckoLib appears in mod list (version 4.7.3)
- No GeckoLib-related errors in logs
- Mod initializes correctly

### Step 3: Verification Checklist

- [ ] Build completes without errors
- [ ] All three loaders launch successfully
- [ ] GeckoLib version appears in mod lists
- [ ] No dependency conflicts
- [ ] No missing dependency errors
- [ ] Mod initialization logs appear correctly

## Common Issues and Solutions

### Issue 1: Dependency Format Error

**Error**: `Could not find geckolib-fabric-1.21.1:4.7.3`

**Solution**: Ensure full dependency format includes group ID:
```groovy
// Wrong
modImplementation "${fabric_geckolib}"

// Correct
modImplementation "software.bernie.geckolib:${fabric_geckolib}"
```

### Issue 2: Inconsistent Conditional Logic

**Error**: Some loaders include GeckoLib when others don't

**Solution**: Ensure all loaders use identical conditional logic:
```groovy
if (project.hasProperty('needs_geckolib') && needs_geckolib.toBoolean()) {
    // dependency here
}
```

### Issue 3: Version Mismatch

**Error**: Different GeckoLib versions across loaders

**Solution**: Verify all version properties in `gradle.properties` use same GeckoLib version:
```properties
neoforge_geckolib=geckolib-neoforge-1.21.1:4.7.3
forge_geckolib=geckolib-forge-1.21.1:4.7.3
fabric_geckolib=geckolib-fabric-1.21.1:4.7.3
```

### Issue 4: Repository Access

**Error**: `Could not resolve software.bernie.geckolib`

**Solution**: Verify GeckoLib repository is configured in main `build.gradle`:
```groovy
maven {
    name = 'GeckoLib'
    url = 'https://dl.cloudsmith.io/public/geckolib3/geckolib/maven/'
}
```

## Best Practices

### 1. Conditional Dependencies
- Always use conditional logic for optional dependencies
- Use consistent property names across projects
- Document the purpose of each conditional flag

### 2. Version Management
- Keep GeckoLib versions consistent across loaders when possible
- Use properties for version management
- Test version updates across all loaders

### 3. Dependency Scope
- Use appropriate dependency scopes for each loader:
  - Fabric: `modImplementation`
  - Forge: `implementation fg.deobf()`
  - NeoForge: `implementation`

### 4. Testing Strategy
- Test build process first
- Test runtime for each loader separately
- Verify mod loading logs
- Check for version conflicts

## Template for Future Dependencies

Use this template for adding other conditional dependencies:

```groovy
// In gradle.properties
needs_[dependency]=true
[loader]_[dependency]=[artifact-id]-[loader]-[mc_version]:[version]

// In loader build.gradle
if (project.hasProperty('needs_[dependency]') && needs_[dependency].toBoolean()) {
    [scope] "[group_id]:${[loader]_[dependency]}"
}
```

## Related Documentation

- [ADR_002_Multiloader_Dependency_Configuration.md](../decisions/ADR_002_Multiloader_Dependency_Configuration.md)
- [GeckoLib Official Documentation](https://geckolib.com/)
- [Multiloader Template Documentation](../../guidelines/templates/)

## Changelog

- **2025-12-12**: Initial implementation for Lovely Lib 1.21.1
- **2025-12-12**: Added comprehensive testing and troubleshooting guide

---

**Note**: This process was successfully tested with GeckoLib 4.7.3 on Minecraft 1.21.1 across Fabric, Forge, and NeoForge loaders.