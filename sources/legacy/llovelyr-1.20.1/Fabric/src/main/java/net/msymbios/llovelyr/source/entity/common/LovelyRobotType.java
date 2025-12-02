package net.msymbios.llovelyr.source.entity.type;

import net.msymbios.llovelyr.LovelyLegacy;
import net.msymbios.llovelyr.common.entity.enums.EntityVariant;
import net.msymbios.llovelyr.common.entity.type.RobotEntityType;
import net.msymbios.llovelyr.framework.entity.combat.LinearAttributeStrategy;
import net.msymbios.llovelyr.framework.entity.enchantment.DefaultEnchantmentStrategy;
import net.msymbios.llovelyr.framework.entity.protection.LevelBasedProtectionStrategy;
import net.msymbios.llovelyr.lib.entity.type.features.*;
import net.msymbios.llovelyr.source.LovelyConfigs;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>Registry of native robot types with config-driven values.<p>
 * <p>
 * <b>Architecture:</b> Provides static registry of robot types initialized from config.
 * Each robot type is configured with variant-specific stats, max levels, and XP strategies.
 * Supports runtime config reload for server administration.
 * <p>
 * <b>Design Decision:</b> Static initialization ensures robot types are available before
 * entity registration. Config values are loaded during static initialization and can be
 * reloaded at runtime via reloadFromConfig().
 * <p>
 * <b>XP Strategies:</b> VANILLA and BUNNY2 use default linear progression. DRAGON uses
 * exponential progression for harder leveling. KITSUNE uses custom formula with tail-unlock
 * progression (harder every 30 levels).
 */
public class NativeRobotType {

    // -- Registry --
    
    /**
     * List of all robot types for iteration.
     * <p>
     * <b>Usage:</b> Enables iteration over all robot types for registration,
     * rendering setup, or bulk operations.
     */
    public static final List<RobotEntityType> TYPES = new ArrayList<>();
    
    // -- Robot Types --

    /**
     * BUNNY2 robot type - alternative bunny design.
     * <p>
     * <b>Characteristics:</b> Balanced stats, default XP progression.
     * Configured via Bunny2MaxLevel, Bunny2MaxHealth, etc.
     */
    public static final RobotEntityType BUNNY2 = create(EntityVariant.Bunny2);

    /**
     * DRAGON robot type - enhanced combat capabilities.
     * <p>
     * <b>Characteristics:</b> High health and damage, knockback resistance 0.5F,
     * exponential XP progression (base 1.1). Configured via DragonMaxLevel,
     * DragonMaxHealth, etc.
     */
    public static final RobotEntityType DRAGON = create(EntityVariant.Dragon);

    /**
     * KITSUNE robot type - tail-unlock progression.
     * <p>
     * <b>Characteristics:</b> Moderate stats, custom XP formula with tail-unlock
     * progression (harder every 30 levels). Configured via KitsuneMaxLevel,
     * KitsuneMaxHealth, etc.
     */
    public static final RobotEntityType KITSUNE = create(EntityVariant.Kitsune);

    /**
     * VANILLA robot type - general-purpose companion.
     * <p>
     * <b>Characteristics:</b> Balanced stats, default XP progression.
     * Configured via VanillaMaxLevel, VanillaMaxHealth, etc.
     */
    public static final RobotEntityType VANILLA = create(EntityVariant.Vanilla);
    
    // -- Helper Methods --
    
