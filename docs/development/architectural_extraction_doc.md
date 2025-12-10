# Architectural Documentation: Multi-Loader Code Extraction Strategy

## Table of Contents
1. [Introduction](#introduction)
2. [Architectural Principles](#architectural-principles)
3. [Analysis Framework](#analysis-framework)
4. [Component Classification](#component-classification)
5. [Extraction Patterns](#extraction-patterns)
6. [Decision Trees](#decision-trees)
7. [Risk Assessment](#risk-assessment)
8. [Long-term Maintenance Strategy](#long-term-maintenance-strategy)

---

## 1. Introduction

### 1.1 Purpose
This document establishes the architectural framework for identifying and extracting loader-agnostic code from multi-loader Minecraft mod implementations. It provides formal analysis methodologies, decision criteria, and architectural patterns for maintaining clean separation between platform-specific and shared logic.

### 1.2 Scope
- Analysis of Fabric and Forge mod loader implementations
- Identification of extractable business logic
- Definition of platform integration boundaries
- Establishment of code organization principles
- Risk assessment for refactoring operations

### 1.3 Context
The mod currently maintains parallel implementations for Fabric and Forge loaders, resulting in significant code duplication. While some duplication is necessary due to fundamental platform differences, substantial business logic is unnecessarily duplicated.

---

## 2. Architectural Principles

### 2.1 Separation of Concerns

**Business Logic vs Platform Integration**

```
┌─────────────────────────────────────────────┐
│         Application Layer (Common)          │
│  Business Rules, Algorithms, Data Models    │
└─────────────────┬───────────────────────────┘
                  │
         ┌────────┴────────┐
         │                 │
┌────────▼────────┐  ┌────▼─────────────┐
│  Fabric Layer   │  │   Forge Layer    │
│  Registration   │  │   Registration   │
│  Event Hooks    │  │   Event Hooks    │
│  Fabric APIs    │  │   Forge APIs     │
└─────────────────┘  └──────────────────┘
```

**Core Principle:** Business logic should execute identically regardless of loader. Platform layers should be thin adapters that translate platform-specific concepts into loader-agnostic operations.

### 2.2 Dependency Direction

**Allowed:**
```
Common ← Fabric (depends on common)
Common ← Forge (depends on common)
```

**Forbidden:**
```
Common → Fabric (common cannot depend on Fabric)
Common → Forge (common cannot depend on Forge)
Fabric ↔ Forge (loaders cannot depend on each other)
```

### 2.3 Interface Segregation

When platform-specific operations are required in common code:

```java
// Common defines the interface
public interface PlatformRegistry {
    void registerItem(ResourceLocation id, Item item);
    boolean isModLoaded(String modId);
}

// Fabric provides implementation
public class FabricRegistryImpl implements PlatformRegistry {
    @Override
    public void registerItem(ResourceLocation id, Item item) {
        Registry.register(BuiltInRegistries.ITEM, id, item);
    }
}

// Forge provides implementation
public class ForgeRegistryImpl implements PlatformRegistry {
    @Override
    public void registerItem(ResourceLocation id, Item item) {
        // Uses DeferredRegister internally
    }
}
```

### 2.4 Thin Adapter Pattern

Platform-specific classes should be minimal wrappers around common logic:

```java
// ❌ Bad: Business logic in platform class
public class FabricSpawnItem extends SpawnEggItem {
    @Override
    public InteractionResult useOn(UseOnContext ctx) {
        // 200 lines of spawn logic here
    }
}

// ✅ Good: Thin adapter delegates to common
public class FabricSpawnItem extends SpawnEggItem {
    private final SpawnItemLogic logic = new SpawnItemLogic();
    
    @Override
    public InteractionResult useOn(UseOnContext ctx) {
        return logic.handleSpawn(ctx, this::getEntityType);
    }
}
```

---

## 3. Analysis Framework

### 3.1 Dependency Analysis

**Step 1: Import Analysis**
Scan code for platform-specific imports:

```bash
# Fabric-specific
net.fabricmc.*
io.github.fabricators_of_create.*

# Forge-specific
net.minecraftforge.*
com.mojang.datafixers.*  # Sometimes Forge-specific usage

# Loader-agnostic
net.minecraft.*
software.bernie.geckolib.*
java.*
```

**Step 2: API Surface Analysis**
Identify which Minecraft/GeckoLib APIs are used:

```
Client-side only:
- net.minecraft.client.renderer.*
- net.minecraft.client.model.*

Common (both sides):
- net.minecraft.world.entity.*
- net.minecraft.world.item.*
- net.minecraft.world.level.*

Registry-related:
- net.minecraft.core.Registry
- net.minecraft.core.registries.*
```

**Step 3: Lifecycle Analysis**
Determine when code executes:

```
Registration Phase:
- Item/Block/Entity registration
- Recipe serializer registration
- Command argument type registration
→ Platform-specific

Runtime Phase:
- Entity AI behavior
- Recipe crafting logic
- Animation calculations
→ Potentially extractable

Rendering Phase:
- Client-only
- Uses vanilla rendering APIs
→ Extractable if no loader APIs
```

### 3.2 Code Similarity Analysis

**Structural Similarity Metric:**

```
Similarity Score = (Identical Lines) / (Total Lines)

> 95%  → Identical (move to common immediately)
80-95% → High similarity (extract with abstraction)
50-80% → Moderate similarity (evaluate case-by-case)
< 50%  → Different (likely platform-specific)
```

**Semantic Similarity:**
Even if code looks different, ask: "Does this implement the same business rule?"

Example:
```java
// Fabric
Registry.register(BuiltInRegistries.ITEM, id, item);

// Forge
ITEMS.register(id.getPath(), () -> item);
```

Structurally different (0% identical lines), but semantically identical (both register items). **Keep separate** because registries ARE the platform difference.

### 3.3 Complexity Analysis

**Extraction Value Score:**

```
Value = (Lines Saved) × (Maintenance Frequency) / (Extraction Complexity)

High Value (> 5.0):
- Large duplicated blocks (>100 lines)
- Frequently modified (bug fixes, features)
- Low complexity to extract

Low Value (< 2.0):
- Small duplicated blocks (<20 lines)
- Rarely changed (stable code)
- High complexity to extract (deep platform integration)
```

---

## 4. Component Classification

### 4.1 Pure Business Logic (Extract to Common)

**Characteristics:**
- No loader-specific imports
- No registry interactions
- No lifecycle dependencies
- Pure calculations, transformations, validations

**Examples from Codebase:**

**✅ InternalAnimation:**
```java
public class InternalAnimation {
    public static RawAnimation IDLE = RawAnimation.begin().thenLoop("idle");
    
    public static <T extends LovelyRobotEntity & GeoAnimatable> 
    AnimationController<T> locomotionAnimation(T entity) {
        return new AnimationController<>(entity, "Locomotion", 2, state -> {
            if (state.isMoving()) return state.setAndContinue(WALK);
            // ... pure animation logic
        });
    }
}
```

**Why extractable:**
- Only depends on GeckoLib (available to both loaders)
- Only depends on LovelyRobotEntity (in common)
- No `net.fabricmc.*` or `net.minecraftforge.*` imports
- Pure animation state machine logic

**✅ Recipe Transfer Strategies:**
```java
public class FullNbtCopyStrategy implements INbtTransferStrategy {
    @Override
    public ItemStack transferNbt(CraftingInput input, ItemStack result) {
        // Find source item, copy NBT, return result
        // Pure data transformation logic
    }
}
```

**Why extractable:**
- No loader dependencies
- Works with vanilla Minecraft APIs only
- Algorithm is identical across loaders

### 4.2 Rendering Logic (Extract to Common)

**Characteristics:**
- Uses `net.minecraft.client.renderer.*` (vanilla)
- Uses GeckoLib rendering APIs
- No loader-specific rendering hooks
- Implements visual calculations

**Examples:**

**✅ Layer System:**
```java
public interface IInternalRenderLayer<T> {
    boolean shouldRender(T entity, float partialTick);
    void render(PoseStack stack, T entity, ...);
}

public class DynamicColorLayer<T> implements IInternalRenderLayer<T> {
    @Override
    public void render(...) {
        int color = colorProvider.apply(entity);
        renderer.reRender(model, ..., color);
    }
}
```

**Why extractable:**
- Only uses vanilla rendering APIs
- GeckoLib is available to both loaders
- No loader-specific renderer registration (that's separate)

### 4.3 Platform Integration (Keep Separate)

**Characteristics:**
- Registry operations
- Event bus registration
- Lifecycle callbacks
- Loader-specific APIs

**Examples:**

**❌ Item Registration (Fabric):**
```java
public static Item register(ResourceLocation name, Item item) {
    return Registry.register(BuiltInRegistries.ITEM, name, item);
}
```

**❌ Item Registration (Forge):**
```java
public static RegistryObject<Item> register(
    DeferredRegister<Item> register, ResourceLocation name, Item item
) {
    return register.register(name.getPath(), () -> item);
}
```

**Why keep separate:**
- Fundamentally different registration systems
- Fabric: immediate registration during init
- Forge: deferred registration with suppliers
- Return types differ (Item vs RegistryObject<Item>)
- Cannot abstract without significant complexity

**❌ Creative Tab Registration:**

Fabric uses `FabricItemGroup.builder()`, Forge uses `CreativeModeTab.builder()` with different APIs. While structurally similar, they're platform APIs and should remain separate.

### 4.4 Hybrid Cases (Extract with Abstraction)

**Characteristics:**
- Core logic is identical
- Platform differences in initialization or access
- Can be abstracted with interface/helper pattern

**Example: Recipe Classes**

**Current State (96% identical):**
```java
// Both loaders have this identical assemble() method
@Override
public ItemStack assemble(CraftingInput inv, HolderLookup.Provider reg) {
    ItemStack result = super.assemble(inv, reg);
    return strategy.transferNbt(inv, result);
}

// Only difference is getSerializer()
@Override
public RecipeSerializer<?> getSerializer() {
    return LovelyRecipes.SPAWN_EGG_CRAFTING;      // Fabric
    return LovelyRecipes.SPAWN_EGG_CRAFTING.get(); // Forge (.get() for RegistryObject)
}
```

**Extraction Strategy:**
```java
// Common: Base class with all logic
public abstract class BaseLovelySpawnRecipe extends ShapedRecipe {
    @Override
    public ItemStack assemble(...) {
        // Shared implementation
    }
    
    // Subclasses provide serializer
    protected abstract RecipeSerializer<?> getSerializer();
}

// Fabric: Thin wrapper
public class LovelySpawnRecipe extends BaseLovelySpawnRecipe {
    @Override
    protected RecipeSerializer<?> getSerializer() {
        return LovelyRecipes.SPAWN_EGG_CRAFTING;
    }
}

// Forge: Thin wrapper
public class LovelySpawnRecipe extends BaseLovelySpawnRecipe {
    @Override
    protected RecipeSerializer<?> getSerializer() {
        return LovelyRecipes.SPAWN_EGG_CRAFTING.get();
    }
}
```

**Alternative Strategy (if serializer resolution is consistent):**
Pass serializer supplier in constructor, move entire class to common.

---

## 5. Extraction Patterns

### 5.1 Pattern: Direct Migration

**When to Use:**
- Code is 100% identical
- No loader-specific imports
- No platform API usage

**Process:**
1. Verify no hidden dependencies
2. Move file to common module
3. Update package imports in loaders
4. Compile and test

**Example:**
```
Before:
fabric/lib/entity/InternalAnimation.java
forge/lib/entity/InternalAnimation.java

After:
common/lib/entity/InternalAnimation.java
```

### 5.2 Pattern: Abstract Base Class

**When to Use:**
- 80-95% code similarity
- Small loader-specific differences
- Differences are in method returns or initialization

**Process:**
1. Identify common logic
2. Create abstract base in common
3. Extract platform differences to abstract methods
4. Create thin subclasses in loaders

**Example:**
```java
// common/
public abstract class BaseEntityRegistry {
    protected <T extends Entity> void registerEntity(
        String name, EntityType.Builder<T> builder
    ) {
        EntityType<T> type = builder.build(name);
        doRegister(name, type);  // Platform-specific
    }
    
    protected abstract void doRegister(String name, EntityType<?> type);
}

// fabric/
public class FabricEntityRegistry extends BaseEntityRegistry {
    @Override
    protected void doRegister(String name, EntityType<?> type) {
        Registry.register(BuiltInRegistries.ENTITY_TYPE, 
                         new ResourceLocation(MODID, name), type);
    }
}
```

### 5.3 Pattern: Helper Class

**When to Use:**
- Complex logic embedded in platform classes
- Logic is identical but context differs
- Cannot change inheritance (extends platform class)

**Process:**
1. Extract logic to static helper or service class
2. Place helper in common
3. Platform classes delegate to helper

**Example:**
```java
// common/lib/items/SpawnItemHelper.java
public class SpawnItemHelper {
    public static boolean checkSpawnLimit(ServerLevel level, Player player) {
        OwnerRobotRegistry registry = RobotRegistryManager.getRegistry(level);
        return registry.canSpawnRobot(player.getUUID(), MAX_ROBOTS);
    }
    
    public static void initializeEntity(CompoundTag nbt, LovelyRobotEntity entity) {
        // 50+ lines of initialization logic
    }
}

// fabric/shared/item/LovelySpawnItem.java (extends SpawnEggItem)
public class LovelySpawnItem extends SpawnEggItem {
    @Override
    public InteractionResult useOn(UseOnContext ctx) {
        if (!SpawnItemHelper.checkSpawnLimit(level, player)) {
            return InteractionResult.FAIL;
        }
        // ... spawn logic
        SpawnItemHelper.initializeEntity(nbt, entity);
    }
}
```

### 5.4 Pattern: Strategy Interface

**When to Use:**
- Algorithm/behavior varies by platform
- Common code needs platform-specific behavior
- Runtime selection of implementation

**Process:**
1. Define interface in common
2. Implement in each loader
3. Provide via service locator or injection

**Example:**
```java
// common/lib/platform/PlatformServices.java
public interface PlatformServices {
    boolean isModLoaded(String modId);
    Path getConfigDir();
    boolean isDevelopmentEnvironment();
}

// Service locator in common
public class Services {
    private static PlatformServices instance;
    
    public static void setInstance(PlatformServices impl) {
        instance = impl;
    }
    
    public static PlatformServices get() {
        return instance;
    }
}

// fabric/lib/platform/FabricServices.java
public class FabricServices implements PlatformServices {
    @Override
    public boolean isModLoaded(String modId) {
        return FabricLoader.getInstance().isModLoaded(modId);
    }
}

// In Fabric mod init:
Services.setInstance(new FabricServices());
```

### 5.5 Pattern: Codec/Serialization Helpers

**When to Use:**
- Recipe/data serialization logic
- Codec building is duplicated
- Network serialization is identical

**Process:**
1. Extract codec construction patterns
2. Create builder utilities in common
3. Platform serializers become thin wrappers

**Example:**
```java
// common/lib/recipes/CodecBuilders.java
public class CodecBuilders {
    public static <R extends ShapedRecipe> MapCodec<R> shapedRecipeCodec(
        Function5<String, CraftingBookCategory, ShapedRecipePattern, 
                  ItemStack, Item, R> constructor,
        Item requiredItem
    ) {
        return RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                Codec.STRING.optionalFieldOf("group", "")
                    .forGetter(ShapedRecipe::getGroup),
                CraftingBookCategory.CODEC.optionalFieldOf("category", MISC)
                    .forGetter(ShapedRecipe::category),
                ShapedRecipePattern.MAP_CODEC
                    .forGetter(r -> ((R)r).pattern()),
                ItemStack.STRICT_SINGLE_ITEM_CODEC.fieldOf("result")
                    .forGetter(r -> r.getResultItem(null))
            ).apply(instance, (group, cat, pattern, result) ->
                constructor.apply(group, cat, pattern, result, requiredItem))
        );
    }
}

// Loader serializer becomes simple:
public class LovelySpawnRecipeSerializer implements RecipeSerializer<...> {
    private final MapCodec<LovelySpawnRecipe> codec = 
        CodecBuilders.shapedRecipeCodec(LovelySpawnRecipe::new, robotCoreItem);
}
```

---

## 6. Decision Trees

### 6.1 Should This Code Move to Common?

```
START
  │
  ├─→ [Has loader imports?] ──YES──→ [Can logic be extracted?]
  │                                      │
  │                                      ├─→ YES → Use Helper Pattern
  │                                      └─→ NO  → Keep in loader
  │
  └─→ NO
       │
       ├─→ [Uses registry APIs?] ──YES──→ Keep in loader
       │
       └─→ NO
            │
            ├─→ [Pure calculation?] ──YES──→ Move to common
            │
            ├─→ [Rendering logic?] 
            │    │
            │    ├─→ [Uses vanilla APIs only?] ──YES──→ Move to common
            │    └─→ [Uses loader rendering?] ──YES──→ Keep in loader
            │
            └─→ [Data model/POJO?] ──YES──→ Move to common
```

### 6.2 How to Extract Code?

```
START: Code identified for extraction
  │
  ├─→ [100% identical?]
  │    │
  │    └─→ YES → Direct Migration Pattern
  │
  ├─→ [80-95% identical?]
  │    │
  │    └─→ YES → Abstract Base Class Pattern
  │
  ├─→ [Embedded in platform class?]
  │    │
  │    └─→ YES → Helper Class Pattern
  │
  ├─→ [Algorithm varies by platform?]
  │    │
  │    └─→ YES → Strategy Interface Pattern
  │
  └─→ [Complex serialization?]
       │
       └─→ YES → Codec Helper Pattern
```

### 6.3 Is Extraction Worth It?

```
Calculate Extraction Value Score:
  Value = (LOC_saved × Change_frequency) / Complexity

  Where:
    LOC_saved = Lines of Code eliminated
    Change_frequency = Changes per month (estimate)
    Complexity = Hours to extract and test

  IF Value > 5.0:
    → HIGH PRIORITY: Extract immediately
  
  ELSE IF Value > 2.0:
    → MEDIUM PRIORITY: Extract when convenient
  
  ELSE IF Value < 2.0:
    → LOW PRIORITY: Consider leaving as-is
```

**Example Calculation:**

InternalAnimation extraction:
- LOC_saved: 150 lines (duplicated file)
- Change_frequency: 0.5 (modified every 2 months for animation tweaks)
- Complexity: 1 hour (simple file move)
- Value = (150 × 0.5) / 1 = **75.0** → **VERY HIGH PRIORITY**

Item registration abstraction:
- LOC_saved: 30 lines (helper methods)
- Change_frequency: 0.1 (rarely changes)
- Complexity: 8 hours (complex registry abstraction)
- Value = (30 × 0.1) / 8 = **0.375** → **LOW PRIORITY** (not worth it)

---

## 7. Risk Assessment

### 7.1 Risk Categories

**Low Risk (Green):**
- Pure logic classes with no dependencies
- Rendering code using only vanilla APIs
- Data models and POJOs
- Already-tested helper classes

**Medium Risk (Yellow):**
- Classes with minor loader differences
- Code requiring abstract base pattern
- Serialization/codec logic
- Client-only rendering with GeckoLib

**High Risk (Red):**
- Registry abstractions
- Event system changes
- Lifecycle coordination
- Platform API wrappers

### 7.2 Risk Mitigation Strategies

**For Low Risk Extractions:**
1. Move code
2. Update imports
3. Compile
4. Quick smoke test
5. Done

**For Medium Risk Extractions:**
1. Create comprehensive test plan
2. Extract in feature branch
3. Test on both loaders
4. Validate all use cases
5. Code review
6. Merge

**For High Risk Extractions:**
1. Design document with alternatives
2. Prototype in separate branch
3. Performance testing
4. Extended QA period
5. Rollback plan
6. Staged rollout

### 7.3 Breaking Change Matrix

| Change Type | Fabric Impact | Forge Impact | Users Affected | Mitigation |
|-------------|---------------|--------------|----------------|------------|
| Move animation logic | None | None | Zero | N/A |
| Move layer system | None | None | Zero | N/A |
| Abstract recipe classes | None | None | Zero | Maintain API compatibility |
| Change entity constructor | Breaking | Breaking | Modpack devs | Deprecation period |
| Modify registry flow | Critical | Critical | All users | Don't do it |

### 7.4 Rollback Procedures

**If extraction causes issues:**

1. **Immediate:** Revert commit in version control
2. **Short-term:** Keep old implementation in deprecated package
3. **Long-term:** Document lesson learned, update extraction guidelines

**Example Rollback:**
```java
// If new common code causes issues
@Deprecated(forRemoval = true)
public class OldInternalAnimation {
    // Keep old implementation as fallback
}

// New code with fallback
try {
    return CommonInternalAnimation.locomotionAnimation(entity);
} catch (Exception e) {
    logger.warn("Falling back to legacy animation", e);
    return OldInternalAnimation.locomotionAnimation(entity);
}
```

---

## 8. Long-term Maintenance Strategy

### 8.1 Preventing Re-Duplication

**Code Review Checklist:**
- [ ] Does this PR add new code to both loaders?
- [ ] Could this logic be in common instead?
- [ ] Have extraction patterns been considered?
- [ ] Is loader-specific code justified?

**Automated Detection:**
```bash
# Git pre-commit hook
if git diff --cached --name-only | grep -E "(fabric|forge)/.*\.java"; then
    echo "Warning: Changes to loader-specific code detected"
    echo "Consider if this logic belongs in common/"
fi
```

### 8.2 Documentation Requirements

**For Each Extracted Component:**
1. **Why it was extracted** (avoid re-duplication)
2. **What remains loader-specific** (avoid over-extraction)
3. **How to maintain it** (update procedures)

**Example:**
```java
/**
 * InternalAnimation - Animation controller factory for robot entities.
 * 
 * LOCATION: common/lib/entity/InternalAnimation.java
 * 
 * EXTRACTED: 2024-01 (was duplicated in Fabric/Forge)
 * REASON: Pure GeckoLib animation logic, no loader dependencies
 * 
 * LOADER-SPECIFIC ASPECTS:
 * - Entity registration (stays in loader packages)
 * - Renderer registration (stays in loader packages)
 * 
 * MAINTENANCE:
 * - When adding animations, add here (not in loaders)
 * - When modifying animation logic, test both loaders
 * - When adding entity-specific animations, use inheritance
 */
public class InternalAnimation { ... }
```

### 8.3 Evolution Guidelines

**When Minecraft Updates:**
1. Check if vanilla APIs changed
2. Verify loader APIs still match
3. Test all extracted components
4. Update common code first, then loaders

**When Adding Features:**
1. Implement in common if possible
2. Add loader adapters if needed
3. Document why loader-specific if kept separate
4. Update this architecture document with new patterns

**When Refactoring:**
1. Review this document's patterns
2. Maintain architectural principles
3. Don't sacrifice clarity for DRY
4. Update decision trees if new patterns emerge

### 8.4 Metrics and Monitoring

**Track These Metrics:**
- **Common LOC vs Total LOC** (target: 60-70% in common)
- **Duplication percentage** (target: <10% intentional duplication)
- **Loader-specific LOC** (target: <30% total, mostly registration)
- **Compilation time** (watch for regression after extractions)

**Review Quarterly:**
- Are new features being added to common?
- Is duplication creeping back in?
- Are extraction patterns still appropriate?
- Do decision trees need updates?

---

## 9. Conclusion

### 9.1 Key Takeaways

1. **Not all duplication is bad** - Registry and event code SHOULD be different
2. **Extract aggressively for business logic** - Algorithms, calculations, data flow
3. **Use patterns consistently** - Direct migration, abstract base, helper, strategy
4. **Calculate extraction value** - Don't extract low-value code
5. **Test thoroughly** - Especially medium/high risk extractions
6. **Document decisions** - Prevent re-duplication and over-extraction

### 9.2 Success Criteria

✅ **30-40% reduction in duplicate code**
✅ **Single source of truth for business logic**
✅ **Loader-specific code limited to platform integration**
✅ **No performance regression**
✅ **Maintainable and understandable architecture**

### 9.3 Continuous Improvement

This is a living document. As new patterns emerge or Minecraft/loader APIs evolve:
- Update decision trees
- Add new extraction patterns
- Refine risk assessments
- Document lessons learned

The goal is not perfect abstraction, but **pragmatic separation** of concerns that makes the codebase easier to maintain, test, and evolve.