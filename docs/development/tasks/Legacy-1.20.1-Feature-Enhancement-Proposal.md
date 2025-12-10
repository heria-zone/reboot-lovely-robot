---
created: 2025-11-30
status: Proposal
tags:
  - Legacy
  - Enhancement
  - Architecture
  - 1.20.1
  - Forge
  - Fabric
---

# Legacy 1.20.1 Feature Enhancement Proposal
## Combat Level System, Data Management Refactoring, Robot Registry, and Command System Overhaul

## Executive Summary

This proposal outlines a comprehensive enhancement to the Legacy 1.20.1 LovelyRobot mod (both Forge and Fabric) to introduce:

1. **CombatLevelFeature** - Level-based attribute calculation system
2. **Data Management Refactoring** - Separate stat classes with NBT serialization
3. **Robot Registry System** - Owner-based robot tracking with spawn limits
4. **Command System Overhaul** - Improved command structure with validation

**Architecture Alignment**: All changes follow the established HZ Framework → HZ Lib → Common → Source pattern, ensuring future extractability and maintaining consistency with the HZLib EntityType Architecture.

**Scope**: Legacy 1.20.1 (Forge & Fabric) with parallel implementation to minimize refactoring when extracting to external libraries.

## Problem Statement

### Current Limitations

1. **InternalLogic Monolith**: All attribute calculations are static methods in InternalLogic, not following the feature-based architecture
2. **Scattered Data Management**: Level, HP, attack, defense, protections are managed directly in entity classes without proper encapsulation
3. **No Robot Tracking**: No system to track active robots per player or enforce spawn limits
4. **Command Validation Issues**: Commands don't validate against config limits (max level, max protections)
5. **Missing Features**: No enchantment data, no structured protection data, no centralized stat management

### Architectural Inconsistency

The current InternalLogic approach doesn't align with the feature-based architecture established in HZLib EntityType system. We need to convert these calculations into proper features that can be:
- Attached to entity types
- Configured per robot variant
- Tested independently
- Reused across mods

## Proposed Solution


### 1. CombatLevelFeature - Level-Based Attribute Calculation

**Location**: 
- Framework: `net.msymbios.llovelyr.framework.entity.combat/` (pure Java)
- Lib: `net.msymbios.llovelyr.lib.entity.type.features/CombatLevelFeature.java`

**Purpose**: Convert InternalLogic calculations into a composable feature with strategy pattern support.

#### Architecture

```java
// Framework Layer - Pure calculation strategies
public interface AttributeCalculationStrategy {
    int calculateHp(int level, int baseValue);
    int calculateAttack(int level, int baseValue);
    int calculateDefense(int level, int baseValue);
    double calculateArmor(int defense);
    double calculateArmorToughness(double armorLevel);
}

// Default strategy (current InternalLogic formula)
public class LinearAttributeStrategy implements AttributeCalculationStrategy {
    @Override
    public int calculateHp(int level, int baseValue) {
        return baseValue + level * baseValue / 50; // 2% per level
    }
    
    @Override
    public int calculateAttack(int level, int baseValue) {
        return baseValue + level * baseValue / 50;
    }
    
    @Override
    public int calculateDefense(int level, int baseValue) {
        return baseValue + level * baseValue / 50;
    }
    
    @Override
    public double calculateArmor(int defense) {
        return Math.min(defense, 30); // Hard cap at 30
    }
    
    @Override
    public double calculateArmorToughness(double armorLevel) {
        return Math.max(0, armorLevel - 30); // Overflow becomes toughness
    }
}

// Lib Layer - Feature implementation
public class CombatLevelFeature {
    private AttributeCalculationStrategy strategy;
    private int baseHp;
    private int baseAttack;
    private int baseDefense;
    
    public CombatLevelFeature(int baseHp, int baseAttack, int baseDefense) {
        this(baseHp, baseAttack, baseDefense, new LinearAttributeStrategy());
    }
    
    public CombatLevelFeature(int baseHp, int baseAttack, int baseDefense, 
                             AttributeCalculationStrategy strategy) {
        this.baseHp = baseHp;
        this.baseAttack = baseAttack;
        this.baseDefense = baseDefense;
        this.strategy = strategy;
    }
    
    public int calculateHp(int level) {
        return strategy.calculateHp(level, baseHp);
    }
    
    public int calculateAttack(int level) {
        return strategy.calculateAttack(level, baseAttack);
    }
    
    public int calculateDefense(int level) {
        return strategy.calculateDefense(level, baseDefense);
    }
    
    public double calculateArmor(int level) {
        int defense = calculateDefense(level);
        return strategy.calculateArmor(defense);
    }
    
    public double calculateArmorToughness(int level) {
        int defense = calculateDefense(level);
        double armor = strategy.calculateArmor(defense);
        return strategy.calculateArmorToughness(armor);
    }
    
    // Setters for runtime config changes
    public void setBaseHp(int baseHp) { this.baseHp = baseHp; }
    public void setBaseAttack(int baseAttack) { this.baseAttack = baseAttack; }
    public void setBaseDefense(int baseDefense) { this.baseDefense = baseDefense; }
    public void setStrategy(AttributeCalculationStrategy strategy) { this.strategy = strategy; }
}
```


#### Integration with RobotEntityType

```java
// In NativeRobotType.java
public static final RobotEntityType VANILLA = create("vanilla")
    .withMaxLevel(LovelyConfigs.VanillaMaxLevel)
    .withCombatStats(/* ... */)
    .withColorPalette(EntityVariant.VANILLA)
    .withFeature(CombatLevelFeature.class, new CombatLevelFeature(
        LovelyConfigs.VanillaBaseHp,
        LovelyConfigs.VanillaBaseAttack,
        LovelyConfigs.VanillaBaseDefense
    ));

// Dragon with different strategy (exponential scaling)
public static final RobotEntityType DRAGON = create("dragon")
    .withMaxLevel(LovelyConfigs.DragonMaxLevel)
    .withCombatStats(/* ... */)
    .withColorPalette(EntityVariant.DRAGON)
    .withFeature(CombatLevelFeature.class, new CombatLevelFeature(
        LovelyConfigs.DragonBaseHp,
        LovelyConfigs.DragonBaseAttack,
        LovelyConfigs.DragonBaseDefense,
        new ExponentialAttributeStrategy(1.05) // 5% per level instead of 2%
    ));
```

#### Migration from InternalLogic

**Phase 1**: Create CombatLevelFeature alongside InternalLogic
**Phase 2**: Update entity classes to use feature instead of static methods
**Phase 3**: Deprecate InternalLogic methods
**Phase 4**: Remove InternalLogic after validation

### 2. EnchantmentFeature & ProtectionFeature

**Location**:
- Framework: `net.msymbios.llovelyr.framework.entity.enchantment/` (pure Java)
- Framework: `net.msymbios.llovelyr.framework.entity.protection/` (pure Java)
- Lib: `net.msymbios.llovelyr.lib.entity.type.features/` (feature implementations)

**Purpose**: Convert enchantment and protection logic into composable features following the same pattern as CombatLevelFeature.

#### EnchantmentFeature Architecture

```java
// Framework Layer - Enchantment calculation strategy
public interface EnchantmentCalculationStrategy {
    int calculateLooting(int level);
    int calculateSharpness(int level);
    int calculateKnockback(int level);
    // Future enchantments can be added
}

// Default strategy (current InternalLogic formula)
public class DefaultEnchantmentStrategy implements EnchantmentCalculationStrategy {
    @Override
    public int calculateLooting(int level) {
        if (!LovelyConfigs.LootEnchantment) return 0;
        int enchantmentLevel = level / LovelyConfigs.LootEnchantmentLevel;
        return Math.min(enchantmentLevel, LovelyConfigs.MaxLootEnchantment);
    }
    
    @Override
    public int calculateSharpness(int level) {
        // Future implementation
        return 0;
    }
    
    @Override
    public int calculateKnockback(int level) {
        // Future implementation
        return 0;
    }
}

// Lib Layer - Feature implementation
public class EnchantmentFeature {
    private EnchantmentCalculationStrategy strategy;
    private boolean lootingEnabled;
    private int maxLootingLevel;
    private int lootingLevelDivisor;
    
    public EnchantmentFeature(boolean lootingEnabled, int maxLootingLevel, int lootingLevelDivisor) {
        this(lootingEnabled, maxLootingLevel, lootingLevelDivisor, new DefaultEnchantmentStrategy());
    }
    
    public EnchantmentFeature(boolean lootingEnabled, int maxLootingLevel, int lootingLevelDivisor,
                             EnchantmentCalculationStrategy strategy) {
        this.lootingEnabled = lootingEnabled;
        this.maxLootingLevel = maxLootingLevel;
        this.lootingLevelDivisor = lootingLevelDivisor;
        this.strategy = strategy;
    }
    
    public int calculateLooting(int robotLevel) {
        if (!lootingEnabled) return 0;
        return strategy.calculateLooting(robotLevel);
    }
    
    public int calculateSharpness(int robotLevel) {
        return strategy.calculateSharpness(robotLevel);
    }
    
    public int calculateKnockback(int robotLevel) {
        return strategy.calculateKnockback(robotLevel);
    }
    
    // Setters for runtime config changes
    public void setLootingEnabled(boolean enabled) { this.lootingEnabled = enabled; }
    public void setMaxLootingLevel(int max) { this.maxLootingLevel = max; }
    public void setLootingLevelDivisor(int divisor) { this.lootingLevelDivisor = divisor; }
    public void setStrategy(EnchantmentCalculationStrategy strategy) { this.strategy = strategy; }
}
```

