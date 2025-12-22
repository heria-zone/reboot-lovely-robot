package net.heriazone.rlovelyr.source;

import net.heriazone.rlovelyr.Reboot;
import net.heriazone.rlovelyr.RebootIdentifier;
import net.heriazone.lovelylib.api.entity.features.PickupFeature;
import net.heriazone.lovelylib.common.configs.SharedConfigs;
import net.heriazone.lovelylib.common.entity.*;
import net.heriazone.lovelylib.common.shared.LovelyConstant;
import net.heriazone.lovelylib.hzlib.api.entity.features.DropFeature;
import net.heriazone.lovelylib.source.reboot.RebootRobotType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Registry for LovelyRobotEntity Reboot entity types (NeoForge).
 * <p>
 * <b>Architecture:</b> Manages entity type registration, attribute creation,
 * and renderer binding for all robot variants in the Reboot mod.
 * <p>
 * <b>Registered Entities:</b> All 7 robot types (Vanilla, Bunny, Bunny2, Dragon, Honey, Kitsune, Neko)
 */
public class RebootEntities {

    // -- Entity Type Registry --

    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, Reboot.MODID);

    // -- Entity Type Definitions --

    public static final DeferredHolder<EntityType<?>, EntityType<RobotEntity>> BUNNY = registerRobot(LovelyConstant.VARIANT_BUNNY, RebootRobotType.BUNNY);
    public static final DeferredHolder<EntityType<?>, EntityType<RobotEntity>> BUNNY2 = registerRobot(LovelyConstant.VARIANT_BUNNY2, RebootRobotType.BUNNY2);
    public static final DeferredHolder<EntityType<?>, EntityType<RobotEntity>> DRAGON = registerRobot(LovelyConstant.VARIANT_DRAGON, RebootRobotType.DRAGON);
    public static final DeferredHolder<EntityType<?>, EntityType<RobotEntity>> HONEY = registerRobot(LovelyConstant.VARIANT_HONEY, RebootRobotType.HONEY);
    public static final DeferredHolder<EntityType<?>, EntityType<RobotEntity>> KITSUNE = registerRobot(LovelyConstant.VARIANT_KITSUNE, RebootRobotType.KITSUNE);
    public static final DeferredHolder<EntityType<?>, EntityType<RobotEntity>> NEKO = registerRobot(LovelyConstant.VARIANT_NEKO, RebootRobotType.NEKO);
    public static final DeferredHolder<EntityType<?>, EntityType<RobotEntity>> VANILLA = registerRobot(LovelyConstant.VARIANT_VANILLA, RebootRobotType.VANILLA);

    // -- Registration Methods --

    /**
     * Registers a robot entity type with RobotEntity implementation.
     * <p>
     * Type-safe registration that ensures RobotEntity is used consistently
     * across all robot variants.
     * <p>
     * <b>Spawn Item Handling:</b> Spawn item is resolved lazily during entity
     * construction to avoid circular dependency issues during registration.
     *
     * @param name entity variant name
     * @param robotType robot configuration data
     * @return registered entity type
     */
    private static DeferredHolder<EntityType<?>, EntityType<RobotEntity>> registerRobot(String name, NativeEntityType robotType) {
        return ENTITY_TYPES.register(name, () -> EntityType.Builder.of(
                        (EntityType<RobotEntity> type, Level level) -> new RobotEntity(type, level, robotType), MobCategory.CREATURE)
                .sized(SharedConfigs.EntityDimensions.DEFAULT_WIDTH, SharedConfigs.EntityDimensions.DEFAULT_HEIGHT)
                .build(RebootIdentifier.getId(name).toString()));
    } // registerRobot()

    /**
     * Registers entity types to the event bus.
     *
     * @param eventBus NeoForge event bus
     */
    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
        Reboot.LOGGER.info("Registering Entities: " + Reboot.MODID);
    } // register()

    /**
     * Registers entity attributes during entity attribute creation event.
     * <p>
     * <b>Timing:</b> Fires during mod construction. Config values are loaded
     * before this event to populate combat stats.
     *
     * @param event entity attribute creation event
     */
    public static void registerAttribute(EntityAttributeCreationEvent event) {
        event.put(BUNNY.get(), LovelyRobotEntity.createAttributes(RebootRobotType.BUNNY));
        event.put(BUNNY2.get(), LovelyRobotEntity.createAttributes(RebootRobotType.BUNNY2));
        event.put(DRAGON.get(), LovelyRobotEntity.createAttributes(RebootRobotType.DRAGON));
        event.put(HONEY.get(), LovelyRobotEntity.createAttributes(RebootRobotType.HONEY));
        event.put(KITSUNE.get(), LovelyRobotEntity.createAttributes(RebootRobotType.KITSUNE));
        event.put(NEKO.get(), LovelyRobotEntity.createAttributes(RebootRobotType.NEKO));
        event.put(VANILLA.get(), LovelyRobotEntity.createAttributes(RebootRobotType.VANILLA));
    } // registerAttribute()

    /**
     * Registers entity renderers on client side.
     *
     * @param event entity renderers registration event
     */
    public static void registerRender(net.neoforged.neoforge.client.event.EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(BUNNY.get(), BunnyRenderer::new);
        event.registerEntityRenderer(BUNNY2.get(), RobotRenderer::new);
        event.registerEntityRenderer(DRAGON.get(), RobotRenderer::new);
        event.registerEntityRenderer(HONEY.get(), RobotRenderer::new);
        event.registerEntityRenderer(KITSUNE.get(), KitsuneRenderer::new);
        event.registerEntityRenderer(NEKO.get(), RobotRenderer::new);
        event.registerEntityRenderer(VANILLA.get(), RobotRenderer::new);
    } // registerRender()

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
     * <b>Thread Safety:</b> Not thread-safe. Should only be called from main thread
     * during mod initialization.
     */
    public static void registerNativeRobotFeature() {
        // BUNNY
        RebootRobotType.BUNNY
                .withFeature(PickupFeature.class, new PickupFeature(RebootItems.BUNNY_SPAWN.get()))
                .withFeature(DropFeature.class, new DropFeature(RebootItems.ROBOT_CORE.get()));

        // BUNNY2
        RebootRobotType.BUNNY2
                .withFeature(PickupFeature.class, new PickupFeature(RebootItems.BUNNY2_SPAWN.get()))
                .withFeature(DropFeature.class, new DropFeature(RebootItems.ROBOT_CORE.get()));

        // DRAGON
        RebootRobotType.DRAGON
                .withFeature(PickupFeature.class, new PickupFeature(RebootItems.DRAGON_SPAWN.get()))
                .withFeature(DropFeature.class, new DropFeature(RebootItems.ROBOT_CORE.get()));

        // HONEY
        RebootRobotType.HONEY
                .withFeature(PickupFeature.class, new PickupFeature(RebootItems.HONEY_SPAWN.get()))
                .withFeature(DropFeature.class, new DropFeature(RebootItems.ROBOT_CORE.get()));

        // KITSUNE
        RebootRobotType.KITSUNE
                .withFeature(PickupFeature.class, new PickupFeature(RebootItems.KITSUNE_SPAWN.get()))
                .withFeature(DropFeature.class, new DropFeature(RebootItems.ROBOT_CORE.get()));

        // NEKO
        RebootRobotType.NEKO
                .withFeature(PickupFeature.class, new PickupFeature(RebootItems.NEKO_SPAWN.get()))
                .withFeature(DropFeature.class, new DropFeature(RebootItems.ROBOT_CORE.get()));

        // VANILLA
        RebootRobotType.VANILLA
                .withFeature(PickupFeature.class, new PickupFeature(RebootItems.VANILLA_SPAWN.get()))
                .withFeature(DropFeature.class, new DropFeature(RebootItems.ROBOT_CORE.get()));
    } // registerNativeRobotFeature ()

} // Class: RebootEntities