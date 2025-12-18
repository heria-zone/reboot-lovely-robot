package net.heriazone.lovelylib.common.entity.common;

import net.heriazone.lovelylib.api.entity.features.*;
import net.heriazone.lovelylib.common.configs.SharedConfigs;
import net.heriazone.lovelylib.common.entity.NativeEntityType;
import net.heriazone.lovelylib.common.entity.combat.LinearAttributeStrategy;
import net.heriazone.lovelylib.common.entity.enums.*;
import net.heriazone.lovelylib.hzlib.api.entity.features.LevelFeature;
import net.heriazone.lovelylib.hzlib.framework.entity.enchantment.DefaultEnchantmentStrategy;
import net.heriazone.lovelylib.hzlib.framework.entity.protection.LevelBasedProtectionStrategy;

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
public class LegacyRobotType extends LovelyRobotType {

    // -- Robot Types --

    /**
     * BUNNY robot type - original bunny design.
     * <p>
     * <b>Characteristics:</b> Balanced stats, default XP progression.
     * Configured via BunnyMaxLevel, BunnyBaseHp, etc.
     */
    public static final NativeEntityType BUNNY = create(EntityVariant.Bunny);

    /**
     * BUNNY2 robot type - alternative bunny design.
     * <p>
     * <b>Characteristics:</b> Balanced stats, default XP progression.
     * Configured via Bunny2MaxLevel, Bunny2BaseHp, etc.
     */
    public static final NativeEntityType BUNNY2 = create(EntityVariant.Bunny2);

    /**
     * DRAGON robot type - enhanced combat capabilities.
     * <p>
     * <b>Characteristics:</b> High health and damage, knockback resistance 0.5F,
     * exponential XP progression (base 1.1). Configured via DragonMaxLevel,
     * DragonBaseHp, etc.
     */
    public static final NativeEntityType DRAGON = create(EntityVariant.Dragon);

    /**
     * HONEY robot type - support-oriented companion.
     * <p>
     * <b>Characteristics:</b> Lower combat stats, default XP progression.
     * Configured via HoneyMaxLevel, HoneyBaseHp, etc.
     */
    public static final NativeEntityType HONEY = create(EntityVariant.Honey);

    /**
     * KITSUNE robot type - tail-unlock progression.
     * <p>
     * <b>Characteristics:</b> Moderate stats, custom XP formula with tail-unlock
     * progression (harder every 30 levels). Configured via KitsuneMaxLevel,
     * KitsuneBaseHp, etc.
     */
    public static final NativeEntityType KITSUNE = create(EntityVariant.Kitsune);

    /**
     * NEKO robot type - agile combat specialist.
     * <p>
     * <b>Characteristics:</b> High attack and speed, default XP progression.
     * Configured via NekoMaxLevel, NekoBaseHp, etc.
     */
    public static final NativeEntityType NEKO = create(EntityVariant.Neko);

    /**
     * VANILLA robot type - general-purpose companion.
     * <p>
     * <b>Characteristics:</b> Balanced stats, default XP progression.
     * Configured via VanillaMaxLevel, VanillaBaseHp, etc.
     */
    public static final NativeEntityType VANILLA = create(EntityVariant.Vanilla);

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
        var defaultExpStrategy = new LevelFeature.FormulaExpStrategy(level -> SharedConfigs.Common.ExperienceBase + level * SharedConfigs.Common.ExperienceMultiplier);
        var defaultProtection = new ProtectionFeature(new LevelBasedProtectionStrategy())
                .withMax(
                        SharedConfigs.Common.ProtectionLimitFire,
                        SharedConfigs.Common.ProtectionLimitFall,
                        SharedConfigs.Common.ProtectionLimitBlast,
                        SharedConfigs.Common.ProtectionLimitProjectile);
        var defaultEnchantment = new EnchantmentFeature(new DefaultEnchantmentStrategy())
                .withLooting(
                        SharedConfigs.Common.LootEnchantment,
                        SharedConfigs.Common.MaxLootEnchantment,
                        SharedConfigs.Common.LootEnchantmentLevel);