#### ProtectionFeature Architecture

```java
// Framework Layer - Protection calculation strategy
public interface ProtectionCalculationStrategy {
    int calculateFireProtection(int level, int currentProtection);
    int calculateFallProtection(int level, int currentProtection);
    int calculateBlastProtection(int level, int currentProtection);
    int calculateProjectileProtection(int level, int currentProtection);
}

// Default strategy (level-based unlock)
public class LevelBasedProtectionStrategy implements ProtectionCalculationStrategy {
    @Override
    public int calculateFireProtection(int level, int currentProtection) {
        // Unlock at specific levels
        if (level >= 50 && currentProtection < 1) return 1;
        if (level >= 100 && currentProtection < 2) return 2;
        if (level >= 150 && currentProtection < 3) return 3;
        return currentProtection;
    }
    
    @Override
    public int calculateFallProtection(int level, int currentProtection) {
        if (level >= 40 && currentProtection < 1) return 1;
        if (level >= 90 && currentProtection < 2) return 2;
        if (level >= 140 && currentProtection < 3) return 3;
        return currentProtection;
    }
    
    @Override
    public int calculateBlastProtection(int level, int currentProtection) {
        if (level >= 60 && currentProtection < 1) return 1;
        if (level >= 110 && currentProtection < 2) return 2;
        if (level >= 160 && currentProtection < 3) return 3;
        return currentProtection;
    }
    
    @Override
    public int calculateProjectileProtection(int level, int currentProtection) {
        if (level >= 45 && currentProtection < 1) return 1;
        if (level >= 95 && currentProtection < 2) return 2;
        if (level >= 145 && currentProtection < 3) return 3;
        return currentProtection;
    }
}

// Lib Layer - Feature implementation
public class ProtectionFeature {
    private ProtectionCalculationStrategy strategy;
    private int maxFireProtection;
    private int maxFallProtection;
    private int maxBlastProtection;
    private int maxProjectileProtection;
    
    public ProtectionFeature(int maxFire, int maxFall, int maxBlast, int maxProjectile) {
        this(maxFire, maxFall, maxBlast, maxProjectile, new LevelBasedProtectionStrategy());
    }
    
    public ProtectionFeature(int maxFire, int maxFall, int maxBlast, int maxProjectile,
                            ProtectionCalculationStrategy strategy) {
        this.maxFireProtection = maxFire;
        this.maxFallProtection = maxFall;
        this.maxBlastProtection = maxBlast;
        this.maxProjectileProtection = maxProjectile;
        this.strategy = strategy;
    }
    
    public boolean canUpgradeFireProtection(int currentLevel) {
        return currentLevel < maxFireProtection;
    }
    
    public boolean canUpgradeFallProtection(int currentLevel) {
        return currentLevel < maxFallProtection;
    }
    
    public boolean canUpgradeBlastProtection(int currentLevel) {
        return currentLevel < maxBlastProtection;
    }
    
    public boolean canUpgradeProjectileProtection(int currentLevel) {
        return currentLevel < maxProjectileProtection;
    }
    
    public int calculateAutoFireProtection(int robotLevel, int currentProtection) {
        return strategy.calculateFireProtection(robotLevel, currentProtection);
    }
    
    public int calculateAutoFallProtection(int robotLevel, int currentProtection) {
        return strategy.calculateFallProtection(robotLevel, currentProtection);
    }
    
    public int calculateAutoBlastProtection(int robotLevel, int currentProtection) {
        return strategy.calculateBlastProtection(robotLevel, currentProtection);
    }
    
    public int calculateAutoProjectileProtection(int robotLevel, int currentProtection) {
        return strategy.calculateProjectileProtection(robotLevel, currentProtection);
    }
    
    // Getters
    public int getMaxFireProtection() { return maxFireProtection; }
    public int getMaxFallProtection() { return maxFallProtection; }
    public int getMaxBlastProtection() { return maxBlastProtection; }
    public int getMaxProjectileProtection() { return maxProjectileProtection; }
    
    // Setters for runtime config changes
    public void setMaxFireProtection(int max) { this.maxFireProtection = max; }
    public void setMaxFallProtection(int max) { this.maxFallProtection = max; }
    public void setMaxBlastProtection(int max) { this.maxBlastProtection = max; }
    public void setMaxProjectileProtection(int max) { this.maxProjectileProtection = max; }
    public void setStrategy(ProtectionCalculationStrategy strategy) { this.strategy = strategy; }
}
```

#### Integration with RobotEntityType

```java
// In NativeRobotType.java
public static final RobotEntityType VANILLA = create("vanilla")
    .withMaxLevel(LovelyConfigs.VanillaMaxLevel)
    .withCombatStats(/* ... */)
    .withColorPalette(EntityVariant.VANILLA)
    .withFeature(CombatLevelFeature.class, new CombatLevelFeature(
        LovelyConfigs.VanillaBaseHp,
        LovelyConfigs.VanillaBaseAttack,
        LovelyConfigs.VanillaBaseDefense
    ))
    .withFeature(EnchantmentFeature.class, new EnchantmentFeature(
        LovelyConfigs.LootEnchantment,
        LovelyConfigs.MaxLootEnchantment,
        LovelyConfigs.LootEnchantmentLevel
    ))
    .withFeature(ProtectionFeature.class, new ProtectionFeature(
        LovelyConfigs.ProtectionLimitFire,
        LovelyConfigs.ProtectionLimitFall,
        LovelyConfigs.ProtectionLimitBlast,
        LovelyConfigs.ProtectionLimitProjectile
    ));

// Dragon with different protection limits (higher)
public static final RobotEntityType DRAGON = create("dragon")
    .withMaxLevel(LovelyConfigs.DragonMaxLevel)
    .withCombatStats(/* ... */)
    .withColorPalette(EntityVariant.DRAGON)
    .withFeature(CombatLevelFeature.class, new CombatLevelFeature(
        LovelyConfigs.DragonBaseHp,
        LovelyConfigs.DragonBaseAttack,
        LovelyConfigs.DragonBaseDefense,
        new ExponentialAttributeStrategy(1.05)
    ))
    .withFeature(EnchantmentFeature.class, new EnchantmentFeature(
        LovelyConfigs.LootEnchantment,
        LovelyConfigs.MaxLootEnchantment + 2, // Dragons get +2 max looting
        LovelyConfigs.LootEnchantmentLevel
    ))
    .withFeature(ProtectionFeature.class, new ProtectionFeature(
        LovelyConfigs.ProtectionLimitFire + 2, // Dragons get +2 fire protection
        LovelyConfigs.ProtectionLimitFall,
        LovelyConfigs.ProtectionLimitBlast + 1,
        LovelyConfigs.ProtectionLimitProjectile
    ));
```

### 3. Data Management Refactoring

**Location**: 
- Framework: `net.msymbios.llovelyr.framework.entity.data/` (pure Java data classes)
- Lib: `net.msymbios.llovelyr.lib.entity.data/` (NBT serialization implementations)

**Purpose**: Separate concerns by creating dedicated stat classes that handle their own data persistence.

#### New Data Classes

