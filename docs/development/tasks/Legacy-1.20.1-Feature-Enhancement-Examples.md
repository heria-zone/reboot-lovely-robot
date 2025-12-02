---
created: 2025-11-30
status: Reference
tags:
  - Legacy
  - Examples
  - Code-Samples
---

# Legacy 1.20.1 Feature Enhancement - Code Examples

## Example 1: Using CombatLevelFeature

### Before (Current InternalLogic)

```java
// Static utility methods - not configurable, not testable
public class InternalLogic {
    public static int calculateHp(int level, int value) {
        return (value + level * value / 50);
    }
    
    public static int calculateAttack(int level, int value) {
        return (value + level * value / 50);
    }
}

// Usage in entity
int hp = InternalLogic.calculateHp(getLevel(), baseHp);
int attack = InternalLogic.calculateAttack(getLevel(), baseAttack);
```

### After (CombatLevelFeature)

```java
// Feature-based, configurable, testable
public class CombatLevelFeature {
    private AttributeCalculationStrategy strategy;
    private int baseHp, baseAttack, baseDefense;
    
    public int calculateHp(int level) {
        return strategy.calculateHp(level, baseHp);
    }
    
    public int calculateAttack(int level) {
        return strategy.calculateAttack(level, baseAttack);
    }
}

// Attach to robot type
VANILLA.withFeature(CombatLevelFeature.class, 
    new CombatLevelFeature(100, 10, 5));

// Usage in entity
if (nativeEntity.hasFeature(CombatLevelFeature.class)) {
    CombatLevelFeature combat = nativeEntity.getFeature(CombatLevelFeature.class).get();
    int hp = combat.calculateHp(getLevel());
    int attack = combat.calculateAttack(getLevel());
}
```

## Example 2: Data Management

### Before (Scattered Fields)

```java
public class InternalEntity extends TamableAnimal {
    private int level;
    private int experience;
    private int currentHp;
    private int maxHp;
    private int attack;
    private int defense;
    private int fireProtection;
    private int fallProtection;
    private int blastProtection;
    private int projectileProtection;
    
    // Scattered get/set methods
    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = level; }
    public int getFireProtection() { return fireProtection; }
    public void setFireProtection(int value) { fireProtection = value; }
    
    // Manual NBT handling
    @Override
    public void readAdditionalSaveData(CompoundTag nbt) {
        level = nbt.getInt("Level");
        experience = nbt.getInt("Experience");
        currentHp = nbt.getInt("CurrentHp");
        maxHp = nbt.getInt("MaxHp");
        attack = nbt.getInt("Attack");
        defense = nbt.getInt("Defense");
        fireProtection = nbt.getInt("FireProtection");
        fallProtection = nbt.getInt("FallProtection");
        blastProtection = nbt.getInt("BlastProtection");
        projectileProtection = nbt.getInt("ProjectileProtection");
    }
    
    @Override
    public void addAdditionalSaveData(CompoundTag nbt) {
        nbt.putInt("Level", level);
        nbt.putInt("Experience", experience);
        nbt.putInt("CurrentHp", currentHp);
        nbt.putInt("MaxHp", maxHp);
        nbt.putInt("Attack", attack);
        nbt.putInt("Defense", defense);
        nbt.putInt("FireProtection", fireProtection);
        nbt.putInt("FallProtection", fallProtection);
        nbt.putInt("BlastProtection", blastProtection);
        nbt.putInt("ProjectileProtection", projectileProtection);
    }
}
```

### After (Encapsulated Stats)