        // Configure BUNNY
        BUNNY.withCombatStats(SharedConfigs.Common.BunnyBaseHp,
                        SharedConfigs.Common.BunnyBaseAttack,
                        SharedConfigs.Common.BunnyAttackSpeed,
                        SharedConfigs.Common.BunnyBaseDefense,
                        SharedConfigs.Common.BunnyBaseToughness,
                        0F,
                        SharedConfigs.Common.BunnyMovementSpeed)
                .withFeature(LevelFeature.class, new LevelFeature(SharedConfigs.Common.BunnyMaxLevel, defaultExpStrategy))
                .withFeature(CombatLevelFeature.class,
                        new CombatLevelFeature(
                                SharedConfigs.Common.BunnyBaseHp,
                                SharedConfigs.Common.BunnyBaseAttack,
                                SharedConfigs.Common.BunnyBaseDefense,
                                new LinearAttributeStrategy()
                        ))
                .withFeature(EnchantmentFeature.class, defaultEnchantment)
                .withFeature(ProtectionFeature.class, defaultProtection);

        // Configure BUNNY2
        BUNNY2.withCombatStats(SharedConfigs.Common.Bunny2BaseHp,
                        SharedConfigs.Common.Bunny2BaseAttack,
                        SharedConfigs.Common.Bunny2AttackSpeed,
                        SharedConfigs.Common.Bunny2BaseDefense,
                        SharedConfigs.Common.Bunny2BaseToughness,
                        0F,
                        SharedConfigs.Common.Bunny2MovementSpeed)
                .withFeature(LevelFeature.class, new LevelFeature(SharedConfigs.Common.Bunny2MaxLevel, defaultExpStrategy))
                .withFeature(CombatLevelFeature.class,
                        new CombatLevelFeature(
                                SharedConfigs.Common.Bunny2BaseHp,
                                SharedConfigs.Common.Bunny2BaseAttack,
                                SharedConfigs.Common.Bunny2BaseDefense,
                                new LinearAttributeStrategy()
                        ))
                .withFeature(EnchantmentFeature.class, defaultEnchantment)
                .withFeature(ProtectionFeature.class, defaultProtection);

        // Configure DRAGON
        DRAGON.withCombatStats(SharedConfigs.Common.DragonBaseHp,
                        SharedConfigs.Common.DragonBaseAttack,
                        SharedConfigs.Common.DragonAttackSpeed,
                        SharedConfigs.Common.DragonBaseDefense,
                        SharedConfigs.Common.DragonBaseToughness,
                        0F,
                        SharedConfigs.Common.DragonMovementSpeed)
                .withFeature(LevelFeature.class, new LevelFeature(SharedConfigs.Common.DragonMaxLevel, defaultExpStrategy))
                .withFeature(CombatLevelFeature.class,
                        new CombatLevelFeature(
                                SharedConfigs.Common.DragonBaseHp,
                                SharedConfigs.Common.DragonBaseAttack,
                                SharedConfigs.Common.DragonBaseDefense,
                                new LinearAttributeStrategy()
                        ))
                .withFeature(EnchantmentFeature.class, defaultEnchantment)
                .withFeature(ProtectionFeature.class, defaultProtection);

