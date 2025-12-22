package net.heriazone.rlovelyr.source;

import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.heriazone.rlovelyr.RebootIdentifier;
import net.heriazone.lovelylib.api.entity.features.PickupFeature;
import net.heriazone.lovelylib.common.configs.SharedConfigs;
import net.heriazone.lovelylib.common.entity.*;
import net.heriazone.lovelylib.common.shared.LovelyConstant;
import net.heriazone.lovelylib.hzlib.api.entity.features.DropFeature;
import net.heriazone.lovelylib.source.reboot.RebootRobotType;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.Level;

/**
 * Registry for LovelyRobotEntity Reboot entity types (Fabric).
 * <p>
 * <b>Architecture:</b> Manages entity type registration, attribute creation,
 * and renderer binding for all robot variants in the Reboot mod.
 * <p>
 * <b>Registered Entities:</b>
 * - Vanilla: Basic robot type
 * - Bunny2: Bunny-themed robot variant
 */
public class RebootEntities {

    // -- Entity Type Definitions --

    public static final EntityType<RobotEntity> BUNNY = registerRobot(LovelyConstant.VARIANT_BUNNY, RebootRobotType.BUNNY);
    public static final EntityType<RobotEntity> BUNNY2 = registerRobot(LovelyConstant.VARIANT_BUNNY2, RebootRobotType.BUNNY2);
    public static final EntityType<RobotEntity> DRAGON = registerRobot(LovelyConstant.VARIANT_DRAGON, RebootRobotType.DRAGON);
    public static final EntityType<RobotEntity> HONEY = registerRobot(LovelyConstant.VARIANT_HONEY, RebootRobotType.HONEY);
    public static final EntityType<RobotEntity> KITSUNE = registerRobot(LovelyConstant.VARIANT_KITSUNE, RebootRobotType.KITSUNE);
    public static final EntityType<RobotEntity> NEKO = registerRobot(LovelyConstant.VARIANT_NEKO, RebootRobotType.NEKO);
    public static final EntityType<RobotEntity> VANILLA = registerRobot(LovelyConstant.VARIANT_VANILLA, RebootRobotType.VANILLA);

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
                RebootIdentifier.getId(name),
                FabricEntityTypeBuilder.create(MobCategory.CREATURE, (EntityType<RobotEntity> type, Level world) -> new RobotEntity(type, world, robotType))
                        .dimensions(EntityDimensions.fixed(SharedConfigs.EntityDimensions.DEFAULT_WIDTH, SharedConfigs.EntityDimensions.DEFAULT_HEIGHT))
                        .build()
        );
    } // registerRobot()

    /**
     * Registers entity attributes.
     */
    public static void register() {
        FabricDefaultAttributeRegistry.register(BUNNY, LovelyRobotEntity.createAttributes(RebootRobotType.BUNNY));
        FabricDefaultAttributeRegistry.register(BUNNY2, LovelyRobotEntity.createAttributes(RebootRobotType.BUNNY2));
        FabricDefaultAttributeRegistry.register(DRAGON, LovelyRobotEntity.createAttributes(RebootRobotType.DRAGON));
        FabricDefaultAttributeRegistry.register(HONEY, LovelyRobotEntity.createAttributes(RebootRobotType.HONEY));
        FabricDefaultAttributeRegistry.register(KITSUNE, LovelyRobotEntity.createAttributes(RebootRobotType.KITSUNE));
        FabricDefaultAttributeRegistry.register(NEKO, LovelyRobotEntity.createAttributes(RebootRobotType.NEKO));
        FabricDefaultAttributeRegistry.register(VANILLA, LovelyRobotEntity.createAttributes(RebootRobotType.VANILLA));
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
     * Configures robot entity features for all Reboot robot variants.
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
     * <b>Timing:</b> Must be called after RebootItems registration completes to ensure
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
        RebootRobotType.BUNNY
                .withFeature(PickupFeature.class, new PickupFeature(RebootItems.BUNNY_SPAWN))
                .withFeature(DropFeature.class, new DropFeature(RebootItems.ROBOT_CORE));

        // BUNNY2
        RebootRobotType.BUNNY2
                .withFeature(PickupFeature.class, new PickupFeature(RebootItems.BUNNY2_SPAWN))
                .withFeature(DropFeature.class, new DropFeature(RebootItems.ROBOT_CORE));

        // DRAGON
        RebootRobotType.DRAGON
                .withFeature(PickupFeature.class, new PickupFeature(RebootItems.DRAGON_SPAWN))
                .withFeature(DropFeature.class, new DropFeature(RebootItems.ROBOT_CORE));

        // HONEY
        RebootRobotType.HONEY
                .withFeature(PickupFeature.class, new PickupFeature(RebootItems.HONEY_SPAWN))
                .withFeature(DropFeature.class, new DropFeature(RebootItems.ROBOT_CORE));

        // KITSUNE
        RebootRobotType.KITSUNE
                .withFeature(PickupFeature.class, new PickupFeature(RebootItems.KITSUNE_SPAWN))
                .withFeature(DropFeature.class, new DropFeature(RebootItems.ROBOT_CORE));

        // NEKO
        RebootRobotType.NEKO
                .withFeature(PickupFeature.class, new PickupFeature(RebootItems.NEKO_SPAWN))
                .withFeature(DropFeature.class, new DropFeature(RebootItems.ROBOT_CORE));

        // VANILLA
        RebootRobotType.VANILLA
                .withFeature(PickupFeature.class, new PickupFeature(RebootItems.VANILLA_SPAWN))
                .withFeature(DropFeature.class, new DropFeature(RebootItems.ROBOT_CORE));
    } // registerNativeRobotFeature ()

} // Class: RebootEntities