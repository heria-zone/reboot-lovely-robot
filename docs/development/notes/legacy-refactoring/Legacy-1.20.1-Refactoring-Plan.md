---
created: 2025-11-29
tags:
  - LovelyRobot
  - Architecture
  - Refactoring
---

# Legacy 1.20.1 Refactoring Plan
## Framework, Lib, Common, Source Architecture

## Executive Summary

This document analyzes the current Legacy 1.20.1 codebase and provides a detailed refactoring plan to introduce the four-layer architecture:
- **framework/** - Pure Java, no mod loader dependencies
- **lib/** - Loader-specific implementations (Forge/Fabric)
- **common/** - LovelyRobot mod-specific shared code
- **source/** - Variant-specific implementations (Vanilla, Bunny2, etc.)

**Current State:** 83-91% code duplication between Forge and Fabric
**Target State:** <20% duplication with shared abstractions

## Current Architecture Analysis

### Existing Package Structure

```
llovelyr-1.20.1/
├── Forge/src/main/java/net/msymbios/llovelyr/
│   ├── LovelyLegacy.java                    [Mod Entry Point]
│   ├── common/                              [Shared Logic - 85% duplicated]
│   │   ├── entity/
│   │   │   ├── goal/                        [AI Goals]
│   │   │   └── internal/                    [Base Classes]
│   │   └── util/                            [Utilities]
│   └── source/                              [Mod-Specific]
│       ├── blocks/
│       ├── commands/                        [Forge-only]
│       ├── configs/
│       ├── entity/
│       ├── events/
│       ├── groups/
│       ├── items/
│       └── recipes/
│
└── Fabric/src/main/java/net/msymbios/llovelyr/
    ├── LovelyLegacy.java                    [Mod Entry Point]
    ├── common/                              [Shared Logic - 85% duplicated]
    │   ├── entity/
    │   │   ├── goal/                        [AI Goals]
    │   │   └── internal/                    [Base Classes]
    │   └── util/                            [Utilities]
    └── source/                              [Mod-Specific]
        ├── client/                          [Fabric-only]
        ├── commands/                        [Fabric-only]
        ├── configs/
        ├── data/                            [Fabric-only]
        ├── entity/
        ├── events/
        ├── groups/
        ├── items/
        ├── mixin/                           [Fabric-only]
        └── recipes/
```


### Code Duplication Analysis

**Identical Files (100% duplication):**
- `common/entity/goal/` - All AI goal classes
- `common/entity/internal/` - InternalEntity, InternalAnimation, InternalLogic, InternalParticle
- `common/util/` - ObjectUtil, Utility, Version
- `source/entity/internal/enums/` - All enum classes
- `source/entity/internal/NativeEntityType.java`
- `source/entity/custom/` - VanillaEntity, Bunny2Entity
- `source/entity/client/model/` - Model classes
- `source/entity/client/layer/` - Layer classes
- `source/configs/LovelyIdentifier.java`
- `source/configs/LovelyResource.java`
- `source/recipes/interfaces/` - All interface classes
- `source/recipes/internal/` - All strategy and modifier classes

**Near-Identical Files (95%+ similarity):**
- `source/entity/LovelyRobot.java` - Only differs in imports and minor API calls
- `source/entity/LovelyEntities.java` - Registration differs
- `source/items/LovelyItems.java` - Registration differs
- `source/recipes/LovelyRecipes.java` - Registration differs
- `source/configs/LovelyConfigs.java` - Config system differs

**Loader-Specific Files:**
- Forge: `source/commands/LovelyRobotCommand.java` (1400+ lines)
- Fabric: `source/commands/LovelyCommand.java` (similar functionality)
- Fabric: `source/mixin/IShapedRecipeAccessor.java`
- Fabric: `source/data/LovelyGenerator.java`
- Fabric: `source/client/LovelyClient.java`

## Target Architecture

### Four-Layer Structure

```
llovelyr-1.20.1/
├── Framework/                               [Pure Java - No MC/Loader deps]
│   └── src/main/java/net/msymbios/hzframework/
│       ├── entity/
│       │   ├── EntityData.java
│       │   ├── EntityAttributes.java
│       │   ├── EntityBehavior.java
│       │   └── EntityState.java
│       ├── animation/
│       │   ├── AnimationController.java
│       │   ├── PoseManager.java
│       │   └── AnimationState.java
│       ├── util/
│       │   ├── Version.java
│       │   ├── ObjectUtil.java
│       │   └── NBTSerializer.java
│       └── interfaces/
│           ├── IEntity.java
│           ├── IAnimatable.java
│           └── IStateful.java
│
├── Lib/                                     [Loader-Specific Bridges]
│   ├── Forge/
│   │   └── src/main/java/net/msymbios/hzlib/forge/
│   │       ├── entity/
│   │       │   ├── ForgeEntityBridge.java
│   │       │   └── ForgeRegistration.java
│   │       ├── rendering/
│   │       │   └── ForgeRenderAdapter.java
│   │       ├── config/
│   │       │   └── ForgeConfigBridge.java
│   │       └── commands/
│   │           └── ForgeCommandBridge.java
│   │
│   └── Fabric/
│       └── src/main/java/net/msymbios/hzlib/fabric/
│           ├── entity/
│           │   ├── FabricEntityBridge.java
│           │   └── FabricRegistration.java
│           ├── rendering/
│           │   └── FabricRenderAdapter.java
│           ├── config/
│           │   └── FabricConfigBridge.java
│           └── commands/
│               └── FabricCommandBridge.java
│
├── Common/                                  [LovelyRobot Mod Shared Code]
│   └── src/main/java/net/msymbios/llovelyr/common/
│       ├── entity/
│       │   ├── LovelyRobotEntity.java       [Base robot entity]
│       │   ├── goal/                        [AI goals]
│       │   │   ├── AiAutoAttackGoal.java
│       │   │   ├── AiBaseDefenseGoal.java
│       │   │   ├── AiConditionalWanderGoal.java
│       │   │   └── AiFollowOwnerGoal.java
│       │   └── behavior/
│       │       ├── RobotBehavior.java
│       │       ├── CombatBehavior.java
│       │       └── DefenseBehavior.java
│       ├── animation/
│       │   ├── RobotAnimation.java
│       │   └── PoseController.java
│       ├── data/
│       │   ├── RobotStats.java
│       │   ├── RobotProtections.java
│       │   └── RobotLevel.java
│       ├── recipes/
│       │   ├── interfaces/
│       │   ├── strategies/
│       │   └── modifiers/
│       └── util/
│           ├── RobotLogic.java
│           ├── RobotParticles.java
│           └── RobotUtility.java
│
└── Source/                                  [Variant-Specific Implementations]
    ├── Forge/
    │   └── src/main/java/net/msymbios/llovelyr/
    │       ├── LovelyLegacy.java            [Mod entry - Forge specific]
    │       ├── entity/
    │       │   ├── VanillaEntity.java       [Minimal - extends Common]
    │       │   ├── Bunny2Entity.java
    │       │   ├── client/
    │       │   │   ├── model/
    │       │   │   ├── renderer/
    │       │   │   └── layer/
    │       │   └── registration/
    │       │       └── ForgeEntityRegistry.java
    │       ├── items/
    │       │   └── ForgeItemRegistry.java
    │       ├── commands/
    │       │   └── ForgeCommands.java
    │       └── config/
    │           └── ForgeConfig.java
    │
    └── Fabric/
        └── src/main/java/net/msymbios/llovelyr/
            ├── LovelyLegacy.java            [Mod entry - Fabric specific]
            ├── entity/
            │   ├── VanillaEntity.java       [Minimal - extends Common]
            │   ├── Bunny2Entity.java
            │   ├── client/
            │   │   ├── model/
            │   │   ├── renderer/
            │   │   └── layer/
            │   └── registration/
            │       └── FabricEntityRegistry.java
            ├── items/
            │   └── FabricItemRegistry.java
            ├── commands/
            │   └── FabricCommands.java
            ├── config/
            │   └── FabricConfig.java
            └── mixin/
                └── FabricMixins.java
```


## Detailed Refactoring Roadmap

### Phase 1: Framework Layer (Pure Java)

**Goal:** Extract all loader-independent logic into pure Java

#### 1.1 Entity Framework

**Move to `framework/entity/`:**

| Current Location | New Location | Refactoring Required |
|-----------------|--------------|---------------------|
| `common/util/internal/Version.java` | `framework/util/Version.java` | None - already pure Java |
| `common/util/ObjectUtil.java` | `framework/util/ObjectUtil.java` | None - already pure Java |
| `source/entity/internal/enums/EntityState.java` | `framework/entity/EntityState.java` | None - pure enum |
| `source/entity/internal/enums/EntityAnimation.java` | `framework/animation/AnimationState.java` | Rename, extract MC-specific parts |
| `source/entity/internal/enums/EntityTexture.java` | `framework/entity/EntityTexture.java` | Extract identifier logic |
| `source/entity/internal/enums/EntityVariant.java` | `framework/entity/EntityVariant.java` | Extract identifier logic |
| `source/entity/internal/enums/EntityModel.java` | `framework/entity/EntityModel.java` | Extract identifier logic |
| `source/entity/internal/enums/EntityHand.java` | `framework/entity/EntityHand.java` | None - pure enum |

**New Framework Classes to Create:**

```java
// framework/entity/EntityData.java
public class EntityData {
    private final String id;
    private final EntityVariant variant;
    private final EntityModel model;
    private final EntityTexture texture;
    // Pure data container
}

// framework/entity/EntityAttributes.java
public class EntityAttributes {
    private int level;
    private int exp;
    private int maxLevel;
    private float health;
    private float maxHealth;
    private int attackDamage;
    private int defense;
    // Pure attribute container
}

// framework/entity/EntityBehavior.java
public interface EntityBehavior {
    EntityState getCurrentState();
    void setState(EntityState state);
    boolean canTransitionTo(EntityState target);
}

// framework/animation/PoseManager.java
public class PoseManager {
    private EntityState currentState;
    private int ticksInState;
    // Pure state machine logic
}
```

**Complexity:** Low - These are mostly data classes and enums
**Dependencies:** None
**Estimated Effort:** 2-3 hours

#### 1.2 Utility Framework

**Move to `framework/util/`:**

| Current Location | New Location | Refactoring Required |
|-----------------|--------------|---------------------|
| `common/util/ObjectUtil.java` | `framework/util/ObjectUtil.java` | None |
| `common/util/internal/Version.java` | `framework/util/Version.java` | None |
| `common/util/internal/Utility.java` | Split into multiple classes | Extract MC-specific parts |

**New Framework Utilities:**

```java
// framework/util/MathUtil.java
public class MathUtil {
    public static int calculateLevel(int exp);
    public static int calculateNextExp(int level);
    public static int calculateHp(int level, int baseHp);
    public static int calculateAttack(int level, int baseAttack);
    // Pure math calculations
}

// framework/util/StringUtil.java
public class StringUtil {
    public static String formatName(String name);
    public static boolean isValidName(String name);
    // Pure string operations
}
```

**Complexity:** Low
**Dependencies:** None
**Estimated Effort:** 1-2 hours

#### 1.3 NBT Serialization Framework

**Create new abstraction:**

```java
// framework/data/IDataSerializer.java
public interface IDataSerializer<T> {
    Map<String, Object> serialize(T object);
    T deserialize(Map<String, Object> data);
}

// framework/data/EntityDataSerializer.java
public class EntityDataSerializer implements IDataSerializer<EntityData> {
    // Pure serialization logic without NBT dependency
}
```

**Complexity:** Medium - Requires abstracting NBT
**Dependencies:** None
**Estimated Effort:** 3-4 hours

**Phase 1 Total Effort:** 6-9 hours


### Phase 2: Lib Layer (Loader Bridges)

**Goal:** Create loader-specific implementations that bridge Framework to Minecraft

#### 2.1 Entity Bridge

**Create `lib/forge/entity/ForgeEntityBridge.java`:**

```java
public class ForgeEntityBridge {
    // Converts Framework EntityData to Forge EntityDataAccessor
    public static EntityDataAccessor<Integer> createIntAccessor(String key);
    public static EntityDataAccessor<Float> createFloatAccessor(String key);
    public static EntityDataAccessor<Boolean> createBoolAccessor(String key);
    
    // NBT serialization bridge
    public static CompoundTag serializeToNBT(EntityData data);
    public static EntityData deserializeFromNBT(CompoundTag nbt);
}
```

**Create `lib/fabric/entity/FabricEntityBridge.java`:**

```java
public class FabricEntityBridge {
    // Converts Framework EntityData to Fabric TrackedData
    public static TrackedData<Integer> createIntTracker(String key);
    public static TrackedData<Float> createFloatTracker(String key);
    public static TrackedData<Boolean> createBoolTracker(String key);
    
    // NBT serialization bridge
    public static NbtCompound serializeToNBT(EntityData data);
    public static EntityData deserializeFromNBT(NbtCompound nbt);
}
```

**Complexity:** Medium - Different APIs but similar concepts
**Dependencies:** Framework layer, Minecraft/Loader APIs
**Estimated Effort:** 4-6 hours per loader

#### 2.2 Registration Bridge

**Create `lib/forge/registration/ForgeRegistration.java`:**

```java
public class ForgeRegistration {
    public static <T extends Entity> RegistryObject<EntityType<T>> registerEntity(
        String id,
        EntityType.Builder<T> builder
    );
    
    public static RegistryObject<Item> registerItem(String id, Item.Properties props);
    public static RegistryObject<RecipeSerializer<?>> registerRecipe(String id, RecipeSerializer<?> serializer);
}
```

**Create `lib/fabric/registration/FabricRegistration.java`:**

```java
public class FabricRegistration {
    public static <T extends Entity> EntityType<T> registerEntity(
        String id,
        EntityType.Builder<T> builder
    );
    
    public static Item registerItem(String id, Item.Settings settings);
    public static RecipeSerializer<?> registerRecipe(String id, RecipeSerializer<?> serializer);
}
```

**Complexity:** Low - Straightforward API wrapping
**Dependencies:** Loader registration APIs
**Estimated Effort:** 2-3 hours per loader

#### 2.3 Rendering Bridge

**Create `lib/forge/rendering/ForgeRenderAdapter.java`:**

```java
public class ForgeRenderAdapter {
    public static void registerRenderer(
        EntityType<?> type,
        EntityRendererProvider<?> provider
    );
    
    public static ResourceLocation getTexture(EntityTexture texture, EntityVariant variant);
    public static ResourceLocation getModel(EntityModel model);
}
```

**Create `lib/fabric/rendering/FabricRenderAdapter.java`:**

```java
public class FabricRenderAdapter {
    public static void registerRenderer(
        EntityType<?> type,
        EntityRendererFactory<?> factory
    );
    
    public static Identifier getTexture(EntityTexture texture, EntityVariant variant);
    public static Identifier getModel(EntityModel model);
}
```

**Complexity:** Low - Resource location wrapping
**Dependencies:** Client-side APIs
**Estimated Effort:** 2-3 hours per loader

#### 2.4 Config Bridge

**Create `lib/forge/config/ForgeConfigBridge.java`:**

```java
public class ForgeConfigBridge {
    private final ForgeConfigSpec spec;
    
    public <T> ConfigValue<T> define(String path, T defaultValue);
    public int getInt(String path);
    public boolean getBoolean(String path);
    public double getDouble(String path);
}
```

**Create `lib/fabric/config/FabricConfigBridge.java`:**

```java
public class FabricConfigBridge {
    private final SimpleConfig config;
    
    public <T> T get(String path, T defaultValue);
    public int getInt(String path);
    public boolean getBoolean(String path);
    public double getDouble(String path);
}
```

**Complexity:** Medium - Different config systems
**Dependencies:** Forge ConfigSpec / Fabric SimpleConfig
**Estimated Effort:** 3-4 hours per loader

#### 2.5 Command Bridge

**Create `lib/forge/commands/ForgeCommandBridge.java`:**

```java
public class ForgeCommandBridge {
    public static void registerCommand(
        CommandDispatcher<CommandSourceStack> dispatcher,
        String name,
        Command<CommandSourceStack> command
    );
    
    public static Collection<? extends Entity> getEntities(
        CommandContext<CommandSourceStack> context,
        String argumentName
    );
}
```

**Create `lib/fabric/commands/FabricCommandBridge.java`:**

```java
public class FabricCommandBridge {
    public static void registerCommand(
        CommandDispatcher<ServerCommandSource> dispatcher,
        String name,
        Command<ServerCommandSource> command
    );
    
    public static Collection<? extends Entity> getEntities(
        CommandContext<ServerCommandSource> context,
        String argumentName
    );
}
```

**Complexity:** Medium - Command API differences
**Dependencies:** Brigadier + loader command APIs
**Estimated Effort:** 4-5 hours per loader

**Phase 2 Total Effort:** 30-42 hours (15-21 hours per loader)


### Phase 3: Common Layer (Mod-Specific Shared Code)

**Goal:** Move all LovelyRobot-specific logic that's shared between loaders

#### 3.1 Base Robot Entity

**Move to `common/entity/LovelyRobotEntity.java`:**

Current: `source/entity/LovelyRobot.java` (1258 lines, duplicated in both loaders)

**Refactoring Strategy:**
1. Extract all business logic (level system, protections, combat, etc.)
2. Use Framework classes for state management
3. Use Lib bridges for loader-specific operations
4. Keep only abstract methods for loader-specific implementations

**New Structure:**

```java
// common/entity/LovelyRobotEntity.java
public abstract class LovelyRobotEntity implements EntityBehavior {
    // Framework dependencies
    protected final EntityData entityData;
    protected final EntityAttributes attributes;
    protected final PoseManager poseManager;
    
    // Business logic (pure, no loader deps)
    public void addExp(int value) { /* ... */ }
    public void levelUp() { /* ... */ }
    public void applyProtection(DamageType type, float amount) { /* ... */ }
    
    // Abstract methods for loader implementation
    protected abstract void syncDataToClient();
    protected abstract void playSound(SoundType sound);
    protected abstract void spawnParticles(ParticleType type);
}
```

**Files to Move:**

| Current Location | New Location | Lines | Refactoring |
|-----------------|--------------|-------|-------------|
| `source/entity/LovelyRobot.java` | `common/entity/LovelyRobotEntity.java` | ~1000 | Extract loader-specific calls |
| `common/entity/internal/InternalEntity.java` | `common/entity/BaseEntity.java` | ~800 | Merge with LovelyRobotEntity |
| `common/entity/internal/InternalLogic.java` | `common/logic/RobotLogic.java` | ~400 | Pure logic extraction |
| `common/entity/internal/InternalAnimation.java` | `common/animation/RobotAnimation.java` | ~200 | Use Framework PoseManager |
| `common/entity/internal/InternalParticle.java` | `common/effects/RobotParticles.java` | ~100 | Abstract particle spawning |

**Complexity:** High - Core refactoring
**Dependencies:** Framework, Lib bridges
**Estimated Effort:** 12-16 hours

#### 3.2 AI Goals

**Move to `common/entity/goal/`:**

All AI goal classes are already 100% identical between loaders. Direct move with minimal changes.

| Current Location | New Location | Refactoring |
|-----------------|--------------|-------------|
| `common/entity/goal/AiAutoAttackGoal.java` | `common/entity/goal/AiAutoAttackGoal.java` | None |
| `common/entity/goal/AiBaseDefenseGoal.java` | `common/entity/goal/AiBaseDefenseGoal.java` | None |
| `common/entity/goal/AiConditionalWanderGoal.java` | `common/entity/goal/AiConditionalWanderGoal.java` | None |
| `common/entity/goal/AiFollowOwnerGoal.java` | `common/entity/goal/AiFollowOwnerGoal.java` | None |

**Complexity:** Low - Direct move
**Dependencies:** Minecraft AI API (unavoidable)
**Estimated Effort:** 1-2 hours

#### 3.3 Recipe System

**Move to `common/recipes/`:**

All recipe interfaces, strategies, and modifiers are 100% identical.

| Current Location | New Location | Refactoring |
|-----------------|--------------|-------------|
| `source/recipes/interfaces/` | `common/recipes/interfaces/` | None |
| `source/recipes/internal/strategies/` | `common/recipes/strategies/` | None |
| `source/recipes/internal/modifiers/` | `common/recipes/modifiers/` | None |
| `source/recipes/custom/LovelySpawnRecipe.java` | `common/recipes/SpawnRecipe.java` | Abstract loader-specific parts |
| `source/recipes/custom/LovelySpawnDyeRecipe.java` | `common/recipes/SpawnDyeRecipe.java` | Abstract loader-specific parts |

**Complexity:** Low - Mostly direct moves
**Dependencies:** Minecraft recipe API
**Estimated Effort:** 2-3 hours

#### 3.4 Shared Utilities

**Move to `common/util/`:**

| Current Location | New Location | Refactoring |
|-----------------|--------------|-------------|
| `common/util/internal/Utility.java` | `common/util/RobotUtility.java` | Extract MC-specific to Lib |
| `source/items/util/TooltipUtils.java` | `common/util/TooltipUtil.java` | Abstract text components |

**Complexity:** Medium - Some MC API dependencies
**Dependencies:** Framework utilities, Lib bridges
**Estimated Effort:** 3-4 hours

#### 3.5 Configuration Definitions

**Move to `common/config/`:**

| Current Location | New Location | Refactoring |
|-----------------|--------------|-------------|
| `source/configs/LovelyIdentifier.java` | `common/config/RobotIdentifiers.java` | None - pure constants |
| `source/configs/LovelyResource.java` | `common/config/RobotResources.java` | Abstract ResourceLocation |
| `source/configs/LovelyConfigs.java` | `common/config/RobotConfig.java` | Define interface, impl in Lib |

**New Structure:**

```java
// common/config/RobotConfig.java
public interface RobotConfig {
    // Define config contract
    int getMaxLevel();
    boolean isFriendlyFireEnabled();
    double getFollowDistance();
    // ... all config values
}