```java
// Framework Layer - Pure data containers
public class CombatStats {
    private int level;
    private int experience;
    private int currentHp;
    private int maxHp;
    private int attack;
    private int defense;
    
    // Getters and setters
}

public class ProtectionStats {
    private int fireProtection;
    private int fallProtection;
    private int blastProtection;
    private int projectileProtection;
    
    // Getters, setters, and validation
    public boolean canUpgradeFire(int maxLevel) {
        return fireProtection < maxLevel;
    }
}

public class EnchantmentStats {
    private int lootingLevel;
    private int sharpnessLevel;
    private int knockbackLevel;
    // Future enchantments
    
    // Getters and setters
}

// Lib Layer - NBT serialization
public class CombatStatsNBT implements IReadWriteNBT {
    private final CombatStats stats;
    
    public CombatStatsNBT(CombatStats stats) {
        this.stats = stats;
    }
    
    @Override
    public void readFromNBT(CompoundTag nbt) {
        stats.setLevel(nbt.getInt("Level"));
        stats.setExperience(nbt.getInt("Experience"));
        stats.setCurrentHp(nbt.getInt("CurrentHp"));
        stats.setMaxHp(nbt.getInt("MaxHp"));
        stats.setAttack(nbt.getInt("Attack"));
        stats.setDefense(nbt.getInt("Defense"));
    }
    
    @Override
    public void writeToNBT(CompoundTag nbt) {
        nbt.putInt("Level", stats.getLevel());
        nbt.putInt("Experience", stats.getExperience());
        nbt.putInt("CurrentHp", stats.getCurrentHp());
        nbt.putInt("MaxHp", stats.getMaxHp());
        nbt.putInt("Attack", stats.getAttack());
        nbt.putInt("Defense", stats.getDefense());
    }
}

public class ProtectionStatsNBT implements IReadWriteNBT {
    private final ProtectionStats stats;
    
    public ProtectionStatsNBT(ProtectionStats stats) {
        this.stats = stats;
    }
    
    @Override
    public void readFromNBT(CompoundTag nbt) {
        stats.setFireProtection(nbt.getInt("FireProtection"));
        stats.setFallProtection(nbt.getInt("FallProtection"));
        stats.setBlastProtection(nbt.getInt("BlastProtection"));
        stats.setProjectileProtection(nbt.getInt("ProjectileProtection"));
    }
    
    @Override
    public void writeToNBT(CompoundTag nbt) {
        nbt.putInt("FireProtection", stats.getFireProtection());
        nbt.putInt("FallProtection", stats.getFallProtection());
        nbt.putInt("BlastProtection", stats.getBlastProtection());
        nbt.putInt("ProjectileProtection", stats.getProjectileProtection());
    }
}

public class EnchantmentStatsNBT implements IReadWriteNBT {
    private final EnchantmentStats stats;
    
    public EnchantmentStatsNBT(EnchantmentStats stats) {
        this.stats = stats;
    }
    
    @Override
    public void readFromNBT(CompoundTag nbt) {
        stats.setLootingLevel(nbt.getInt("Looting"));
        stats.setSharpnessLevel(nbt.getInt("Sharpness"));
        stats.setKnockbackLevel(nbt.getInt("Knockback"));
    }
    
    @Override
    public void writeToNBT(CompoundTag nbt) {
        nbt.putInt("Looting", stats.getLootingLevel());
        nbt.putInt("Sharpness", stats.getSharpnessLevel());
        nbt.putInt("Knockback", stats.getKnockbackLevel());
    }
}
```


#### Integration with InternalEntity

```java
public abstract class InternalEntity extends TamableAnimal {
    // Replace scattered fields with stat objects
    protected CombatStats combatStats;
    protected ProtectionStats protectionStats;
    protected EnchantmentStats enchantmentStats;
    
    // NBT handlers
    protected CombatStatsNBT combatStatsNBT;
    protected ProtectionStatsNBT protectionStatsNBT;
    protected EnchantmentStatsNBT enchantmentStatsNBT;
    
    protected InternalEntity(EntityType<? extends TamableAnimal> entityType, 
                           Level world, 
                           InternalEntityType<?> nativeEntityType) {
        super(entityType, world);
        this.nativeEntity = nativeEntityType;
        
        // Initialize stat objects
        this.combatStats = new CombatStats();
        this.protectionStats = new ProtectionStats();
        this.enchantmentStats = new EnchantmentStats();
        
        // Initialize NBT handlers
        this.combatStatsNBT = new CombatStatsNBT(combatStats);
        this.protectionStatsNBT = new ProtectionStatsNBT(protectionStats);
        this.enchantmentStatsNBT = new EnchantmentStatsNBT(enchantmentStats);
    }
    
    // Simplified accessors
    public int getLevel() { return combatStats.getLevel(); }
    public void setLevel(int level) { 
        combatStats.setLevel(level);
        recalculateAttributes();
    }
    
    public int getFireProtection() { return protectionStats.getFireProtection(); }
    public void setFireProtection(int level) { protectionStats.setFireProtection(level); }
    
    public int getLootingLevel() { return enchantmentStats.getLootingLevel(); }
    public void setLootingLevel(int level) { enchantmentStats.setLootingLevel(level); }
    
    // Centralized attribute recalculation
    protected void recalculateAttributes() {
        int level = combatStats.getLevel();
        
        // Combat stats from CombatLevelFeature
        if (nativeEntity.hasFeature(CombatLevelFeature.class)) {
            CombatLevelFeature combat = nativeEntity.getFeature(CombatLevelFeature.class).get();
            
            combatStats.setMaxHp(combat.calculateHp(level));
            combatStats.setAttack(combat.calculateAttack(level));
            combatStats.setDefense(combat.calculateDefense(level));
            
            // Apply to entity attributes
            updateEntityAttribute(Attributes.MAX_HEALTH, combatStats.getMaxHp());
            updateEntityAttribute(Attributes.ATTACK_DAMAGE, combatStats.getAttack());
            updateEntityAttribute(Attributes.ARMOR, combat.calculateArmor(level));
            updateEntityAttribute(Attributes.ARMOR_TOUGHNESS, combat.calculateArmorToughness(level));
        }
        
        // Enchantments from EnchantmentFeature
        if (nativeEntity.hasFeature(EnchantmentFeature.class)) {
            EnchantmentFeature enchantment = nativeEntity.getFeature(EnchantmentFeature.class).get();
            
            enchantmentStats.setLootingLevel(enchantment.calculateLooting(level));
            enchantmentStats.setSharpnessLevel(enchantment.calculateSharpness(level));
            enchantmentStats.setKnockbackLevel(enchantment.calculateKnockback(level));
        }
        
        // Auto-upgrade protections from ProtectionFeature (optional)
        if (nativeEntity.hasFeature(ProtectionFeature.class)) {
            ProtectionFeature protection = nativeEntity.getFeature(ProtectionFeature.class).get();
            
            // Only auto-upgrade if enabled in config
            if (LovelyConfigs.AutoUpgradeProtections) {
                int newFire = protection.calculateAutoFireProtection(level, protectionStats.getFireProtection());
                int newFall = protection.calculateAutoFallProtection(level, protectionStats.getFallProtection());
                int newBlast = protection.calculateAutoBlastProtection(level, protectionStats.getBlastProtection());
                int newProjectile = protection.calculateAutoProjectileProtection(level, protectionStats.getProjectileProtection());
                
                if (newFire > protectionStats.getFireProtection()) {
                    protectionStats.setFireProtection(newFire);
                }
                if (newFall > protectionStats.getFallProtection()) {
                    protectionStats.setFallProtection(newFall);
                }
                if (newBlast > protectionStats.getBlastProtection()) {
                    protectionStats.setBlastProtection(newBlast);
                }
                if (newProjectile > protectionStats.getProjectileProtection()) {
                    protectionStats.setProjectileProtection(newProjectile);
                }
            }
        }
    }
    
    @Override
    public void readAdditionalSaveData(CompoundTag nbt) {
        super.readAdditionalSaveData(nbt);
        combatStatsNBT.readFromNBT(nbt);
        protectionStatsNBT.readFromNBT(nbt);
        enchantmentStatsNBT.readFromNBT(nbt);
        recalculateAttributes();
    }
    
    @Override
    public void addAdditionalSaveData(CompoundTag nbt) {
        super.addAdditionalSaveData(nbt);
        combatStatsNBT.writeToNBT(nbt);
        protectionStatsNBT.writeToNBT(nbt);
        enchantmentStatsNBT.writeToNBT(nbt);
    }
}
```

### 3. Robot Registry System

**Location**: 
- Lib: `net.msymbios.llovelyr.lib.registry/` (Minecraft integration - moved from Framework)
- Common: `net.msymbios.llovelyr.common.registry/` (mod-specific)

**Purpose**: Track active robots per player, enforce spawn limits, and provide query capabilities.

**Design Decision**: Store `WeakReference<LivingEntity>` instead of cached data for:
- **Live Data Access**: Position, health, stats always current
- **No Synchronization**: No need to update cached values
- **Memory Safety**: Weak references prevent memory leaks
- **Future Features**: Enables teleport, recall, heal, and other entity operations
- **Simplicity**: Less code, fewer bugs

#### Architecture

