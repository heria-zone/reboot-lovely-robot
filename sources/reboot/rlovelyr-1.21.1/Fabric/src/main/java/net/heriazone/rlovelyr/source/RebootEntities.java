package net.heriazone.rlovelyr.source;

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
import net.heriazone.lovelylib.source.reboot.RebootRobotFamilies;
import net.heriazone.rlovelyr.RebootIdentifier;
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
 * Registry for Reboot entity types (Fabric).
 * <p>
 * <b>Architecture:</b> Mirrors LegacyEntities Fabric — only namespace
 * (RebootIdentifier), families source (RebootRobotFamilies), and ModTarget differ.
 * <p>
 * <i>Note:</i> Reboot uses hzlib PickupFeature (not lovelylib api) — preserved
 * from the original file to maintain behavioral parity.
 */
public class RebootEntities {

    // -- State --

    private static final Map<RobotVariant, EntityType<NativeRobotEntity>> entityTypes = new HashMap<>();

    // -- Access --

    public static EntityType<NativeRobotEntity> getEntityType(RobotVariant variant) {
        return Objects.requireNonNull(entityTypes.get(variant),
            "No entity type registered for Reboot variant: " + variant);
    } // getEntityType()

    // -- Registration --

    public static void register() {
        for (RobotEntityDefinition def : RobotDefinitionRegistry.getForMod(ModTarget.REBOOT)) {
            RobotFamily family = RebootRobotFamilies.get(def.getVariant());
            EntityType<NativeRobotEntity> type = Registry.register(
                BuiltInRegistries.ENTITY_TYPE,
                RebootIdentifier.getId(def.getVariantKey()),
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

    @SuppressWarnings("unchecked")
    public static void registerRender() {
        for (RobotEntityDefinition def : RobotDefinitionRegistry.getForMod(ModTarget.REBOOT)) {
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

    public static void registerNativeRobotFeature() {
        for (RobotEntityDefinition def : RobotDefinitionRegistry.getForMod(ModTarget.REBOOT)) {
            RebootRobotFamilies.get(def.getVariant())
                .withFeature(PickupFeature.class,
                    new PickupFeature(RebootItems.getSpawnItem(def.getVariant())))
                .withFeature(DropFeature.class,
                    new DropFeature(RebootItems.ROBOT_CORE));
        }
    } // registerNativeRobotFeature()

} // Class: RebootEntities