// lib/forge/config/ForgeRobotConfig.java
public class ForgeRobotConfig implements RobotConfig {
    private final ForgeConfigBridge bridge;
    // Implement using Forge config
}

// lib/fabric/config/FabricRobotConfig.java
public class FabricRobotConfig implements RobotConfig {
    private final FabricConfigBridge bridge;
    // Implement using Fabric config
}
```

**Complexity:** Medium - Config abstraction
**Dependencies:** Lib config bridges
**Estimated Effort:** 4-5 hours

**Phase 3 Total Effort:** 22-30 hours


### Phase 4: Source Layer (Loader-Specific Implementations)

**Goal:** Minimal loader-specific code that uses Common + Lib

#### 4.1 Entity Implementations

**Forge: `source/forge/entity/VanillaEntity.java`**

Before (200+ lines):
```java
public class VanillaEntity extends LovelyRobot {
    // All entity logic duplicated
}
```

After (~30 lines):
```java
public class VanillaEntity extends LovelyRobotEntity {
    public VanillaEntity(EntityType<? extends VanillaEntity> type, Level level) {
        super(type, level, NativeEntityType.VANILLA);
    }
    
    @Override
    protected void syncDataToClient() {
        ForgeEntityBridge.syncData(this);
    }
    
    @Override
    protected void playSound(SoundType sound) {
        ForgeEntityBridge.playSound(this, sound);
    }
    
