# GeckoLib Isolation Requirements

**Status**: Active  
**Last Updated**: 2024-12-10  
**Author(s)**: AI Agent  
**Related Documents**: 
- [Extraction Documentation](docs/development/extraction-documentation.md)
- [Developer Guidelines](docs/development/developer-guidelines-future-development.md)
- [Requirements](specs/multi-loader-code-extraction/requirements.md)

## Purpose

This document establishes the critical requirements for maintaining GeckoLib isolation during and after the multi-loader code extraction project. GeckoLib is a loader-specific animation library that must never be extracted to the common module to prevent dependency conflicts and maintain architectural integrity.

## Critical Constraint

**ABSOLUTE RULE**: GeckoLib dependencies must NEVER be moved to the common module under any circumstances.

This constraint is fundamental to the project architecture and violating it would:
- Create circular dependencies between common and loader modules
- Introduce loader-specific dependencies in common code
- Break the clean dependency direction (Loaders → Common → Minecraft APIs)
- Cause compilation failures and runtime errors
- Compromise the entire extraction architecture

## GeckoLib Components That Must Remain in Loaders

### 1. Core GeckoLib Interfaces and Classes

**Forbidden in Common**:
```java
// NEVER import these in common module
software.bernie.geckolib.*

// Specific classes that must stay in loaders:
- GeoAnimatable           // Core animation interface
- GeoEntity              // Entity animation interface  
- GeoRenderer            // Base renderer class
- AnimationController    // Animation state management
- RawAnimation          // Animation definitions
- AnimationState        // Animation state tracking
- PlayState             // Animation playback state
- AnimatableManager     // Animation manager
- SingletonAnimatableInstanceCache  // Animation caching
```

### 2. Animation System Components

**Location**: Each loader's `lib/entity/` directory

**Components That Must Stay**:
- **Animation Controllers**: All classes managing animation state and transitions
- **Animation Definitions**: RawAnimation instances and animation sequences
- **Animation State Management**: PlayState handling and animation triggers
- **Animation Caching**: SingletonAnimatableInstanceCache and related caching

**Example (Must Remain in Loaders)**:
```java
// This code MUST stay in loader modules
public class RobotAnimationController {
    private final AnimationController<LovelyRobotEntity> controller = 
        new AnimationController<>(this, "controller", 0, this::predicate);
    
    private PlayState predicate(AnimationState<LovelyRobotEntity> state) {
        // Animation logic that depends on GeckoLib
        return PlayState.CONTINUE;
    }
}
```

### 3. Entity Models and Geometry

**Location**: Each loader's `lib/entity/` directory

**Components That Must Stay**:
- **GeoModel Classes**: All classes extending GeoModel
- **Model Definitions**: Geometry and bone structure definitions
- **Model Caching**: Model instance management and caching
- **Bone Manipulation**: Runtime bone transformations and animations

**Example (Must Remain in Loaders)**:
```java
// This code MUST stay in loader modules
public class RobotModel extends GeoModel<LovelyRobotEntity> {
    @Override
    public ResourceLocation getModelResource(LovelyRobotEntity entity) {
        // Model resource logic
    }
    
    @Override
    public ResourceLocation getTextureResource(LovelyRobotEntity entity) {
        // Texture resource logic
    }
    
    @Override
    public ResourceLocation getAnimationResource(LovelyRobotEntity entity) {
        // Animation resource logic
    }
}
```

### 4. Entity Renderers and Layers

**Location**: Each loader's `lib/entity/` directory

**Components That Must Stay**:
- **GeoRenderer Classes**: All classes extending GeoEntityRenderer
- **Render Layers**: GeoRenderLayer implementations
- **Render State Management**: Rendering pipeline integration
- **Texture and Model Binding**: Resource location management for rendering

**Example (Must Remain in Loaders)**:
```java
// This code MUST stay in loader modules
public class RobotRenderer extends GeoEntityRenderer<LovelyRobotEntity> {
    public RobotRenderer(EntityRendererProvider.Context context) {
        super(context, new RobotModel());
        // GeckoLib-specific renderer setup
    }
    
    @Override
    public void render(LovelyRobotEntity entity, float entityYaw, float partialTick,
                      PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        // GeckoLib rendering logic
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }
}
```

