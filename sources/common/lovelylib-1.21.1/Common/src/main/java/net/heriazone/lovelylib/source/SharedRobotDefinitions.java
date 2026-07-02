package net.heriazone.lovelylib.source;

import net.heriazone.hzlib.api.entity.features.BoneVisibilityFeature;
import net.heriazone.lovelylib.common.entity.definition.ModTarget;
import net.heriazone.lovelylib.common.entity.definition.RobotDefinitionRegistry;
import net.heriazone.lovelylib.common.entity.definition.RobotEntityDefinition;
import net.heriazone.lovelylib.common.entity.definition.RobotVariant;
import net.heriazone.lovelylib.common.entity.enums.EntityTexture;

/**
 * Declares all robot entity definitions shared between Legacy and Reboot.
 * <p>
 * <b>Why a third file:</b> RobotDefinitionRegistry enforces one definition per
 * RobotVariant. Variants that exist in both mods must be declared once with
 * .forMods(LEGACY, REBOOT). Keeping those declarations here — rather than in
 * LegacyRobotDefinitions or RebootRobotDefinitions — keeps both mod-specific
 * files responsible only for their own exclusive entities.
 * <p>
 * <b>To add a new shared entity:</b> add one builder call here with
 * .forMods(ModTarget.LEGACY, ModTarget.REBOOT).
 * <p>
 * <b>To add a Legacy-exclusive entity:</b> add it in LegacyRobotDefinitions.
 * <b>To add a Reboot-exclusive entity:</b> add it in RebootRobotDefinitions.
 * <p>
 * Called exactly once by Lovely.onInitialize(), before mod-specific files.
 */
public final class SharedRobotDefinitions {

    // -- Constructor --

    private SharedRobotDefinitions() {}

    // -- Registration --

    public static void register() {

        // ── Bunny — balanced all-rounder ──────────────────────────────────────
        RobotDefinitionRegistry.register(new RobotEntityDefinition.Builder(RobotVariant.Bunny)
            .displayName("Bunny")
            .forMods(ModTarget.LEGACY, ModTarget.REBOOT)
            .stats(200, 26, 5, 1.7f, 4, 0f, 0.37f)
            .build());

        // ── Bunny 2 — enhanced bunny with improved stats ───────────────────────
        RobotDefinitionRegistry.register(new RobotEntityDefinition.Builder(RobotVariant.Bunny2)
            .displayName("Bunny 2.0")
            .forMods(ModTarget.LEGACY, ModTarget.REBOOT)
            .stats(200, 27, 6, 1.8f, 5, 0f, 0.36f)
            .build());

        // ── Dragon — tank with high HP and defense ────────────────────────────
        RobotDefinitionRegistry.register(new RobotEntityDefinition.Builder(RobotVariant.Dragon)
            .displayName("Dragon")
            .forMods(ModTarget.LEGACY, ModTarget.REBOOT)
            .stats(200, 30, 8, 1.0f, 7, 0f, 0.3f)
            .build());

        // ── Honey — support type with healing focus ───────────────────────────
        RobotDefinitionRegistry.register(new RobotEntityDefinition.Builder(RobotVariant.Honey)
            .displayName("Honey")
            .forMods(ModTarget.LEGACY, ModTarget.REBOOT)
            .stats(200, 29, 4, 1.1f, 5, 0f, 0.31f)
            .build());

        // ── Kitsune — bone visibility feature + custom renderer ──────────────
        // tail0 always visible; tail01–tail09 hidden until assets are finalised.
        RobotDefinitionRegistry.register(new RobotEntityDefinition.Builder(RobotVariant.Kitsune)
            .displayName("Kitsune")
            .forMods(ModTarget.LEGACY, ModTarget.REBOOT)
            .stats(200, 28, 2, 1.3f, 6, 0f, 0.33f)
            .featureConfigurator((family, cfg) ->
                family.withFeature(BoneVisibilityFeature.class,
                    BoneVisibilityFeature.builder()
                        .showWhen("tail0",  entity -> true)
                        .hideWhen("tail01", entity -> true)
                        .hideWhen("tail02", entity -> true)
                        .hideWhen("tail03", entity -> true)
                        .hideWhen("tail04", entity -> true)
                        .hideWhen("tail05", entity -> true)
                        .hideWhen("tail06", entity -> true)
                        .hideWhen("tail07", entity -> true)
                        .hideWhen("tail08", entity -> true)
                        .hideWhen("tail09", entity -> true)
                        .build()))
            .build());

        // ── Neko — high damage glass cannon ───────────────────────────────────
        RobotDefinitionRegistry.register(new RobotEntityDefinition.Builder(RobotVariant.Neko)
            .displayName("Neko")
            .forMods(ModTarget.LEGACY, ModTarget.REBOOT)
            .stats(200, 28, 7, 1.4f, 5, 0f, 0.34f)
            .build());

        // ── Vanilla — general-purpose companion ───────────────────────────────
        RobotDefinitionRegistry.register(new RobotEntityDefinition.Builder(RobotVariant.Vanilla)
            .displayName("Vanilla")
            .forMods(ModTarget.LEGACY, ModTarget.REBOOT)
            .stats(200, 25, 5, 1.3f, 5, 0f, 0.32f)
            .build());

        // ── Bunny 3 — restricted 5-color pastel palette ───────────────────────
        RobotDefinitionRegistry.register(new RobotEntityDefinition.Builder(RobotVariant.Bunny3)
            .displayName("Bunny 3.0")
            .forMods(ModTarget.LEGACY, ModTarget.REBOOT)
            .palette(
                EntityTexture.LIGHT_BLUE,
                EntityTexture.YELLOW,
                EntityTexture.LIME,
                EntityTexture.PINK,
                EntityTexture.PURPLE)
            .stats(200, 28, 7, 1.9f, 6, 0f, 0.35f)
            .build());

        // ─────────────────────────────────────────────────────────────────────
        // ADD ENTITIES SHARED BETWEEN LEGACY AND REBOOT HERE.
        // Legacy-exclusive entities → LegacyRobotDefinitions
        // Reboot-exclusive entities → RebootRobotDefinitions
        // ─────────────────────────────────────────────────────────────────────

    } // register()

} // Class: SharedRobotDefinitions