```java
// Lib Layer - Minecraft-aware registry (moved from Framework)
public class RobotRegistryEntry {
    private final UUID robotId;
    private final UUID ownerId;
    private final WeakReference<LivingEntity> entityRef; // Weak reference to avoid memory leaks
    private String robotType;
    private long lastUpdate;
    
    public RobotRegistryEntry(UUID robotId, UUID ownerId, LivingEntity entity, String robotType) {
        this.robotId = robotId;
        this.ownerId = ownerId;
        this.entityRef = new WeakReference<>(entity);
        this.robotType = robotType;
        this.lastUpdate = System.currentTimeMillis();
    }
    
    /**
     * Gets the living entity reference if still valid.
     * <p>
     * <b>Weak Reference:</b> Returns null if entity was garbage collected or unloaded.
     * Always check for null before using.
     * 
     * @return the entity or null if no longer valid
     */
    public LivingEntity getEntity() {
        return entityRef.get();
    }
    
    /**
     * Checks if entity reference is still valid.
     * 
     * @return true if entity exists and is alive
     */
    public boolean isEntityValid() {
        LivingEntity entity = entityRef.get();
        return entity != null && entity.isAlive() && !entity.isRemoved();
    }
    
    /**
     * Gets current position from entity if available.
     * <p>
     * <b>Live Data:</b> Always returns current position, not cached.
     * 
     * @return position array [x, y, z] or null if entity invalid
     */
    public double[] getPosition() {
        LivingEntity entity = entityRef.get();
        if (entity != null) {
            return new double[]{entity.getX(), entity.getY(), entity.getZ()};
        }
        return null;
    }
    
    /**
     * Gets current dimension from entity if available.
     * 
     * @return dimension key or null if entity invalid
     */
    public String getDimension() {
        LivingEntity entity = entityRef.get();
        if (entity != null) {
            return entity.level().dimension().location().toString();
        }
        return null;
    }
    
    /**
     * Gets robot name from entity if available.
     * 
     * @return custom name or null if entity invalid or unnamed
     */
    public String getRobotName() {
        LivingEntity entity = entityRef.get();
        if (entity != null && entity.hasCustomName()) {
            return entity.getCustomName().getString();
        }
        return null;
    }
    
    /**
     * Gets current health from entity if available.
     * 
     * @return health value or -1 if entity invalid
     */
    public float getHealth() {
        LivingEntity entity = entityRef.get();
        return entity != null ? entity.getHealth() : -1;
    }
    
    /**
     * Gets max health from entity if available.
     * 
     * @return max health value or -1 if entity invalid
     */
    public float getMaxHealth() {
        LivingEntity entity = entityRef.get();
        return entity != null ? entity.getMaxHealth() : -1;
    }
    
    // Getters
    public UUID getRobotId() { return robotId; }
    public UUID getOwnerId() { return ownerId; }
    public String getRobotType() { return robotType; }
    public long getLastUpdate() { return lastUpdate; }
    
    public void updateTimestamp() {
        this.lastUpdate = System.currentTimeMillis();
    }
}

public class OwnerRobotRegistry {
    private final Map<UUID, Set<RobotRegistryEntry>> ownerToRobots = new ConcurrentHashMap<>();
    private final Map<UUID, RobotRegistryEntry> robotIdToEntry = new ConcurrentHashMap<>();
    
    public boolean canSpawnRobot(UUID ownerId, int maxRobots) {
        return getRobotCount(ownerId) < maxRobots;
    }
    
    public int getRobotCount(UUID ownerId) {
        return ownerToRobots.getOrDefault(ownerId, Collections.emptySet()).size();
    }
    
    public void registerRobot(RobotRegistryEntry entry) {
        ownerToRobots.computeIfAbsent(entry.getOwnerId(), k -> new HashSet<>()).add(entry);
        robotIdToEntry.put(entry.getRobotId(), entry);
    }
    
    public void unregisterRobot(UUID robotId) {
        RobotRegistryEntry entry = robotIdToEntry.remove(robotId);
        if (entry != null) {
            Set<RobotRegistryEntry> robots = ownerToRobots.get(entry.getOwnerId());
            if (robots != null) {
                robots.remove(entry);
                if (robots.isEmpty()) {
                    ownerToRobots.remove(entry.getOwnerId());
                }
            }
        }
    }
    
    public List<RobotRegistryEntry> getRobotsForOwner(UUID ownerId) {
        return new ArrayList<>(ownerToRobots.getOrDefault(ownerId, Collections.emptySet()));
    }
    
    /**
     * Gets registry entry for specific robot.
     * 
     * @param robotId the robot's UUID
     * @return registry entry or null if not found
     */
    public RobotRegistryEntry getEntry(UUID robotId) {
        return robotIdToEntry.get(robotId);
    }
    
    /**
     * Cleans up invalid entries (entities that were unloaded/removed).
     * <p>
     * <b>Maintenance:</b> Call periodically to prevent memory leaks from
     * stale weak references.
     */
    public void cleanupInvalidEntries() {
        List<UUID> toRemove = new ArrayList<>();
        
        for (Map.Entry<UUID, RobotRegistryEntry> entry : robotIdToEntry.entrySet()) {
            if (!entry.getValue().isEntityValid()) {
                toRemove.add(entry.getKey());
            }
        }
        
        for (UUID robotId : toRemove) {
            unregisterRobot(robotId);
        }
    }
}
```


#### Lib Layer - Minecraft Integration

```java
// Lib Layer - Server-side registry manager
public class RobotRegistryManager {
    private static final Map<String, OwnerRobotRegistry> serverRegistries = new ConcurrentHashMap<>();
    
    public static OwnerRobotRegistry getRegistry(ServerLevel level) {
        String serverId = level.getServer().getServerDirectory().toString();
        return serverRegistries.computeIfAbsent(serverId, k -> new OwnerRobotRegistry());
    }
    
    public static void clearRegistry(String serverId) {
        serverRegistries.remove(serverId);
    }
}
```

#### Common Layer - Robot Entity Integration

```java
public abstract class InternalEntity extends TamableAnimal {
    
    @Override
    public void tick() {
        super.tick();
        
        // Update registry timestamp every 20 ticks (1 second)
        // Position is always live via entity reference
        if (!level().isClientSide && tickCount % 20 == 0) {
            updateRegistryTimestamp();
        }
    }
    
    protected void updateRegistryTimestamp() {
        if (getOwnerUUID() != null && level() instanceof ServerLevel serverLevel) {
            OwnerRobotRegistry registry = RobotRegistryManager.getRegistry(serverLevel);
            RobotRegistryEntry entry = registry.getEntry(getUUID());
            if (entry != null) {
                entry.updateTimestamp();
            }
        }
    }
    
    @Override
    public void setTame(boolean tamed) {
        super.setTame(tamed);
        
        if (tamed && !level().isClientSide && getOwnerUUID() != null) {
            registerRobot();
        }
    }
    
    protected void registerRobot() {
        if (level() instanceof ServerLevel serverLevel) {
            OwnerRobotRegistry registry = RobotRegistryManager.getRegistry(serverLevel);
            
            RobotRegistryEntry entry = new RobotRegistryEntry(
                getUUID(),
                getOwnerUUID(),
                this, // Pass entity reference
                nativeEntity.getKey()
            );
            
            registry.registerRobot(entry);
        }
    }
    
    @Override
    public void remove(RemovalReason reason) {
        if (!level().isClientSide && getOwnerUUID() != null) {
            unregisterRobot();
        }
        super.remove(reason);
    }
    
    protected void unregisterRobot() {
        if (level() instanceof ServerLevel serverLevel) {
            OwnerRobotRegistry registry = RobotRegistryManager.getRegistry(serverLevel);
            registry.unregisterRobot(getUUID());
        }
    }
}
```

#### Spawn Item Integration

```java
// In robot spawn item class
@Override
public InteractionResult useOn(UseOnContext context) {
    Level level = context.getLevel();
    
    if (!level.isClientSide && context.getPlayer() != null) {
        Player player = context.getPlayer();
        ServerLevel serverLevel = (ServerLevel) level;
        OwnerRobotRegistry registry = RobotRegistryManager.getRegistry(serverLevel);
        
        // Check spawn limit
        int maxRobots = LovelyConfigs.MaxOwnerRobots;
        if (!registry.canSpawnRobot(player.getUUID(), maxRobots)) {
            player.displayClientMessage(
                Component.translatable("message.llovelyr.max_robots_reached", maxRobots),
                true
            );
            return InteractionResult.FAIL;
        }
        
        // Spawn robot and register
        InternalEntity robot = spawnRobot(context);
        if (robot != null) {
            robot.setTame(true);
            robot.setOwnerUUID(player.getUUID());
            // Registration happens automatically in setTame()
            
            return InteractionResult.SUCCESS;
        }
    }
    
    return InteractionResult.PASS;
}
```

### 4. Command System Overhaul

**Location**: `net.msymbios.llovelyr.common.commands/`

**Purpose**: Restructure commands with proper validation, registry integration, and improved UX.

#### New Command Structure

```
/llovelyr group <selector> <action> [args...]
/llovelyr owner <player> list
/llovelyr owner <player> robot <robot_selector> <action> [args...]
/llovelyr owner <player> teleport <index>        [NEW - Entity reference enabled]
/llovelyr owner <player> recall <index>          [NEW - Entity reference enabled]
/llovelyr owner <player> heal all                [NEW - Entity reference enabled]
/llovelyr owner <player> stats <index>           [NEW - Entity reference enabled]
/llovelyr robot <action> [args...]
```

**Entity Reference-Enabled Commands**: These new commands leverage the entity reference in the registry to provide powerful robot management features without needing to find entities in the world.

