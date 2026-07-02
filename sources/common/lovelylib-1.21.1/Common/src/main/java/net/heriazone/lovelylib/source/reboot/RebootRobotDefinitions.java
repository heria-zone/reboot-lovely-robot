package net.heriazone.lovelylib.source.reboot;

import net.heriazone.lovelylib.api.entity.features.BlazeCycleFeature;
import net.heriazone.lovelylib.common.entity.definition.ModTarget;
import net.heriazone.lovelylib.common.entity.definition.RobotDefinitionRegistry;
import net.heriazone.lovelylib.common.entity.definition.RobotEntityDefinition;
import net.heriazone.lovelylib.common.entity.definition.RobotVariant;
import net.heriazone.lovelylib.common.entity.enums.EntityTexture;

import java.util.List;

/**
 * Reboot-exclusive robot entity definitions — Aldarian Tech tier.
 * <p>
 * All three robots deviate from the standard 16-colour palette. Their
 * {@code featureConfigurator} wires the deviation at reload time:
 * Prime and Hyperion get {@link BlazeCycleFeature} for Blaze Rod cycling;
 * Empyrium gets {@link BlockAllItemInteractionFeature} to reject all item use.
 * <p>
 * Called once by {@code Lovely.onInitialize()}, after {@code SharedRobotDefinitions}.
 */
public final class RebootRobotDefinitions {

    private RebootRobotDefinitions() {}

    public static void register() {

        // ── Prime — 7-colour Aldarian palette, Blaze Rod cycles forward ──────
        // Palette order defines cycle direction: DARK_MATTER → … → HESTIA → wrap.
        // Stats sit above the shared Dragon (30 HP / 8 atk) to reflect Aldarian tier.
        final List<EntityTexture> primePalette = List.of(
                EntityTexture.DARK_MATTER,
                EntityTexture.SUPERNOVA,
                EntityTexture.COLD_GOLD,
                EntityTexture.EMBRYON,
                EntityTexture.DARK_GOLD,
                EntityTexture.GOLD_MATTER,
                EntityTexture.HESTIA);

        RobotDefinitionRegistry.register(new RobotEntityDefinition.Builder(RobotVariant.Prime)
                .displayName("Prime")
                .forMod(ModTarget.REBOOT)
                .palette(EntityTexture.DARK_MATTER, EntityTexture.SUPERNOVA, EntityTexture.COLD_GOLD,
                         EntityTexture.EMBRYON, EntityTexture.DARK_GOLD, EntityTexture.GOLD_MATTER,
                         EntityTexture.HESTIA)
                .stats(200, 30, 8, 1.5f, 6, 1.0f, 0.33f)
                .featureConfigurator((family, cfg) ->
                        family.withFeature(BlazeCycleFeature.class,
                                new BlazeCycleFeature(primePalette,
                                        EntityTexture.DARK_MATTER,
                                        EntityTexture.HESTIA)))
                .build());

        // ── Hyperion — 2-colour toggle, Blaze Rod switches COMMANDER ↔ VALKYRIE
        // The tightest possible restricted palette: one Blaze Rod use cycles both ways.
        final List<EntityTexture> hyperionPalette = List.of(
                EntityTexture.COMMANDER,
                EntityTexture.VALKYRIE);

        RobotDefinitionRegistry.register(new RobotEntityDefinition.Builder(RobotVariant.Hyperion)
                .displayName("Hyperion")
                .forMod(ModTarget.REBOOT)
                .palette(EntityTexture.COMMANDER, EntityTexture.VALKYRIE)
                .stats(200, 32, 9, 1.4f, 7, 1.0f, 0.34f)
                .featureConfigurator((family, cfg) ->
                        family.withFeature(BlazeCycleFeature.class,
                                new BlazeCycleFeature(hyperionPalette,
                                        EntityTexture.COMMANDER,
                                        EntityTexture.VALKYRIE)))
                .build());

        // ── Empyrium — single COLD_GOLD texture, no palette cycling possible ────
        // Apex-tier robot with one fixed texture. No BlazeCycleFeature — the palette
        // has only one entry so cycling is a no-op. Standard interactions (sit, state,
        // follow, base-defence) remain fully functional.
        RobotDefinitionRegistry.register(new RobotEntityDefinition.Builder(RobotVariant.Empyrium)
                .displayName("Empyrium")
                .forMod(ModTarget.REBOOT)
                .palette(EntityTexture.COLD_GOLD)
                .stats(200, 35, 12, 1.2f, 8, 2.0f, 0.30f)
                .build());

    } // register()

} // Class: RebootRobotDefinitions