```java
public class InternalEntity extends TamableAnimal {
    protected CombatStats combatStats;
    protected ProtectionStats protectionStats;
    protected EnchantmentStats enchantmentStats;
    
    protected CombatStatsNBT combatStatsNBT;
    protected ProtectionStatsNBT protectionStatsNBT;
    protected EnchantmentStatsNBT enchantmentStatsNBT;
    
    protected InternalEntity(EntityType<? extends TamableAnimal> entityType, 
                           Level world, 
                           InternalEntityType<?> nativeEntityType) {
        super(entityType, world);
        
        // Initialize stat objects
        this.combatStats = new CombatStats();
        this.protectionStats = new ProtectionStats();
        this.enchantmentStats = new EnchantmentStats();
        
        // Initialize NBT handlers
        this.combatStatsNBT = new CombatStatsNBT(combatStats);
        this.protectionStatsNBT = new ProtectionStatsNBT(protectionStats);
        this.enchantmentStatsNBT = new EnchantmentStatsNBT(enchantmentStats);
    }
    
    // Clean accessors
    public int getLevel() { return combatStats.getLevel(); }
    public void setLevel(int level) { 
        combatStats.setLevel(level);
        recalculateAttributes();
    }
    
    public int getFireProtection() { return protectionStats.getFireProtection(); }
    public void setFireProtection(int level) { protectionStats.setFireProtection(level); }
    
    // Automatic NBT handling
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


## Example 3: Robot Registry

### Spawn Item with Registry Check

```java
public class RobotSpawnItem extends Item {
    
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
                    Component.translatable("message.llovelyr.max_robots_reached", 
                        maxRobots, registry.getRobotCount(player.getUUID())),
                    true
                );
                return InteractionResult.FAIL;
            }
            
            // Spawn robot
            InternalEntity robot = createRobot(context);
            if (robot != null) {
                robot.setTame(true);
                robot.setOwnerUUID(player.getUUID());
                // Registration happens automatically in setTame()
                
                player.displayClientMessage(
                    Component.translatable("message.llovelyr.robot_spawned",
                        registry.getRobotCount(player.getUUID()), maxRobots),
                    true
                );
                
                return InteractionResult.SUCCESS;
            }
        }
        
        return InteractionResult.PASS;
    }
}
```

### Entity Registration Lifecycle

```java
public abstract class InternalEntity extends TamableAnimal {
    
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
            
            // Pass entity reference - registry stores weak reference
            RobotRegistryEntry entry = new RobotRegistryEntry(
                getUUID(),
                getOwnerUUID(),
                this, // Entity reference for live data access
                nativeEntity.getKey()
            );
            
            registry.registerRobot(entry);
            
