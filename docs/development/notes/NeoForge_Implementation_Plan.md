# NeoForge Implementation Action Plan - Legacy 1.21.1

**Status**: Planning
**Created**: 2025-12-07
**Target**: Implement NeoForge loader support for Legacy 1.21.1
**Related Documents**:
- [SPRINT_01_TASK.md](../sprints/active/SPRINT_01_TASK.md) - Current sprint
- [CURRENT_STATE.md](../../workflow/CURRENT_STATE.md) - Project state
- [Final_Implementation_Status.md](Final_Implementation_Status.md) - Sprint 01 completion

---

## Executive Summary

This document outlines the complete action plan for implementing NeoForge loader support for the Legacy 1.21.1 variant. The Common module (shared code) is complete, and both Fabric and Forge implementations are functional. NeoForge requires a new loader-specific implementation that bridges the Common code with NeoForge's API.

**Current State**:
- ✅ Common module: Complete with all entity logic, features, and systems
- ✅ Fabric loader: Fully implemented and tested
- ✅ Forge loader: Fully implemented and tested
- ⏳ NeoForge loader: Skeleton structure exists, needs full implementation

**Goal**: Create a complete NeoForge implementation that mirrors Fabric/Forge functionality while leveraging NeoForge-specific APIs and patterns.

---

## Project Structure Overview

```
sources/legacy/llovelyr-1.21.1/
├── Common/                    # ✅ Complete - Shared code
│   └── src/main/java/net/msymbios/llovelyr/
│       ├── common/           # Entity logic, features, utilities
│       └── lib/              # Reusable systems
│
├── Fabric/                    # ✅ Complete - Fabric loader
│   └── src/main/java/net/msymbios/llovelyr/
│       ├── LovelyLegacy.java
│       └── source/           # Fabric-specific registration
│
├── Forge/                     # ✅ Complete - Forge loader
│   └── src/main/java/net/msymbios/llovelyr/
│       ├── LovelyLegacy.java
│       └── source/           # Forge-specific registration
│
└── NeoForge/                  # ⏳ In Progress - NeoForge loader
    ├── build.gradle          # ✅ Build configuration exists
    └── src/main/java/net/msymbios/llovelyr/
        ├── LovelyLegacy.java      # ⚠️ Template code (needs replacement)
        ├── LovelyLegacyClient.java # ⚠️ Exists but empty
        └── Config.java            # ⚠️ Template code (needs replacement)
```

---

## Phase 1: Analysis & Preparation

### 1.1 Understand NeoForge Differences from Forge

**Key Changes in NeoForge**:
1. **Package Changes**: `net.minecraftforge.*` → `net.neoforged.*`
2. **Event System**: Similar but with NeoForge-specific event classes
3. **Registration**: Uses `DeferredRegister` with NeoForge's registry system
4. **Mod Loading**: `@Mod` annotation with `IEventBus` and `ModContainer` parameters
5. **Configuration**: NeoForge's config system (similar to Forge but updated)
6. **Client Setup**: Separate client-side initialization patterns

**Resources**:
- NeoForge Documentation: https://docs.neoforged.net/
- Migration Guide: https://docs.neoforged.net/docs/gettingstarted/migration/
- Example Mods: NeoForge GitHub examples

### 1.2 Review Existing Implementations

**Files to Review**:
- ✅ `Forge/src/main/java/net/msymbios/llovelyr/LovelyLegacy.java` - Main mod class pattern
- ✅ `Forge/src/main/java/net/msymbios/llovelyr/source/` - All registration classes
- ✅ `Fabric/src/main/java/net/msymbios/llovelyr/source/` - Alternative patterns
- ✅ `Common/src/main/java/net/msymbios/llovelyr/` - Shared logic to integrate

**Key Classes to Port**:
1. `LovelyConstant.java` - Constants and mod ID
2. `LovelyConfigs.java` - Configuration system
3. `LovelyItems.java` - Item registration
4. `LovelyGroups.java` - Creative tabs
5. `LovelyEntities.java` - Entity registration
6. `LovelyRecipes.java` - Recipe serializers
7. `LovelyCommandArguments.java` - Command argument types
8. `LovelyEvents.java` - Event handlers (if needed)

### 1.3 Identify NeoForge-Specific Requirements

**Must Implement**:
- [ ] NeoForge mod metadata (`META-INF/neoforge.mods.toml`)
- [ ] NeoForge event bus registration
- [ ] NeoForge deferred registers
- [ ] NeoForge client-side setup
- [ ] NeoForge configuration system
- [ ] NeoForge data generation (optional)

