package net.heriazone.lovelylib.source.reboot;

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
 * Runtime map of all Reboot robot families, keyed by variant.
 * <p>
 * <b>Architecture:</b> Mirrors LegacyRobotFamilies exactly, substituting ModTarget.REBOOT
 * and RebootConfigs. The registry drives both — adding a new Reboot-exclusive entity
 * requires only a builder call in RebootRobotDefinitions, no change here.
 * <p>
 * <b>Shared Variants:</b> Bunny3 is declared with forMods(LEGACY, REBOOT) in
 * LegacyRobotDefinitions. getForMod(REBOOT) returns it here — no duplicate declaration.
 */
public class RebootRobotFamilies extends RobotFamilyRegistry {

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
        for (RobotEntityDefinition def : RobotDefinitionRegistry.getForMod(ModTarget.REBOOT)) {
            RobotFamily family = def.isFullPalette()
                ? create(def.getVariant())
                : create(def.getVariant(), def.getPalette());
            families.put(def.getVariant(), family);
        }
    } // initialize()

    // -- Access --

    /**
     * Returns the RobotFamily for a Reboot variant.
     *
     * @throws NullPointerException if the variant was not registered for REBOOT
     */
    public static RobotFamily get(RobotVariant variant) {
        return Objects.requireNonNull(families.get(variant),
            "No Reboot RobotFamily for: " + variant
            + ". Was RebootRobotFamilies.initialize() called after Lovely.onInitialize()?");
    } // get()

    // -- Config Reload --

    /**
     * Applies per-variant stats and standard features from the current config values.
     * Identical loop structure to LegacyRobotFamilies.reloadFromConfig() — only the
     * config source (RebootConfigs) and mod target (REBOOT) differ.
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

        for (RobotEntityDefinition def : RobotDefinitionRegistry.getForMod(ModTarget.REBOOT)) {
            SharedConfigs.EntityConfigData cfg = RebootConfigs.getEntityConfig(def.getVariantKey());
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

} // Class: RebootRobotFamilies
