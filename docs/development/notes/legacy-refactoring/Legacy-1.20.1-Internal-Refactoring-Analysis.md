---
created: 2025-11-29
tags:
  - LovelyRobot
  - Architecture
  - Refactoring
  - Analysis
---

# Legacy 1.20.1 Internal Refactoring Analysis
## Current State vs. Target Architecture (Internal Structure)

## Executive Summary

This document analyzes the current Legacy 1.20.1 codebase against the refactoring plan, with the constraint that all code remains within Forge and Fabric folders (no external modules yet). The goal is to:

1. **Organize code internally** using the four-layer concept within each loader
2. **Identify reusable code** that should eventually move to external libraries
3. **Reduce duplication** between Forge and Fabric
4. **Prepare for future extraction** to HZLib/HZFramework

## Current Structure Analysis

### Forge Structure

```
Forge/src/main/java/net/msymbios/llovelyr/
├── LovelyLegacy.java                           [Mod Entry - 150 lines]
├── common/                                     [Shared Logic - DUPLICATED]
│   ├── entity/
│   │   ├── goal/                               [4 AI goal classes]
│   │   │   ├── AiAutoAttackGoal.java          [~150 lines]
│   │   │   ├── AiBaseDefenseGoal.java         [~200 lines]
│   │   │   ├── AiConditionalWanderGoal.java   [~100 lines]
│   │   │   └── AiFollowOwnerGoal.java         [~150 lines]
│   │   └── internal/                           [Base classes]
│   │       ├── InternalAnimation.java          [~200 lines]
│   │       ├── InternalEntity.java             [~800 lines]
│   │       ├── InternalEntityType.java         [~150 lines]
│   │       ├── InternalLayer.java              [~100 lines]
│   │       ├── InternalLogic.java              [~400 lines]
│   │       ├── InternalModel.java              [~100 lines]
│   │       └── InternalParticle.java           [~100 lines]
│   └── util/                                   [Utilities]
│       ├── ObjectUtil.java                     [~50 lines]
│       ├── interfaces/
│       │   └── IReadWriteNBT.java              [~20 lines]
│       └── internal/
│           ├── Utility.java                    [~300 lines]
│           └── Version.java                    [~100 lines]
└── source/                                     [Mod-Specific]
    ├── blocks/
    │   └── LovelyBlocks.java                   [~30 lines]
    ├── commands/
    │   ├── ColorArgumentType.java              [~100 lines]
    │   ├── ColorArgumentTypeInfo.java          [~50 lines]
    │   └── LovelyRobotCommand.java             [~1400 lines]
    ├── configs/
    │   ├── LovelyConfigs.java                  [~400 lines]
    │   ├── LovelyIdentifier.java               [~200 lines]
    │   └── LovelyResource.java                 [~50 lines]
    ├── entity/
    │   ├── LovelyEntities.java                 [~100 lines]
    │   ├── LovelyRobot.java                    [~1258 lines]
    │   ├── client/
    │   │   ├── layer/                          [2 layer classes]
    │   │   ├── model/                          [2 model classes]
    │   │   └── renderer/                       [2 renderer classes]
    │   ├── custom/
    │   │   ├── Bunny2Entity.java               [~40 lines]
    │   │   └── VanillaEntity.java              [~40 lines]
    │   └── internal/
    │       ├── NativeEntityType.java           [~150 lines]
    │       └── enums/                          [7 enum classes]
    ├── events/
    │   └── LovelyEvents.java                   [~100 lines]
    ├── groups/
    │   └── LovelyGroups.java                   [~50 lines]
    ├── items/
    │   ├── LovelyItems.java                    [~100 lines]
    │   ├── custom/
    │   │   ├── LovelyCoreItem.java             [~100 lines]
    │   │   └── LovelySpawnItem.java            [~300 lines]
    │   └── util/
    │       └── TooltipUtils.java               [~100 lines]
    └── recipes/
        ├── LovelyRecipes.java                  [~50 lines]
        ├── custom/                             [4 recipe classes]
        ├── interfaces/                         [2 interface classes]
        └── internal/
            ├── modifiers/                      [1 modifier class]
            └── strategies/                     [2 strategy classes]
```

