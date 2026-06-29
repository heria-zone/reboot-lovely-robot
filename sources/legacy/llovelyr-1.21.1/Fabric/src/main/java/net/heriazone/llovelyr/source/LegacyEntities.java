package net.heriazone.llovelyr.source;

import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.*;
import net.heriazone.lovelylib.api.entity.features.PickupFeature;
import net.heriazone.lovelylib.source.legacy.LegacyRobotFamilies;
import net.heriazone.lovelylib.common.shared.LovelyConstant;
import net.heriazone.lovelylib.common.configs.SharedConfigs;
import net.heriazone.lovelylib.common.entity.*;
import net.heriazone.llovelyr.LegacyIdentifier;
import net.heriazone.hzlib.api.entity.NativeEntityFamily;
import net.heriazone.hzlib.api.entity.features.DropFeature;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.Registry;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.*;

/**
 * Registry for NativeRobotEntity Legacy entity types (Fabric).
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

    public static final EntityType<NativeRobotEntity> BUNNY = registerRobot(LovelyConstant.VARIANT_BUNNY, LegacyRobotFamilies.BUNNY);
    public static final EntityType<NativeRobotEntity> BUNNY2 = registerRobot(LovelyConstant.VARIANT_BUNNY2, LegacyRobotFamilies.BUNNY2);
    public static final EntityType<NativeRobotEntity> BUNNY3 = registerRobot(LovelyConstant.VARIANT_BUNNY3, LegacyRobotFamilies.BUNNY3);
    public static final EntityType<NativeRobotEntity> DRAGON = registerRobot(LovelyConstant.VARIANT_DRAGON, LegacyRobotFamilies.DRAGON);
    public static final EntityType<NativeRobotEntity> HONEY = registerRobot(LovelyConstant.VARIANT_HONEY, LegacyRobotFamilies.HONEY);
    public static final EntityType<NativeRobotEntity> KITSUNE = registerRobot(LovelyConstant.VARIANT_KITSUNE, LegacyRobotFamilies.KITSUNE);
    public static final EntityType<NativeRobotEntity> NEKO = registerRobot(LovelyConstant.VARIANT_NEKO, LegacyRobotFamilies.NEKO);
    public static final EntityType<NativeRobotEntity> VANILLA = registerRobot(LovelyConstant.VARIANT_VANILLA, LegacyRobotFamilies.VANILLA);

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
    private static EntityType<NativeRobotEntity> registerRobot(String name, RobotFamily robotType) {
        return Registry.register(
                BuiltInRegistries.ENTITY_TYPE,
                LegacyIdentifier.getId(name),
                FabricEntityTypeBuilder.create(MobCategory.CREATURE, (EntityType<NativeRobotEntity> type, Level world) -> new NativeRobotEntity(type, world, robotType))
                        .dimensions(EntityDimensions.fixed(SharedConfigs.EntityDimensions.DEFAULT_WIDTH, SharedConfigs.EntityDimensions.DEFAULT_HEIGHT))
                        .build()
        );
    } // registerRobot()

    /**
     * Registers entity attributes.
     */
    public static void register() {
        FabricDefaultAttributeRegistry.register(BUNNY,   NativeEntityFamily.createAttributes(LegacyRobotFamilies.BUNNY));
        FabricDefaultAttributeRegistry.register(BUNNY2,  NativeEntityFamily.createAttributes(LegacyRobotFamilies.BUNNY2));
        FabricDefaultAttributeRegistry.register(BUNNY3,  NativeEntityFamily.createAttributes(LegacyRobotFamilies.BUNNY3));
        FabricDefaultAttributeRegistry.register(DRAGON,  NativeEntityFamily.createAttributes(LegacyRobotFamilies.DRAGON));
        FabricDefaultAttributeRegistry.register(HONEY,   NativeEntityFamily.createAttributes(LegacyRobotFamilies.HONEY));
        FabricDefaultAttributeRegistry.register(KITSUNE, NativeEntityFamily.createAttributes(LegacyRobotFamilies.KITSUNE));
        FabricDefaultAttributeRegistry.register(NEKO,    NativeEntityFamily.createAttributes(LegacyRobotFamilies.NEKO));
        FabricDefaultAttributeRegistry.register(VANILLA, NativeEntityFamily.createAttributes(LegacyRobotFamilies.VANILLA));
    } // register ()

    /**
     * Registers entity renderers on client side.
     */
    public static void registerRender() {
        EntityRendererRegistry.register(BUNNY, BunnyRenderer::new);
        EntityRendererRegistry.register(BUNNY2, NativeRobotRenderer::new);
        EntityRendererRegistry.register(BUNNY3, NativeRobotRenderer::new);
        EntityRendererRegistry.register(DRAGON, NativeRobotRenderer::new);
        EntityRendererRegistry.register(HONEY, NativeRobotRenderer::new);
        EntityRendererRegistry.register(KITSUNE, KitsuneRenderer::new);
        EntityRendererRegistry.register(NEKO, NativeRobotRenderer::new);
        EntityRendererRegistry.register(VANILLA, NativeRobotRenderer::new);
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
        LegacyRobotFamilies.BUNNY
                .withFeature(PickupFeature.class, new PickupFeature(LegacyItems.BUNNY_SPAWN))
                .withFeature(DropFeature.class, new DropFeature(LegacyItems.ROBOT_CORE));

        // BUNNY2
        LegacyRobotFamilies.BUNNY2
                .withFeature(PickupFeature.class, new PickupFeature(LegacyItems.BUNNY2_SPAWN))
                .withFeature(DropFeature.class, new DropFeature(LegacyItems.ROBOT_CORE));

        // BUNNY3
        LegacyRobotFamilies.BUNNY3
                .withFeature(PickupFeature.class, new PickupFeature(LegacyItems.BUNNY3_SPAWN))
                .withFeature(DropFeature.class, new DropFeature(LegacyItems.ROBOT_CORE));

        // DRAGON
        LegacyRobotFamilies.DRAGON
                .withFeature(PickupFeature.class, new PickupFeature(LegacyItems.DRAGON_SPAWN))
                .withFeature(DropFeature.class, new DropFeature(LegacyItems.ROBOT_CORE));

        // HONEY
        LegacyRobotFamilies.HONEY
                .withFeature(PickupFeature.class, new PickupFeature(LegacyItems.HONEY_SPAWN))
                .withFeature(DropFeature.class, new DropFeature(LegacyItems.ROBOT_CORE));

        // KITSUNE
        LegacyRobotFamilies.KITSUNE
                .withFeature(PickupFeature.class, new PickupFeature(LegacyItems.KITSUNE_SPAWN))
                .withFeature(DropFeature.class, new DropFeature(LegacyItems.ROBOT_CORE));

        // NEKO
        LegacyRobotFamilies.NEKO
                .withFeature(PickupFeature.class, new PickupFeature(LegacyItems.NEKO_SPAWN))
                .withFeature(DropFeature.class, new DropFeature(LegacyItems.ROBOT_CORE));

        // VANILLA
        LegacyRobotFamilies.VANILLA
                .withFeature(PickupFeature.class, new PickupFeature(LegacyItems.VANILLA_SPAWN))
                .withFeature(DropFeature.class, new DropFeature(LegacyItems.ROBOT_CORE));
    } // registerNativeRobotFeature ()

} // Class: LegacyEntities