    @Override
    protected void spawnParticles(ParticleType type) {
        ForgeEntityBridge.spawnParticles(this, type);
    }
    
    @Override
    public ItemStack setDropItem() {
        return new ItemStack(ForgeItems.ROBOT_CORE.get(), 1);
    }
}
```

**Fabric: `source/fabric/entity/VanillaEntity.java`**

After (~30 lines):
```java
public class VanillaEntity extends LovelyRobotEntity {
    public VanillaEntity(EntityType<? extends VanillaEntity> type, World world) {
        super(type, world, NativeEntityType.VANILLA);
    }
    
    @Override
    protected void syncDataToClient() {
        FabricEntityBridge.syncData(this);
    }
    
    @Override
    protected void playSound(SoundType sound) {
        FabricEntityBridge.playSound(this, sound);
    }
    
    @Override
    protected void spawnParticles(ParticleType type) {
        FabricEntityBridge.spawnParticles(this, type);
    }
    
    @Override
    public ItemStack setDropItem() {
        return new ItemStack(FabricItems.ROBOT_CORE, 1);
    }
}
```

**Complexity:** Low - Thin wrappers
**Estimated Effort:** 1 hour per entity per loader

#### 4.2 Registration

**Forge: `source/forge/registration/ForgeEntityRegistry.java`**

```java
public class ForgeEntityRegistry {
    public static final DeferredRegister<EntityType<?>> ENTITIES = 
        DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, LovelyLegacy.MODID);
    
    public static final RegistryObject<EntityType<VanillaEntity>> VANILLA = 
        ForgeRegistration.registerEntity("vanilla", 
            EntityType.Builder.of(VanillaEntity::new, MobCategory.CREATURE)
                .sized(0.6f, 1.8f)
        );
    
    public static final RegistryObject<EntityType<Bunny2Entity>> BUNNY2 = 
        ForgeRegistration.registerEntity("bunny2", 
            EntityType.Builder.of(Bunny2Entity::new, MobCategory.CREATURE)
                .sized(0.6f, 1.8f)
        );
}
```

**Fabric: `source/fabric/registration/FabricEntityRegistry.java`**

```java
public class FabricEntityRegistry {
    public static final EntityType<VanillaEntity> VANILLA = 
        FabricRegistration.registerEntity("vanilla",
            FabricEntityTypeBuilder.create(MobCategory.CREATURE, VanillaEntity::new)
                .dimensions(EntityDimensions.fixed(0.6f, 1.8f))
                .build()
        );
    
