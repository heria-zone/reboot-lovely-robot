---
created: 2025-04-27 09:52
tags:
  - LovelyRobot
---
## Executive Summary

This document outlines the vision for a complete redesign of the LovelyRobot mod while simultaneously creating a robust framework library that can be leveraged by companion mods like Monsters & Girls, Mermaids Secrets, and Fantasy Expansion. The goal is to develop a data-driven, modular architecture with a fluent API that is loader-agnostic, allowing seamless compatibility with Forge, Fabric, NeoForge, and other loaders.

## Core Architecture Vision

### Key Design Principles

1. **Loader-Agnostic Core**: Shared functionality independent of specific mod loaders
2. **Data-Driven Design**: Configuration-based approach to reduce hard-coding
3. **Modular Architecture**: Clearly separated concerns for extensibility 
4. **Fluent API**: Intuitive builder patterns and factories for easy component creation
5. **Object Composition**: Favouring composition over inheritance where appropriate
6. **Progressive Disclosure**: Simple interfaces with advanced customization options

### High-Level Architecture

```
net.msymbios.framework    # (native java gradle, with multi-gradle project)
├── api/                  # Public interfaces for extension
├── core/                 # Core implementation classes 
│   ├── assembly/         # Assembly system components
│   ├── entity/           # Base entity implementations
│   ├── animation/        # Animation handling
│   ├── task/             # Task and AI system
│   └── inventory/        # Inventory management
├── loader/               # Loader-specific bridges (multi-gradle)
│   ├── common/           # Shared loader utilities
│   ├── forge/            # Forge implementation
│   ├── fabric/           # Fabric implementation
│   └── neoforge/         # NeoForge implementation
└── util/                 # Common utilities
```

## Framework Components

### 1. Entity Framework

A modular entity system designed around composition rather than deep inheritance hierarchies:

```java
public final class EntityBuilder<T extends ModEntity> {
    public EntityBuilder<T> withTexture(EntityTexture texture) {...}
    public EntityBuilder<T> withModel(EntityModel model) {...}
    public EntityBuilder<T> withAnimator(EntityAnimator animator) {...}
    public EntityBuilder<T> withAttributes(EntityAttributes attributes) {...}
    public EntityBuilder<T> withBehavior(EntityBehavior behavior) {...}
    public EntityBuilder<T> withSound(EntitySound sound) {...}
    public T build() {...}
}
```

#### Entity Component Types:

- **EntityTexture**: Manages appearance variations and states
- **EntityModel**: Handles 3D model resources and rendering
- **EntityAnimator**: Controls animation states and transitions
- **EntityAttributes**: Manages stats and capabilities
- **EntityBehavior**: Controls AI and interactions
- **EntitySound**: Manages sound effects and conditions
- **EntityState**: Tracks current state (following, defensive, etc.)

### 2. Animation System

An abstracted animation system that works across different animation backends:

```java
public interface AnimationController {
    void registerAnimation(String name, Animation animation);
    void setCurrentAnimation(String name);
    void addTransition(String from, String to, TransitionCondition condition);
    void updateAnimationState(AnimationState state);
}
```

Integrates with GeckoLib initially, but designed for potential future replacement with custom system:

```java
public class GeckoLibAdapter implements AnimationAdapter {
    // Implementation that bridges to GeckoLib
}
```

### 3. Task System

A comprehensive task system inspired by the Blocklings approach but with greater modularity:

```java
public interface Task {
    boolean canStart(TaskContext context);
    TaskResult execute(TaskContext context);
    boolean shouldContinue(TaskContext context);
    void onComplete(TaskContext context);
}

public class TaskRegistry {
    public static Task register(String id, Task task) {...}
    public static Task getTask(String id) {...}
}
```

Tasks are composed into behaviors with priority management:

```java
public class TaskManager {
    public void addTask(Task task, int priority);
    public void removeTask(String taskId);
    public void updateTasks();
}
```

### 4. Assembly System

A flexible assembly system for creating and customizing entities:

```java
public class AssemblyManager {
    public <T extends ModEntity> EntityBuilder<T> createEntity(EntityType<T> type);
    public void registerAssemblyComponent(String id, AssemblyComponent component);
    public AssemblyRecipe createRecipe(String id);
}

public interface AssemblyComponent {
    void applyToEntity(ModEntity entity);
    ItemStack getRepresentingItem();
}
```

### 5. Configuration System

