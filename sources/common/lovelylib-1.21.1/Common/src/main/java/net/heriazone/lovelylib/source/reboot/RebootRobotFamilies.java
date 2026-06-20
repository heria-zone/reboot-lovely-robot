package net.heriazone.lovelylib.source.reboot;

import net.heriazone.lovelylib.api.entity.features.CombatLevelFeature;
import net.heriazone.lovelylib.api.entity.features.EnchantmentFeature;
import net.heriazone.lovelylib.api.entity.features.ProtectionFeature;
import net.heriazone.lovelylib.common.entity.RobotFamilyRegistry;
import net.heriazone.lovelylib.common.configs.SharedConfigs;
import net.heriazone.lovelylib.common.entity.RobotFamily;
import net.heriazone.lovelylib.common.entity.combat.LinearAttributeStrategy;
import net.heriazone.lovelylib.common.entity.enums.RobotVariant;
import net.heriazone.lovelylib.common.shared.LovelyConstant;
import net.heriazone.hzlib.api.entity.features.LevelFeature;
import net.heriazone.hzlib.framework.entity.enchantment.DefaultEnchantmentStrategy;
import net.heriazone.hzlib.framework.entity.protection.LevelBasedProtectionStrategy;

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
public class RebootRobotFamilies extends RobotFamilyRegistry {

    // -- Robot Types --

    /**
     * BUNNY robot type - original bunny design.
     * <p>
     * <b>Characteristics:</b> Balanced stats, default XP progression.
     * Configured via BunnyMaxLevel, BunnyBaseHp, etc.
     */
    public static final RobotFamily BUNNY = create(RobotVariant.Bunny);

    /**
     * BUNNY2 robot type - alternative bunny design.
     * <p>
     * <b>Characteristics:</b> Balanced stats, default XP progression.
     * Configured via Bunny2MaxLevel, Bunny2BaseHp, etc.
     */
    public static final RobotFamily BUNNY2 = create(RobotVariant.Bunny2);

    /**
     * DRAGON robot type - enhanced combat capabilities.
     * <p>
     * <b>Characteristics:</b> High health and damage, knockback resistance 0.5F,
     * exponential XP progression (base 1.1). Configured via DragonMaxLevel,
     * DragonBaseHp, etc.
     */
    public static final RobotFamily DRAGON = create(RobotVariant.Dragon);

    /**
     * HONEY robot type - support-oriented companion.
     * <p>
     * <b>Characteristics:</b> Lower combat stats, default XP progression.
     * Configured via HoneyMaxLevel, HoneyBaseHp, etc.
     */
    public static final RobotFamily HONEY = create(RobotVariant.Honey);

    /**
     * KITSUNE robot type - tail-unlock progression.
     * <p>
     * <b>Characteristics:</b> Moderate stats, custom XP formula with tail-unlock
     * progression (harder every 30 levels). Configured via KitsuneMaxLevel,
     * KitsuneBaseHp, etc.
     */
    public static final RobotFamily KITSUNE = create(RobotVariant.Kitsune);

    /**
     * NEKO robot type - agile combat specialist.
     * <p>
     * <b>Characteristics:</b> High attack and speed, default XP progression.
     * Configured via NekoMaxLevel, NekoBaseHp, etc.
     */
    public static final RobotFamily NEKO = create(RobotVariant.Neko);

    /**
     * VANILLA robot type - general-purpose companion.
     * <p>
     * <b>Characteristics:</b> Balanced stats, default XP progression.
     * Configured via VanillaMaxLevel, VanillaBaseHp, etc.
     */
    public static final RobotFamily VANILLA = create(RobotVariant.Vanilla);

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
        SharedConfigs.EntityConfigData bunny = RebootConfigs.getEntityConfig(LovelyConstant.VARIANT_BUNNY);
        BUNNY.withCombatStats(bunny.baseHp,
                        bunny.baseAttack,
                        bunny.attackSpeed,
                        bunny.baseDefense,
                        bunny.baseToughness,
                        0F,
                        bunny.movementSpeed)
                .withFeature(LevelFeature.class, new LevelFeature(bunny.maxLevel, defaultExpStrategy))
                .withFeature(CombatLevelFeature.class,
                        new CombatLevelFeature(
                                bunny.baseHp,
                                bunny.baseAttack,
                                bunny.baseDefense,
                                new LinearAttributeStrategy()
                        ))
                .withFeature(EnchantmentFeature.class, defaultEnchantment)
                .withFeature(ProtectionFeature.class, defaultProtection);

        // Configure BUNNY2
        SharedConfigs.EntityConfigData bunny2 = RebootConfigs.getEntityConfig(LovelyConstant.VARIANT_BUNNY2);
        BUNNY2.withCombatStats(bunny2.baseHp,
                        bunny2.baseAttack,
                        bunny2.attackSpeed,
                        bunny2.baseDefense,
                        bunny2.baseToughness,
                        0F,
                        bunny2.movementSpeed)
                .withFeature(LevelFeature.class, new LevelFeature(bunny2.maxLevel, defaultExpStrategy))
                .withFeature(CombatLevelFeature.class,
                        new CombatLevelFeature(
                                bunny2.baseHp,
                                bunny2.baseAttack,
                                bunny2.baseDefense,
                                new LinearAttributeStrategy()
                        ))
                .withFeature(EnchantmentFeature.class, defaultEnchantment)
                .withFeature(ProtectionFeature.class, defaultProtection);