#### Command Architecture

```java
// Common Layer - Command tree builder
public class LovelyRobotCommands {
    
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
            Commands.literal("llovelyr")
                .requires(source -> source.hasPermission(2)) // OP level 2
                .then(buildGroupCommands())
                .then(buildOwnerCommands())
                .then(buildRobotCommands())
        );
    }
    
    // Group commands - existing functionality
    private static LiteralArgumentBuilder<CommandSourceStack> buildGroupCommands() {
        return Commands.literal("group")
            .then(Commands.argument("targets", EntityArgument.entities())
                .then(buildStatsCommands())
                .then(buildDesignCommands())
                .then(buildOwnerCommands())
            );
    }
    
    // Owner commands - new registry-based functionality
    private static LiteralArgumentBuilder<CommandSourceStack> buildOwnerCommands() {
        return Commands.literal("owner")
            .then(Commands.argument("player", EntityArgument.player())
                .then(Commands.literal("list")
                    .executes(LovelyRobotCommands::executeOwnerList)
                )
                .then(Commands.literal("robot")
                    .then(Commands.argument("robot_index", IntegerArgumentType.integer(0))
                        .then(buildStatsCommands())
                        .then(buildDesignCommands())
                        .then(buildOwnershipCommands())
                    )
                )
                // NEW: Entity reference-enabled commands
                .then(Commands.literal("teleport")
                    .then(Commands.argument("robot_index", IntegerArgumentType.integer(0))
                        .executes(ctx -> executeTeleportToRobot(ctx, 
                            IntegerArgumentType.getInteger(ctx, "robot_index")))
                    )
                )
                .then(Commands.literal("recall")
                    .then(Commands.argument("robot_index", IntegerArgumentType.integer(0))
                        .executes(ctx -> executeRecallRobot(ctx, 
                            IntegerArgumentType.getInteger(ctx, "robot_index")))
                    )
                )
                .then(Commands.literal("heal")
                    .then(Commands.literal("all")
                        .executes(LovelyRobotCommands::executeHealAllRobots)
                    )
                )
                .then(Commands.literal("stats")
                    .then(Commands.argument("robot_index", IntegerArgumentType.integer(0))
                        .executes(ctx -> executeGetRobotStats(ctx, 
                            IntegerArgumentType.getInteger(ctx, "robot_index")))
                    )
                )
            );
    }
    
    // Robot commands - target robot in front of player
    private static LiteralArgumentBuilder<CommandSourceStack> buildRobotCommands() {
        return Commands.literal("robot")
            .then(buildStatsCommands())
            .then(buildDesignCommands())
            .then(buildOwnershipCommands());
    }
    
    // Stats subcommands with validation
    private static LiteralArgumentBuilder<CommandSourceStack> buildStatsCommands() {
        return Commands.literal("stats")
            .then(Commands.literal("level")
                .then(Commands.argument("value", IntegerArgumentType.integer(0))
                    .executes(ctx -> executeSetLevel(ctx, IntegerArgumentType.getInteger(ctx, "value")))
                )
            )
            .then(Commands.literal("exp")
                .then(Commands.argument("value", IntegerArgumentType.integer(0))
                    .executes(ctx -> executeSetExp(ctx, IntegerArgumentType.getInteger(ctx, "value")))
                )
            )
            .then(Commands.literal("protection")
                .then(Commands.literal("fire")
                    .then(Commands.argument("value", IntegerArgumentType.integer(0))
                        .executes(ctx -> executeSetProtection(ctx, "fire", IntegerArgumentType.getInteger(ctx, "value")))
                    )
                )
                .then(Commands.literal("fall")
                    .then(Commands.argument("value", IntegerArgumentType.integer(0))
                        .executes(ctx -> executeSetProtection(ctx, "fall", IntegerArgumentType.getInteger(ctx, "value")))
                    )
                )
                .then(Commands.literal("blast")
                    .then(Commands.argument("value", IntegerArgumentType.integer(0))
                        .executes(ctx -> executeSetProtection(ctx, "blast", IntegerArgumentType.getInteger(ctx, "value")))
                    )
                )
                .then(Commands.literal("projectile")
                    .then(Commands.argument("value", IntegerArgumentType.integer(0))
                        .executes(ctx -> executeSetProtection(ctx, "projectile", IntegerArgumentType.getInteger(ctx, "value")))
                    )
                )
            );
    }
    
    // Design subcommands
    private static LiteralArgumentBuilder<CommandSourceStack> buildDesignCommands() {
        return Commands.literal("design")
            .then(Commands.argument("color", ColorArgumentType.color())
                .executes(ctx -> executeSetDesign(ctx, ColorArgumentType.getColor(ctx, "color")))
            );
    }
    
    // Ownership subcommands
    private static LiteralArgumentBuilder<CommandSourceStack> buildOwnershipCommands() {
        return Commands.literal("owner")
            .then(Commands.literal("get")
                .executes(LovelyRobotCommands::executeGetOwner)
            )
            .then(Commands.literal("set")
                .then(Commands.argument("new_owner", EntityArgument.player())
                    .executes(ctx -> executeSetOwner(ctx, EntityArgument.getPlayer(ctx, "new_owner")))
                )
            );
    }
}
```


#### Command Implementations with Validation

