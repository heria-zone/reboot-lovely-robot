package net.heriazone.lovelylib.source.legacy;

import net.heriazone.lovelylib.api.entity.features.CombatLevelFeature;
import net.heriazone.lovelylib.api.entity.features.EnchantmentFeature;
import net.heriazone.lovelylib.api.entity.features.ProtectionFeature;
import net.heriazone.lovelylib.common.configs.SharedConfigs;
import net.heriazone.lovelylib.common.entity.RobotFamily;
import net.heriazone.lovelylib.common.entity.RobotFamilyRegistry;
import net.heriazone.lovelylib.common.entity.combat.LinearAttributeStrategy;
import net.heriazone.lovelylib.common.entity.definition.ModTarget;
import net.heriazone.lovelylib.common.entity.definition.RobotDefinitionRegistry;
import net.heriazone.lovelylib.common.entity.definition.RobotEntityDefinition;
import net.heriazone.lovelylib.common.entity.definition.RobotVariant;
import net.heriazone.hzlib.api.entity.features.LevelFeature;
import net.heriazone.hzlib.framework.entity.enchantment.DefaultEnchantmentStrategy;
import net.heriazone.hzlib.framework.entity.protection.LevelBasedProtectionStrategy;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Runtime map of all Legacy robot families, keyed by variant.
 * <p>
 * <b>Architecture:</b> Replaces 8 named static fields with a single map populated
 * from RobotDefinitionRegistry in initialize(). Adding a new Legacy entity requires
 * no change here — only a builder call in LegacyRobotDefinitions.
 * <p>
 * <b>Initialization Order:</b> initialize() must be called after Lovely.onInitialize()
 * seals the registry, and before any deferred register supplier fires. All 6 loader
 * entry points call it immediately after Lovely.onInitialize().
 * <p>
 * <b>Palette Handling:</b> Bunny3 uses a restricted 5-color pastel palette declared
 * in LegacyRobotDefinitions. isFullPalette() gates which create() overload to use —
 * no hard-coded palette list lives here.
 */
public class LegacyRobotFamilies extends RobotFamilyRegistry {

    // -- State --

    private static final Map<RobotVariant, RobotFamily> families = new HashMap<>();

    // -- Initialization --

    /**
     * Populates the family map from the registry. Must be called once, after
     * Lovely.onInitialize() seals RobotDefinitionRegistry and before any
     * registration supplier fires.
     */
    public static void initialize() {
        families.clear();
        for (RobotEntityDefinition def : RobotDefinitionRegistry.getForMod(ModTarget.LEGACY)) {
            RobotFamily family = def.isFullPalette()
                ? create(def.getVariant())
                : create(def.getVariant(), def.getPalette());
            families.put(def.getVariant(), family);
        }
    } // initialize()

    // -- Access --

    /**
     * Returns the RobotFamily for a Legacy variant.
     *
     * @throws NullPointerException if the variant was not registered for LEGACY —
     *         a programmer error that would have already caused a fail-fast at seal() time
     */
    public static RobotFamily get(RobotVariant variant) {
        return Objects.requireNonNull(families.get(variant),
            "No Legacy RobotFamily for: " + variant
            + ". Was LegacyRobotFamilies.initialize() called after Lovely.onInitialize()?");
    } // get()

    // -- Config Reload --

    /**
     * Applies per-variant stats and standard features from the current config values.
     * <p>
     * <b>Loop Design:</b> One iteration per registered Legacy variant. Standard features
     * (Level, CombatLevel, Enchantment, Protection) are applied uniformly. Variant-specific
     * extras (e.g. Kitsune's BoneVisibilityFeature) are applied via
     * def.applyFeatureConfigurator() — a no-op for variants without one, keeping the
     * loop body free of any per-variant if-blocks.
     */
    public static void reloadFromConfig() {
        var defaultExpStrategy = new LevelFeature.FormulaExpStrategy(
            level -> SharedConfigs.Common.ExperienceBase + level * SharedConfigs.Common.ExperienceMultiplier);
        var defaultProtection = new ProtectionFeature(new LevelBasedProtectionStrategy())
            .withMax(SharedConfigs.Common.ProtectionLimitFire,
                     SharedConfigs.Common.ProtectionLimitFall,
                     SharedConfigs.Common.ProtectionLimitBlast,
                     SharedConfigs.Common.ProtectionLimitProjectile);
        var defaultEnchantment = new EnchantmentFeature(new DefaultEnchantmentStrategy())
            .withLooting(SharedConfigs.Common.LootEnchantment,
                         SharedConfigs.Common.MaxLootEnchantment,
                         SharedConfigs.Common.LootEnchantmentLevel);

        for (RobotEntityDefinition def : RobotDefinitionRegistry.getForMod(ModTarget.LEGACY)) {
            SharedConfigs.EntityConfigData cfg = LegacyConfigs.getEntityConfig(def.getVariantKey());
            get(def.getVariant())
                .withCombatStats(cfg.baseHp, cfg.baseAttack, cfg.attackSpeed,
                                 cfg.baseDefense, cfg.baseToughness, 0F, cfg.movementSpeed)
                .withFeature(LevelFeature.class,
                    new LevelFeature(cfg.maxLevel, defaultExpStrategy))
                .withFeature(CombatLevelFeature.class,
                    new CombatLevelFeature(cfg.baseHp, cfg.baseAttack, cfg.baseDefense,
                                           new LinearAttributeStrategy()))
                .withFeature(EnchantmentFeature.class, defaultEnchantment)
                .withFeature(ProtectionFeature.class, defaultProtection);

            // No-op for all variants that have no featureConfigurator set.
            def.applyFeatureConfigurator(get(def.getVariant()), cfg);
        }
    } // reloadFromConfig()

} // Class: LegacyRobotFamilies