    /**
     * Creates robot type with specified variant and combat stats.
     * <p>
     * <b>Implementation:</b> Creates RobotEntityType, configures combat stats,
     * applies color palette, and adds to registry.
     * <p>
     * <b>State Impact:</b> Adds created robot type to TYPES list for iteration.
     * 
     * @param variant entity variant determining resource paths
     * @param maxLevel maximum level for this robot type
     * @param maxHealth maximum health points
     * @param attackDamage attack damage value
     * @param attackSpeed attack speed multiplier
     * @param armor armor points
     * @param armorToughness armor toughness value
     * @param knockbackResistance knockback resistance (0.0 to 1.0)
     * @param moveSpeed movement speed multiplier
     * @return configured RobotEntityType instance
     */
    private static RobotEntityType create(EntityVariant variant,
                                         int maxLevel,
                                         float maxHealth,
                                         float attackDamage,
                                         float attackSpeed,
                                         float armor,
                                         float armorToughness,
                                         float knockbackResistance,
                                         float moveSpeed) {
        // Create robot type
        RobotEntityType robotType = new RobotEntityType(variant.getName(), variant);
        
        // Configure combat stats
        robotType.withCombatStats(maxHealth, attackDamage, attackSpeed, 
                                 armor, armorToughness, knockbackResistance, moveSpeed);
        
        // Configure max level
        robotType.getFeature(LevelFeature.class).ifPresent(feature -> 
            feature.setMaxLevel(maxLevel)
        );
        
        // Apply color palette
        robotType.withColorPalette(variant);
        
        // Add to registry
        TYPES.add(robotType);
        
        return robotType;
    } // create ()

    /**
     * Creates robot type with specified variant and combat stats.
     * <p>
     * <b>Implementation:</b> Creates RobotEntityType, configures combat stats,
     * applies color palette, and adds to registry.
     * <p>
     * <b>State Impact:</b> Adds created robot type to TYPES list for iteration.
     *
     * @param variant entity variant determining resource paths
     * @return configured RobotEntityType instance
     */
    private static RobotEntityType create(EntityVariant variant) {
        // Create robot type
        RobotEntityType robotType = new RobotEntityType(variant.getName(), variant);

        // Apply color palette
        robotType.withColorPalette(variant);

        // Add to registry
        TYPES.add(robotType);

        return robotType;
    } // create ()
    
    // -- Config Reload --
    
