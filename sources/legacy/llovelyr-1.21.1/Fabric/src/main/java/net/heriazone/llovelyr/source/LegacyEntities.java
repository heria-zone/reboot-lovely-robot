package net.heriazone.llovelyr.source;

import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.heriazone.hzlib.api.entity.NativeEntityFamily;
import net.heriazone.hzlib.api.entity.features.DropFeature;
import net.heriazone.hzlib.api.entity.features.PickupFeature;
import net.heriazone.lovelylib.common.configs.SharedConfigs;
import net.heriazone.lovelylib.common.entity.NativeRobotEntity;
import net.heriazone.lovelylib.common.entity.NativeRobotRenderer;
import net.heriazone.lovelylib.common.entity.RobotFamily;
import net.heriazone.lovelylib.common.entity.definition.ModTarget;
import net.heriazone.lovelylib.common.entity.definition.RobotDefinitionRegistry;
import net.heriazone.lovelylib.common.entity.definition.RobotEntityDefinition;
import net.heriazone.lovelylib.common.entity.definition.RobotVariant;
import net.heriazone.lovelylib.source.legacy.LegacyRobotFamilies;
import net.heriazone.llovelyr.LegacyIdentifier;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Registry for Legacy entity types (Fabric).
 * <p>
 * <b>Architecture:</b> Registry-driven map replaces 8 named static fields.
 * Adding a new Legacy entity requires no change here — the loop over
 * RobotDefinitionRegistry.getForMod(LEGACY) picks it up automatically.
 * <p>
 * <b>Renderer dispatch:</b> usesNativeRenderer() branches between
 * NativeRobotRenderer and the RendererFactory stored in the definition.
 * This avoids per-variant if-blocks in registerRender().
 */
public class LegacyEntities {

    // -- State --

    private static final Map<RobotVariant, EntityType<NativeRobotEntity>> entityTypes = new HashMap<>();

    // -- Access --

    /**
     * Returns the EntityType for a Legacy variant.
     * <p>
     * On Fabric, types are registered immediately during class-load; this is
     * safe to call any time after LegacyEntities is initialized.
     *
     * @throws NullPointerException if the variant was never registered for LEGACY
     */
    public static EntityType<NativeRobotEntity> getEntityType(RobotVariant variant) {
        return Objects.requireNonNull(entityTypes.get(variant),
            "No entity type registered for Legacy variant: " + variant
            + ". Was LegacyEntities initialized?");
    } // getEntityType()

    // -- Registration --

    /**
     * Registers entity types and attribute sets for all Legacy variants.
     * Called during Fabric onInitialize().
     */
    public static void register() {
        for (RobotEntityDefinition def : RobotDefinitionRegistry.getForMod(ModTarget.LEGACY)) {
            RobotFamily family = LegacyRobotFamilies.get(def.getVariant());
            EntityType<NativeRobotEntity> type = Registry.register(
                BuiltInRegistries.ENTITY_TYPE,
                LegacyIdentifier.getId(def.getVariantKey()),
                FabricEntityTypeBuilder.create(MobCategory.CREATURE,
                        (EntityType<NativeRobotEntity> t, Level w) -> new NativeRobotEntity(t, w, family))
                    .dimensions(EntityDimensions.fixed(
                        SharedConfigs.EntityDimensions.DEFAULT_WIDTH,
                        SharedConfigs.EntityDimensions.DEFAULT_HEIGHT))
                    .build());
            entityTypes.put(def.getVariant(), type);
            FabricDefaultAttributeRegistry.register(type,
                NativeEntityFamily.createAttributes(family));
        }
    } // register()

    /**
     * Registers client-side renderers. NativeRobotRenderer is the default;
     * variants with a custom RendererFactory use it via the definition.
     */
    @SuppressWarnings("unchecked")
    public static void registerRender() {
        for (RobotEntityDefinition def : RobotDefinitionRegistry.getForMod(ModTarget.LEGACY)) {
            EntityType<NativeRobotEntity> type = getEntityType(def.getVariant());
            if (def.usesNativeRenderer()) {
                EntityRendererRegistry.register(type, NativeRobotRenderer::new);
            } else {
                EntityRendererRegistry.register(type,
                    ctx -> (net.minecraft.client.renderer.entity.EntityRenderer<NativeRobotEntity>)
                        def.getRendererFactory().create(ctx));
            }
        }
    } // registerRender()

    /**
     * Attaches PickupFeature and DropFeature to each family.
     * <p>
     * Called after items are registered so item references are valid.
     * On Fabric this is called directly in onInitialize(); on Forge/NeoForge
     * it runs in commonSetup via enqueueWork.
     */
    public static void registerNativeRobotFeature() {
        for (RobotEntityDefinition def : RobotDefinitionRegistry.getForMod(ModTarget.LEGACY)) {
            LegacyRobotFamilies.get(def.getVariant())
                .withFeature(PickupFeature.class,
                    new PickupFeature(LegacyItems.getSpawnItem(def.getVariant())))
                .withFeature(DropFeature.class,
                    new DropFeature(LegacyItems.ROBOT_CORE));
        }
    } // registerNativeRobotFeature()

} // Class: LegacyEntities