A powerful configuration system that supports both data packs and programmatic config:

```java
public class ConfigManager {
    public <T> T getConfig(String key, Class<T> type);
    public <T> void registerConfig(String key, T defaultValue, ConfigValidator<T> validator);
    public void loadConfigs();
    public void saveConfigs();
}
```

### 6. Registry System

A type-safe registry system that works across mod loaders:

```java
public class Registry<T> {
    public T register(String id, T value);
    public T get(String id);
    public Collection<T> getAll();
}

public class Registries {
    public static final Registry<EntityType<?>> ENTITIES = new Registry<>();
    public static final Registry<ItemType> ITEMS = new Registry<>();
    // Other registries
}
```

## LovelyRobot Implementation

Using the framework components, the LovelyRobot mod will be implemented with the following main features:

### 1. Robot Types and Variants

#### Humanoid Model
- **Dragon**: Combat specialist with high damage
- **Kitsune**: Support specialist with buff capabilities
- **Neko**: Secondary combat specialist with speed focus
- **Bunny/Bunny2.0**: Speed-focused utility robot
- **Vanilla**: General-purpose starter robot
- **Honey**: Resource management specialist

#### Planned Future Models
- **Mermaid**: Water specialist with land/water transformation
- **Arachnic**: Climbing specialist with web capabilities
- **Flora**: Nature-focused with growth capabilities
- **Skulk**: Stealth specialist with vibration sensing
- **Copper**: Redstone specialist with weather reactions

### 2. Assembly System

A tiered system for robot construction:

#### Frame Tiers
| Tier | Material | Slots | Base Stats |
|------|----------|-------|------------|
| 0 | Stone | 1 | 100% |
| 1 | Iron | 2 | 125% |
| 2 | Gold | 3 | 150% |
| 3 | Obsidian | 4 | 175% |
| 4 | Diamond | 5 | 200% |
| 5 | Netherite | 6 | 250% |

#### Core Components
- **Shell**: Determines level cap and base durability
- **Núcleo**: Determines specialization (Speed/Defense/Mining)

#### Assembly Station
- **Basic Tier**: Stone to Iron robots, basic repair
- **Intermediate Tier**: Up to Obsidian robots, gear attachments
- **Advanced Tier**: All robot types, blueprint saving

### 3. Enhancement Systems

#### Slot-Based Enhancements
- **Gears**: Wings, tails, claws (2-3 slots)
- **Enchantments**: Protection, efficiency (1 slot)
- **Special Abilities**: Unique powers (1-2 slots)

#### Level Progression
- Level-based stat increases
- Unlockable abilities at milestone levels
- XP gained through activities

#### Recall System
- Remote robot recovery when damaged
- Tiered recovery stations with varying capabilities
- Repair kits for maintenance

### 4. Task System

Comprehensive AI capabilities:

#### Basic Tasks
- Follow owner
- Guard area
- Collect items
- Store items
- Combat patrol

#### Advanced Tasks
- Resource gathering
- Automatic crafting
- Item sorting
- Complex patrol patterns
- Coordinated group activities

#### Control Interface
- LovelyRemote for direct control
- Terminal for advanced programming
- Blueprint system for saving configurations

## Implementation Roadmap

The development will be broken down into three major updates:

### Update 1: Assembly System

**Focus**: Core framework and robot construction

- Framework foundation and loader bridges
- Basic entity system
- Assembly station functionality
- Frame and core system
- Initial robot variants (Vanilla, Bunny)

### Update 2: Characteristics

**Focus**: Robot capabilities and progression

- Enhanced entity behaviors
- Leveling and XP system
- Robot specializations
- Recall system
- Additional robot variants (Dragon, Kitsune, Neko)

### Update 3: Content

**Focus**: Advanced features and expanded content

- Task programming system
- Advanced enhancement options
- Blueprint sharing
- Additional model types (Mermaid, Arachnic)
- Special ability system

## Framework Integration with Other Mods

### Monsters & Girls Integration

The framework will be used to power the Monsters & Girls mod:

- Entity creation using the EntityBuilder
- Animation system for character movements
- Effect system borrowed from current implementation
- Shared registry and configuration systems

Example integration:

```java
// Monster & Girls implementation using the framework
EntityBuilder<WispEntity> wispBuilder = entityManager.createEntity(EntityTypes.WISP)
    .withTexture(new EntityTexture()
        .addVariant("blue", new Identifier("monsters_girls:textures/entity/wisp/wisp_girl_blue.png"))
        .addVariant("green", new Identifier("monsters_girls:textures/entity/wisp/wisp_girl_green.png"))
        .addVariant("yellow", new Identifier("monsters_girls:textures/entity/wisp/wisp_girl_yellow.png")))
    .withModel(new EntityModel(new Identifier("monsters_girls:geo/wisp_girl.geo.json")))
    .withAnimator(new EntityAnimator(new Identifier("monsters_girls:animations/wisp_girl.animation.json")))
    .withSound(new EntitySound()
        .addSound(EntitySoundType.DEFAULT, MonstersGirlsSounds.WISP_LAUGH)
        .addSound(EntitySoundType.HURT, MonstersGirlsSounds.WISP_HURT)
        .addSound(EntitySoundType.DEATH, MonstersGirlsSounds.WISP_DEATH))
    .withBehavior(new EntityBehavior()
        .addGoal(1, new SitGoal())
        .addGoal(3, new AttackGoal())
        .addGoal(4, new FollowOwnerGoal(0.7f)))
    .withAttributes(new EntityAttributes()
        .setHealth(18.0f)
        .setAttackDamage(3.0f)
        .setMovementSpeed(0.6f));

WispEntity wispEntity = wispBuilder.build();
```

### Fantasy Expansion Integration

The framework will accommodate specialized entities from Fantasy Expansion:

- Custom model system
- Extended animation capabilities
- Specialized effects system
- Shared configuration

### Mermaids Secrets Integration

Specialized water-based features will be built on the framework:

- Transformation mechanics
- Underwater movement
- Special water-based abilities
- Shared registry system

## Technical Implementation Details

### 1. Data-Driven Design

JSON-based configuration for common elements:

```json
{
  "entity_types": {
    "lovely_robot:dragon": {
      "attributes": {
        "max_health": 40.0,
        "attack_damage": 8.0,
        "attack_speed": 1.2,
        "armor": 10.0,
        "armor_toughness": 4.0,
        "movement_speed": 0.23
      },
      "models": {
        "default": "lovely_robot:geo/dragon.geo.json",
        "armed": "lovely_robot:geo/dragon.attack.geo.json"
      },
      "textures": {
        "default": "lovely_robot:textures/entity/dragon/dragon_00.png",
        "variants": [
          "lovely_robot:textures/entity/dragon/dragon_01.png",
          "lovely_robot:textures/entity/dragon/dragon_02.png"
        ]
      },
      "animations": {
        "default": "lovely_robot:animations/default.animation.json"
      }
    }
  }
}
```

### 2. Fluent API Examples

Builder patterns for intuitive component creation:

```java
// Robot creation example
RobotEntity dragon = new RobotBuilder()
    .type(RobotType.DRAGON)
    .frame(FrameMaterial.NETHERITE)
    .core(new CoreBuilder()
        .shell(ShellType.NETHERITE)
        .nucleo(NucleoType.SPEED)
        .build())
    .addEnhancement(Enhancements.MECHANICAL_WINGS)
    .addEnhancement(Enhancements.FIRE_PROTECTION, 3)
    .texture(DragonTexture.RED)
    .name("Inferno")
    .build();

// Task programming example
TaskSequence patrolSequence = new TaskSequenceBuilder()
    .addTask(Tasks.TRAVEL_TO, new BlockPos(100, 64, 200))
    .addTask(Tasks.WAIT, 200) // ticks
    .addTask(Tasks.TRAVEL_TO, new BlockPos(150, 64, 200))
    .addTask(Tasks.WAIT, 200)
    .addTask(Tasks.RETURN_TO_OWNER)
    .setRepeating(true)
    .build();

dragon.getTaskManager().setCurrentTask(patrolSequence);
```

### 3. Factory Pattern Examples

Creating consistent entities and components:

```java
// Entity factory
public class EntityFactory {
    public static RobotEntity createRobot(RobotType type, FrameMaterial frame) {
        switch (type) {
            case DRAGON:
                return new DragonEntity(frame);
            case KITSUNE:
                return new KitsuneEntity(frame);
            // Other cases
            default:
                return new VanillaEntity(frame);
        }
    }
}

// Assembly component factory
public class AssemblyComponentFactory {
    public static Frame createFrame(FrameMaterial material) {
        return new Frame(material);
    }
    
    public static Core createCore(ShellType shell, NucleoType nucleo) {
        return new Core(shell, nucleo);
    }
}
```