---

## Phase 2: Core Infrastructure Setup

### 2.1 Create LovelyConstant.java

**Purpose**: Define mod ID and constants
**Location**: `NeoForge/src/main/java/net/msymbios/llovelyr/LovelyConstant.java`

**Implementation**:
```java
package net.msymbios.llovelyr;

public class LovelyConstant {
    public static final String MODID = "llovelyr";
    public static final String MOD_NAME = "Lovely Legacy";
    public static final String MOD_VERSION = "1.0.0";
}
```

**Status**: ⏳ To Do

### 2.2 Update LovelyLegacy.java (Main Mod Class)

**Purpose**: Main mod entry point with NeoForge lifecycle
**Location**: `NeoForge/src/main/java/net/msymbios/llovelyr/LovelyLegacy.java`

**Key Changes from Template**:
1. Remove example blocks/items/tabs
2. Add proper imports for Common classes
3. Register all deferred registers (items, entities, recipes, etc.)
4. Add configuration system integration
5. Add event bus listeners
6. Add client setup delegation

**Pattern to Follow**: Forge's `LovelyLegacy.java` with NeoForge API adjustments

**Status**: ⏳ To Do

### 2.3 Create LovelyLegacyClient.java

**Purpose**: Client-side initialization (rendering, models, etc.)
**Location**: `NeoForge/src/main/java/net/msymbios/llovelyr/LovelyLegacyClient.java`

**Responsibilities**:
- Entity renderer registration
- Item model property registration
- Client-side event handlers
- GeckoLib client initialization

**Status**: ⏳ To Do

### 2.4 Update Config.java

**Purpose**: NeoForge configuration system
**Location**: `NeoForge/src/main/java/net/msymbios/llovelyr/Config.java`

**Implementation Strategy**:
- Port Forge's `LovelyConfigs.java` to NeoForge config API
- Maintain same config values for consistency
- Add reload callback support for `LovelyRobotType`

**Status**: ⏳ To Do

---

## Phase 3: Registration System Implementation

### 3.1 Create LovelyItems.java

**Purpose**: Register all spawn items and robot cores
**Location**: `NeoForge/src/main/java/net/msymbios/llovelyr/source/LovelyItems.java`

**Implementation**:
```java
public class LovelyItems {
    public static final DeferredRegister.Items ITEMS = 
        DeferredRegister.createItems(LovelyConstant.MODID);
    
    // Register all 7 robot spawn items (16 colors each)
    // Register robot core items
    
    public static void register(IEventBus bus) {
        ITEMS.register(bus);
    }
}
```

**Key Differences from Forge**:
- Use NeoForge's `DeferredRegister.Items`
- Update import statements
- Maintain same item registration logic

**Status**: ⏳ To Do

### 3.2 Create LovelyGroups.java

**Purpose**: Register creative tabs
**Location**: `NeoForge/src/main/java/net/msymbios/llovelyr/source/LovelyGroups.java`

**Implementation**:
```java
public class LovelyGroups {
    public static final DeferredRegister<CreativeModeTab> TABS = 
        DeferredRegister.create(Registries.CREATIVE_MODE_TAB, LovelyConstant.MODID);
    
    // Register creative tab with all items
    
    public static void register(IEventBus bus) {
        TABS.register(bus);
    }
    
    public static void registerItems(IEventBus bus) {
        bus.addListener(LovelyGroups::addCreative);
    }
}
```

**Status**: ⏳ To Do

### 3.3 Create LovelyEntities.java

**Purpose**: Register all 7 robot entity types
**Location**: `NeoForge/src/main/java/net/msymbios/llovelyr/source/LovelyEntities.java`

**Implementation**:
```java
public class LovelyEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES = 
        DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, LovelyConstant.MODID);
    
    // Register all 7 robot types from LovelyRobotType
    
    public static void register(IEventBus bus) {
        ENTITIES.register(bus);
        bus.addListener(LovelyEntities::registerAttributes);
        bus.addListener(LovelyEntities::registerRenderers);
    }
}
```

**Key Components**:
- Entity type registration
- Attribute registration (EntityAttributeCreationEvent)
- Renderer registration (EntityRenderersEvent)
- Spawn egg registration

**Status**: ⏳ To Do

### 3.4 Create LovelyRecipes.java

**Purpose**: Register recipe serializers
**Location**: `NeoForge/src/main/java/net/msymbios/llovelyr/source/LovelyRecipes.java`