### 5. Animation-Dependent Entity Logic

**Location**: Each loader's entity implementations

**Components That Must Stay**:
- **Animation Triggers**: Code that triggers specific animations
- **Animation State Synchronization**: Client-server animation sync
- **Animation-Dependent Behavior**: AI behavior that depends on animation state
- **Animation Event Handling**: Responding to animation events

**Example (Must Remain in Loaders)**:
```java
// This code MUST stay in loader modules
public class LovelyRobotEntity extends LivingEntity implements GeoAnimatable {
    private final AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);
    
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        // GeckoLib controller registration
        controllers.add(new AnimationController<>(this, "controller", 0, this::predicate));
    }
    
    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
    
    private PlayState predicate(AnimationState<LovelyRobotEntity> state) {
        // Animation state logic that uses GeckoLib APIs
        if (this.isInSittingPose()) {
            state.getController().setAnimation(RawAnimation.begin().thenLoop("sit"));
        } else if (this.isMoving()) {
            state.getController().setAnimation(RawAnimation.begin().thenLoop("walk"));
        } else {
            state.getController().setAnimation(RawAnimation.begin().thenLoop("idle"));
        }
        return PlayState.CONTINUE;
    }
}
```

## Safe Components for Common Module

### 1. Pure Business Logic

**Safe for Extraction**:
```java
// Safe to extract to common - no GeckoLib dependencies
public class EntityBehaviorHelper {
    public static boolean shouldSit(LovelyRobotEntity entity) {
        return entity.getEntityState() == EntityState.STANDBY && 
               entity.getOwner() != null &&
               entity.distanceToSqr(entity.getOwner()) < 4.0;
    }
    
    public static void updateEntityStats(LovelyRobotEntity entity, int level) {
        // Pure calculation logic without animation dependencies
        float health = calculateHealthForLevel(level);
        float attack = calculateAttackForLevel(level);
        entity.setMaxHealth(health);
        entity.setAttackDamage(attack);
    }
}
```

### 2. Data Processing and Validation

**Safe for Extraction**:
```java
// Safe to extract to common - no GeckoLib dependencies
public class EntityDataHelper {
    public static CompoundTag validateEntityData(CompoundTag nbt) {
        // Data validation logic without animation dependencies
        if (!nbt.contains("level")) {
            nbt.putInt("level", 0);
        }
        if (!nbt.contains("health")) {
            nbt.putFloat("health", 20.0f);
        }
        return nbt;
    }
    
    public static void applyEntityData(CompoundTag nbt, LovelyRobotEntity entity) {
        // Apply data without triggering animations
        entity.setCurrentLevel(nbt.getInt("level"));
        entity.setHealth(nbt.getFloat("health"));
        // Note: Animation state changes must remain in loader code
    }
}
```

### 3. Mathematical Calculations

**Safe for Extraction**:
```java
// Safe to extract to common - pure calculations
public class EntityCalculations {
    public static float calculateHealthForLevel(int level) {
        return 20.0f + (level * 2.0f);
    }
    
    public static float calculateAttackForLevel(int level) {
        return 4.0f + (level * 0.5f);
    }
    
    public static int calculateExperienceRequired(int level) {
        return level * level * 10;
    }
}
```

## Validation and Enforcement

### 1. Automated Detection

#### Pre-Commit Hook
```bash
#!/bin/bash
# Pre-commit hook to detect GeckoLib violations

echo "Checking for GeckoLib imports in common module..."

# Check for GeckoLib imports
GECKOLIB_IMPORTS=$(find Common/src/ -name "*.java" -exec grep -l "software\.bernie\.geckolib" {} \;)

if [ ! -z "$GECKOLIB_IMPORTS" ]; then
    echo "ERROR: GeckoLib imports found in common module!"
    echo "Files with violations:"
    echo "$GECKOLIB_IMPORTS"
    echo ""
    echo "GeckoLib dependencies must remain in loader-specific modules."
    echo "Please move the following to appropriate loader modules:"
    
    # Show specific imports
    find Common/src/ -name "*.java" -exec grep -H "software\.bernie\.geckolib" {} \;
    
    exit 1
fi

echo "GeckoLib isolation check passed."
```

