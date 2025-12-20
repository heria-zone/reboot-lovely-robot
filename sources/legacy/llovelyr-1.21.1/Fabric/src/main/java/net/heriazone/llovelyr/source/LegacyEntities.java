package net.heriazone.llovelyr.source;

import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.heriazone.llovelyr.LegacyIdentifier;
import net.heriazone.lovelylib.api.entity.features.PickupFeature;
import net.heriazone.lovelylib.common.entity.LovelyRobotEntity;
import net.heriazone.lovelylib.common.shared.LovelyConstant;
import net.heriazone.llovelyr.entity.*;
import net.heriazone.lovelylib.common.configs.SharedConfigs;
import net.heriazone.lovelylib.common.entity.NativeEntityType;
import net.heriazone.lovelylib.hzlib.api.entity.features.DropFeature;
import net.heriazone.lovelylib.source.legacy.LegacyRobotType;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;

/**
 * Registry for LovelyRobotEntity Legacy entity types (Fabric).
 * <p>
 * <b>Architecture:</b> Manages entity type registration, attribute creation,
 * and renderer binding for all robot variants in the Legacy mod.
 * <p>
 * <b>Registered Entities:</b>
 * - Vanilla: Basic robot type
 * - Bunny2: Bunny-themed robot variant
 */
public class LegacyEntities {

    // -- Entity Type Definitions --

    public static final EntityType<RobotEntity> BUNNY = registerRobot(LovelyConstant.VARIANT_BUNNY, LegacyRobotType.BUNNY);
    public static final EntityType<RobotEntity> BUNNY2 = registerRobot(LovelyConstant.VARIANT_BUNNY2, LegacyRobotType.BUNNY2);
    public static final EntityType<RobotEntity> DRAGON = registerRobot(LovelyConstant.VARIANT_DRAGON, LegacyRobotType.DRAGON);
    public static final EntityType<RobotEntity> HONEY = registerRobot(LovelyConstant.VARIANT_HONEY, LegacyRobotType.HONEY);
    public static final EntityType<RobotEntity> KITSUNE = registerRobot(LovelyConstant.VARIANT_KITSUNE, LegacyRobotType.KITSUNE);
    public static final EntityType<RobotEntity> NEKO = registerRobot(LovelyConstant.VARIANT_NEKO, LegacyRobotType.NEKO);
    public static final EntityType<RobotEntity> VANILLA = registerRobot(LovelyConstant.VARIANT_VANILLA, LegacyRobotType.VANILLA);

    // -- Registration Methods --

    /**
     * Registers a robot entity type with RobotEntity implementation.
     * <p>
     * Type-safe registration that ensures RobotEntity is used consistently
     * across all robot variants.
     *
     * @param name entity variant name
     * @param robotType robot configuration data
     * @return registered entity type
     */
    private static EntityType<RobotEntity> registerRobot(String name, NativeEntityType robotType) {
        return Registry.register(
                BuiltInRegistries.ENTITY_TYPE,
                LegacyIdentifier.getId(name),
                FabricEntityTypeBuilder.create(MobCategory.CREATURE, (EntityType<RobotEntity> type, Level world) -> new RobotEntity(type, world, robotType))
                        .dimensions(EntityDimensions.fixed(SharedConfigs.EntityDimensions.DEFAULT_WIDTH, SharedConfigs.EntityDimensions.DEFAULT_HEIGHT))
                        .build()
        );
    } // registerRobot()

    /**
     * Registers entity attributes.
     */
    public static void register() {
        FabricDefaultAttributeRegistry.register(BUNNY, LovelyRobotEntity.createAttributes(LegacyRobotType.BUNNY));
        FabricDefaultAttributeRegistry.register(BUNNY2, LovelyRobotEntity.createAttributes(LegacyRobotType.BUNNY2));
        FabricDefaultAttributeRegistry.register(DRAGON, LovelyRobotEntity.createAttributes(LegacyRobotType.DRAGON));
        FabricDefaultAttributeRegistry.register(HONEY, LovelyRobotEntity.createAttributes(LegacyRobotType.HONEY));
        FabricDefaultAttributeRegistry.register(KITSUNE, LovelyRobotEntity.createAttributes(LegacyRobotType.KITSUNE));
        FabricDefaultAttributeRegistry.register(NEKO, LovelyRobotEntity.createAttributes(LegacyRobotType.NEKO));
        FabricDefaultAttributeRegistry.register(VANILLA, LovelyRobotEntity.createAttributes(LegacyRobotType.VANILLA));
    } // register ()

    /**
     * Registers entity renderers on client side.
     */
    public static void registerRender() {
        EntityRendererRegistry.register(BUNNY, BunnyRenderer::new);
        EntityRendererRegistry.register(BUNNY2, RobotRenderer::new);
        EntityRendererRegistry.register(DRAGON, RobotRenderer::new);
        EntityRendererRegistry.register(HONEY, RobotRenderer::new);
        EntityRendererRegistry.register(KITSUNE, KitsuneRenderer::new);
        EntityRendererRegistry.register(NEKO, RobotRenderer::new);
        EntityRendererRegistry.register(VANILLA, RobotRenderer::new);
    } // registerRender ()

    /**
     * Configures robot entity features for all Legacy robot variants.
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
     * <b>Timing:</b> Must be called after LegacyItems registration completes to ensure
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
        LegacyRobotType.BUNNY
                .withFeature(PickupFeature.class, new PickupFeature(LegacyItems.BUNNY_SPAWN))
                .withFeature(DropFeature.class, new DropFeature(LegacyItems.ROBOT_CORE));

        // BUNNY2
        LegacyRobotType.BUNNY2
                .withFeature(PickupFeature.class, new PickupFeature(LegacyItems.BUNNY2_SPAWN))
                .withFeature(DropFeature.class, new DropFeature(LegacyItems.ROBOT_CORE));

        // DRAGON
        LegacyRobotType.DRAGON
                .withFeature(PickupFeature.class, new PickupFeature(LegacyItems.DRAGON_SPAWN))
                .withFeature(DropFeature.class, new DropFeature(LegacyItems.ROBOT_CORE));

        // HONEY
        LegacyRobotType.HONEY
                .withFeature(PickupFeature.class, new PickupFeature(LegacyItems.HONEY_SPAWN))
                .withFeature(DropFeature.class, new DropFeature(LegacyItems.ROBOT_CORE));

        // KITSUNE
        LegacyRobotType.KITSUNE
                .withFeature(PickupFeature.class, new PickupFeature(LegacyItems.KITSUNE_SPAWN))
                .withFeature(DropFeature.class, new DropFeature(LegacyItems.ROBOT_CORE));

        // NEKO
        LegacyRobotType.NEKO
                .withFeature(PickupFeature.class, new PickupFeature(LegacyItems.NEKO_SPAWN))
                .withFeature(DropFeature.class, new DropFeature(LegacyItems.ROBOT_CORE));

        // VANILLA
        LegacyRobotType.VANILLA
                .withFeature(PickupFeature.class, new PickupFeature(LegacyItems.VANILLA_SPAWN))
                .withFeature(DropFeature.class, new DropFeature(LegacyItems.ROBOT_CORE));
    } // registerNativeRobotFeature ()

} // Class: LegacyEntities