    public static final EntityType<Bunny2Entity> BUNNY2 = 
        FabricRegistration.registerEntity("bunny2",
            FabricEntityTypeBuilder.create(MobCategory.CREATURE, Bunny2Entity::new)
                .dimensions(EntityDimensions.fixed(0.6f, 1.8f))
                .build()
        );
}
```

**Complexity:** Low - Registration boilerplate
**Estimated Effort:** 2-3 hours per loader

#### 4.3 Client Rendering

**Keep in Source (loader-specific):**

- `source/forge/entity/client/model/` - Forge model classes
- `source/forge/entity/client/renderer/` - Forge renderers
- `source/forge/entity/client/layer/` - Forge layers
- `source/fabric/entity/client/model/` - Fabric model classes
- `source/fabric/entity/client/renderer/` - Fabric renderers
- `source/fabric/entity/client/layer/` - Fabric layers

**Rationale:** GeckoLib has loader-specific APIs, not worth abstracting yet

**Complexity:** None - Keep as-is
**Estimated Effort:** 0 hours

#### 4.4 Commands

**Forge: `source/forge/commands/ForgeCommands.java`**

Extract command logic to Common, keep only registration in Source:

```java
public class ForgeCommands {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        ForgeCommandBridge.registerCommand(dispatcher, "robot", 
            RobotCommands.buildCommandTree()
        );
    }
}
```

**Fabric: `source/fabric/commands/FabricCommands.java`**

```java
public class FabricCommands {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        FabricCommandBridge.registerCommand(dispatcher, "robot",
            RobotCommands.buildCommandTree()
        );
    }
}
```

**Common: `common/commands/RobotCommands.java`**

```java
public class RobotCommands {
    public static <T> LiteralArgumentBuilder<T> buildCommandTree() {
        // All command logic here, loader-agnostic
        return Commands.literal("robot")
            .then(Commands.literal("stats")
                .then(Commands.argument("target", EntityArgument.entities())
                    .executes(RobotCommands::executeStats)
                )
            )
            // ... rest of command tree
    }
    