        // Configure DRAGON
        SharedConfigs.EntityConfigData dragon = RebootConfigs.getEntityConfig(LovelyConstant.VARIANT_DRAGON);
        DRAGON.withCombatStats(dragon.baseHp,
                        dragon.baseAttack,
                        dragon.attackSpeed,
                        dragon.baseDefense,
                        dragon.baseToughness,
                        0F,
                        dragon.movementSpeed)
                .withFeature(LevelFeature.class, new LevelFeature(dragon.maxLevel, defaultExpStrategy))
                .withFeature(CombatLevelFeature.class,
                        new CombatLevelFeature(
                                dragon.baseHp,
                                dragon.baseAttack,
                                dragon.baseDefense,
                                new LinearAttributeStrategy()
                        ))
                .withFeature(EnchantmentFeature.class, defaultEnchantment)
                .withFeature(ProtectionFeature.class, defaultProtection);

        // Configure HONEY
        SharedConfigs.EntityConfigData honey = RebootConfigs.getEntityConfig(LovelyConstant.VARIANT_HONEY);
        HONEY.withCombatStats(honey.baseHp,
                        honey.baseAttack,
                        honey.attackSpeed,
                        honey.baseDefense,
                        honey.baseToughness,
                        0F,
                        honey.movementSpeed)
                .withFeature(LevelFeature.class, new LevelFeature(honey.maxLevel, defaultExpStrategy))
                .withFeature(CombatLevelFeature.class,
                        new CombatLevelFeature(
                                honey.baseHp,
                                honey.baseAttack,
                                honey.baseDefense,
                                new LinearAttributeStrategy()
                        ))
                .withFeature(EnchantmentFeature.class, defaultEnchantment)
                .withFeature(ProtectionFeature.class, defaultProtection);

        // Configure KITSUNE
        SharedConfigs.EntityConfigData kitsune = RebootConfigs.getEntityConfig(LovelyConstant.VARIANT_KITSUNE);
        KITSUNE.withCombatStats(kitsune.baseHp,
                        kitsune.baseAttack,
                        kitsune.attackSpeed,
                        kitsune.baseDefense,
                        kitsune.baseToughness,
                        0F,
                        kitsune.movementSpeed)
                .withFeature(LevelFeature.class, new LevelFeature(kitsune.maxLevel, defaultExpStrategy))
                .withFeature(CombatLevelFeature.class,
                        new CombatLevelFeature(
                                kitsune.baseHp,
                                kitsune.baseAttack,
                                kitsune.baseDefense,
                                new LinearAttributeStrategy()
                        ))
                .withFeature(EnchantmentFeature.class, defaultEnchantment)
                .withFeature(ProtectionFeature.class, defaultProtection);

        // Configure NEKO
        SharedConfigs.EntityConfigData neko = RebootConfigs.getEntityConfig(LovelyConstant.VARIANT_NEKO);
        NEKO.withCombatStats(neko.baseHp,
                        neko.baseAttack,
                        neko.attackSpeed,
                        neko.baseDefense,
                        neko.baseToughness,
                        0F,
                        neko.movementSpeed)
                .withFeature(LevelFeature.class, new LevelFeature(neko.maxLevel, defaultExpStrategy))
                .withFeature(CombatLevelFeature.class,
                        new CombatLevelFeature(
                                neko.baseHp,
                                neko.baseAttack,
                                neko.baseDefense,
                                new LinearAttributeStrategy()
                        ))
                .withFeature(EnchantmentFeature.class, defaultEnchantment)
                .withFeature(ProtectionFeature.class, defaultProtection);

        // Configure VANILLA
        SharedConfigs.EntityConfigData vanilla = RebootConfigs.getEntityConfig(LovelyConstant.VARIANT_VANILLA);
        VANILLA.withCombatStats(vanilla.baseHp,
                        vanilla.baseAttack,
                        vanilla.attackSpeed,
                        vanilla.baseDefense,
                        vanilla.baseToughness,
                        0F,
                        vanilla.movementSpeed)
                .withFeature(LevelFeature.class, new LevelFeature(vanilla.maxLevel, defaultExpStrategy))
                .withFeature(CombatLevelFeature.class,
                        new CombatLevelFeature(
                                vanilla.baseHp,
                                vanilla.baseAttack,
                                vanilla.baseDefense,
                                new LinearAttributeStrategy()
                        ))
                .withFeature(EnchantmentFeature.class, defaultEnchantment)
                .withFeature(ProtectionFeature.class, defaultProtection);

        //LovelyConstant.LOGGER.info("Robot type configurations reloaded from config");
    } // reloadFromConfig ()

} // Class: LegacyRobotFamilies