            LovelyLegacy.LOGGER.info("Registered robot {} for owner {}", 
                getUUID(), getOwnerUUID());
        }
    }
    
    @Override
    public void tick() {
        super.tick();
        
        // Update registry timestamp every 20 ticks (1 second)
        // Position is always live via entity reference - no need to update!
        if (!level().isClientSide && tickCount % 20 == 0) {
            updateRegistryTimestamp();
        }
    }
    
    protected void updateRegistryTimestamp() {
        if (getOwnerUUID() != null && level() instanceof ServerLevel serverLevel) {
            OwnerRobotRegistry registry = RobotRegistryManager.getRegistry(serverLevel);
            RobotRegistryEntry entry = registry.getEntry(getUUID());
            if (entry != null) {
                entry.updateTimestamp(); // Just update last-seen time
            }
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
            
            LovelyLegacy.LOGGER.info("Unregistered robot {} for owner {}", 
                getUUID(), getOwnerUUID());
        }
    }
}
```

## Example 4: Command System

### Owner List Command

```java
private static int executeOwnerList(CommandContext<CommandSourceStack> ctx) 
        throws CommandSyntaxException {
    ServerPlayer targetPlayer = EntityArgument.getPlayer(ctx, "player");
    ServerLevel level = targetPlayer.serverLevel();
    OwnerRobotRegistry registry = RobotRegistryManager.getRegistry(level);
    
    List<RobotRegistryEntry> robots = registry.getRobotsForOwner(targetPlayer.getUUID());
    
    if (robots.isEmpty()) {
        ctx.getSource().sendSuccess(
            () -> Component.translatable("command.llovelyr.owner.no_robots", 
                targetPlayer.getName()),
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
        
        // Get live data from entity reference
        String name = entry.getRobotName() != null ? entry.getRobotName() : "Unnamed";
        String type = entry.getRobotType();
        String dimension = entry.getDimension();
        double[] pos = entry.getPosition();
        
        // Check if entity is still valid
        if (!entry.isEntityValid() || pos == null) {
            int index = i;
            ctx.getSource().sendSuccess(
                () -> Component.literal(String.format(
                    "§7[§e%d§7] §b%s §7(§a%s§7) §8| §c[OFFLINE/UNLOADED]", 
                    index, name, type
                )),
                false
            );
            continue;
        }
        
        String posStr = String.format("%.1f, %.1f, %.1f", pos[0], pos[1], pos[2]);
        
        // Get health info
        float health = entry.getHealth();
        float maxHealth = entry.getMaxHealth();
        String healthStr = String.format("%.0f/%.0f", health, maxHealth);
        
        int index = i;
        ctx.getSource().sendSuccess(
            () -> Component.literal(String.format(
                "§7[§e%d§7] §b%s §7(§a%s§7) §8| §6%s§7: §f%s §8| §c❤ §f%s", 
                index, name, type, dimension, posStr, healthStr
            )),
            false
        );
    }
    
    return robots.size();
}
```

### Set Level with Validation

```java
private static int executeSetLevel(CommandContext<CommandSourceStack> ctx, int newLevel) 
        throws CommandSyntaxException {
    Collection<? extends Entity> targets = getTargetRobots(ctx);
    int successCount = 0;
    int failCount = 0;
    
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
                failCount++;
                continue;
            }
            
            if (newLevel < 0) {
                ctx.getSource().sendFailure(
                    Component.translatable("command.llovelyr.level.negative", 
                        robot.getName())
                );
                failCount++;
                continue;
            }
            
            int oldLevel = robot.getLevel();
            robot.setLevel(newLevel);
            robot.recalculateAttributes();
            
            LovelyLegacy.LOGGER.info("Set level for robot {} from {} to {}", 
                robot.getUUID(), oldLevel, newLevel);
            
            successCount++;
        }
    }
    
    if (successCount > 0) {
        int finalCount = successCount;
        ctx.getSource().sendSuccess(
            () -> Component.translatable("command.llovelyr.level.success", 
                finalCount, newLevel),
            true
        );
    }
    
    if (failCount > 0) {
        int finalFailCount = failCount;
        ctx.getSource().sendFailure(
            Component.translatable("command.llovelyr.level.failed", finalFailCount)
        );
    }
    
    return successCount;
}
```

### Set Exp with Auto-Level-Up

```java
private static int executeSetExp(CommandContext<CommandSourceStack> ctx, int newExp) 
        throws CommandSyntaxException {
    Collection<? extends Entity> targets = getTargetRobots(ctx);
    int successCount = 0;
    
    for (Entity entity : targets) {
        if (entity instanceof InternalEntity robot) {
            int oldLevel = robot.getLevel();
            robot.combatStats.setExperience(newExp);
            
            // Attempt level-ups
            if (robot.nativeEntity.hasFeature(LevelFeature.class)) {
                LevelFeature levelFeature = robot.nativeEntity.getFeature(LevelFeature.class).get();
                int levelsGained = 0;
                
                while (levelFeature.canLevelUp() && 
                       robot.combatStats.getExperience() >= levelFeature.getExpForNextLevel()) {
                    
                    int expNeeded = levelFeature.getExpForNextLevel();
                    
                    if (levelFeature.tryLevelUp()) {
                        robot.combatStats.setLevel(levelFeature.getCurrentLevel());
                        robot.combatStats.setExperience(
                            robot.combatStats.getExperience() - expNeeded
                        );
                        robot.recalculateAttributes();
                        levelsGained++;
                    } else {
                        break;
                    }
                }
                
                if (levelsGained > 0) {
                    int newLevel = robot.getLevel();
                    LovelyLegacy.LOGGER.info("Robot {} leveled up from {} to {} (gained {} levels)", 
                        robot.getUUID(), oldLevel, newLevel, levelsGained);
                    
                    ctx.getSource().sendSuccess(
                        () -> Component.translatable("command.llovelyr.exp.leveled_up",
                            robot.getName(), oldLevel, newLevel),
                        false
                    );
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
```


## Example 5: Different Calculation Strategies

### Linear Strategy (Current Formula)

```java
public class LinearAttributeStrategy implements AttributeCalculationStrategy {
    @Override
    public int calculateHp(int level, int baseValue) {
        return baseValue + level * baseValue / 50; // 2% per level
    }
    
    @Override
    public int calculateAttack(int level, int baseValue) {
        return baseValue + level * baseValue / 50; // 2% per level
    }
    
    @Override
    public int calculateDefense(int level, int baseValue) {
        return baseValue + level * baseValue / 50; // 2% per level
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

// Usage
VANILLA.withFeature(CombatLevelFeature.class, 
    new CombatLevelFeature(100, 10, 5, new LinearAttributeStrategy()));
```

### Exponential Strategy (For Powerful Robots)

```java
public class ExponentialAttributeStrategy implements AttributeCalculationStrategy {
    private final double multiplier;
    
    public ExponentialAttributeStrategy(double multiplier) {
        this.multiplier = multiplier; // e.g., 1.05 for 5% per level
    }
    
    @Override
    public int calculateHp(int level, int baseValue) {
        return (int) (baseValue * Math.pow(multiplier, level));
    }
    
    @Override
    public int calculateAttack(int level, int baseValue) {
        return (int) (baseValue * Math.pow(multiplier, level));
    }
    
    @Override
    public int calculateDefense(int level, int baseValue) {
        return (int) (baseValue * Math.pow(multiplier, level));
    }
    
    @Override
    public double calculateArmor(int defense) {
        return Math.min(defense, 30);
    }
    
    @Override
    public double calculateArmorToughness(double armorLevel) {
        return Math.max(0, armorLevel - 30);
    }
}

// Usage - Dragon gets stronger faster
DRAGON.withFeature(CombatLevelFeature.class, 
    new CombatLevelFeature(150, 15, 10, new ExponentialAttributeStrategy(1.05)));
```

### Custom Strategy (For Special Robots)

```java
public class KitsuneTailAttributeStrategy implements AttributeCalculationStrategy {
    // Kitsune gets power boosts every 30 levels (tail unlock)
    
    @Override
    public int calculateHp(int level, int baseValue) {
        int tailTier = level / 30; // 0-9 tails
        double tailBonus = 1.0 + (tailTier * 0.15); // 15% per tail
        return (int) ((baseValue + level * baseValue / 50) * tailBonus);
    }
    
    @Override
    public int calculateAttack(int level, int baseValue) {
        int tailTier = level / 30;
        double tailBonus = 1.0 + (tailTier * 0.20); // 20% per tail
        return (int) ((baseValue + level * baseValue / 50) * tailBonus);
    }
    
    @Override
    public int calculateDefense(int level, int baseValue) {
        int tailTier = level / 30;
        double tailBonus = 1.0 + (tailTier * 0.10); // 10% per tail
        return (int) ((baseValue + level * baseValue / 50) * tailBonus);
    }
    
    @Override
    public double calculateArmor(int defense) {
        return Math.min(defense, 30);
    }
    
    @Override
    public double calculateArmorToughness(double armorLevel) {
        return Math.max(0, armorLevel - 30);
    }
}

// Usage - Kitsune gets tail-based power spikes
KITSUNE.withFeature(CombatLevelFeature.class, 
    new CombatLevelFeature(120, 12, 8, new KitsuneTailAttributeStrategy()));
```

## Example 6: Config Integration

### Config Entries

```java
public class LovelyConfigs {
    // Existing configs...
    
    // New configs for registry
    public static int MaxOwnerRobots = 10;
    
    // New configs for base stats (per robot type)
    public static int VanillaBaseHp = 100;
    public static int VanillaBaseAttack = 10;
    public static int VanillaBaseDefense = 5;
    
    public static int DragonBaseHp = 150;
    public static int DragonBaseAttack = 15;
    public static int DragonBaseDefense = 10;
    
    public static int KitsuneBaseHp = 120;
    public static int KitsuneBaseAttack = 12;
    public static int KitsuneBaseDefense = 8;
    
    // Strategy selection (optional)
    public static String VanillaStrategy = "linear";
    public static String DragonStrategy = "exponential";
    public static String KitsuneStrategy = "kitsune_tail";
}
```

### Config Reload

```java
public class NativeRobotType {
    
    public static void reloadFromConfig() {
        // Update max levels
        VANILLA.withMaxLevel(LovelyConfigs.VanillaMaxLevel);
        DRAGON.withMaxLevel(LovelyConfigs.DragonMaxLevel);
        KITSUNE.withMaxLevel(LovelyConfigs.KitsuneMaxLevel);
        
        // Update combat features
        VANILLA.getFeature(CombatLevelFeature.class).ifPresent(combat -> {
            combat.setBaseHp(LovelyConfigs.VanillaBaseHp);
            combat.setBaseAttack(LovelyConfigs.VanillaBaseAttack);
            combat.setBaseDefense(LovelyConfigs.VanillaBaseDefense);
            combat.setStrategy(getStrategyFromConfig(LovelyConfigs.VanillaStrategy));
        });
        
        DRAGON.getFeature(CombatLevelFeature.class).ifPresent(combat -> {
            combat.setBaseHp(LovelyConfigs.DragonBaseHp);
            combat.setBaseAttack(LovelyConfigs.DragonBaseAttack);
            combat.setBaseDefense(LovelyConfigs.DragonBaseDefense);
            combat.setStrategy(getStrategyFromConfig(LovelyConfigs.DragonStrategy));
        });
        
        KITSUNE.getFeature(CombatLevelFeature.class).ifPresent(combat -> {
            combat.setBaseHp(LovelyConfigs.KitsuneBaseHp);
            combat.setBaseAttack(LovelyConfigs.KitsuneBaseAttack);
            combat.setBaseDefense(LovelyConfigs.KitsuneBaseDefense);
            combat.setStrategy(getStrategyFromConfig(LovelyConfigs.KitsuneStrategy));
        });
        
        LovelyLegacy.LOGGER.info("Reloaded robot types from config");
    }
    
    private static AttributeCalculationStrategy getStrategyFromConfig(String strategyName) {
        return switch (strategyName.toLowerCase()) {
            case "exponential" -> new ExponentialAttributeStrategy(1.05);
            case "kitsune_tail" -> new KitsuneTailAttributeStrategy();
            default -> new LinearAttributeStrategy();
        };
    }
}
```

## Example 7: Benefits of Entity Reference

### Teleport to Robot Command

```java
// New command enabled by entity reference
private static int executeTeleportToRobot(CommandContext<CommandSourceStack> ctx, int robotIndex) 
        throws CommandSyntaxException {
    ServerPlayer player = ctx.getSource().getPlayerOrException();
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
    
    // Get entity directly from reference
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
```

### Recall Robot Command

```java
// Teleport robot to player
private static int executeRecallRobot(CommandContext<CommandSourceStack> ctx, int robotIndex) 
        throws CommandSyntaxException {
    ServerPlayer player = ctx.getSource().getPlayerOrException();
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
```

### Heal All Robots Command

```java
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
            // Direct access to entity for healing
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
```

### Get Robot Stats Command

```java
// Get detailed stats for a robot
private static int executeGetRobotStats(CommandContext<CommandSourceStack> ctx, int robotIndex) 
        throws CommandSyntaxException {
    ServerPlayer player = ctx.getSource().getPlayerOrException();
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
    
    // Access entity directly for all stats
    if (robot instanceof InternalEntity internalRobot) {
        String name = entry.getRobotName() != null ? entry.getRobotName() : "Unnamed";
        String type = entry.getRobotType();
        double[] pos = entry.getPosition();
        String dimension = entry.getDimension();
        
        ctx.getSource().sendSuccess(
            () -> Component.literal(String.format(
                "§b=== %s (%s) ===", name, type
            )),
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

## Example 8: Testing

### Unit Test for CombatLevelFeature

```java
public class CombatLevelFeatureTest {
    
    @Test
    public void testLinearScaling() {
        CombatLevelFeature combat = new CombatLevelFeature(100, 10, 5);
        
        // Level 0
        assertEquals(100, combat.calculateHp(0));
        assertEquals(10, combat.calculateAttack(0));
        assertEquals(5, combat.calculateDefense(0));
        
        // Level 50 (should be 2x base)
        assertEquals(200, combat.calculateHp(50));
        assertEquals(20, combat.calculateAttack(50));
        assertEquals(10, combat.calculateDefense(50));
        
        // Level 100 (should be 3x base)
        assertEquals(300, combat.calculateHp(100));
        assertEquals(30, combat.calculateAttack(100));
        assertEquals(15, combat.calculateDefense(100));
    }
    
    @Test
    public void testArmorCap() {
        CombatLevelFeature combat = new CombatLevelFeature(100, 10, 50);
        
        // Defense 50 should cap armor at 30
        assertEquals(30.0, combat.calculateArmor(200), 0.01);
        
        // Overflow becomes toughness
        assertEquals(20.0, combat.calculateArmorToughness(200), 0.01);
    }
    
    @Test
    public void testExponentialScaling() {
        CombatLevelFeature combat = new CombatLevelFeature(
            100, 10, 5, 
            new ExponentialAttributeStrategy(1.05)
        );
        
        // Level 10 should be ~1.63x base
        assertTrue(combat.calculateHp(10) > 160);
        assertTrue(combat.calculateHp(10) < 165);
        
        // Level 20 should be ~2.65x base
        assertTrue(combat.calculateHp(20) > 260);
        assertTrue(combat.calculateHp(20) < 270);
    }
}
```

### Integration Test for Registry

```java
public class RobotRegistryTest {
    
    @Test
    public void testSpawnLimit() {
        OwnerRobotRegistry registry = new OwnerRobotRegistry();
        UUID ownerId = UUID.randomUUID();
        
        // Can spawn up to limit
        assertTrue(registry.canSpawnRobot(ownerId, 3));
        
        // Register 3 robots
        for (int i = 0; i < 3; i++) {
            RobotRegistryEntry entry = new RobotRegistryEntry(
                UUID.randomUUID(), ownerId, "Robot" + i, "vanilla",
                "minecraft:overworld", 0, 0, 0
            );
            registry.registerRobot(entry);
        }
        
        // Cannot spawn more
        assertFalse(registry.canSpawnRobot(ownerId, 3));
        assertEquals(3, registry.getRobotCount(ownerId));
    }
    
    @Test
    public void testUnregister() {
        OwnerRobotRegistry registry = new OwnerRobotRegistry();
        UUID ownerId = UUID.randomUUID();
        UUID robotId = UUID.randomUUID();
        
        RobotRegistryEntry entry = new RobotRegistryEntry(
            robotId, ownerId, "TestRobot", "vanilla",
            "minecraft:overworld", 0, 0, 0
        );
        registry.registerRobot(entry);
        
        assertEquals(1, registry.getRobotCount(ownerId));
        
        registry.unregisterRobot(robotId);
        
        assertEquals(0, registry.getRobotCount(ownerId));
        assertTrue(registry.canSpawnRobot(ownerId, 1));
    }
}
```

---

**Note**: These examples demonstrate the practical usage of all proposed features. Each example shows both the "before" and "after" states where applicable, making it easy to understand the improvements.