    private static <T> int executeStats(CommandContext<T> context) {
        // Command implementation using Common classes
    }
}
```

**Complexity:** High - Large command system
**Estimated Effort:** 8-10 hours

#### 4.5 Mod Entry Points

**Forge: `source/forge/LovelyLegacy.java`**

```java
@Mod(LovelyLegacy.MODID)
public class LovelyLegacy {
    public static final String MODID = "llovelyr";
    
    public LovelyLegacy() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        
        // Register using Lib bridges
        ForgeEntityRegistry.ENTITIES.register(modEventBus);
        ForgeItemRegistry.ITEMS.register(modEventBus);
        ForgeRecipeRegistry.RECIPES.register(modEventBus);
        
        // Initialize config
        ForgeRobotConfig.init();
        
        // Register events
        MinecraftForge.EVENT_BUS.register(ForgeEvents.class);
    }
}
```

**Fabric: `source/fabric/LovelyLegacy.java`**

```java
public class LovelyLegacy implements ModInitializer {
    public static final String MODID = "llovelyr";
    
    @Override
    public void onInitialize() {
        // Register using Lib bridges
        FabricEntityRegistry.register();
        FabricItemRegistry.register();
        FabricRecipeRegistry.register();
        
        // Initialize config
        FabricRobotConfig.init();
        
        // Register events
        FabricEvents.register();
    }
}
```

**Complexity:** Low - Thin initialization
**Estimated Effort:** 1-2 hours per loader

**Phase 4 Total Effort:** 20-30 hours


## Migration Strategy

### Step-by-Step Execution Plan

#### Week 1: Framework Foundation

**Day 1-2: Create Framework Structure**
- Create `Framework/` directory
- Set up `framework/entity/`, `framework/util/`, `framework/animation/`
- Move pure Java enums and data classes
- Create `build.gradle` for Framework module

**Day 3-4: Framework Core Classes**
- Implement `EntityData`, `EntityAttributes`, `EntityBehavior`
- Implement `PoseManager`, `AnimationState`
- Implement `MathUtil`, `StringUtil`
- Write unit tests for Framework classes

**Day 5: Framework Validation**
- Ensure Framework has ZERO Minecraft dependencies
- Run tests
- Document Framework API

**Deliverable:** Working Framework module with tests

#### Week 2: Lib Bridges (Forge)

**Day 1-2: Forge Entity Bridge**
- Create `Lib/Forge/` structure
- Implement `ForgeEntityBridge`
- Implement `ForgeRegistration`
- Test entity data serialization

**Day 3: Forge Rendering & Config**
- Implement `ForgeRenderAdapter`
- Implement `ForgeConfigBridge`
- Test resource loading

**Day 4: Forge Commands**
- Implement `ForgeCommandBridge`
- Test command registration

**Day 5: Forge Integration Tests**
- Create test mod using Forge Lib
- Validate all bridges work

**Deliverable:** Working Forge Lib module

#### Week 3: Lib Bridges (Fabric)

**Day 1-2: Fabric Entity Bridge**
- Create `Lib/Fabric/` structure
- Implement `FabricEntityBridge`
- Implement `FabricRegistration`
- Test entity data serialization

**Day 3: Fabric Rendering & Config**
- Implement `FabricRenderAdapter`
- Implement `FabricConfigBridge`
- Test resource loading

**Day 4: Fabric Commands**
- Implement `FabricCommandBridge`
- Test command registration

**Day 5: Fabric Integration Tests**
- Create test mod using Fabric Lib
- Validate all bridges work

**Deliverable:** Working Fabric Lib module

#### Week 4: Common Layer

**Day 1-2: Base Entity**
- Create `Common/` structure
- Refactor `LovelyRobot.java` → `LovelyRobotEntity.java`
- Extract business logic
- Use Framework classes

**Day 3: AI Goals & Recipes**
- Move AI goal classes to Common
- Move recipe system to Common
- Update imports

**Day 4: Utilities & Config**
- Move utilities to Common
- Create config interface
- Implement config in Lib

**Day 5: Common Integration Tests**
- Test Common classes with both Lib implementations
- Validate behavior consistency

**Deliverable:** Working Common module

#### Week 5: Source Refactoring (Forge)

**Day 1: Entity Implementations**
- Refactor `VanillaEntity` to use Common
- Refactor `Bunny2Entity` to use Common
- Test entity spawning

**Day 2: Registration**
- Create `ForgeEntityRegistry`
- Create `ForgeItemRegistry`
- Create `ForgeRecipeRegistry`

**Day 3: Commands**
- Extract command logic to Common
- Implement Forge command registration
- Test commands

**Day 4: Mod Entry Point**
- Refactor `LovelyLegacy.java`
- Wire up all registrations
- Test mod loading

**Day 5: Forge Testing**
- Full gameplay testing
- Verify all features work
- Fix any issues

**Deliverable:** Working Forge implementation using new architecture

#### Week 6: Source Refactoring (Fabric)

**Day 1: Entity Implementations**
- Refactor `VanillaEntity` to use Common
- Refactor `Bunny2Entity` to use Common
- Test entity spawning

**Day 2: Registration**
- Create `FabricEntityRegistry`
- Create `FabricItemRegistry`
- Create `FabricRecipeRegistry`

**Day 3: Commands**
- Implement Fabric command registration
- Test commands

**Day 4: Mod Entry Point**
- Refactor `LovelyLegacy.java`
- Wire up all registrations
- Test mod loading

**Day 5: Fabric Testing**
- Full gameplay testing
- Verify all features work
- Fix any issues

**Deliverable:** Working Fabric implementation using new architecture

#### Week 7: Validation & Documentation

**Day 1-2: Cross-Loader Testing**
- Test Forge and Fabric side-by-side
- Verify feature parity
- Test multiplayer compatibility

**Day 3: Performance Testing**
- Benchmark entity performance
- Test with 10+ robots
- Verify no regressions

**Day 4: Code Quality**
- Run static analysis
- Check code duplication metrics
- Verify <20% duplication achieved

**Day 5: Documentation**
- Document new architecture
- Update developer guides
- Create migration guide for other versions

**Deliverable:** Validated, documented refactored codebase


## Risk Assessment & Mitigation

### High-Risk Areas

#### 1. Entity Data Synchronization
**Risk:** Client-server desync after refactoring
**Impact:** High - Breaks multiplayer
**Mitigation:**
- Extensive multiplayer testing
- Keep original NBT structure
- Test save/load compatibility
- Validate data tracker synchronization

#### 2. GeckoLib Integration
**Risk:** Animation system breaks during refactoring
**Impact:** High - Visual bugs
**Mitigation:**
- Keep rendering code in Source initially
- Test animations after each change
- Maintain GeckoLib version compatibility

#### 3. Command System
**Risk:** Complex command tree breaks during extraction
**Impact:** Medium - Admin features unavailable
**Mitigation:**
- Extract incrementally
- Test each command individually
- Keep Forge implementation as reference

#### 4. Config System
**Risk:** Config values don't load correctly
**Impact:** Medium - Gameplay balance issues
**Mitigation:**
- Maintain config file compatibility
- Test default values
- Validate config reload

### Medium-Risk Areas

#### 5. Recipe System
**Risk:** NBT transfer recipes break
**Impact:** Medium - Crafting issues
**Mitigation:**
- Test all recipe types
- Validate NBT preservation
- Check preview functionality

#### 6. AI Goals
**Risk:** Behavior changes after refactoring
**Impact:** Medium - Robot AI issues
**Mitigation:**
- Behavioral testing
- Compare before/after
- Test all AI states

### Low-Risk Areas

#### 7. Enums & Data Classes
**Risk:** Minimal - Pure data
**Impact:** Low
**Mitigation:** Unit tests

#### 8. Utilities
**Risk:** Minimal - Pure functions
**Impact:** Low
**Mitigation:** Unit tests

## Success Metrics

### Code Quality Metrics

**Before Refactoring:**
- Code Duplication: 83-91%
- Total LOC (both loaders): ~15,000
- Files duplicated: ~80
- Maintenance effort: 2x for every change

**After Refactoring (Target):**
- Code Duplication: <20%
- Total LOC (all layers): ~10,000
- Files duplicated: <10
- Maintenance effort: 1x for most changes

### Architecture Metrics

**Before:**
- Layers: 2 (common, source)
- Loader coupling: High
- Shared code: 0%
- Framework code: 0%

**After:**
- Layers: 4 (framework, lib, common, source)
- Loader coupling: Low (isolated to Lib)
- Shared code: 70-80%
- Framework code: 15-20%

### Development Velocity Metrics

**Before:**
- Time to add new robot: 4-6 hours (both loaders)
- Time to add new feature: 2x effort
- Bug fix propagation: Manual to both loaders

**After:**
- Time to add new robot: 1-2 hours (both loaders)
- Time to add new feature: 1x effort
- Bug fix propagation: Automatic (Common layer)

## File Movement Summary

### Framework Layer (Pure Java)
**Total Files:** ~15
**New Files:** ~10
**Moved Files:** ~5

```
framework/
├── entity/
│   ├── EntityData.java                 [NEW]
│   ├── EntityAttributes.java           [NEW]
│   ├── EntityBehavior.java             [NEW]
│   ├── EntityState.java                [MOVED from enums]
│   ├── EntityTexture.java              [MOVED from enums]
│   ├── EntityVariant.java              [MOVED from enums]
│   ├── EntityModel.java                [MOVED from enums]
│   └── EntityHand.java                 [MOVED from enums]
├── animation/
│   ├── PoseManager.java                [NEW]
│   ├── AnimationState.java             [REFACTORED from EntityAnimation]
│   └── AnimationController.java        [NEW]
├── util/
│   ├── Version.java                    [MOVED from common/util/internal]
│   ├── ObjectUtil.java                 [MOVED from common/util]
│   ├── MathUtil.java                   [NEW - extracted from Utility]
│   └── StringUtil.java                 [NEW - extracted from Utility]
└── data/
    ├── IDataSerializer.java            [NEW]
    └── EntityDataSerializer.java       [NEW]