### Fabric Structure (Similar, with differences)

```
Fabric/src/main/java/net/msymbios/llovelyr/
├── LovelyLegacy.java                           [Mod Entry - Different API]
├── common/                                     [DUPLICATED from Forge]
│   └── [Same structure as Forge]
├── source/                                     [Mod-Specific]
│   ├── client/
│   │   └── LovelyClient.java                   [Fabric-only]
│   ├── commands/
│   │   ├── ColorArgumentType.java              [Same as Forge]
│   │   ├── LovelyCommand.java                  [Different from Forge]
│   │   └── LovelyCommandArguments.java         [Fabric-only]
│   ├── configs/
│   │   ├── LovelyConfigs.java                  [Different structure]
│   │   ├── LovelyIdentifier.java               [Same as Forge]
│   │   └── LovelyResource.java                 [Same as Forge]
│   ├── data/
│   │   └── LovelyGenerator.java                [Fabric-only]
│   ├── entity/
│   │   └── [Similar to Forge with API differences]
│   ├── events/
│   │   └── LovelyEvents.java                   [Different from Forge]
│   ├── groups/
│   │   └── LovelyGroups.java                   [Different from Forge]
│   ├── items/
│   │   └── [Similar to Forge with API differences]
│   ├── mixin/
│   │   └── IShapedRecipeAccessor.java          [Fabric-only]
│   └── recipes/
        └── [Similar to Forge with API differences]
```



## Current State Assessment

### ✅ Already Completed

You've already created the four-layer folder structure in both Forge and Fabric:
- `framework/` - Created with some files already moved
- `lib/` - Created (empty, ready for loader-specific bridges)
- `common/` - Created with significant content already moved
- `source/` - Exists with remaining mod-specific code

### 📊 Current Organization Status

#### Framework Layer (Partially Complete)

**Forge: `framework/`**
```
framework/
├── common/
│   └── InternalIdentifier.java             ✅ MOVED
├── entity/
│   └── enums/
│       ├── EntityHand.java                  ✅ MOVED
│       └── EntityState.java                 ✅ MOVED
└── utils/
    ├── ObjectUtil.java                      ✅ MOVED
    └── Version.java                         ✅ MOVED
```

**Status:** 5 files moved, ~10-15 more files need to move here

#### Common Layer (Significantly Complete)

**Forge: `common/`**
```
common/
├── commands/
│   ├── ColorArgumentType.java               ✅ MOVED
│   └── ColorArgumentTypeInfo.java           ✅ MOVED
├── entity/
│   ├── enums/
│   │   ├── EntityAnimation.java             ✅ MOVED
│   │   ├── EntityAnimator.java              ✅ MOVED
│   │   ├── EntityModel.java                 ✅ MOVED
│   │   ├── EntityTexture.java               ✅ MOVED
│   │   └── EntityVariant.java               ✅ MOVED
│   ├── goal/
│   │   ├── AiAutoAttackGoal.java            ✅ MOVED
│   │   ├── AiBaseDefenseGoal.java           ✅ MOVED
│   │   ├── AiConditionalWanderGoal.java     ✅ MOVED
│   │   └── AiFollowOwnerGoal.java           ✅ MOVED
│   └── internal/
│       ├── InternalAnimation.java           ✅ MOVED
│       ├── InternalEntity.java              ✅ MOVED
│       ├── InternalEntityType.java          ✅ MOVED
│       ├── InternalLayer.java               ✅ MOVED
│       ├── InternalLogic.java               ✅ MOVED
│       ├── InternalModel.java               ✅ MOVED
│       └── InternalParticle.java            ✅ MOVED
├── items/
│   ├── custom/
│   │   ├── LovelyCoreItem.java              ✅ MOVED
│   │   └── LovelySpawnItem.java             ✅ MOVED
│   └── utils/
│       └── TooltipUtils.java                ✅ MOVED
├── recipes/
│   ├── LovelySpawnDyeRecipe.java            ✅ MOVED
│   ├── LovelySpawnDyeRecipeSerializer.java  ✅ MOVED
│   ├── LovelySpawnRecipe.java               ✅ MOVED
│   └── LovelySpawnRecipeSerializer.java     ✅ MOVED
├── shared/
│   ├── LovelyIdentifier.java                ✅ MOVED
│   └── LovelyResource.java                  ✅ MOVED
└── utils/
    └── internal/
        └── Utility.java                     ✅ MOVED
```