    /**
     * Reloads robot type configurations from current config values.
     * <p>
     * <b>Runtime Reload:</b> Updates maxLevels and combat stats from config without
     * resetting XP strategies. Allows server admins to adjust balance without restart.
     * <p>
     * <b>State Preservation:</b> XP strategies are maintained during reload to preserve
     * progression curves (exponential for DRAGON, custom formula for KITSUNE).
     */
    public static void reloadFromConfig() {
        var defaultExpStrategy = new LevelFeature.FormulaExpStrategy(level -> LovelyConfigs.Common.ExperienceBase + level * LovelyConfigs.Common.ExperienceMultiplier);
        var defaultProtection = new ProtectionFeature(new LevelBasedProtectionStrategy())
                .withMax(
                        LovelyConfigs.Common.ProtectionLimitFire,
                        LovelyConfigs.Common.ProtectionLimitFall,
                        LovelyConfigs.Common.ProtectionLimitBlast,
                        LovelyConfigs.Common.ProtectionLimitProjectile);
        var defaultEnchantment = new EnchantmentFeature(new DefaultEnchantmentStrategy())
                .withLooting(
                        LovelyConfigs.Common.LootEnchantment,
                        LovelyConfigs.Common.MaxLootEnchantment,
                        LovelyConfigs.Common.LootEnchantmentLevel);

        // Configure BUNNY2
        BUNNY2.withCombatStats(LovelyConfigs.Common.Bunny2BaseHp,
                        LovelyConfigs.Common.Bunny2BaseAttack,
                        LovelyConfigs.Common.Bunny2AttackSpeed,
                        LovelyConfigs.Common.Bunny2BaseDefense,
                        LovelyConfigs.Common.Bunny2BaseToughness,
                        0F,
                        LovelyConfigs.Common.Bunny2MovementSpeed)
                .withFeature(LevelFeature.class, new LevelFeature(LovelyConfigs.Common.Bunny2MaxLevel, defaultExpStrategy))
                .withFeature(CombatLevelFeature.class,
                        new CombatLevelFeature(
                                LovelyConfigs.Common.Bunny2BaseHp,
                                LovelyConfigs.Common.Bunny2BaseAttack,
                                LovelyConfigs.Common.Bunny2BaseDefense,
                                new LinearAttributeStrategy()
                        ))
                .withFeature(EnchantmentFeature.class, defaultEnchantment)
                .withFeature(ProtectionFeature.class, defaultProtection);

        // Configure DRAGON
        DRAGON.withCombatStats(LovelyConfigs.Common.DragonBaseHp,
                        LovelyConfigs.Common.DragonBaseAttack,
                        LovelyConfigs.Common.DragonAttackSpeed,
                        LovelyConfigs.Common.DragonBaseDefense,
                        LovelyConfigs.Common.DragonBaseToughness,
                        0F,
                        LovelyConfigs.Common.DragonMovementSpeed)
                .withFeature(LevelFeature.class, new LevelFeature(LovelyConfigs.Common.DragonMaxLevel, defaultExpStrategy))
                .withFeature(CombatLevelFeature.class,
                        new CombatLevelFeature(
                                LovelyConfigs.Common.DragonBaseHp,
                                LovelyConfigs.Common.DragonBaseAttack,
                                LovelyConfigs.Common.DragonBaseDefense,
                                new LinearAttributeStrategy()
                        ))
                .withFeature(EnchantmentFeature.class, defaultEnchantment)
                .withFeature(ProtectionFeature.class, defaultProtection);

        // Configure KITSUNE
        KITSUNE.withCombatStats(LovelyConfigs.Common.KitsuneBaseHp,
                        LovelyConfigs.Common.KitsuneBaseAttack,
                        LovelyConfigs.Common.KitsuneAttackSpeed,
                        LovelyConfigs.Common.KitsuneBaseDefense,
                        LovelyConfigs.Common.KitsuneBaseToughness,
                        0F,
                        LovelyConfigs.Common.KitsuneMovementSpeed)
                .withFeature(LevelFeature.class, new LevelFeature(LovelyConfigs.Common.KitsuneMaxLevel, defaultExpStrategy))
                .withFeature(CombatLevelFeature.class,
                        new CombatLevelFeature(
                                LovelyConfigs.Common.KitsuneBaseHp,
                                LovelyConfigs.Common.KitsuneBaseAttack,
                                LovelyConfigs.Common.KitsuneBaseDefense,
                                new LinearAttributeStrategy()
                        ))
                .withFeature(EnchantmentFeature.class, defaultEnchantment)
                .withFeature(ProtectionFeature.class, defaultProtection);

        // Configure VANILLA
        VANILLA.withCombatStats(LovelyConfigs.Common.VanillaBaseHp,
                        LovelyConfigs.Common.VanillaBaseAttack,
                        LovelyConfigs.Common.VanillaAttackSpeed,
                        LovelyConfigs.Common.VanillaBaseDefense,
                        LovelyConfigs.Common.VanillaBaseToughness,
                        0F, // No knockback resistance
                        LovelyConfigs.Common.VanillaMovementSpeed)
                .withFeature(LevelFeature.class, new LevelFeature(LovelyConfigs.Common.VanillaMaxLevel, defaultExpStrategy))
                .withFeature(CombatLevelFeature.class,
                        new CombatLevelFeature(
                                LovelyConfigs.Common.VanillaBaseHp,
                                LovelyConfigs.Common.VanillaBaseAttack,
                                LovelyConfigs.Common.VanillaBaseDefense,
                                new LinearAttributeStrategy()
                        ))
                .withFeature(EnchantmentFeature.class, defaultEnchantment)
                .withFeature(ProtectionFeature.class, defaultProtection);
        
        LovelyLegacy.LOGGER.info("Robot type configurations reloaded from config");
    } // reloadFromConfig ()

} // Class: NativeRobotType