        // Configure HONEY
        HONEY.withCombatStats(SharedConfigs.Common.HoneyBaseHp,
                        SharedConfigs.Common.HoneyBaseAttack,
                        SharedConfigs.Common.HoneyAttackSpeed,
                        SharedConfigs.Common.HoneyBaseDefense,
                        SharedConfigs.Common.HoneyBaseToughness,
                        0F,
                        SharedConfigs.Common.HoneyMovementSpeed)
                .withFeature(LevelFeature.class, new LevelFeature(SharedConfigs.Common.HoneyMaxLevel, defaultExpStrategy))
                .withFeature(CombatLevelFeature.class,
                        new CombatLevelFeature(
                                SharedConfigs.Common.HoneyBaseHp,
                                SharedConfigs.Common.HoneyBaseAttack,
                                SharedConfigs.Common.HoneyBaseDefense,
                                new LinearAttributeStrategy()
                        ))
                .withFeature(EnchantmentFeature.class, defaultEnchantment)
                .withFeature(ProtectionFeature.class, defaultProtection);

        // Configure KITSUNE
        KITSUNE.withCombatStats(SharedConfigs.Common.KitsuneBaseHp,
                        SharedConfigs.Common.KitsuneBaseAttack,
                        SharedConfigs.Common.KitsuneAttackSpeed,
                        SharedConfigs.Common.KitsuneBaseDefense,
                        SharedConfigs.Common.KitsuneBaseToughness,
                        0F,
                        SharedConfigs.Common.KitsuneMovementSpeed)
                .withFeature(LevelFeature.class, new LevelFeature(SharedConfigs.Common.KitsuneMaxLevel, defaultExpStrategy))
                .withFeature(CombatLevelFeature.class,
                        new CombatLevelFeature(
                                SharedConfigs.Common.KitsuneBaseHp,
                                SharedConfigs.Common.KitsuneBaseAttack,
                                SharedConfigs.Common.KitsuneBaseDefense,
                                new LinearAttributeStrategy()
                        ))
                .withFeature(EnchantmentFeature.class, defaultEnchantment)
                .withFeature(ProtectionFeature.class, defaultProtection);

        // Configure NEKO
        NEKO.withCombatStats(SharedConfigs.Common.NekoBaseHp,
                        SharedConfigs.Common.NekoBaseAttack,
                        SharedConfigs.Common.NekoAttackSpeed,
                        SharedConfigs.Common.NekoBaseDefense,
                        SharedConfigs.Common.NekoBaseToughness,
                        0F,
                        SharedConfigs.Common.NekoMovementSpeed)
                .withFeature(LevelFeature.class, new LevelFeature(SharedConfigs.Common.NekoMaxLevel, defaultExpStrategy))
                .withFeature(CombatLevelFeature.class,
                        new CombatLevelFeature(
                                SharedConfigs.Common.NekoBaseHp,
                                SharedConfigs.Common.NekoBaseAttack,
                                SharedConfigs.Common.NekoBaseDefense,
                                new LinearAttributeStrategy()
                        ))
                .withFeature(EnchantmentFeature.class, defaultEnchantment)
                .withFeature(ProtectionFeature.class, defaultProtection);

        // Configure VANILLA
        VANILLA.withCombatStats(SharedConfigs.Common.VanillaBaseHp,
                        SharedConfigs.Common.VanillaBaseAttack,
                        SharedConfigs.Common.VanillaAttackSpeed,
                        SharedConfigs.Common.VanillaBaseDefense,
                        SharedConfigs.Common.VanillaBaseToughness,
                        0F, // No knockback resistance
                        SharedConfigs.Common.VanillaMovementSpeed)
                .withFeature(LevelFeature.class, new LevelFeature(SharedConfigs.Common.VanillaMaxLevel, defaultExpStrategy))
                .withFeature(CombatLevelFeature.class,
                        new CombatLevelFeature(
                                SharedConfigs.Common.VanillaBaseHp,
                                SharedConfigs.Common.VanillaBaseAttack,
                                SharedConfigs.Common.VanillaBaseDefense,
                                new LinearAttributeStrategy()
                        ))
                .withFeature(EnchantmentFeature.class, defaultEnchantment)
                .withFeature(ProtectionFeature.class, defaultProtection);

        //LovelyConstant.LOGGER.info("Robot type configurations reloaded from config");
    } // reloadFromConfig ()

} // Class: LegacyRobotType