#### Build-Time Validation
```gradle
// Gradle task to validate GeckoLib isolation
task validateGeckoLibIsolation {
    doLast {
        def commonSrc = file('Common/src')
        def violations = []
        
        commonSrc.eachFileRecurse { file ->
            if (file.name.endsWith('.java')) {
                file.eachLine { line, lineNumber ->
                    if (line.contains('software.bernie.geckolib')) {
                        violations.add("${file.path}:${lineNumber}: ${line.trim()}")
                    }
                }
            }
        }
        
        if (!violations.empty) {
            throw new GradleException(
                "GeckoLib imports found in common module:\n" + 
                violations.join('\n')
            )
        }
    }
}

// Run validation before compilation
compileJava.dependsOn validateGeckoLibIsolation
```

### 2. Code Review Checklist

#### GeckoLib Isolation Review
```markdown
## GeckoLib Isolation Review Checklist

### Common Module Review
- [ ] No GeckoLib imports present
- [ ] No AnimationController usage
- [ ] No GeoModel references
- [ ] No GeoRenderer usage
- [ ] No RawAnimation definitions
- [ ] No AnimationState handling
- [ ] No GeoAnimatable implementations

### Loader Module Review
- [ ] All GeckoLib code remains in loader
- [ ] Animation controllers properly isolated
- [ ] Model classes in correct location
- [ ] Renderer classes in correct location
- [ ] No common module dependencies on GeckoLib

### Integration Review
- [ ] Common code doesn't trigger animations directly
- [ ] Loader code properly delegates to common for business logic
- [ ] Clear separation between data and animation logic
- [ ] No circular dependencies created
```

### 3. Testing Validation

#### Isolation Test
```java
/**
 * Test to verify GeckoLib isolation is maintained.
 */
public class GeckoLibIsolationTest {
    
    @Test
    public void commonModule_ShouldNotHaveGeckoLibDependencies() {
        // Scan common module for GeckoLib imports
        Path commonSrc = Paths.get("Common/src/main/java");
        List<String> violations = new ArrayList<>();
        
        try {
            Files.walk(commonSrc)
                .filter(path -> path.toString().endsWith(".java"))
                .forEach(path -> {
                    try {
                        List<String> lines = Files.readAllLines(path);
                        for (int i = 0; i < lines.size(); i++) {
                            String line = lines.get(i);
                            if (line.contains("software.bernie.geckolib")) {
                                violations.add(path + ":" + (i + 1) + ": " + line.trim());
                            }
                        }
                    } catch (IOException e) {
                        fail("Failed to read file: " + path);
                    }
                });
        } catch (IOException e) {
            fail("Failed to scan common module");
        }
        
        if (!violations.isEmpty()) {
            fail("GeckoLib imports found in common module:\n" + 
                 String.join("\n", violations));
        }
    }
    
    @Test
    public void loaderModules_ShouldContainAllGeckoLibCode() {
        // Verify that animation, model, and renderer code exists in loaders
        String[] loaders = {"Fabric", "Forge", "NeoForge"};
        
        for (String loader : loaders) {
            Path loaderSrc = Paths.get(loader + "/src/main/java");
            
            // Check for required GeckoLib components
            assertTrue("Animation controllers missing in " + loader,
                      hasGeckoLibComponents(loaderSrc, "AnimationController"));
            assertTrue("Models missing in " + loader,
                      hasGeckoLibComponents(loaderSrc, "GeoModel"));
            assertTrue("Renderers missing in " + loader,
                      hasGeckoLibComponents(loaderSrc, "GeoRenderer"));
        }
    }
    
    private boolean hasGeckoLibComponents(Path srcPath, String componentType) {
        try {
            return Files.walk(srcPath)
                .filter(path -> path.toString().endsWith(".java"))
                .anyMatch(path -> {
                    try {
                        return Files.readString(path).contains(componentType);
                    } catch (IOException e) {
                        return false;
                    }
                });
        } catch (IOException e) {
            return false;
        }
    }
}
```