### 4. Loader Bridge Implementation

Abstractions for cross-loader compatibility:

```java
public interface LoaderPlatform {
    void registerEntity(String id, EntityType<?> entityType);
    void registerItem(String id, Item item);
    Path getConfigDir();
    boolean isClient();
}

public class ForgePlatform implements LoaderPlatform {
    @Override
    public void registerEntity(String id, EntityType<?> entityType) {
        // Forge-specific registration
    }
    
    // Other implementations
}

public class FabricPlatform implements LoaderPlatform {
    @Override
    public void registerEntity(String id, EntityType<?> entityType) {
        // Fabric-specific registration
    }
    
    // Other implementations
}
```

### 5. Component System Implementation

Composition-based entity capabilities:

```java
public class RobotEntity implements ModEntity {
    private final EntityTexture texture;
    private final EntityModel model;
    private final EntityAnimator animator;
    private final EntityAttributes attributes;
    private final TaskManager taskManager;
    private final InventoryComponent inventory;
    private final Frame frame;
    private final Core core;
    
    // Constructor with all components
    // Getters for components
    // Implementation of entity methods leveraging components
}
```

## Framework Integration Scenarios

### Scenario 1: Adding a New Robot Type

```java
// Register the entity type
EntityType<CustomRobotEntity> CUSTOM_ROBOT = Registry.register(
    Registry.ENTITY_TYPE,
    new Identifier("my_mod", "custom_robot"),
    FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, CustomRobotEntity::new)
        .dimensions(EntityDimensions.fixed(0.6F, 1.8F))
        .build()
);

// Define the robot variant
RobotVariant CUSTOM = RobotVariantRegistry.create("custom_robot")
    .addCombat(25.0F, 5.0F, 1.3F, 6.0F, 2.0F, 0.1F, 0.25F)
    .addTextures(false, EntityTexture.DEFAULT, EntityTexture.TUMMY)
    .addAnimations(EntityAnimation.IDLE, EntityAnimation.WALK, EntityAnimation.ATTACK)
    .addSounds(SoundEvents.ENTITY_GENERIC_HURT, SoundEvents.ENTITY_GENERIC_DEATH)
    .build();

// Create the entity class
public class CustomRobotEntity extends RobotEntity {
    public CustomRobotEntity(EntityType<? extends CustomRobotEntity> entityType, World world) {
        super(entityType, world);
        this.nativeEntity = RobotVariant.CUSTOM;
    }
    
    @Override
    protected void initGoals() {
        super.initGoals();
        // Add custom goals
    }
}
```

### Scenario 2: Creating a New Effect for Monsters & Girls

```java
// Register the effect
StatusEffect CUSTOM_EFFECT = EffectRegistry.register(
    "custom_effect",
    new CustomEffect(StatusEffectCategory.BENEFICIAL, 0x45F2A3)
);

// Create the effect class
public class CustomEffect extends StatusEffect {
    public CustomEffect(StatusEffectCategory category, int color) {
        super(category, color);
    }
    
    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true;
    }
    
    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        if (entity == null || entity.getWorld().isClient()) return;
        
        // Custom effect logic
        entity.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 10, amplifier));
        
        super.applyUpdateEffect(entity, amplifier);
    }
}
```

### Scenario 3: Implementing Custom Tasks

```java
// Register the task
Task CUSTOM_TASK = TaskRegistry.register("custom_task", new CustomTask());

// Create the task class
public class CustomTask implements Task {
    @Override
    public boolean canStart(TaskContext context) {
        ModEntity entity = context.getEntity();
        return entity.isOwnerNearby() && entity.getHealth() < entity.getMaxHealth() * 0.5;
    }
    
    @Override
    public TaskResult execute(TaskContext context) {
        ModEntity entity = context.getEntity();
        entity.getNavigation().startMovingTo(entity.getOwner(), 1.0);
        
        // If reached owner
        if (entity.squaredDistanceTo(entity.getOwner()) < 4.0) {
            // Do something when reaching owner
            return TaskResult.SUCCESS;
        }
        
        return TaskResult.RUNNING;
    }
    
    @Override
    public boolean shouldContinue(TaskContext context) {
        return !context.getEntity().getNavigation().isIdle();
    }
    
    @Override
    public void onComplete(TaskContext context) {
        // Cleanup any task resources
    }
}
```