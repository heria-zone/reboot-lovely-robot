# Multi-Loader Extraction Architecture Guide

**Status**: Active Reference  
**Last Updated**: 2025-01-10  
**Purpose**: Architectural patterns and decision framework for multi-loader code extraction

## Core Architectural Principles

### 1. Separation of Concerns
**Business Logic vs Platform Integration**

```
┌─────────────────────────────────────────────┐
│         Common Module (Business Logic)      │
│  Algorithms, Data Models, Validation        │
└─────────────────┬───────────────────────────┘
                  │
         ┌────────┴────────┐
         │                 │
┌────────▼────────┐  ┌────▼─────────────┐
│  Fabric Layer   │  │   Forge Layer    │
│  Registration   │  │   Registration   │
│  Event Hooks    │  │   Event Hooks    │
│  Platform APIs  │  │   Platform APIs  │
└─────────────────┘  └──────────────────┘
```

### 2. GeckoLib Isolation Boundary (CRITICAL)
**Absolute Rule**: GeckoLib dependencies must NEVER be in common module

**Forbidden in Common**:
```java
software.bernie.geckolib.*  // All GeckoLib imports
- GeoAnimatable, GeoEntity, GeoRenderer
- AnimationController, RawAnimation
- Any GeckoLib classes or interfaces
```

**Safe for Common**:
```java
net.minecraft.*     // Minecraft APIs
java.*, javax.*     // Java Standard Library
```

### 3. Dependency Direction
**Allowed**: `Loaders → Common → Minecraft APIs → Java`  
**Forbidden**: `Common → Loaders` (never allowed)

## Extraction Patterns

### Pattern 1: Direct Migration (100% Identical)
**When**: Code is identical across all loaders with no platform dependencies
**Process**: Move file directly to common, update imports
**Examples**: Utility functions, data validation, mathematical calculations

```java
// Before: Duplicated in each loader
fabric/utils/MathUtils.java
forge/utils/MathUtils.java

// After: Single implementation
common/lib/utils/MathUtils.java
```

### Pattern 2: Abstract Base Class (80-95% Similar)
**When**: Core logic similar but loaders have specific differences
**Process**: Extract common behavior to abstract base, create thin subclasses

```java
// Common: Abstract base with shared logic
public abstract class BaseSpawnItem {
    public InteractionResult useOn(UseOnContext context) {
        // Common validation and spawn logic
        EntityType<?> entityType = getEntityType(context.getItemInHand());
        return performSpawn(context, entityType);
    }
    
    protected abstract EntityType<?> getEntityType(ItemStack stack);
}

// Fabric: Thin implementation
public class LovelySpawnItem extends BaseSpawnItem {
    @Override
    protected EntityType<?> getEntityType(ItemStack stack) {
        return this.entityType; // Fabric-specific access
    }
}
```

### Pattern 3: Helper Class (Complex Logic Extraction)
**When**: Complex logic embedded in platform classes that can't change inheritance
**Process**: Extract logic to static helper, platform classes delegate

```java
// Common: Helper with extracted logic
public class ItemSpawnHelper {
    public static boolean canSpawnRobot(ServerLevel level, Player player) {
        // Complex spawn validation logic (50+ lines)
    }
    
    public static void initializeEntity(CompoundTag nbt, LovelyRobotEntity entity) {
        // Complex initialization logic (100+ lines)
    }
}

// Loader: Delegates to helper
public class LovelySpawnItem extends SpawnEggItem {
    @Override
    public InteractionResult useOn(UseOnContext context) {
        if (!ItemSpawnHelper.canSpawnRobot(level, player)) return FAIL;
        // ... spawn entity
        ItemSpawnHelper.initializeEntity(nbt, entity);
        return SUCCESS;
    }
}
```

### Pattern 4: Strategy Interface (Platform Variations)
**When**: Algorithm varies by platform but common code needs access
**Process**: Define interface in common, implement in loaders, use service locator

```java
// Common: Interface and service locator
public interface PlatformServices {
    boolean isModLoaded(String modId);
    Path getConfigDirectory();
}

public class Services {
    private static PlatformServices instance;
    public static PlatformServices get() { return instance; }
    public static void setInstance(PlatformServices impl) { instance = impl; }
}

// Fabric: Implementation
public class FabricServices implements PlatformServices {
    @Override
    public boolean isModLoaded(String modId) {
        return FabricLoader.getInstance().isModLoaded(modId);
    }
}

// Usage in common code
if (Services.get().isModLoaded("jei")) {
    // Enable JEI integration
}
```

### Pattern 5: Codec Helper (Serialization Logic)
**When**: Recipe/data serialization logic is duplicated
**Process**: Extract codec construction patterns to common utilities

```java
// Common: Codec building utilities
public class CodecHelper {
    public static <R extends ShapedRecipe> MapCodec<R> shapedRecipeCodec(
        Function5<String, CraftingBookCategory, ShapedRecipePattern, ItemStack, Item, R> constructor,
        Item requiredItem
    ) {
        return RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                Codec.STRING.optionalFieldOf("group", "").forGetter(ShapedRecipe::getGroup),
                // ... other fields
            ).apply(instance, (group, cat, pattern, result) ->
                constructor.apply(group, cat, pattern, result, requiredItem))
        );
    }
}

// Loader: Simple serializer using helper
public class LovelySpawnRecipeSerializer implements RecipeSerializer<LovelySpawnRecipe> {
    private final MapCodec<LovelySpawnRecipe> codec = 
        CodecHelper.shapedRecipeCodec(LovelySpawnRecipe::new, Items.ROBOT_CORE);
}
```