**Status:** ~30 files moved, excellent progress!

#### Source Layer (Needs Refactoring)

**Forge: `source/`**
```
source/
├── LovelyBlocks.java                        ⚠️ NEEDS REVIEW
├── LovelyCommands.java                      ⚠️ NEEDS REFACTORING (1400+ lines)
├── LovelyConfigs.java                       ⚠️ NEEDS REVIEW
├── LovelyEntities.java                      ⚠️ NEEDS REVIEW
├── LovelyEvents.java                        ⚠️ NEEDS REVIEW
├── LovelyGroups.java                        ⚠️ NEEDS REVIEW
├── LovelyItems.java                         ⚠️ NEEDS REVIEW
├── LovelyRecipes.java                       ⚠️ NEEDS REVIEW
└── entity/
    ├── NativeEntityType.java                ⚠️ NEEDS REVIEW
    ├── LovelyRobot.java                     ⚠️ NEEDS MAJOR REFACTORING (1258 lines)
    ├── client/
    │   ├── layer/                           ✅ KEEP (GeckoLib specific)
    │   ├── model/                           ✅ KEEP (GeckoLib specific)
    │   └── renderer/                        ✅ KEEP (GeckoLib specific)
    └── custom/
        ├── Bunny2Entity.java                ⚠️ NEEDS REFACTORING
        └── VanillaEntity.java               ⚠️ NEEDS REFACTORING
```

**Status:** Needs significant refactoring to use common layer



## Detailed Refactoring Recommendations

### Priority 1: Complete Framework Layer (Pure Java)

**Goal:** Move all code with ZERO Minecraft dependencies to framework

#### Files to Move to Framework

| Current Location | Target Location | Reason | Effort |
|-----------------|-----------------|--------|--------|
| `common/entity/enums/EntityAnimation.java` | `framework/entity/enums/EntityAnimation.java` | Pure enum, no MC deps | Low |
| `common/entity/enums/EntityAnimator.java` | `framework/entity/enums/EntityAnimator.java` | Pure enum, no MC deps | Low |
| `common/entity/enums/EntityModel.java` | `framework/entity/enums/EntityModel.java` | Pure enum, no MC deps | Low |
| `common/entity/enums/EntityTexture.java` | `framework/entity/enums/EntityTexture.java` | Pure enum, no MC deps | Low |
| `common/entity/enums/EntityVariant.java` | `framework/entity/enums/EntityVariant.java` | Pure enum, no MC deps | Low |

**Action Items:**
1. Move remaining enums from `common/entity/enums/` to `framework/entity/enums/`
2. Update imports in dependent files
3. Verify no Minecraft dependencies remain in framework

**Estimated Time:** 1-2 hours

### Priority 2: Establish Lib Layer (Loader Bridges)

**Goal:** Create loader-specific bridges that connect Framework to Minecraft APIs

#### Lib Structure to Create