```

### Lib Layer (Loader Bridges)
**Total Files:** ~20 (10 per loader)

```
lib/
├── forge/
│   ├── entity/
│   │   ├── ForgeEntityBridge.java      [NEW]
│   │   └── ForgeRegistration.java      [NEW]
│   ├── rendering/
│   │   └── ForgeRenderAdapter.java     [NEW]
│   ├── config/
│   │   ├── ForgeConfigBridge.java      [NEW]
│   │   └── ForgeRobotConfig.java       [NEW]
│   └── commands/
│       └── ForgeCommandBridge.java     [NEW]
└── fabric/
    ├── entity/
    │   ├── FabricEntityBridge.java     [NEW]
    │   └── FabricRegistration.java     [NEW]
    ├── rendering/
    │   └── FabricRenderAdapter.java    [NEW]
    ├── config/
    │   ├── FabricConfigBridge.java     [NEW]
    │   └── FabricRobotConfig.java      [NEW]
    └── commands/
        └── FabricCommandBridge.java    [NEW]
```

### Common Layer (Mod Shared)
**Total Files:** ~40
**Moved Files:** ~35
**New Files:** ~5

```
common/
├── entity/
│   ├── LovelyRobotEntity.java          [REFACTORED from LovelyRobot]
│   ├── BaseEntity.java                 [MERGED from InternalEntity]
│   ├── goal/
│   │   ├── AiAutoAttackGoal.java       [MOVED - no changes]
│   │   ├── AiBaseDefenseGoal.java      [MOVED - no changes]
│   │   ├── AiConditionalWanderGoal.java [MOVED - no changes]
│   │   └── AiFollowOwnerGoal.java      [MOVED - no changes]
│   └── behavior/
│       ├── RobotBehavior.java          [NEW]
│       ├── CombatBehavior.java         [NEW]
│       └── DefenseBehavior.java        [NEW]
├── animation/
│   ├── RobotAnimation.java             [REFACTORED from InternalAnimation]
│   └── PoseController.java             [NEW]
├── logic/
│   ├── RobotLogic.java                 [REFACTORED from InternalLogic]
│   └── LevelSystem.java                [NEW - extracted]
├── effects/
│   └── RobotParticles.java             [REFACTORED from InternalParticle]
├── recipes/
│   ├── interfaces/
│   │   ├── INbtTransferStrategy.java   [MOVED - no changes]
│   │   └── INbtModifier.java           [MOVED - no changes]
│   ├── strategies/
│   │   ├── FullNbtCopyStrategy.java    [MOVED - no changes]
│   │   └── AdditiveNbtMergeStrategy.java [MOVED - no changes]
│   ├── modifiers/
│   │   └── DyeColorModifier.java       [MOVED - no changes]
│   └── custom/
│       ├── SpawnRecipe.java            [REFACTORED from LovelySpawnRecipe]
│       └── SpawnDyeRecipe.java         [REFACTORED from LovelySpawnDyeRecipe]
├── commands/
│   └── RobotCommands.java              [NEW - extracted from loader-specific]
├── config/
│   ├── RobotConfig.java                [NEW - interface]
│   ├── RobotIdentifiers.java           [MOVED from LovelyIdentifier]
│   └── RobotResources.java             [MOVED from LovelyResource]
└── util/
    ├── RobotUtility.java               [REFACTORED from Utility]
    └── TooltipUtil.java                [MOVED from items/util]