**Implementation**:
```java
public class LovelyRecipes {
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS = 
        DeferredRegister.create(BuiltInRegistries.RECIPE_SERIALIZER, LovelyConstant.MODID);
    
    // Register spawn recipe serializer
    // Register dye recipe serializer
    
    public static void register(IEventBus bus) {
        SERIALIZERS.register(bus);
    }
}
```

**Status**: ⏳ To Do

### 3.5 Create LovelyCommandArguments.java

**Purpose**: Register custom command argument types
**Location**: `NeoForge/src/main/java/net/msymbios/llovelyr/source/LovelyCommandArguments.java`

**Implementation**:
```java
public class LovelyCommandArguments {
    public static final DeferredRegister<ArgumentTypeInfo<?, ?>> ARGUMENTS = 
        DeferredRegister.create(BuiltInRegistries.COMMAND_ARGUMENT_TYPE, LovelyConstant.MODID);
    
    // Register robot selector argument
    // Register other custom arguments
    
    public static void register(IEventBus bus) {
        ARGUMENTS.register(bus);
    }
    
    public static void register(FMLCommonSetupEvent event) {
        // Register commands
    }
}
```

**Status**: ⏳ To Do

### 3.6 Create LovelyEvents.java (if needed)

**Purpose**: Handle gameplay events
**Location**: `NeoForge/src/main/java/net/msymbios/llovelyr/source/LovelyEvents.java`

**Potential Events**:
- Server starting (command registration)
- Player login (data sync)
- Entity spawn (custom logic)
- Crafting events (NBT handling)

**Status**: ⏳ To Do (evaluate necessity)

---

## Phase 4: Client-Side Implementation

### 4.1 Entity Renderers

**Purpose**: Register GeckoLib renderers for all 7 robot types
**Location**: `LovelyEntities.java` - `registerRenderers()` method

**Implementation**:
```java
private static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
    event.registerEntityRenderer(VANILLA_ENTITY.get(), RobotRenderer::new);
    event.registerEntityRenderer(BUNNY_ENTITY.get(), BunnyRenderer::new);
    event.registerEntityRenderer(KITSUNE_ENTITY.get(), KitsuneRenderer::new);
    // ... register all 7 types
}
```

**Renderers to Use** (from Common):
- `RobotRenderer` - Default renderer (Vanilla, Bunny2, Honey, Dragon, Neko)
- `BunnyRenderer` - Specialized for Bunny
- `KitsuneRenderer` - Specialized for Kitsune

**Status**: ⏳ To Do

### 4.2 Item Model Properties

**Purpose**: Register color variant model predicates for spawn items
**Location**: `LovelyLegacyClient.java` or `LovelyItems.java`

**Implementation**:
```java
private static void registerItemProperties(RegisterItemPropertiesEvent event) {
    for (DeferredItem<LovelySpawnItem> item : SPAWN_ITEMS) {
        event.register(item.get(), 
            ResourceLocation.fromNamespaceAndPath(LovelyConstant.MODID, "color"),
            (stack, level, entity, seed) -> {
                // Return color index from NBT
            });
    }
}
```

**Status**: ⏳ To Do

### 4.3 GeckoLib Initialization

**Purpose**: Initialize GeckoLib animation system
**Location**: `LovelyLegacyClient.java`

**Implementation**:
```java
public static void init() {
    // GeckoLib client initialization if needed
    // NeoForge may handle this automatically
}
```

**Status**: ⏳ To Do (verify if needed)

---

## Phase 5: Resource Files

### 5.1 Mod Metadata

**File**: `NeoForge/src/main/resources/META-INF/neoforge.mods.toml`

**Content**:
```toml
modLoader="javafml"
loaderVersion="${neoforge_loader_version_range}"
license="${mod_license}"

[[mods]]
modId="${mod_id}"
version="${mod_version}"
displayName="${mod_name}"
description="${mod_description}"
authors="${mod_authors}"
displayURL="${mod_homepage}"
issueTrackerURL="${mod_issues}"

[[dependencies.llovelyr]]
    modId="neoforge"
    type="required"
    versionRange="${neoforge_version_range}"
    ordering="NONE"
    side="BOTH"

[[dependencies.llovelyr]]
    modId="minecraft"
    type="required"
    versionRange="${minecraft_version_range}"
    ordering="NONE"
    side="BOTH"

[[dependencies.llovelyr]]
    modId="geckolib"
    type="required"
    versionRange="[4.0,)"
    ordering="AFTER"
    side="BOTH"
```

**Status**: ⏳ To Do