```
lib/
├── entity/
│   ├── EntityBridge.java                   [NEW - Forge/Fabric specific]
│   ├── EntityDataSync.java                 [NEW - Data tracker bridge]
│   └── EntityRegistry.java                 [NEW - Registration bridge]
├── rendering/
│   ├── RenderBridge.java                   [NEW - Resource location bridge]
│   └── ParticleBridge.java                 [NEW - Particle spawning bridge]
├── config/
│   └── ConfigBridge.java                   [NEW - Config system bridge]
├── commands/
│   └── CommandBridge.java                  [NEW - Command registration bridge]
└── utils/
    ├── NBTBridge.java                      [NEW - NBT serialization bridge]
    └── TextBridge.java                     [NEW - Text component bridge]
```

**Key Responsibilities:**
- **EntityBridge:** Convert Framework EntityData ↔ Minecraft EntityDataAccessor/TrackedData
- **RenderBridge:** Convert Framework resource paths ↔ ResourceLocation/Identifier
- **ConfigBridge:** Abstract Forge ConfigSpec vs Fabric SimpleConfig
- **CommandBridge:** Abstract command registration differences
- **NBTBridge:** Abstract CompoundTag/NbtCompound differences

**Estimated Time:** 8-12 hours per loader

### Priority 3: Refactor Common Layer

**Goal:** Make Common layer use Framework + Lib instead of direct Minecraft APIs

#### Files Needing Refactoring

**High Priority (Core Entity Logic):**

| File | Current State | Target State | Complexity |
|------|--------------|--------------|------------|
| `common/entity/internal/InternalEntity.java` | 800 lines, direct MC API | Use Framework + Lib bridges | High |
| `common/entity/internal/InternalLogic.java` | 400 lines, mixed logic | Extract pure logic to Framework | Medium |
| `common/entity/internal/InternalAnimation.java` | 200 lines, GeckoLib specific | Keep but use Framework enums | Low |
| `common/entity/internal/InternalParticle.java` | 100 lines, MC particle API | Use Lib ParticleBridge | Low |

**Medium Priority (Items & Recipes):**

| File | Current State | Target State | Complexity |
|------|--------------|--------------|------------|
| `common/items/custom/LovelySpawnItem.java` | 300 lines, MC API | Use Lib bridges | Medium |
| `common/items/custom/LovelyCoreItem.java` | 100 lines, MC API | Use Lib bridges | Low |
| `common/recipes/*` | 4 files, MC recipe API | Keep as-is (MC-specific) | Low |

**Low Priority (Utilities):**

| File | Current State | Target State | Complexity |
|------|--------------|--------------|------------|
| `common/utils/internal/Utility.java` | 300 lines, mixed | Extract pure logic to Framework | Medium |
| `common/shared/LovelyIdentifier.java` | 200 lines, text components | Use Lib TextBridge | Low |
| `common/shared/LovelyResource.java` | 50 lines, ResourceLocation | Use Lib RenderBridge | Low |

**Estimated Time:** 15-20 hours

### Priority 4: Refactor Source Layer

**Goal:** Make Source layer thin wrappers that use Common + Lib

#### Critical Refactoring: LovelyRobot.java

**Current:** 1258 lines in `source/entity/LovelyRobot.java`

**Target Structure:**
```java
// source/entity/LovelyRobot.java (Forge-specific, ~100 lines)
public abstract class LovelyRobot extends InternalEntity implements GeoEntity {
    // Only Forge-specific implementations
    
    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        // Use Lib EntityBridge for data sync
    }
    
    @Override
    public void addAdditionalSaveData(CompoundTag nbt) {
        // Use Lib NBTBridge
    }
    
    // All business logic moved to Common InternalEntity
}
```

**Refactoring Steps:**
1. Move all business logic to `common/entity/internal/InternalEntity.java`
2. Keep only Forge-specific API calls in `source/entity/LovelyRobot.java`
3. Use Lib bridges for data sync, NBT, particles, sounds
4. Repeat for Fabric version

**Estimated Time:** 10-15 hours

#### Other Source Files