```java
// Owner list command - shows all robots for a player
private static int executeOwnerList(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
    ServerPlayer targetPlayer = EntityArgument.getPlayer(ctx, "player");
    ServerLevel level = targetPlayer.serverLevel();
    OwnerRobotRegistry registry = RobotRegistryManager.getRegistry(level);
    
    List<RobotRegistryEntry> robots = registry.getRobotsForOwner(targetPlayer.getUUID());
    
    if (robots.isEmpty()) {
        ctx.getSource().sendSuccess(
            () -> Component.translatable("command.llovelyr.owner.no_robots", targetPlayer.getName()),
            false
        );
        return 0;
    }
    
    ctx.getSource().sendSuccess(
        () -> Component.translatable("command.llovelyr.owner.robot_list", 
            targetPlayer.getName(), robots.size()),
        false
    );
    
    for (int i = 0; i < robots.size(); i++) {
        RobotRegistryEntry entry = robots.get(i);
        String name = entry.getRobotName() != null ? entry.getRobotName() : "Unnamed";
        String type = entry.getRobotType();
        String dimension = entry.getDimension();
        String pos = String.format("%.1f, %.1f, %.1f", entry.getPosX(), entry.getPosY(), entry.getPosZ());
        
        ctx.getSource().sendSuccess(
            () -> Component.literal(String.format("[%d] %s (%s) | %s: %s", 
                i, name, type, dimension, pos)),
            false
        );
    }
    
    return robots.size();
}

// Set level with validation
private static int executeSetLevel(CommandContext<CommandSourceStack> ctx, int newLevel) throws CommandSyntaxException {
    Collection<? extends Entity> targets = getTargetRobots(ctx);
    int successCount = 0;
    
    for (Entity entity : targets) {
        if (entity instanceof InternalEntity robot) {
            // Validate against max level from entity type
            int maxLevel = robot.nativeEntity.getFeature(LevelFeature.class)
                .map(LevelFeature::getMaxLevel)
                .orElse(200);
            
            if (newLevel > maxLevel) {
                ctx.getSource().sendFailure(
                    Component.translatable("command.llovelyr.level.exceeds_max", 
                        robot.getName(), newLevel, maxLevel)
                );
                continue;
            }
            
            robot.setLevel(newLevel);
            robot.recalculateAttributes();
            successCount++;
        }
    }
    
    if (successCount > 0) {
        int finalCount = successCount;
        ctx.getSource().sendSuccess(
            () -> Component.translatable("command.llovelyr.level.success", finalCount, newLevel),
            true
        );
    }
    
    return successCount;
}

// Set exp with auto-level-up
private static int executeSetExp(CommandContext<CommandSourceStack> ctx, int newExp) throws CommandSyntaxException {
    Collection<? extends Entity> targets = getTargetRobots(ctx);
    int successCount = 0;
    
    for (Entity entity : targets) {
        if (entity instanceof InternalEntity robot) {
            robot.combatStats.setExperience(newExp);
            
            // Attempt level-ups
            if (robot.nativeEntity.hasFeature(LevelFeature.class)) {
                LevelFeature levelFeature = robot.nativeEntity.getFeature(LevelFeature.class).get();
                
                while (levelFeature.canLevelUp() && 
                       robot.combatStats.getExperience() >= levelFeature.getExpForNextLevel()) {
                    if (levelFeature.tryLevelUp()) {
                        robot.combatStats.setLevel(levelFeature.getCurrentLevel());
                        robot.combatStats.setExperience(
                            robot.combatStats.getExperience() - levelFeature.getExpForLevel(levelFeature.getCurrentLevel())
                        );
                        robot.recalculateAttributes();
                    } else {
                        break;
                    }
                }
            }
            
            successCount++;
        }
    }
    
    if (successCount > 0) {
        int finalCount = successCount;
        ctx.getSource().sendSuccess(
            () -> Component.translatable("command.llovelyr.exp.success", finalCount, newExp),
            true
        );
    }
    
    return successCount;
}

// Set protection with validation using ProtectionFeature
private static int executeSetProtection(CommandContext<CommandSourceStack> ctx, 
                                       String protectionType, 
                                       int newLevel) throws CommandSyntaxException {
    Collection<? extends Entity> targets = getTargetRobots(ctx);
    int successCount = 0;
    
    for (Entity entity : targets) {
        if (entity instanceof InternalEntity robot) {
            // Validate against ProtectionFeature limits
            if (!robot.nativeEntity.hasFeature(ProtectionFeature.class)) {
                ctx.getSource().sendFailure(
                    Component.translatable("command.llovelyr.protection.not_supported", 
                        robot.getName())
                );
                continue;
            }
            
            ProtectionFeature protection = robot.nativeEntity.getFeature(ProtectionFeature.class).get();
            
            // Get max level from feature
            int maxLevel = switch (protectionType) {
                case "fire" -> protection.getMaxFireProtection();
                case "fall" -> protection.getMaxFallProtection();
                case "blast" -> protection.getMaxBlastProtection();
                case "projectile" -> protection.getMaxProjectileProtection();
                default -> 0;
            };
            
            if (newLevel > maxLevel) {
                ctx.getSource().sendFailure(
                    Component.translatable("command.llovelyr.protection.exceeds_max", 
                        robot.getName(), protectionType, newLevel, maxLevel)
                );
                continue;
            }
            
            // Apply protection
            switch (protectionType) {
                case "fire" -> robot.protectionStats.setFireProtection(newLevel);
                case "fall" -> robot.protectionStats.setFallProtection(newLevel);
                case "blast" -> robot.protectionStats.setBlastProtection(newLevel);
                case "projectile" -> robot.protectionStats.setProjectileProtection(newLevel);
            }
            
            successCount++;
        }
    }
    
    if (successCount > 0) {
        int finalCount = successCount;
        ctx.getSource().sendSuccess(
            () -> Component.translatable("command.llovelyr.protection.success", 
                finalCount, protectionType, newLevel),
            true
        );
    }
    
    return successCount;
}

// Set owner with registry update
private static int executeSetOwner(CommandContext<CommandSourceStack> ctx, 
                                  ServerPlayer newOwner) throws CommandSyntaxException {
    Collection<? extends Entity> targets = getTargetRobots(ctx);
    int successCount = 0;
    
    for (Entity entity : targets) {
        if (entity instanceof InternalEntity robot && robot.isTame()) {
            ServerLevel level = (ServerLevel) robot.level();
            OwnerRobotRegistry registry = RobotRegistryManager.getRegistry(level);
            
            UUID oldOwnerId = robot.getOwnerUUID();
            
            // Unregister from old owner
            if (oldOwnerId != null) {
                registry.unregisterRobot(robot.getUUID());
            }
            
            // Set new owner
            robot.setOwnerUUID(newOwner.getUUID());
            robot.setTame(true);
            
            // Register with new owner (happens automatically in setTame)
            
            successCount++;
        }
    }
    
    if (successCount > 0) {
        int finalCount = successCount;
        ctx.getSource().sendSuccess(
            () -> Component.translatable("command.llovelyr.owner.changed", 
                finalCount, newOwner.getName()),
            true
        );
    }
    
    return successCount;
}

// Helper to get target robots based on command context
private static Collection<? extends Entity> getTargetRobots(CommandContext<CommandSourceStack> ctx) 
        throws CommandSyntaxException {
    
    // Try to get from "targets" argument (group command)
    try {
        return EntityArgument.getEntities(ctx, "targets");
    } catch (IllegalArgumentException e) {
        // Not a group command
    }
    
    // Try to get from owner + robot_index (owner robot command)
    try {
        ServerPlayer owner = EntityArgument.getPlayer(ctx, "player");
        int robotIndex = IntegerArgumentType.getInteger(ctx, "robot_index");
        
        ServerLevel level = owner.serverLevel();
        OwnerRobotRegistry registry = RobotRegistryManager.getRegistry(level);
        List<RobotRegistryEntry> robots = registry.getRobotsForOwner(owner.getUUID());
        
        if (robotIndex >= robots.size()) {
            throw new CommandSyntaxException(
                new SimpleCommandExceptionType(
                    Component.translatable("command.llovelyr.robot.invalid_index", robotIndex)
                ),
                Component.translatable("command.llovelyr.robot.invalid_index", robotIndex)
            );
        }
        
        RobotRegistryEntry entry = robots.get(robotIndex);
        Entity robot = level.getEntity(entry.getRobotId());
        
        if (robot == null) {
            throw new CommandSyntaxException(
                new SimpleCommandExceptionType(
                    Component.translatable("command.llovelyr.robot.not_found")
                ),
                Component.translatable("command.llovelyr.robot.not_found")
            );
        }
        
        return Collections.singletonList(robot);
    } catch (IllegalArgumentException e) {
        // Not an owner robot command
    }
    
    // Must be a direct robot command - find robot in front of player
    CommandSourceStack source = ctx.getSource();
    Entity sourceEntity = source.getEntity();
    
    if (!(sourceEntity instanceof Player player)) {
        throw new CommandSyntaxException(
            new SimpleCommandExceptionType(
                Component.translatable("command.llovelyr.robot.player_only")
            ),
            Component.translatable("command.llovelyr.robot.player_only")
        );
    }
    
    // Raycast to find robot
    Entity targetRobot = findRobotInFront(player, 5.0);
    
    if (targetRobot == null) {
        throw new CommandSyntaxException(
            new SimpleCommandExceptionType(
                Component.translatable("command.llovelyr.robot.none_targeted")
            ),
            Component.translatable("command.llovelyr.robot.none_targeted")
        );
    }
    
    return Collections.singletonList(targetRobot);
}

// Helper to find robot in front of player
private static Entity findRobotInFront(Player player, double maxDistance) {
    Vec3 eyePos = player.getEyePosition();
    Vec3 lookVec = player.getLookAngle();
    Vec3 endPos = eyePos.add(lookVec.scale(maxDistance));
    
    AABB searchBox = new AABB(eyePos, endPos).inflate(1.0);
    List<Entity> entities = player.level().getEntities(player, searchBox);
    
    Entity closestRobot = null;
    double closestDistance = maxDistance;
    
    for (Entity entity : entities) {
        if (entity instanceof InternalEntity) {
            double distance = entity.distanceTo(player);
            if (distance < closestDistance) {
                closestRobot = entity;
                closestDistance = distance;
            }
        }
    }
    
    return closestRobot;
}

// NEW: Entity reference-enabled commands

// Teleport player to robot
private static int executeTeleportToRobot(CommandContext<CommandSourceStack> ctx, int robotIndex) 
        throws CommandSyntaxException {
    ServerPlayer player = EntityArgument.getPlayer(ctx, "player");
    ServerLevel level = player.serverLevel();
    OwnerRobotRegistry registry = RobotRegistryManager.getRegistry(level);
    
    List<RobotRegistryEntry> robots = registry.getRobotsForOwner(player.getUUID());
    
    if (robotIndex >= robots.size()) {
        throw new CommandSyntaxException(
            new SimpleCommandExceptionType(
                Component.translatable("command.llovelyr.robot.invalid_index", robotIndex)
            ),
            Component.translatable("command.llovelyr.robot.invalid_index", robotIndex)
        );
    }
    
    RobotRegistryEntry entry = robots.get(robotIndex);
    LivingEntity robot = entry.getEntity();
    
    if (robot == null || !entry.isEntityValid()) {
        ctx.getSource().sendFailure(
            Component.translatable("command.llovelyr.robot.offline")
        );
        return 0;
    }
    
    // Teleport player to robot
    player.teleportTo(
        (ServerLevel) robot.level(),
        robot.getX(),
        robot.getY(),
        robot.getZ(),
        player.getYRot(),
        player.getXRot()
    );
    
    ctx.getSource().sendSuccess(
        () -> Component.translatable("command.llovelyr.teleport.success", 
            entry.getRobotName() != null ? entry.getRobotName() : "robot"),
        true
    );
    
    return 1;
}

// Recall robot to player
private static int executeRecallRobot(CommandContext<CommandSourceStack> ctx, int robotIndex) 
        throws CommandSyntaxException {
    ServerPlayer player = EntityArgument.getPlayer(ctx, "player");
    ServerLevel level = player.serverLevel();
    OwnerRobotRegistry registry = RobotRegistryManager.getRegistry(level);
    
    List<RobotRegistryEntry> robots = registry.getRobotsForOwner(player.getUUID());
    
    if (robotIndex >= robots.size()) {
        throw new CommandSyntaxException(
            new SimpleCommandExceptionType(
                Component.translatable("command.llovelyr.robot.invalid_index", robotIndex)
            ),
            Component.translatable("command.llovelyr.robot.invalid_index", robotIndex)
        );
    }
    
    RobotRegistryEntry entry = robots.get(robotIndex);
    LivingEntity robot = entry.getEntity();
    
    if (robot == null || !entry.isEntityValid()) {
        ctx.getSource().sendFailure(
            Component.translatable("command.llovelyr.robot.offline")
        );
        return 0;
    }
    
    // Teleport robot to player
    robot.teleportTo(
        player.serverLevel(),
        player.getX(),
        player.getY(),
        player.getZ(),
        robot.getYRot(),
        robot.getXRot()
    );
    
    ctx.getSource().sendSuccess(
        () -> Component.translatable("command.llovelyr.recall.success", 
            entry.getRobotName() != null ? entry.getRobotName() : "robot"),
        true
    );
    
    return 1;
}

// Heal all robots for a player
private static int executeHealAllRobots(CommandContext<CommandSourceStack> ctx) 
        throws CommandSyntaxException {
    ServerPlayer player = EntityArgument.getPlayer(ctx, "player");
    ServerLevel level = player.serverLevel();
    OwnerRobotRegistry registry = RobotRegistryManager.getRegistry(level);
    
    List<RobotRegistryEntry> robots = registry.getRobotsForOwner(player.getUUID());
    int healedCount = 0;
    
    for (RobotRegistryEntry entry : robots) {
        LivingEntity robot = entry.getEntity();
        
        if (robot != null && entry.isEntityValid()) {
            robot.setHealth(robot.getMaxHealth());
            healedCount++;
        }
    }
    
    if (healedCount > 0) {
        int finalCount = healedCount;
        ctx.getSource().sendSuccess(
            () -> Component.translatable("command.llovelyr.heal.success", 
                finalCount, player.getName()),
            true
        );
    } else {
        ctx.getSource().sendFailure(
            Component.translatable("command.llovelyr.heal.none_available")
        );
    }
    
    return healedCount;
}

// Get detailed stats for a robot
private static int executeGetRobotStats(CommandContext<CommandSourceStack> ctx, int robotIndex) 
        throws CommandSyntaxException {
    ServerPlayer player = EntityArgument.getPlayer(ctx, "player");
    ServerLevel level = player.serverLevel();
    OwnerRobotRegistry registry = RobotRegistryManager.getRegistry(level);
    
    List<RobotRegistryEntry> robots = registry.getRobotsForOwner(player.getUUID());
    
    if (robotIndex >= robots.size()) {
        throw new CommandSyntaxException(
            new SimpleCommandExceptionType(
                Component.translatable("command.llovelyr.robot.invalid_index", robotIndex)
            ),
            Component.translatable("command.llovelyr.robot.invalid_index", robotIndex)
        );
    }
    
    RobotRegistryEntry entry = robots.get(robotIndex);
    LivingEntity robot = entry.getEntity();
    
    if (robot == null || !entry.isEntityValid()) {
        ctx.getSource().sendFailure(
            Component.translatable("command.llovelyr.robot.offline")
        );
        return 0;
    }
    
    // Display comprehensive stats
    if (robot instanceof InternalEntity internalRobot) {
        String name = entry.getRobotName() != null ? entry.getRobotName() : "Unnamed";
        String type = entry.getRobotType();
        double[] pos = entry.getPosition();
        String dimension = entry.getDimension();
        
        ctx.getSource().sendSuccess(
            () -> Component.literal(String.format("§b=== %s (%s) ===", name, type)),
            false
        );
        
        ctx.getSource().sendSuccess(
            () -> Component.literal(String.format(
                "§7Location: §f%s §7at §f%.1f, %.1f, %.1f", 
                dimension, pos[0], pos[1], pos[2]
            )),
            false
        );
        
        ctx.getSource().sendSuccess(
            () -> Component.literal(String.format(
                "§7Level: §e%d §8| §7XP: §e%d", 
                internalRobot.getLevel(), 
                internalRobot.combatStats.getExperience()
            )),
            false
        );
        
        ctx.getSource().sendSuccess(
            () -> Component.literal(String.format(
                "§7Health: §c%.1f§7/§c%.1f", 
                robot.getHealth(), 
                robot.getMaxHealth()
            )),
            false
        );
        
        ctx.getSource().sendSuccess(
            () -> Component.literal(String.format(
                "§7Attack: §6%d §8| §7Defense: §6%d", 
                internalRobot.combatStats.getAttack(),
                internalRobot.combatStats.getDefense()
            )),
            false
        );
        
        ctx.getSource().sendSuccess(
            () -> Component.literal(String.format(
                "§7Protections: §aFire:%d Fall:%d Blast:%d Projectile:%d", 
                internalRobot.getFireProtection(),
                internalRobot.getFallProtection(),
                internalRobot.getBlastProtection(),
                internalRobot.getProjectileProtection()
            )),
            false
        );
    }
    
    return 1;
}
```