### 5.2 Pack Metadata

**File**: `NeoForge/src/main/resources/pack.mcmeta`

**Content**:
```json
{
  "pack": {
    "description": "${mod_name} Resources",
    "pack_format": 34,
    "supported_formats": {
      "min_inclusive": 34,
      "max_inclusive": 34
    }
  }
}
```

**Status**: ⏳ To Do

### 5.3 Mixin Configuration (if needed)

**File**: `NeoForge/src/main/resources/${mod_id}.mixins.json`

**Content**:
```json
{
  "required": true,
  "package": "net.msymbios.llovelyr.mixin",
  "compatibilityLevel": "JAVA_21",
  "refmap": "${mod_id}.refmap.json",
  "mixins": [],
  "client": [],
  "injectors": {
    "defaultRequire": 1
  }
}
```

**Status**: ⏳ To Do (if mixins needed)

### 5.4 Language Files

**File**: `NeoForge/src/main/resources/assets/llovelyr/lang/en_us.json`

**Strategy**: Copy from Common or Forge, no changes needed

**Status**: ⏳ To Do

### 5.5 Recipe Data Files

**Location**: `NeoForge/src/main/resources/data/llovelyr/recipes/`

**Strategy**: Copy from Forge, no changes needed
- All spawn recipes
- All dye recipes
- Tag files

**Status**: ⏳ To Do

---

## Phase 6: Testing & Validation

### 6.1 Build Testing

**Tasks**:
- [ ] Run `./gradlew :NeoForge:build`
- [ ] Verify no compilation errors
- [ ] Check JAR file generation
- [ ] Validate mod metadata

**Expected Output**: `llovelyr-neoforge-1.21.1-{version}.jar`

### 6.2 Runtime Testing

**Test Cases**:
1. **Mod Loading**
   - [ ] Mod appears in mod list
   - [ ] No crash on startup
   - [ ] Creative tab appears

2. **Item Registration**
   - [ ] All 7 robot spawn items exist
   - [ ] All 16 color variants work
   - [ ] Robot cores exist
   - [ ] Tooltips display correctly

3. **Entity Spawning**
   - [ ] All 7 robot types spawn
   - [ ] Entities render correctly
   - [ ] Animations play
   - [ ] Color variants display

4. **Gameplay Features**
   - [ ] Taming works
   - [ ] Following works
   - [ ] Combat works
   - [ ] Leveling works
   - [ ] Protection system works
   - [ ] Commands work
   - [ ] Interactions work (stick, book, dye, etc.)

5. **Recipes**
   - [ ] Core → spawn egg crafting
   - [ ] Spawn egg + dye crafting
   - [ ] NBT preservation
   - [ ] Preview in crafting grid

6. **Data Persistence**
   - [ ] Save and reload world
   - [ ] Robot data persists
   - [ ] NBT migration works
   - [ ] No data loss

### 6.3 Compatibility Testing

**Test Scenarios**:
- [ ] Single-player world
- [ ] Multiplayer server
- [ ] With other mods (GeckoLib, etc.)
- [ ] World migration from Forge version

### 6.4 Performance Testing

**Metrics**:
- [ ] Startup time
- [ ] Memory usage
- [ ] FPS with multiple robots
- [ ] Server TPS impact

---

## Phase 7: Documentation & Finalization

### 7.1 Update CURRENT_STATE.md

**Changes**:
- Update version support matrix
- Add NeoForge status
- Document NeoForge-specific features
- Update implementation status

### 7.2 Update CHANGELOG.md

**Entry**:
```markdown
## [Version] - YYYY-MM-DD

### Added
- NeoForge loader support for MC 1.21.1
- All 7 robot types available on NeoForge
- Full feature parity with Forge and Fabric versions

### Technical
- NeoForge deferred register system
- NeoForge event bus integration
- NeoForge configuration system
```

### 7.3 Create Migration Guide (if needed)

**File**: `docs/documentation/migrations/MIGRATE_Forge_to_NeoForge.md`

**Content**:
- How to migrate worlds
- Config file changes
- Known differences
- Troubleshooting

### 7.4 Update README.md

**Changes**:
- Add NeoForge to supported loaders
- Update download links
- Update installation instructions

---

## Implementation Checklist

### Phase 1: Analysis ✅ COMPLETE
- [x] Review NeoForge documentation
- [x] Analyze Forge implementation
- [x] Analyze Fabric implementation
- [x] Identify key differences
- [x] Create action plan

