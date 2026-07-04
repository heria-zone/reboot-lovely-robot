package net.heriazone.lovelylib.source.tribute;

import net.heriazone.lovelylib.api.entity.features.CombatLevelFeature;
import net.heriazone.lovelylib.api.entity.features.EnchantmentFeature;
import net.heriazone.lovelylib.api.entity.features.ProtectionFeature;
import net.heriazone.lovelylib.common.configs.SharedConfigs;
import net.heriazone.lovelylib.common.entity.RobotFamilyRegistry;
import net.heriazone.lovelylib.common.entity.RobotFamily;
import net.heriazone.lovelylib.common.entity.combat.LinearAttributeStrategy;
import net.heriazone.lovelylib.common.entity.enums.EntityTexture;
import net.heriazone.lovelylib.common.entity.definition.RobotVariant;
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
public class TributeRobotFamilies extends RobotFamilyRegistry {

    // -- Restricted Color Palettes --

    /**
     * BUNNY palette — pink, yellow, purple, blue, red (5 colors).
     * <p>
     * Faithful recreation colors from the original LovelyRobot mod.
     * Default random spawn color: pink (06).
     */
    public static final java.util.List<EntityTexture> BUNNY_COLORS = java.util.List.of(
            EntityTexture.PINK,
            EntityTexture.YELLOW,
            EntityTexture.PURPLE,
            EntityTexture.BLUE,
            EntityTexture.RED
    );

    /**
     * BUNNY2 palette — pink, blue (2 colors).
     * <p>
     * Original Bunny2 color scheme from the original LovelyRobot mod.
     * Default random spawn color: pink (06).
     */
    public static final java.util.List<EntityTexture> BUNNY2_COLORS = java.util.List.of(
            EntityTexture.PINK,
            EntityTexture.BLUE
    );

    /**
     * HONEY palette — yellow, blue, pink (3 colors).
     * <p>
     * Original Honey color scheme from the original LovelyRobot mod.
     * Default random spawn color: yellow (04).
     */
    public static final java.util.List<EntityTexture> HONEY_COLORS = java.util.List.of(
            EntityTexture.YELLOW,
            EntityTexture.LIGHT_BLUE,
            EntityTexture.PINK
    );

    /**
     * VANILLA palette — pink, yellow, blue, black (4 colors).
     * <p>
     * Original Vanilla color scheme from the original LovelyRobot mod.
     * Default random spawn color: pink (06).
     */
    public static final java.util.List<EntityTexture> VANILLA_COLORS = java.util.List.of(
            EntityTexture.PINK,
            EntityTexture.YELLOW,
            EntityTexture.LIGHT_BLUE,
            EntityTexture.BLACK
    );

    // -- Robot Types --

    /**
     * BUNNY robot type - original bunny design.
     * <p>
     * <b>Characteristics:</b> Balanced stats, default XP progression.
     * Configured via BunnyMaxLevel, BunnyBaseHp, etc.
     */
    public static final RobotFamily BUNNY   = createTribute(RobotVariant.Bunny,   BUNNY_COLORS,   "lovely_robot");

    /**
     * BUNNY2 robot type - alternative bunny design.
     * <p>
     * <b>Characteristics:</b> Balanced stats, default XP progression.
     * Configured via Bunny2MaxLevel, Bunny2BaseHp, etc.
     */
    public static final RobotFamily BUNNY2  = createTribute(RobotVariant.Bunny2,  BUNNY2_COLORS,  "lovely_robot");

    /**
     * HONEY robot type - support-oriented companion.
     * <p>
     * <b>Characteristics:</b> Lower combat stats, default XP progression.
     * Configured via HoneyMaxLevel, HoneyBaseHp, etc.
     */
    public static final RobotFamily HONEY   = createTribute(RobotVariant.Honey,   HONEY_COLORS,   "lovely_robot");

    /**
     * VANILLA robot type - general-purpose companion.
     * <p>
     * <b>Characteristics:</b> Balanced stats, default XP progression.
     * Configured via VanillaMaxLevel, VanillaBaseHp, etc.
     */
    public static final RobotFamily VANILLA = createTribute(RobotVariant.Vanilla, VANILLA_COLORS, "lovely_robot");

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
        SharedConfigs.EntityConfigData bunny = TributeConfigs.getEntityConfig(RobotVariant.Bunny.getName());
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
        SharedConfigs.EntityConfigData bunny2 = TributeConfigs.getEntityConfig(RobotVariant.Bunny2.getName());
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

        // Configure HONEY
        SharedConfigs.EntityConfigData honey = TributeConfigs.getEntityConfig(RobotVariant.Honey.getName());
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

        // Configure VANILLA
        SharedConfigs.EntityConfigData vanilla = TributeConfigs.getEntityConfig(RobotVariant.Vanilla.getName());
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