## Architecture Boundaries

### 1. Dependency Flow

**Correct Dependency Direction**:
```
Loader Modules → Common Module → Minecraft APIs → Java Standard Library
```

**GeckoLib Integration**:
```
Loader Entity Classes → GeckoLib APIs
Loader Entity Classes → Common Business Logic (for non-animation behavior)
```

**Forbidden Dependencies**:
```
Common Module → GeckoLib APIs (NEVER)
Common Module → Loader Modules (NEVER)
```

### 2. Interface Design

#### Safe Abstraction Pattern
```java
// Common interface - safe abstraction
public interface EntityAnimationTrigger {
    void triggerSitAnimation();
    void triggerWalkAnimation();
    void triggerIdleAnimation();
}

// Loader implementation - contains GeckoLib code
public class GeckoLibAnimationTrigger implements EntityAnimationTrigger {
    private final LovelyRobotEntity entity;
    
    @Override
    public void triggerSitAnimation() {
        // GeckoLib-specific animation triggering
        entity.getAnimationController().setAnimation(
            RawAnimation.begin().thenLoop("sit")
        );
    }
    
    // Other animation methods...
}

// Common business logic - uses safe abstraction
public class EntityBehaviorManager {
    public void updateBehavior(LovelyRobotEntity entity, EntityAnimationTrigger animator) {
        if (shouldSit(entity)) {
            animator.triggerSitAnimation(); // Safe call to loader implementation
        }
        // Other behavior logic...
    }
}
```

## Migration Guidelines

### 1. Extracting Entity Logic

#### Safe Extraction Process
```markdown
## Entity Logic Extraction Checklist

### Analysis Phase
- [ ] Identify business logic vs animation logic
- [ ] Separate data processing from animation triggers
- [ ] Map GeckoLib dependencies in current code
- [ ] Plan abstraction interfaces if needed

### Extraction Phase
- [ ] Extract pure business logic to common helpers
- [ ] Create abstraction interfaces for animation triggers
- [ ] Keep all GeckoLib code in loader implementations
- [ ] Update loader code to use common helpers

### Validation Phase
- [ ] Verify no GeckoLib imports in common
- [ ] Test animation functionality still works
- [ ] Validate business logic consistency across loaders
- [ ] Run isolation tests
```

#### Example Migration
```java
// BEFORE: Mixed logic in loader (problematic)
public class LovelyRobotEntity extends LivingEntity implements GeoAnimatable {
    public void updateBehavior() {
        // Business logic mixed with animation
        if (getOwner() != null && distanceToSqr(getOwner()) > 100) {
            // Business logic - can be extracted
            setEntityState(EntityState.FOLLOW);
            
            // Animation logic - must stay in loader
            getAnimationController().setAnimation(RawAnimation.begin().thenLoop("walk"));
        }
    }
}

// AFTER: Separated logic (correct)

// Common helper - pure business logic
public class EntityBehaviorHelper {
    public static EntityState calculateDesiredState(LovelyRobotEntity entity) {
        if (entity.getOwner() != null && entity.distanceToSqr(entity.getOwner()) > 100) {
            return EntityState.FOLLOW;
        }
        return entity.getEntityState();
    }
}

// Loader implementation - animation logic
public class LovelyRobotEntity extends LivingEntity implements GeoAnimatable {
    public void updateBehavior() {
        // Use common helper for business logic
        EntityState desiredState = EntityBehaviorHelper.calculateDesiredState(this);
        setEntityState(desiredState);
        
        // Handle animation in loader code
        if (desiredState == EntityState.FOLLOW) {
            getAnimationController().setAnimation(RawAnimation.begin().thenLoop("walk"));
        }
    }
}
```