```

### Source Layer (Loader Specific)
**Total Files:** ~30 per loader (reduced from ~80)
**Kept Files:** ~15 per loader
**New Files:** ~5 per loader
**Removed Files:** ~60 per loader (moved to Common)

```
source/
├── forge/
│   ├── LovelyLegacy.java               [REFACTORED - thin entry point]
│   ├── entity/
│   │   ├── VanillaEntity.java          [REFACTORED - ~30 lines]
│   │   ├── Bunny2Entity.java           [REFACTORED - ~30 lines]
│   │   ├── client/
│   │   │   ├── model/                  [KEPT - GeckoLib specific]
│   │   │   ├── renderer/               [KEPT - GeckoLib specific]
│   │   │   └── layer/                  [KEPT - GeckoLib specific]
│   │   └── registration/
│   │       └── ForgeEntityRegistry.java [NEW]
│   ├── items/
│   │   ├── ForgeItems.java             [REFACTORED]
│   │   └── ForgeItemRegistry.java      [NEW]
│   ├── recipes/
│   │   └── ForgeRecipeRegistry.java    [NEW]
│   ├── commands/
│   │   └── ForgeCommands.java          [NEW - thin wrapper]
│   ├── events/
│   │   └── ForgeEvents.java            [REFACTORED]
│   └── groups/
│       └── ForgeGroups.java            [REFACTORED]
└── fabric/
    ├── LovelyLegacy.java               [REFACTORED - thin entry point]
    ├── entity/
    │   ├── VanillaEntity.java          [REFACTORED - ~30 lines]
    │   ├── Bunny2Entity.java           [REFACTORED - ~30 lines]
    │   ├── client/
    │   │   ├── model/                  [KEPT - GeckoLib specific]
    │   │   ├── renderer/               [KEPT - GeckoLib specific]
    │   │   └── layer/                  [KEPT - GeckoLib specific]
    │   └── registration/
    │       └── FabricEntityRegistry.java [NEW]
    ├── items/
    │   ├── FabricItems.java            [REFACTORED]
    │   └── FabricItemRegistry.java     [NEW]
    ├── recipes/
    │   └── FabricRecipeRegistry.java   [NEW]
    ├── commands/
    │   └── FabricCommands.java         [NEW - thin wrapper]
    ├── events/
    │   └── FabricEvents.java           [REFACTORED]
    ├── groups/
    │   └── FabricGroups.java           [REFACTORED]
    ├── mixin/
    │   └── IShapedRecipeAccessor.java  [KEPT - Fabric specific]
    └── data/
        └── FabricDatagen.java          [KEPT - Fabric specific]
```


## Build System Changes

### Current Build Structure

```
llovelyr-1.20.1/
├── build.gradle                        [Root project]
├── settings.gradle                     [Includes Forge, Fabric]
├── Forge/
│   └── build.gradle                    [Forge-specific]
└── Fabric/
    └── build.gradle                    [Fabric-specific]
```

### New Build Structure

```
llovelyr-1.20.1/
├── build.gradle                        [Root project]
├── settings.gradle                     [Includes all modules]
├── Framework/
│   └── build.gradle                    [Pure Java, no MC deps]
├── Lib/
│   ├── Forge/
│   │   └── build.gradle                [Depends on Framework + Forge]
│   └── Fabric/
│       └── build.gradle                [Depends on Framework + Fabric]
├── Common/
│   └── build.gradle                    [Depends on Framework + MC common]
└── Source/
    ├── Forge/
    │   └── build.gradle                [Depends on Framework + Lib/Forge + Common]
    └── Fabric/
        └── build.gradle                [Depends on Framework + Lib/Fabric + Common]
```

### Gradle Configuration Examples

**Root `settings.gradle`:**
```gradle
rootProject.name = 'llovelyr-1.20.1'

