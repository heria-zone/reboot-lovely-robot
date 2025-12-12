# Tribute 1.21.1 GeckoLib Implementation Plan

**Status**: Planning
**Date**: 2025-12-12
**Project**: Lovely Tribute 1.21.1 (tlovelyr-1.21.1)
**Purpose**: Plan for implementing GeckoLib dependency in the Tribute multiloader project

## Current State Analysis

### ✅ What's Already Configured
1. **Repository Access**: GeckoLib repository already configured in main `build.gradle`
2. **Version Properties**: GeckoLib versions already defined in `gradle.properties`
3. **Conditional Logic**: All loader build.gradle files have conditional GeckoLib dependencies
4. **Dependency Format**: Correct format already implemented

### ❌ What Needs to Be Fixed
1. **Enable Flag**: `needs_geckolib=false` in `gradle.properties` - needs to be set to `true`
2. **Dependency Format Issues**: Some loaders missing `software.bernie.geckolib:` prefix

## Detailed Analysis

### Current Configuration Status

#### gradle.properties
```properties
# ✅ GOOD: Versions already defined
neoforge_geckolib=geckolib-neoforge-1.21.1:4.7.3
forge_geckolib=geckolib-forge-1.21.1:4.7.3
fabric_geckolib=geckolib-fabric-1.21.1:4.7.3

# ❌ NEEDS CHANGE: Currently disabled
needs_geckolib=false
```

#### Fabric/build.gradle
```groovy
// ❌ ISSUE: Missing software.bernie.geckolib prefix
if (project.hasProperty('needs_geckolib') && needs_geckolib.toBoolean()) {
    modImplementation "${fabric_geckolib}"  // Should be "software.bernie.geckolib:${fabric_geckolib}"
}
```

#### Forge/build.gradle
```groovy
// ✅ GOOD: Correct format already implemented
if (project.hasProperty('needs_geckolib') && needs_geckolib.toBoolean()) {
    implementation fg.deobf("software.bernie.geckolib:${forge_geckolib}")
}
```

#### NeoForge/build.gradle
```groovy
// ❌ ISSUE: Missing software.bernie.geckolib prefix
if (project.hasProperty('needs_geckolib') && needs_geckolib.toBoolean()) {
    implementation "${neoforge_geckolib}"  // Should be "software.bernie.geckolib:${neoforge_geckolib}"
}
```

## Implementation Plan

### Step 1: Enable GeckoLib Flag
**File**: `sources/tribute/tlovelyr-1.21.1/gradle.properties`

**Change**:
```properties
# From:
needs_geckolib=false

# To:
needs_geckolib=true
```

### Step 2: Fix Fabric Dependency Format
**File**: `sources/tribute/tlovelyr-1.21.1/Fabric/build.gradle`

**Change**:
```groovy
// From:
if (project.hasProperty('needs_geckolib') && needs_geckolib.toBoolean()) {
    modImplementation "${fabric_geckolib}"
}

// To:
if (project.hasProperty('needs_geckolib') && needs_geckolib.toBoolean()) {
    modImplementation "software.bernie.geckolib:${fabric_geckolib}"
}
```

### Step 3: Fix NeoForge Dependency Format
**File**: `sources/tribute/tlovelyr-1.21.1/NeoForge/build.gradle`

**Change**:
```groovy
// From:
if (project.hasProperty('needs_geckolib') && needs_geckolib.toBoolean()) {
    implementation "${neoforge_geckolib}"
}

// To:
if (project.hasProperty('needs_geckolib') && needs_geckolib.toBoolean()) {
    implementation "software.bernie.geckolib:${neoforge_geckolib}"
}
```

### Step 4: Verify Forge Configuration
**File**: `sources/tribute/tlovelyr-1.21.1/Forge/build.gradle`

**Status**: ✅ Already correct - no changes needed
```groovy
// Already correct:
if (project.hasProperty('needs_geckolib') && needs_geckolib.toBoolean()) {
    implementation fg.deobf("software.bernie.geckolib:${forge_geckolib}")
}
```

## Comparison with Legacy Implementation