### 2. Rollback Procedures

#### Emergency Rollback for GeckoLib Violations
```bash
#!/bin/bash
# Emergency rollback for GeckoLib isolation violations

echo "EMERGENCY: Rolling back GeckoLib isolation violation"

# Identify violated files
VIOLATED_FILES=$(find Common/src/ -name "*.java" -exec grep -l "software\.bernie\.geckolib" {} \;)

for FILE in $VIOLATED_FILES; do
    echo "Processing violation in: $FILE"
    
    # Create backup
    cp "$FILE" "$FILE.violation-backup"
    
    # Remove GeckoLib imports (emergency measure)
    sed -i '/software\.bernie\.geckolib/d' "$FILE"
    
    # Comment out GeckoLib usage (emergency measure)
    sed -i 's/\(.*GeoAnimatable.*\)/\/\/ VIOLATION: \1/' "$FILE"
    sed -i 's/\(.*AnimationController.*\)/\/\/ VIOLATION: \1/' "$FILE"
    sed -i 's/\(.*RawAnimation.*\)/\/\/ VIOLATION: \1/' "$FILE"
    
    echo "Emergency cleanup applied to: $FILE"
    echo "Manual review and proper fix required!"
done

echo "EMERGENCY ROLLBACK COMPLETE"
echo "Files require manual review and proper architectural fix"
echo "Backup files created with .violation-backup extension"
```

## Best Practices

### 1. Development Guidelines

#### When Adding New Features
1. **Analyze Dependencies**: Check if feature requires GeckoLib functionality
2. **Separate Concerns**: Identify business logic vs animation logic
3. **Design Interfaces**: Create abstractions for animation triggers if needed
4. **Implement Safely**: Keep GeckoLib code in loaders, business logic in common
5. **Validate Isolation**: Run isolation tests before committing

#### When Modifying Existing Code
1. **Preserve Boundaries**: Don't move GeckoLib code to common
2. **Extract Safely**: Only extract pure business logic
3. **Maintain Functionality**: Ensure animations still work after changes
4. **Test Thoroughly**: Validate both business logic and animations
5. **Document Changes**: Update isolation documentation if needed

### 2. Code Organization

#### Recommended Package Structure
```
Loader/src/main/java/net/msymbios/llovelyr/
├── lib/
│   ├── entity/
│   │   ├── animations/          # GeckoLib animation controllers
│   │   ├── models/              # GeckoLib models
│   │   ├── renderers/           # GeckoLib renderers
│   │   └── layers/              # GeckoLib render layers
│   └── services/                # Platform service implementations
├── shared/
│   ├── entity/                  # Thin entity wrappers (delegate to common)
│   └── item/                    # Thin item wrappers (delegate to common)
└── source/                      # Registration, events, lifecycle

Common/src/main/java/net/msymbios/llovelyr/
├── lib/
│   ├── entity/
│   │   ├── base/                # Abstract base classes (no GeckoLib)
│   │   ├── helpers/             # Entity helper utilities (no GeckoLib)
│   │   ├── data/                # Entity data components (no GeckoLib)
│   │   └── features/            # Entity feature logic (no GeckoLib)
│   └── utils/                   # General utilities (no GeckoLib)
```

### 3. Documentation Requirements

#### Component Documentation
Every component that interacts with the GeckoLib boundary must document:
- What GeckoLib functionality it uses (if any)
- Why certain code must remain in loaders
- How business logic is separated from animation logic
- What abstractions are used to bridge the boundary

#### Change Documentation
Every change that affects the GeckoLib boundary must document:
- What was moved or modified
- Why the change preserves isolation
- How animation functionality is maintained
- What testing was performed to validate isolation

---

**Critical Reminder**: GeckoLib isolation is not optional—it is a fundamental architectural constraint that must be respected at all times. Violating this constraint will compromise the entire extraction architecture and create significant technical debt. When in doubt, keep GeckoLib code in loader modules.