### Phase 2: Core Infrastructure ✅ COMPLETE
- [x] Use `LovelyConstant.java` from Common (no changes needed)
- [x] Create `LovelyLegacy.java` (NeoForge main mod class)
- [x] Update `LovelyConfigs.java` (adapted from Forge)
- [x] Client setup integrated in LovelyEvents

### Phase 3: Registration System ✅ COMPLETE
- [x] Create `LovelyItems.java`
- [x] Create `LovelyGroups.java`
- [x] Create `LovelyEntities.java`
- [x] Create `LovelyRecipes.java`
- [x] Create `LovelyCommandArguments.java`
- [x] Create `LovelyEvents.java`

### Phase 4: Client-Side ✅ COMPLETE
- [x] Register entity renderers (in LovelyEvents)
- [x] Register item model properties (in LovelyEvents)
- [x] GeckoLib works automatically (no special init needed)

### Phase 5: Resources ✅ COMPLETE
- [x] Update `neoforge.mods.toml` (added GeckoLib dependency)
- [x] Copy `pack.mcmeta` from Forge
- [x] Copy language files from Forge
- [x] Copy recipe files from Forge
- [x] No mixin config needed

### Phase 6: Testing ⏳ PENDING
- [ ] Build testing
- [ ] Runtime testing
- [ ] Compatibility testing
- [ ] Performance testing

### Phase 7: Documentation ⏳ PENDING
- [ ] Update `CURRENT_STATE.md`
- [ ] Update `CHANGELOG.md`
- [ ] Create migration guide (if needed)
- [ ] Update `README.md`

---

## Risk Assessment

### High Risk
1. **NeoForge API Changes**: NeoForge is newer and APIs may differ significantly
   - **Mitigation**: Consult NeoForge docs, check example mods, test incrementally

2. **GeckoLib Compatibility**: GeckoLib version for NeoForge may have differences
   - **Mitigation**: Verify GeckoLib NeoForge version, test animations early

3. **Event System Differences**: Event handling may differ from Forge
   - **Mitigation**: Review NeoForge event documentation, test event firing

### Medium Risk
1. **Configuration System**: NeoForge config API may have subtle differences
   - **Mitigation**: Port carefully, test config reload

2. **Recipe System**: Recipe serialization may need adjustments
   - **Mitigation**: Test crafting early, verify NBT handling

3. **Command System**: Command registration may differ
   - **Mitigation**: Test commands early, verify argument types

### Low Risk
1. **Common Code Integration**: Common module is stable and tested
   - **Mitigation**: No changes needed to Common

2. **Resource Files**: Most resources can be copied directly
   - **Mitigation**: Verify paths and formats

---

## Timeline Estimate

**Total Estimated Time**: 12-16 hours

### Breakdown:
- **Phase 1 (Analysis)**: 2 hours ✅ COMPLETE
- **Phase 2 (Core Infrastructure)**: 2-3 hours
- **Phase 3 (Registration)**: 3-4 hours
- **Phase 4 (Client-Side)**: 1-2 hours
- **Phase 5 (Resources)**: 1 hour
- **Phase 6 (Testing)**: 2-3 hours
- **Phase 7 (Documentation)**: 1 hour

**Recommended Approach**: Implement in order, test incrementally after each phase.

---

## Success Criteria

### Minimum Viable Product (MVP)
- [ ] Mod loads without errors
- [ ] All 7 robot types spawn
- [ ] Basic functionality works (taming, following, combat)
- [ ] Recipes work
- [ ] Data persists

### Full Feature Parity
- [ ] All Forge features work on NeoForge
- [ ] All Fabric features work on NeoForge
- [ ] Performance is comparable
- [ ] No known bugs
- [ ] Documentation complete

### Quality Standards
- [ ] Code follows project style guide
- [ ] All classes have JavaDoc
- [ ] No compiler warnings
- [ ] Passes all tests
- [ ] Ready for release

---

## Next Steps

1. **Immediate**: Begin Phase 2 - Create core infrastructure files
2. **Short-term**: Complete registration system (Phase 3)
3. **Medium-term**: Implement client-side and resources (Phases 4-5)
4. **Long-term**: Testing and documentation (Phases 6-7)

**Recommended Start**: Create `LovelyConstant.java` and update `LovelyLegacy.java` to establish the foundation.

---

**Last Updated**: 2025-12-07
**Status**: ✅ IMPLEMENTATION COMPLETE - Ready for Testing

**See**: [NeoForge_Implementation_Complete.md](NeoForge_Implementation_Complete.md) for completion summary