### Legacy 1.21.1 (Working Reference)
- **Fabric**: `modImplementation "software.bernie.geckolib:${fabric_geckolib}"`
- **Forge**: `implementation fg.deobf("software.bernie.geckolib:${forge_geckolib}")`
- **NeoForge**: `implementation "software.bernie.geckolib:${neoforge_geckolib}"`

### Tribute 1.21.1 (After Implementation)
- **Fabric**: `modImplementation "software.bernie.geckolib:${fabric_geckolib}"` ✅
- **Forge**: `implementation fg.deobf("software.bernie.geckolib:${forge_geckolib}")` ✅
- **NeoForge**: `implementation "software.bernie.geckolib:${neoforge_geckolib}"` ✅

## Testing Strategy

### Phase 1: Build Testing
```bash
cd sources/tribute/tlovelyr-1.21.1
./gradlew clean build
```

**Expected Result**: Build completes successfully without dependency resolution errors.

### Phase 2: Runtime Testing
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
- GeckoLib 4.7.3 appears in mod list
- Tribute mod initializes correctly
- No GeckoLib-related errors in logs

### Phase 3: Integration Testing
Since Tribute depends on LovelyLib (which also has GeckoLib):
1. Verify no version conflicts between Tribute and LovelyLib GeckoLib dependencies
2. Test that both mods load correctly together
3. Verify GeckoLib features work in both mods

## Risk Assessment

### Low Risk
- **Repository Access**: Already configured correctly
- **Version Compatibility**: Using same GeckoLib version as Legacy (4.7.3)
- **Forge Implementation**: Already correct

### Medium Risk
- **Dependency Conflicts**: Tribute depends on LovelyLib which also has GeckoLib
  - **Mitigation**: Both use same GeckoLib version (4.7.3)
- **Local JAR Dependencies**: Complex local dependency system
  - **Mitigation**: Test with `refreshDependencies` task

### Minimal Risk
- **Simple Changes**: Only need to enable flag and fix dependency format
- **Proven Pattern**: Following exact same pattern as working Legacy implementation

## Implementation Checklist

### Pre-Implementation
- [ ] Backup current project state
- [ ] Verify Legacy 1.21.1 is working as reference
- [ ] Ensure LovelyLib JARs are up to date in libs/ directory

### Implementation Steps
- [ ] Step 1: Enable `needs_geckolib=true` in gradle.properties
- [ ] Step 2: Fix Fabric dependency format
- [ ] Step 3: Fix NeoForge dependency format
- [ ] Step 4: Verify Forge configuration (no changes needed)

### Testing Steps
- [ ] Phase 1: Clean build test
- [ ] Phase 2: Runtime test for each loader
- [ ] Phase 3: Integration test with LovelyLib
- [ ] Verify GeckoLib appears in mod lists
- [ ] Check for any dependency conflicts

### Post-Implementation
- [ ] Document any issues encountered
- [ ] Update implementation process if needed
- [ ] Commit changes with proper prefix

## Expected Outcome

After implementation, Tribute 1.21.1 will have:
1. **GeckoLib 4.7.3** properly integrated across all loaders
2. **Consistent dependency format** matching Legacy implementation
3. **No dependency conflicts** with LovelyLib
4. **Working animation system** ready for robot entity animations

## Differences from LovelyLib Implementation

### Tribute-Specific Considerations
1. **Local Dependencies**: Tribute uses local JAR dependencies for LovelyLib
2. **Dependency Chain**: Tribute → LovelyLib → GeckoLib (both need GeckoLib)
3. **Version Alignment**: Must ensure GeckoLib versions match between Tribute and LovelyLib

### Simplified Implementation
Unlike LovelyLib, Tribute already has:
- Conditional logic in place
- Repository configuration
- Version properties defined
- Most dependency formats correct

This makes the implementation much simpler - only need to enable the flag and fix two dependency format issues.

## Related Documentation

- [GeckoLib Implementation Process](GeckoLib_Implementation_Process.md) - General implementation guide
- [ADR_002_Multiloader_Dependency_Configuration](../decisions/ADR_002_Multiloader_Dependency_Configuration.md) - Architectural decisions
- Legacy 1.21.1 project - Working reference implementation

---

**Key Insight**: Tribute 1.21.1 is already 90% configured for GeckoLib. The implementation is much simpler than LovelyLib was, requiring only enabling the flag and fixing two dependency format issues.