| File | Current Lines | Target Lines | Action |
|------|--------------|--------------|--------|
| `LovelyCommands.java` | 1400 | ~100 | Extract logic to Common, keep registration |
| `LovelyConfigs.java` | 400 | ~50 | Use Lib ConfigBridge |
| `LovelyEntities.java` | 100 | ~50 | Use Lib EntityRegistry |
| `LovelyItems.java` | 100 | ~50 | Use Lib ItemRegistry |
| `LovelyRecipes.java` | 50 | ~30 | Use Lib RecipeRegistry |
| `LovelyEvents.java` | 100 | ~50 | Keep loader-specific |
| `LovelyGroups.java` | 50 | ~30 | Keep loader-specific |
| `LovelyBlocks.java` | 30 | ~20 | Keep loader-specific |

**Estimated Time:** 12-18 hours



## Code Reusability Analysis

### What Should Eventually Move to External HZLib

Based on your goal to make code reusable for other mods, here's what should eventually be extracted:

#### Tier 1: Highly Reusable (Move to HZLib First)

**Framework Layer (100% reusable):**
- ✅ All enums (EntityState, EntityHand, etc.)
- ✅ ObjectUtil, Version
- ✅ Pure data classes

**Lib Layer (100% reusable):**
- 🔄 EntityBridge - Any mod with entities needs this
- 🔄 NBTBridge - Universal NBT handling
- 🔄 ConfigBridge - Universal config abstraction
- 🔄 RenderBridge - Universal resource handling
- 🔄 CommandBridge - Universal command abstraction

**Common Layer (80% reusable):**
- 🔄 AI Goals - Reusable for any tameable entity mod
- 🔄 InternalEntityType - Generic entity type system
- 🔄 InternalLogic - Math/calculation utilities
- 🔄 Utility - Generic helper functions

**Reusability Score:** ⭐⭐⭐⭐⭐ (Essential for any entity mod)

#### Tier 2: Moderately Reusable (Move to HZLib Later)

**Common Layer (50% reusable):**
- 🔄 InternalEntity - Base entity with level/XP system
- 🔄 InternalAnimation - GeckoLib animation wrapper
- 🔄 InternalParticle - Particle effect utilities
- 🔄 Recipe system - NBT transfer recipes

**Reusability Score:** ⭐⭐⭐⭐ (Useful for similar mods)

#### Tier 3: LovelyRobot-Specific (Keep in Mod)

**Common Layer (0% reusable):**
- ❌ LovelySpawnItem - Robot-specific spawn mechanics
- ❌ LovelyCoreItem - Robot-specific core mechanics
- ❌ LovelyIdentifier - Mod-specific identifiers
- ❌ LovelyResource - Mod-specific resources

**Source Layer (0% reusable):**
- ❌ VanillaEntity, Bunny2Entity - Robot variants
- ❌ Client rendering - Robot-specific models
- ❌ LovelyCommands - Robot-specific commands
- ❌ LovelyConfigs - Robot-specific config

**Reusability Score:** ⭐ (Mod-specific, not reusable)

### Extraction Priority for HZLib

**Phase 1: Extract Framework (Week 1)**
- Move `framework/` to external `hzframework` module
- Pure Java, no dependencies
- Can be used by ANY Java project

**Phase 2: Extract Lib Bridges (Week 2-3)**
- Move `lib/` to external `hzlib-forge` and `hzlib-fabric` modules
- Depends on `hzframework` + Minecraft
- Can be used by any Minecraft mod

**Phase 3: Extract Reusable Common (Week 4-5)**
- Move Tier 1 & 2 common code to `hzlib-common` module
- Depends on `hzframework` + `hzlib-{loader}`
- Can be used by similar entity mods

**Phase 4: Keep Mod-Specific (Ongoing)**
- Keep Tier 3 in LovelyRobot mod
- Depends on `hzlib-common`
- Mod-specific implementation

## Duplication Analysis

### Current Duplication Between Forge and Fabric