## Decision Framework

### Extraction Decision Tree
```
Should this code move to common?
├─ Has loader-specific imports?
│  ├─ YES → Can logic be extracted? → YES: Helper Pattern | NO: Keep in loader
│  └─ NO → Continue analysis
├─ Uses registry APIs? → YES: Keep in loader
├─ Pure calculation/validation? → YES: Direct Migration
├─ Rendering with vanilla APIs only? → YES: Direct Migration  
├─ GeckoLib dependent? → YES: Keep in loader
└─ Data model/POJO? → YES: Direct Migration
```

### Pattern Selection Matrix
| Similarity | Platform Dependencies | Inheritance Constraints | Pattern |
|------------|----------------------|------------------------|---------|
| 100% | None | Any | Direct Migration |
| 80-95% | Minor | Can change | Abstract Base Class |
| 50-95% | Any | Cannot change | Helper Class |
| Any | Varies by platform | Any | Strategy Interface |
| Any | Serialization only | Any | Codec Helper |

### Extraction Value Calculation
```
Value = (Lines Saved × Change Frequency) / Extraction Complexity

Where:
- Lines Saved = Total duplicate lines eliminated
- Change Frequency = Estimated changes per month
- Extraction Complexity = Hours to extract and test

Priority:
- High (>5.0): Extract immediately
- Medium (2.0-5.0): Extract when convenient  
- Low (<2.0): Consider leaving as-is
```

## Component Classification

### Extract to Common (Business Logic)
- **Pure Calculations**: Math utilities, algorithms, data transformations
- **Validation Logic**: Input validation, constraint checking, data sanitization
- **Data Models**: POJOs, data structures, NBT processing utilities
- **Business Rules**: Game logic, entity behavior, recipe processing
- **Rendering Logic**: Vanilla Minecraft rendering (no loader APIs)

### Keep in Loaders (Platform Integration)
- **Registration Systems**: Item, entity, recipe, command registration
- **Event Handling**: Platform-specific event bus operations
- **Creative Tabs**: Platform-specific creative tab implementations
- **GeckoLib Components**: All animation, model, and rendering using GeckoLib
- **Lifecycle Management**: Mod initialization, configuration loading

### Hybrid Cases (Extract with Abstraction)
- **Recipe Classes**: Common logic with loader-specific serializers
- **Entity Classes**: Common behavior with loader-specific registration
- **Item Classes**: Common interaction logic with loader-specific registration

## Risk Assessment

### Risk Categories
- **Low Risk**: Pure logic, no dependencies, vanilla APIs only
- **Medium Risk**: Minor loader differences, client-only rendering
- **High Risk**: Registry abstractions, event systems, lifecycle coordination

### Risk Mitigation
- **Low Risk**: Simple move-compile-test workflow
- **Medium Risk**: Comprehensive test plans, validation on all loaders
- **High Risk**: Design documents, extended QA periods, rollback plans

## Package Organization

### Common Module Structure
```
Common/src/main/java/net/msymbios/llovelyr/lib/
├── entity/
│   ├── base/        # Abstract base classes
│   ├── helpers/     # Entity helper utilities
│   ├── data/        # Entity data components
│   └── features/    # Entity feature logic
├── items/
│   ├── base/        # Abstract base classes
│   └── helpers/     # Item helper utilities
├── recipes/
│   ├── base/        # Abstract base classes
│   ├── strategies/  # Recipe strategies
│   └── utils/       # Recipe utilities
├── services/        # Platform services
├── registry/        # Registry management
└── utils/           # General utilities
```

### Naming Conventions
- **Base Classes**: `Base[ComponentName]` (e.g., `BaseSpawnItem`)
- **Helper Classes**: `[Domain]Helper` (e.g., `ItemSpawnHelper`)
- **Utility Classes**: `[Purpose]Utils` (e.g., `ValidationUtils`)
- **Service Interfaces**: `[Domain]Services` (e.g., `PlatformServices`)
- **Strategy Interfaces**: `I[Purpose]Strategy` (e.g., `INbtTransferStrategy`)

## Validation and Testing

### Compilation Validation
```bash
# Test all modules compile
./gradlew :Common:compileJava
./gradlew :Fabric:compileJava  
./gradlew :Forge:compileJava
./gradlew :NeoForge:compileJava
```

### Behavioral Consistency Testing
```java
// Cross-loader consistency test
@Test
void testIdenticalBehavior() {
    // Setup identical conditions
    ItemStack spawnItem = createTestSpawnItem();
    UseOnContext context = createTestContext();
    
    // Execute on each loader
    InteractionResult result = spawnItem.useOn(context);
    
    // Assert identical behavior
    assertEquals(InteractionResult.SUCCESS, result);
}
```

### GeckoLib Isolation Validation
```bash
# Check for GeckoLib imports in common
if grep -r "software.bernie.geckolib" Common/src/; then
    echo "ERROR: GeckoLib imports found in common module!"
    exit 1
fi
```

## Performance Considerations

### Optimization Guidelines
- **Static Methods**: Prefer static methods to avoid object creation overhead
- **Caching**: Cache expensive computations when appropriate
- **Object Pooling**: Use object pools for frequently created objects
- **Memory Management**: Minimize allocations in hot paths

### Performance Monitoring
- **Compilation Time**: Monitor build times for regression detection
- **Runtime Performance**: Benchmark critical paths before/after extraction
- **Memory Usage**: Profile memory consumption of common components

---

**Key Principle**: This architecture enables systematic code extraction while maintaining clean separation between platform-specific and shared logic. The patterns and decision framework ensure consistent, maintainable results across all extraction efforts.