## Implementation Plan

### Phase 1: Framework Layer (Pure Java)

**Duration**: 3-4 days

**Tasks**:
1. Create `framework/entity/combat/` package
2. Implement `AttributeCalculationStrategy` interface
3. Implement `LinearAttributeStrategy` (current InternalLogic formula)
4. Implement `ExponentialAttributeStrategy` (for Dragon/advanced robots)
5. **NEW: Create `framework/entity/enchantment/` package**
6. **NEW: Implement `EnchantmentCalculationStrategy` interface**
7. **NEW: Implement `DefaultEnchantmentStrategy` (current looting formula)**
8. **NEW: Create `framework/entity/protection/` package**
9. **NEW: Implement `ProtectionCalculationStrategy` interface**
10. **NEW: Implement `LevelBasedProtectionStrategy` (auto-unlock at levels)**
11. Create `framework/entity/data/` package
12. Implement `CombatStats`, `ProtectionStats`, `EnchantmentStats` data classes
13. Write unit tests for all framework classes

**Deliverables**:
- Pure Java classes with zero Minecraft dependencies
- Comprehensive unit tests
- Documentation for all public APIs

### Phase 2: Lib Layer (Minecraft Integration)

**Duration**: 4-5 days

**Tasks**:
1. Create `lib/entity/type/features/CombatLevelFeature.java`
2. **NEW: Create `lib/entity/type/features/EnchantmentFeature.java`**
3. **NEW: Create `lib/entity/type/features/ProtectionFeature.java`**
4. Implement NBT serialization classes:
   - `CombatStatsNBT`
   - `ProtectionStatsNBT`
   - `EnchantmentStatsNBT`
5. Create `lib/registry/RobotRegistryManager.java`
6. Implement `RobotRegistryEntry` with `WeakReference<LivingEntity>`
7. Implement `OwnerRobotRegistry`
8. Implement Forge-specific registry integration
9. Implement Fabric-specific registry integration
10. Write integration tests

**Deliverables**:
- Loader-specific implementations (Forge & Fabric)
- All three features implemented (Combat, Enchantment, Protection)
- NBT serialization working correctly
- Registry manager functional with entity references

### Phase 3: Common Layer (Mod Integration)

**Duration**: 5-6 days

**Tasks**:
1. Update `InternalEntity` to use new stat classes
2. Integrate `CombatLevelFeature` with `RobotEntityType`
3. **NEW: Integrate `EnchantmentFeature` with `RobotEntityType`**
4. **NEW: Integrate `ProtectionFeature` with `RobotEntityType`**
5. Update `NativeRobotType` to attach all three features
6. Update `recalculateAttributes()` to use all features
7. Implement registry integration in entity lifecycle:
   - Registration on tame with entity reference
   - Unregistration on death/removal
   - Timestamp updates (position is live via entity reference)
8. Update spawn items to check registry limits
9. Migrate existing NBT data to new format (backward compatibility)
10. Write migration tests

**Deliverables**:
- Entities using new stat system with all three features
- Registry tracking all robots with entity references
- Spawn limits enforced
- Enchantments calculated via feature
- Protections validated via feature
- Backward compatibility maintained

### Phase 4: Command System

**Duration**: 4-5 days

**Tasks**:
1. Implement new command tree structure
2. Add validation for all stat modifications
3. Implement owner list command with registry
4. Implement owner robot selector
5. Implement direct robot targeting (raycast)
6. **NEW: Implement entity reference-enabled commands**:
   - `/llovelyr owner <player> teleport <index>` - Teleport player to robot
   - `/llovelyr owner <player> recall <index>` - Recall robot to player
   - `/llovelyr owner <player> heal all` - Heal all robots for player
   - `/llovelyr owner <player> stats <index>` - Display live robot stats
7. Add proper error messages and feedback
8. Update command registration (Forge & Fabric)
9. Write command tests (including new entity reference commands)

**Deliverables**:
- New command structure functional
- All commands validated
- Registry integration working
- Entity reference commands working
- Comprehensive error handling