**100% Duplicated (Identical Files):**
```
common/                                      [~30 files, ~5000 lines]
├── commands/                                ✅ Can be shared
├── entity/enums/                            ✅ Can be shared
├── entity/goal/                             ✅ Can be shared
├── entity/internal/                         ✅ Can be shared
├── items/                                   ✅ Can be shared
├── recipes/                                 ✅ Can be shared
├── shared/                                  ✅ Can be shared
└── utils/                                   ✅ Can be shared

framework/                                   [~5 files, ~500 lines]
└── All files                                ✅ Can be shared
```

**95% Duplicated (Minor API Differences):**
```
source/entity/LovelyRobot.java               ⚠️ Needs abstraction
source/entity/custom/*.java                  ⚠️ Needs abstraction
```

**50% Duplicated (Different Implementations):**
```
source/LovelyCommands.java                   ⚠️ Extract logic to Common
source/LovelyConfigs.java                    ⚠️ Use Lib ConfigBridge
source/LovelyEntities.java                   ⚠️ Use Lib EntityRegistry
source/LovelyItems.java                      ⚠️ Use Lib ItemRegistry
```

**0% Duplicated (Loader-Specific):**
```
source/LovelyEvents.java                     ✅ Keep separate
source/LovelyGroups.java                     ✅ Keep separate
Fabric: source/LovelyClient.java             ✅ Fabric-only
Fabric: source/LovelyGenerator.java          ✅ Fabric-only
Fabric: lib/mixin/                           ✅ Fabric-only
```

### Duplication Reduction Roadmap

**Current State:**
- Total LOC (both loaders): ~15,000
- Duplicated LOC: ~12,750 (85%)
- Unique LOC: ~2,250 (15%)

**After Internal Refactoring:**
- Total LOC (both loaders): ~12,000
- Duplicated LOC: ~3,000 (25%)
- Unique LOC: ~9,000 (75%)
- **Reduction: 60% less duplication**

**After External HZLib Extraction:**
- HZLib LOC: ~6,000 (reusable)
- Mod LOC (both loaders): ~6,000
- Duplicated LOC: ~500 (8%)
- Unique LOC: ~5,500 (92%)
- **Reduction: 92% less duplication**

## Implementation Roadmap

### Phase 1: Complete Framework Layer (Week 1)

**Tasks:**
1. Move remaining enums to framework
2. Create pure data classes in framework
3. Extract pure logic from Utility to framework
4. Write unit tests for framework
5. Verify zero Minecraft dependencies

**Deliverable:** Complete, tested Framework layer

**Estimated Time:** 8-12 hours

### Phase 2: Create Lib Bridges (Week 2-3)

**Tasks:**
1. Create EntityBridge (Forge & Fabric)
2. Create NBTBridge (Forge & Fabric)
3. Create RenderBridge (Forge & Fabric)
4. Create ConfigBridge (Forge & Fabric)
5. Create CommandBridge (Forge & Fabric)
6. Write integration tests

**Deliverable:** Complete Lib layer for both loaders

**Estimated Time:** 20-30 hours

### Phase 3: Refactor Common Layer (Week 4)

**Tasks:**
1. Refactor InternalEntity to use Framework + Lib
2. Refactor InternalLogic to use Framework
3. Refactor InternalParticle to use Lib
4. Refactor items to use Lib
5. Refactor utilities to use Framework + Lib
6. Update all imports

**Deliverable:** Common layer using Framework + Lib

**Estimated Time:** 15-20 hours

### Phase 4: Refactor Source Layer (Week 5-6)

**Tasks:**
1. Refactor LovelyRobot.java (Forge & Fabric)
2. Refactor entity variants (Forge & Fabric)
3. Refactor LovelyCommands (Forge & Fabric)
4. Refactor registration classes (Forge & Fabric)
5. Refactor config classes (Forge & Fabric)
6. Full gameplay testing

