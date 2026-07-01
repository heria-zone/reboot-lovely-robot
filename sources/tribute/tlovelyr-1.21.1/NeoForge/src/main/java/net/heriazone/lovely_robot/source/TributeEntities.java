package net.heriazone.lovely_robot.source;

import net.heriazone.hzlib.api.entity.NativeEntityFamily;
import net.heriazone.lovely_robot.Tribute;
import net.heriazone.lovely_robot.TributeIdentifier;
import net.heriazone.hzlib.api.entity.features.PickupFeature;
import net.heriazone.lovelylib.common.configs.SharedConfigs;
import net.heriazone.lovelylib.common.entity.*;
import net.heriazone.lovelylib.common.shared.LovelyConstant;
import net.heriazone.hzlib.api.entity.features.DropFeature;
import net.heriazone.lovelylib.source.tribute.TributeRobotFamilies;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Registry for NativeRobotEntity Tribute entity types (NeoForge).
 * <p>
 * <b>Architecture:</b> Manages entity type registration, attribute creation,
 * and renderer binding for all robot variants in the Tribute mod.
 * <p>
 * <b>Registered Entities:</b> All 7 robot types (Vanilla, Bunny, Bunny2, Dragon, Honey, Kitsune, Neko)
 */
public class TributeEntities {

    // -- Entity Type Registry --

    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, Tribute.MODID);

    // -- Entity Type Definitions --

    public static final DeferredHolder<EntityType<?>, EntityType<TributeRobotEntity>> BUNNY   = registerRobot(LovelyConstant.VARIANT_BUNNY,   TributeRobotFamilies.BUNNY);
    public static final DeferredHolder<EntityType<?>, EntityType<TributeRobotEntity>> BUNNY2  = registerRobot(LovelyConstant.VARIANT_BUNNY2,  TributeRobotFamilies.BUNNY2);
    public static final DeferredHolder<EntityType<?>, EntityType<TributeRobotEntity>> HONEY   = registerRobot(LovelyConstant.VARIANT_HONEY,   TributeRobotFamilies.HONEY);
    public static final DeferredHolder<EntityType<?>, EntityType<TributeRobotEntity>> VANILLA = registerRobot(LovelyConstant.VARIANT_VANILLA, TributeRobotFamilies.VANILLA);

    // -- Registration Methods --

    /**
     * Registers a robot entity type with NativeRobotEntity implementation.
     * <p>
     * Type-safe registration that ensures NativeRobotEntity is used consistently
     * across all robot variants.
     * <p>
     * <b>Spawn Item Handling:</b> Spawn item is resolved lazily during entity
     * construction to avoid circular dependency issues during registration.
     *
     * @param name entity variant name
     * @param robotType robot configuration data
     * @return registered entity type
     */
    private static DeferredHolder<EntityType<?>, EntityType<TributeRobotEntity>> registerRobot(String name, RobotFamily robotType) {
        return ENTITY_TYPES.register(name, () -> EntityType.Builder.of(
                        (EntityType<TributeRobotEntity> type, Level level) -> new TributeRobotEntity(type, level, robotType), MobCategory.CREATURE)
                .sized(SharedConfigs.EntityDimensions.DEFAULT_WIDTH, SharedConfigs.EntityDimensions.DEFAULT_HEIGHT)
                .build(TributeIdentifier.getId(name).toString()));
    } // registerRobot()

    /**
     * Registers entity types to the event bus.
     *
     * @param eventBus NeoForge event bus
     */
    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
        Tribute.LOGGER.info("Registering Entities: " + Tribute.MODID);
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
        event.put(BUNNY.get(),   NativeEntityFamily.createAttributes(TributeRobotFamilies.BUNNY));
        event.put(BUNNY2.get(),  NativeEntityFamily.createAttributes(TributeRobotFamilies.BUNNY2));
        event.put(HONEY.get(),   NativeEntityFamily.createAttributes(TributeRobotFamilies.HONEY));
        event.put(VANILLA.get(), NativeEntityFamily.createAttributes(TributeRobotFamilies.VANILLA));
    } // registerAttribute()

    /**
     * Registers entity renderers on client side.
     *
     * @param event entity renderers registration event
     */
    public static void registerRender(net.neoforged.neoforge.client.event.EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(BUNNY.get(),    TributeRobotRenderer::new);
        event.registerEntityRenderer(BUNNY2.get(),   TributeRobotRenderer::new);
        event.registerEntityRenderer(HONEY.get(),    TributeRobotRenderer::new);
        event.registerEntityRenderer(VANILLA.get(),  TributeRobotRenderer::new);
    } // registerRender()

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
     * <b>Thread Safety:</b> Not thread-safe. Should only be called from main thread
     * during mod initialization.
     */
    public static void registerNativeRobotFeature() {
        // BUNNY
        TributeRobotFamilies.BUNNY
                .withFeature(PickupFeature.class, new PickupFeature(TributeItems.BUNNY_SPAWN.get()))
                .withFeature(DropFeature.class, new DropFeature(TributeItems.ROBOT_CORE.get()));

        // BUNNY2
        TributeRobotFamilies.BUNNY2
                .withFeature(PickupFeature.class, new PickupFeature(TributeItems.BUNNY2_SPAWN.get()))
                .withFeature(DropFeature.class, new DropFeature(TributeItems.ROBOT_CORE.get()));

        // HONEY
        TributeRobotFamilies.HONEY
                .withFeature(PickupFeature.class, new PickupFeature(TributeItems.HONEY_SPAWN.get()))
                .withFeature(DropFeature.class, new DropFeature(TributeItems.ROBOT_CORE.get()));

        // VANILLA
        TributeRobotFamilies.VANILLA
                .withFeature(PickupFeature.class, new PickupFeature(TributeItems.VANILLA_SPAWN.get()))
                .withFeature(DropFeature.class, new DropFeature(TributeItems.ROBOT_CORE.get()));
    } // registerNativeRobotFeature ()

} // Class: TributeEntities