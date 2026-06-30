package net.heriazone.lovely_robot.source;

import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.heriazone.hzlib.api.entity.NativeEntityFamily;
import net.heriazone.lovely_robot.TributeIdentifier;
import net.heriazone.hzlib.api.entity.features.PickupFeature;
import net.heriazone.lovelylib.common.configs.SharedConfigs;
import net.heriazone.lovelylib.common.entity.*;
import net.heriazone.lovelylib.common.shared.LovelyConstant;
import net.heriazone.hzlib.api.entity.features.DropFeature;
import net.heriazone.lovelylib.source.tribute.TributeRobotFamilies;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.Level;

/**
 * Registry for NativeRobotEntity Tribute entity types (Fabric).
 * <p>
 * <b>Architecture:</b> Manages entity type registration, attribute creation,
 * and renderer binding for all robot variants in the Tribute mod.
 * <p>
 * <b>Registered Entities:</b>
 * - Vanilla: Basic robot type
 * - Bunny2: Bunny-themed robot variant
 */
public class TributeEntities {

    // -- Entity Type Definitions --

    public static final EntityType<TributeRobotEntity> BUNNY   = registerRobot(LovelyConstant.VARIANT_BUNNY,   TributeRobotFamilies.BUNNY);
    public static final EntityType<TributeRobotEntity> BUNNY2  = registerRobot(LovelyConstant.VARIANT_BUNNY2,  TributeRobotFamilies.BUNNY2);
    public static final EntityType<TributeRobotEntity> HONEY   = registerRobot(LovelyConstant.VARIANT_HONEY,   TributeRobotFamilies.HONEY);
    public static final EntityType<TributeRobotEntity> VANILLA = registerRobot(LovelyConstant.VARIANT_VANILLA, TributeRobotFamilies.VANILLA);

    // -- Registration Methods --

    /**
     * Registers a robot entity type with NativeRobotEntity implementation.
     * <p>
     * Type-safe registration that ensures NativeRobotEntity is used consistently
     * across all robot variants.
     *
     * @param name entity variant name
     * @param robotType robot configuration data
     * @return registered entity type
     */
    private static EntityType<TributeRobotEntity> registerRobot(String name, RobotFamily robotType) {
        return Registry.register(
                BuiltInRegistries.ENTITY_TYPE,
                TributeIdentifier.getId(name),
                FabricEntityTypeBuilder.create(MobCategory.CREATURE, (EntityType<TributeRobotEntity> type, Level world) -> new TributeRobotEntity(type, world, robotType))
                        .dimensions(EntityDimensions.fixed(SharedConfigs.EntityDimensions.DEFAULT_WIDTH, SharedConfigs.EntityDimensions.DEFAULT_HEIGHT))
                        .build()
        );
    } // registerRobot()

    /**
     * Registers entity attributes.
     */
    public static void register() {
        FabricDefaultAttributeRegistry.register(BUNNY,   NativeEntityFamily.createAttributes(TributeRobotFamilies.BUNNY));
        FabricDefaultAttributeRegistry.register(BUNNY2,  NativeEntityFamily.createAttributes(TributeRobotFamilies.BUNNY2));
        FabricDefaultAttributeRegistry.register(HONEY,   NativeEntityFamily.createAttributes(TributeRobotFamilies.HONEY));
        FabricDefaultAttributeRegistry.register(VANILLA, NativeEntityFamily.createAttributes(TributeRobotFamilies.VANILLA));
    } // register ()

    /**
     * Registers entity renderers on client side.
     */
    public static void registerRender() {
        EntityRendererRegistry.register(BUNNY, BunnyRenderer::new);
        EntityRendererRegistry.register(BUNNY2, NativeRobotRenderer::new);
        EntityRendererRegistry.register(HONEY, NativeRobotRenderer::new);
        EntityRendererRegistry.register(VANILLA, NativeRobotRenderer::new);
    } // registerRender ()

    /**
     * Configures robot entity features for all Tribute robot variants.
     * <p>
     * <b>Architecture:</b> Attaches behavioral features to each robot type using the
     * feature composition system. Features are registered after entity types but before
     * world loading to ensure proper initialization order.
     * <p>
     * <b>Feature Configuration:</b> Each robot receives standardized feature set:
     * - PickupFeature: Enables robot to pick up its corresponding spawn item
     * - DropFeature: Configures robot to drop robot core on death
     * <p>
     * <b>Design Decision:</b> Uniform feature application across all variants maintains
     * consistent behavior while allowing individual customization through feature parameters.
     * Spawn items are variant-specific, but drop items are standardized.
     * <p>
     * <b>Timing:</b> Must be called after TributeItems registration completes to ensure
     * item references are available. Typically invoked during mod initialization phase.
     * <p>
     * <b>Fabric Specifics:</b> Uses direct item references rather than deferred registry
     * objects, as Fabric registration is immediate rather than deferred.
     * <p>
     * <b>Thread Safety:</b> Not thread-safe. Should only be called from main thread
     * during mod initialization.
     */
    public static void registerNativeRobotFeature() {
        // BUNNY
        TributeRobotFamilies.BUNNY
                .withFeature(PickupFeature.class, new PickupFeature(TributeItems.BUNNY_SPAWN))
                .withFeature(DropFeature.class, new DropFeature(TributeItems.ROBOT_CORE));

        // BUNNY2
        TributeRobotFamilies.BUNNY2
                .withFeature(PickupFeature.class, new PickupFeature(TributeItems.BUNNY2_SPAWN))
                .withFeature(DropFeature.class, new DropFeature(TributeItems.ROBOT_CORE));

        // HONEY
        TributeRobotFamilies.HONEY
                .withFeature(PickupFeature.class, new PickupFeature(TributeItems.HONEY_SPAWN))
                .withFeature(DropFeature.class, new DropFeature(TributeItems.ROBOT_CORE));

        // VANILLA
        TributeRobotFamilies.VANILLA
                .withFeature(PickupFeature.class, new PickupFeature(TributeItems.VANILLA_SPAWN))
                .withFeature(DropFeature.class, new DropFeature(TributeItems.ROBOT_CORE));
    } // registerNativeRobotFeature ()

} // Class: TributeEntities