**Deliverable:** Thin Source layer using Common + Lib

**Estimated Time:** 25-35 hours

### Phase 5: Testing & Validation (Week 7)

**Tasks:**
1. Cross-loader testing
2. Multiplayer testing
3. Performance testing
4. Code quality analysis
5. Documentation updates

**Deliverable:** Validated, production-ready refactored codebase

**Estimated Time:** 10-15 hours

**Total Estimated Time:** 78-112 hours (10-14 working days)



## Immediate Next Steps

### Step 1: Verify Current State (30 minutes)

**Checklist:**
- [ ] Verify all files in `common/` are identical between Forge and Fabric
- [ ] Verify all files in `framework/` are identical between Forge and Fabric
- [ ] Check if any files in `lib/` exist yet
- [ ] List all files still in old `source/` structure

**Commands to Run:**
```bash
# Compare common folders
diff -r Forge/src/main/java/net/msymbios/llovelyr/common/ \
        Fabric/src/main/java/net/msymbios/llovelyr/common/

# Compare framework folders
diff -r Forge/src/main/java/net/msymbios/llovelyr/framework/ \
        Fabric/src/main/java/net/msymbios/llovelyr/framework/

# List source files
find Forge/src/main/java/net/msymbios/llovelyr/source/ -name "*.java"
find Fabric/src/main/java/net/msymbios/llovelyr/source/ -name "*.java"
```

### Step 2: Complete Framework Migration (2-3 hours)

**Move these files from `common/entity/enums/` to `framework/entity/enums/`:**

1. EntityAnimation.java
2. EntityAnimator.java
3. EntityModel.java
4. EntityTexture.java
5. EntityVariant.java

**Update imports in:**
- All files in `common/entity/internal/`
- All files in `common/entity/goal/`
- All files in `source/entity/`

**Verification:**
```bash
# Check for Minecraft imports in framework
grep -r "import net.minecraft" framework/

# Should return ZERO results
```

### Step 3: Create First Lib Bridge (3-4 hours)

**Create `lib/entity/EntityBridge.java` (Forge version):**

```java
package net.msymbios.llovelyr.lib.entity;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.msymbios.llovelyr.framework.entity.EntityData;

/**
 * Forge-specific bridge for entity data synchronization.
 * <p>
 * <b>Architecture:</b> Converts Framework EntityData to Forge's EntityDataAccessor
 * system, enabling client-server synchronization without Framework knowing about
 * Minecraft's networking.
 */
public class EntityBridge {
    
    /**
     * Creates a synchronized integer data accessor.
     */
    public static EntityDataAccessor<Integer> createIntAccessor(Class<?> entityClass) {
        return SynchedEntityData.defineId(entityClass, EntityDataSerializers.INT);
    }
    
    /**
     * Creates a synchronized float data accessor.
     */
    public static EntityDataAccessor<Float> createFloatAccessor(Class<?> entityClass) {
        return SynchedEntityData.defineId(entityClass, EntityDataSerializers.FLOAT);
    }
    
    /**
     * Creates a synchronized boolean data accessor.
     */
    public static EntityDataAccessor<Boolean> createBoolAccessor(Class<?> entityClass) {
        return SynchedEntityData.defineId(entityClass, EntityDataSerializers.BOOLEAN);
    }
    
    /**
     * Creates a synchronized string data accessor.
     */
    public static EntityDataAccessor<String> createStringAccessor(Class<?> entityClass) {
        return SynchedEntityData.defineId(entityClass, EntityDataSerializers.STRING);
    }
}
```

**Create `lib/entity/EntityBridge.java` (Fabric version):**