### Phase 5: Config Integration

**Duration**: 2-3 days

**Tasks**:
1. Add config entries for:
   - `MaxOwnerRobots` (default: 10)
   - Base stats per robot type (HP, Attack, Defense)
   - Attribute calculation strategy selection
2. Update `NativeRobotType.reloadFromConfig()` to update features
3. Implement runtime config reload
4. Test config changes apply correctly

**Deliverables**:
- Config system updated
- Runtime reload working
- All features configurable

### Phase 6: Testing & Validation

**Duration**: 3-4 days

**Tasks**:
1. Comprehensive testing:
   - Unit tests for all framework classes
   - Integration tests for lib layer
   - Gameplay tests for common layer
   - Command tests
2. Backward compatibility testing:
   - Load old robot NBT data
   - Verify stats migrate correctly
   - Test with existing worlds
3. Performance testing:
   - Registry performance with 100+ robots
   - Stat calculation performance
   - Command execution performance
4. Cross-loader testing:
   - Verify Forge implementation
   - Verify Fabric implementation
   - Ensure identical behavior

**Deliverables**:
- All tests passing
- Backward compatibility confirmed
- Performance acceptable
- Both loaders working identically

### Phase 7: Documentation

**Duration**: 2 days

**Tasks**:
1. Update `CURRENT_STATE.md` with new architecture
2. Create ADR for CombatLevelFeature design
3. Create ADR for Registry system design
4. Document command changes
5. Update API documentation
6. Create migration guide for users
7. Update CHANGELOG.md

**Deliverables**:
- Complete documentation
- ADRs created
- Migration guide available
- CHANGELOG updated

## Total Estimated Duration: 23-31 days

**Note**: Duration increased to account for:
- EnchantmentFeature and ProtectionFeature implementation (+2-3 days)
- Entity reference-enabled commands (+1-2 days)


## Architecture Alignment

### HZ Framework → HZ Lib → Common → Source Pattern

This proposal strictly follows the established architecture:

**Framework Layer** (Pure Java):
- `framework/entity/combat/` - Calculation strategies
- `framework/entity/data/` - Data containers
- `framework/registry/` - Registry data structures
- Zero Minecraft dependencies
- 100% portable and testable

**Lib Layer** (Loader-Specific):
- `lib/entity/type/features/CombatLevelFeature` - Feature implementation
- `lib/entity/data/` - NBT serialization
- `lib/registry/RobotRegistryManager` - Server integration
- Minimal Minecraft dependencies
- Identical public API across loaders

**Common Layer** (Mod-Specific Shared):
- `common/entity/internal/InternalEntity` - Entity integration
- `common/commands/` - Command implementations
- Uses Framework + Lib
- Shared between Forge and Fabric

**Source Layer** (Loader-Specific Implementation):
- `source/entity/type/NativeRobotType` - Robot type definitions
- `source/LovelyCommands` - Command registration
- Minimal loader-specific code

### Feature System Integration

All new functionality integrates with the existing feature system:

```java
// CombatLevelFeature is attached like LevelFeature
robotType.withFeature(CombatLevelFeature.class, new CombatLevelFeature(...));

// Access via type-safe feature system
Optional<CombatLevelFeature> combat = robotType.getFeature(CombatLevelFeature.class);
combat.ifPresent(c -> {
    int hp = c.calculateHp(level);
    int attack = c.calculateAttack(level);
});
```

### Extractability

All framework and lib code is designed for future extraction:

1. **Framework** → External `hzframework` library (pure Java)
2. **Lib** → External `hzlib-forge` and `hzlib-fabric` libraries
3. **Common** → Stays in mod, depends on external libraries
4. **Source** → Stays in mod, minimal changes needed

## Benefits

### 1. Architectural Consistency

- Follows established HZLib EntityType pattern
- Feature-based composition instead of static utilities
- Clear separation of concerns
- Testable components

### 2. Flexibility

- Different robots can use different calculation strategies
- Easy to add new stat types
- Config-driven behavior
- Runtime reconfiguration

### 3. Maintainability

- Encapsulated stat management
- Clear data ownership
- Centralized NBT serialization
- Reduced coupling

### 4. Extensibility

- Easy to add new features (e.g., `MagicStats`, `CraftingStats`)
- Strategy pattern allows custom formulas
- Registry system enables advanced features (teleport to robot, squad management)
- Command system easily extended

### 5. User Experience

- Spawn limits prevent server overload
- Registry enables powerful commands
- Validation prevents invalid states
- Better error messages

### 6. Future-Proof

- Designed for extraction to external library
- Minimal refactoring needed later
- Reusable across other mods
- Follows industry best practices

## Risks & Mitigation

### Risk 1: Backward Compatibility

**Risk**: Existing robots might not load correctly with new NBT structure

**Mitigation**:
- Implement NBT migration layer
- Support both old and new formats during transition
- Comprehensive testing with existing worlds
- Provide migration tool if needed

### Risk 2: Performance Impact

**Risk**: Registry updates every tick might impact performance

**Mitigation**:
- Update position only every 20 ticks (1 second)
- Use efficient data structures (ConcurrentHashMap)
- Lazy initialization of registry
- Performance testing with 100+ robots

### Risk 3: Complexity Increase

**Risk**: More classes and layers might confuse developers

**Mitigation**:
- Comprehensive documentation
- Clear examples in code
- ADRs explaining design decisions
- Gradual migration path

### Risk 4: Cross-Loader Differences

**Risk**: Forge and Fabric implementations might diverge

**Mitigation**:
- Maximize code sharing in Framework and Common layers
- Identical public APIs in Lib layer
- Cross-loader integration tests
- Regular synchronization checks

## Success Criteria

### Functional Requirements

- [ ] CombatLevelFeature calculates attributes correctly
- [ ] All stat classes serialize/deserialize via NBT
- [ ] Registry tracks all active robots
- [ ] Spawn limits enforced correctly
- [ ] Commands validate against config limits
- [ ] Owner list command shows all robots
- [ ] Robot targeting works (raycast and index)
- [ ] Ownership transfer updates registry

### Non-Functional Requirements

- [ ] Backward compatibility with existing robots
- [ ] Performance acceptable with 100+ robots
- [ ] Identical behavior on Forge and Fabric
- [ ] All unit tests passing
- [ ] All integration tests passing
- [ ] Documentation complete
- [ ] Code follows project coding standards

### Quality Gates

- [ ] Code review completed
- [ ] All tests passing
- [ ] Performance benchmarks met
- [ ] Documentation reviewed
- [ ] ADRs approved
- [ ] Backward compatibility verified

## Dependencies

### Internal Dependencies

- HZLib EntityType Architecture (already implemented)
- LevelFeature (already implemented)
- InternalEntity (existing, will be modified)
- Config system (existing, will be extended)

### External Dependencies

- Minecraft 1.20.1
- Forge 47.x / Fabric 0.14.x
- GeckoLib (existing)
- No new external dependencies

## Alternatives Considered

### Alternative 1: Keep InternalLogic as Static Utility

**Pros**:
- No refactoring needed
- Simple to understand
- Works currently

**Cons**:
- Doesn't follow feature architecture
- Not configurable per robot type
- Hard to test
- Not reusable
- Violates established patterns

**Decision**: Rejected - doesn't align with architecture

### Alternative 2: Use Minecraft's Attribute System Directly

**Pros**:
- Native Minecraft system
- Well-tested
- Automatic synchronization

**Cons**:
- Limited to predefined attributes
- Can't add custom stats easily
- No level-based calculation support
- Harder to serialize custom data

**Decision**: Rejected - too limiting for our needs

### Alternative 3: Global Robot Registry (Not Per-Owner)

**Pros**:
- Simpler implementation
- Faster lookups

**Cons**:
- Can't enforce per-owner limits
- Can't query by owner efficiently
- Less useful for commands

**Decision**: Rejected - per-owner tracking is essential

## Next Steps

1. **Review & Approval**: Present this proposal to team/stakeholders
2. **Create ADRs**: Document key architectural decisions
3. **Create Tasks**: Break down into detailed implementation tasks
4. **Sprint Planning**: Allocate to sprints based on priority
5. **Implementation**: Follow phase-by-phase plan
6. **Testing**: Comprehensive testing at each phase
7. **Documentation**: Update all relevant docs
8. **Release**: Deploy to both Forge and Fabric

## References

- **HZLib EntityType Architecture**: `docs/development/HZLib-EntityType-Architecture.md`
- **Legacy 1.20.1 Refactoring Plan**: `docs/development/Legacy-1.20.1-Refactoring-Plan.md`
- **Project Coding Style**: `steering/project-coding-style.md`
- **Project Structure**: `steering/project-structure.md`
- **Development Guidelines**: `steering/development.md`
- **Documentation Standards**: `steering/documentation.md`

---

**Status**: Proposal - Awaiting Review
**Author**: Development Team
**Date**: 2025-11-30
**Version**: 1.0