include 'Framework'
include 'Lib:Forge'
include 'Lib:Fabric'
include 'Common'
include 'Source:Forge'
include 'Source:Fabric'
```

**Framework `build.gradle`:**
```gradle
plugins {
    id 'java-library'
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

dependencies {
    // Pure Java - no Minecraft dependencies
    testImplementation 'org.junit.jupiter:junit-jupiter:5.9.0'
}

test {
    useJUnitPlatform()
}
```

**Lib/Forge `build.gradle`:**
```gradle
plugins {
    id 'java-library'
    id 'net.minecraftforge.gradle' version '6.0.+'
}

dependencies {
    implementation project(':Framework')
    minecraft "net.minecraftforge:forge:1.20.1-47.2.0"
}
```

**Common `build.gradle`:**
```gradle
plugins {
    id 'java-library'
}

dependencies {
    implementation project(':Framework')
    compileOnly 'net.minecraft:minecraft:1.20.1'
    compileOnly 'io.github.llamalad7:mixinextras-common:0.3.5'
}
```

**Source/Forge `build.gradle`:**
```gradle
plugins {
    id 'java'
    id 'net.minecraftforge.gradle' version '6.0.+'
}

dependencies {
    implementation project(':Framework')
    implementation project(':Lib:Forge')
    implementation project(':Common')
    
    minecraft "net.minecraftforge:forge:1.20.1-47.2.0"
    implementation fg.deobf("software.bernie.geckolib:geckolib-forge-1.20.1:4.4.4")
}
```

## Testing Strategy

### Unit Tests (Framework)

**Test Coverage Target:** 80%+

```java
// framework/src/test/java/EntityDataTest.java
@Test
public void testEntityDataSerialization() {
    EntityData data = new EntityData("vanilla", EntityVariant.VANILLA);
    Map<String, Object> serialized = new EntityDataSerializer().serialize(data);
    EntityData deserialized = new EntityDataSerializer().deserialize(serialized);
    assertEquals(data, deserialized);
}

// framework/src/test/java/MathUtilTest.java
@Test
public void testLevelCalculation() {
    assertEquals(1, MathUtil.calculateLevel(0));
    assertEquals(2, MathUtil.calculateLevel(100));
    assertEquals(10, MathUtil.calculateLevel(5000));
}
```

### Integration Tests (Lib)

**Test Coverage Target:** 60%+

```java
// lib/forge/src/test/java/ForgeEntityBridgeTest.java
@Test
public void testEntityDataSync() {
    EntityData data = new EntityData("vanilla", EntityVariant.VANILLA);
    CompoundTag nbt = ForgeEntityBridge.serializeToNBT(data);
    EntityData restored = ForgeEntityBridge.deserializeFromNBT(nbt);
    assertEquals(data, restored);
}
```

### Behavioral Tests (Common)

**Test Coverage Target:** 50%+

```java
// common/src/test/java/LevelSystemTest.java
@Test
public void testLevelUp() {
    LovelyRobotEntity robot = createTestRobot();
    robot.addExp(100);
    assertEquals(2, robot.getCurrentLevel());
}

@Test
public void testNamedRobotExpBonus() {
    LovelyRobotEntity robot = createTestRobot();
    robot.setCustomName("TestBot");
    robot.addExp(100);
    // Should get 150 exp due to 1.5x multiplier
    assertTrue(robot.getExp() >= 150);
}
```

### End-to-End Tests (Source)

**Manual Testing Checklist:**

- [ ] Robot spawns correctly
- [ ] Robot follows owner
- [ ] Robot enters defense mode
- [ ] Robot levels up
- [ ] Robot protections work
- [ ] Robot retrieval works (Ctrl+Shift)
- [ ] Core drops with glow effect
- [ ] Smart retrieval works
- [ ] Sitting animation works
- [ ] Commands work
- [ ] Config loads correctly
- [ ] Recipes work (spawn egg, dyeing)
- [ ] Multiplayer sync works
- [ ] Save/load preserves data

## Documentation Requirements

### Framework Documentation

**Required:**
- API documentation (JavaDoc)
- Architecture overview
- Usage examples
- Extension guide

**Location:** `Framework/docs/`

### Lib Documentation

**Required:**
- Bridge API documentation
- Loader-specific notes
- Migration guide from direct MC API

**Location:** `Lib/docs/`

### Common Documentation

**Required:**
- Robot behavior documentation
- AI goal documentation
- Recipe system documentation
- Command system documentation

**Location:** `Common/docs/`

### Source Documentation

**Required:**
- Mod setup guide
- Development environment setup
- Build instructions
- Testing guide

**Location:** `Source/docs/`

## Rollback Plan

### If Refactoring Fails

**Checkpoint Strategy:**
1. Create git branch before starting: `feature/four-layer-architecture`
2. Create checkpoint branches after each phase:
   - `checkpoint/framework-complete`
   - `checkpoint/lib-forge-complete`
   - `checkpoint/lib-fabric-complete`
   - `checkpoint/common-complete`
   - `checkpoint/source-forge-complete`
   - `checkpoint/source-fabric-complete`

**Rollback Triggers:**
- Critical bugs that can't be fixed within 2 days
- Performance regression >20%
- Feature parity cannot be achieved
- Testing reveals fundamental architecture flaw

**Rollback Process:**
1. Document what went wrong
2. Revert to last stable checkpoint
3. Analyze failure
4. Revise architecture plan
5. Retry with lessons learned

## Next Steps

### Immediate Actions (This Week)

1. **Review this document** with team/stakeholders
2. **Create git branch** `feature/four-layer-architecture`
3. **Set up Framework module** structure
4. **Move first enum** (EntityState) to validate process
5. **Write first unit test** to validate Framework isolation

### Phase 0: Proof of Concept (Weekend)

**Goal:** Validate architecture with minimal implementation

**Tasks:**
1. Create Framework module with EntityState enum
2. Create Forge Lib with minimal bridge
3. Create Common with minimal entity
4. Create Source/Forge with minimal VanillaEntity
5. Spawn ONE robot successfully

**Success Criteria:**
- Robot spawns
- Robot has correct texture
- Robot can be interacted with
- Code is cleaner than original

**Time Estimate:** 4-6 hours

### Decision Point

After Phase 0 proof of concept:
- ✅ **Continue:** If robot spawns and code is cleaner
- ❌ **Revise:** If architecture feels wrong or too complex
- 🔄 **Iterate:** If close but needs adjustments

## Conclusion

This refactoring plan provides a comprehensive roadmap to transform the Legacy 1.20.1 codebase from a duplicated, tightly-coupled structure to a clean, four-layer architecture that will:

1. **Reduce duplication** from 83-91% to <20%
2. **Improve maintainability** by centralizing shared logic
3. **Enable rapid development** of new features and robot types
4. **Facilitate multi-loader support** without code duplication
5. **Establish foundation** for Tribute and Reboot variants

**Total Estimated Effort:** 78-117 hours (10-15 working days)

**Expected Benefits:**
- 60% reduction in codebase size
- 50% reduction in development time for new features
- 90% reduction in bug fix propagation time
- 100% improvement in code organization

**Risk Level:** Medium - Manageable with proper testing and incremental approach

**Recommendation:** Proceed with Phase 0 proof of concept to validate architecture before committing to full refactoring.

---

**Document Status:** Draft for Review
**Next Review Date:** After Phase 0 completion
**Owner:** Development Team
**Last Updated:** 2025-11-29