```java
package net.msymbios.llovelyr.lib.entity;

import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.msymbios.llovelyr.framework.entity.EntityData;

/**
 * Fabric-specific bridge for entity data synchronization.
 * <p>
 * <b>Architecture:</b> Converts Framework EntityData to Fabric's TrackedData
 * system, enabling client-server synchronization without Framework knowing about
 * Minecraft's networking.
 */
public class EntityBridge {
    
    /**
     * Creates a synchronized integer data tracker.
     */
    public static TrackedData<Integer> createIntTracker(Class<?> entityClass) {
        return DataTracker.registerData(entityClass, TrackedDataHandlerRegistry.INTEGER);
    }
    
    /**
     * Creates a synchronized float data tracker.
     */
    public static TrackedData<Float> createFloatTracker(Class<?> entityClass) {
        return DataTracker.registerData(entityClass, TrackedDataHandlerRegistry.FLOAT);
    }
    
    /**
     * Creates a synchronized boolean data tracker.
     */
    public static TrackedData<Boolean> createBoolTracker(Class<?> entityClass) {
        return DataTracker.registerData(entityClass, TrackedDataHandlerRegistry.BOOLEAN);
    }
    
    /**
     * Creates a synchronized string data tracker.
     */
    public static TrackedData<String> createStringTracker(Class<?> entityClass) {
        return DataTracker.registerData(entityClass, TrackedDataHandlerRegistry.STRING);
    }
}
```

**Test the bridge:**
- Use it in one entity data accessor
- Verify compilation
- Verify runtime behavior

### Step 4: Document Progress (30 minutes)

**Update this document with:**
- What was completed
- What issues were encountered
- What needs to be done next
- Updated time estimates

### Step 5: Sync Fabric with Forge (1 hour)

**After completing Forge changes:**
1. Copy identical files from Forge to Fabric
2. Adjust API differences in Fabric
3. Verify both compile
4. Test both in-game

## Success Criteria

### Short-Term (After Phase 1-2)

- [ ] Framework has zero Minecraft dependencies
- [ ] Framework is identical between Forge and Fabric
- [ ] At least one Lib bridge is working
- [ ] Code compiles and runs

### Medium-Term (After Phase 3-4)

- [ ] Common layer uses Framework + Lib
- [ ] Source layer is thin (<100 lines per file)
- [ ] Duplication reduced to <25%
- [ ] All features still work

### Long-Term (After Phase 5)

- [ ] Code is ready for external extraction
- [ ] Clear separation of concerns
- [ ] Duplication reduced to <10%
- [ ] Performance is maintained or improved
- [ ] Documentation is complete

## Risk Mitigation

### Risk 1: Breaking Changes During Refactoring

**Mitigation:**
- Work in feature branch
- Commit after each successful step
- Keep old code until new code is verified
- Test frequently

### Risk 2: API Differences Between Loaders

**Mitigation:**
- Create Lib bridges early
- Test both loaders in parallel
- Document API differences
- Use abstraction layers

### Risk 3: Time Overruns

**Mitigation:**
- Work in small increments
- Validate each phase before continuing
- Adjust estimates based on actual progress
- Focus on high-value refactoring first

### Risk 4: Feature Regressions

**Mitigation:**
- Maintain comprehensive test checklist
- Test after each major change
- Keep backup of working code
- Document all behavior changes

## Conclusion

You've made excellent progress with the initial organization! The four-layer structure is in place, and significant code has already been moved to the appropriate layers.

**Current Status:**
- ✅ Framework: 30% complete
- ✅ Lib: 0% complete (ready to start)
- ✅ Common: 80% complete
- ⚠️ Source: Needs refactoring

**Recommended Next Steps:**
1. Complete Framework migration (2-3 hours)
2. Create first Lib bridge (3-4 hours)
3. Refactor one entity to use new structure (4-6 hours)
4. Validate and test (2-3 hours)

**Total Time to Working Prototype:** 11-16 hours

Once you have a working prototype with one entity using the new architecture, you can confidently proceed with the full refactoring knowing the approach works.

---

**Document Status:** Analysis Complete
**Next Action:** Complete Framework migration
**Owner:** Development Team
**Last Updated:** 2025-11-29

