package net.heriazone.llovelyr.source;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.entity.*;
import net.heriazone.lovelylib.api.entity.features.PickupFeature;
import net.heriazone.lovelylib.source.legacy.LegacyRobotType;
import net.heriazone.lovelylib.common.shared.LovelyConstant;
import net.heriazone.lovelylib.common.configs.SharedConfigs;
import net.heriazone.lovelylib.common.entity.*;
import net.heriazone.llovelyr.LegacyIdentifier;
import net.heriazone.llovelyr.Legacy;
import net.heriazone.hzlib.api.entity.InternalEntity;
import net.heriazone.hzlib.api.entity.features.DropFeature;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.neoforge.registries.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.*;

/**
 * Registry for NativeRobotEntity Legacy entity types (NeoForge).
 * <p>
 * <b>Architecture:</b> Manages entity type registration, attribute creation,
 * and renderer binding for all robot variants in the Legacy mod.
 * <p>
 * <b>Registered Entities:</b> All 7 robot types (Vanilla, Bunny, Bunny2, Dragon, Honey, Kitsune, Neko)
 */
public class LegacyEntities {

    // -- Entity Type Registry --

    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, Legacy.MODID);

    // -- Entity Type Definitions --

    public static final DeferredHolder<EntityType<?>, EntityType<NativeRobotEntity>> BUNNY = registerRobot(LovelyConstant.VARIANT_BUNNY, LegacyRobotType.BUNNY);
    public static final DeferredHolder<EntityType<?>, EntityType<NativeRobotEntity>> BUNNY2 = registerRobot(LovelyConstant.VARIANT_BUNNY2, LegacyRobotType.BUNNY2);
    public static final DeferredHolder<EntityType<?>, EntityType<NativeRobotEntity>> DRAGON = registerRobot(LovelyConstant.VARIANT_DRAGON, LegacyRobotType.DRAGON);
    public static final DeferredHolder<EntityType<?>, EntityType<NativeRobotEntity>> HONEY = registerRobot(LovelyConstant.VARIANT_HONEY, LegacyRobotType.HONEY);
    public static final DeferredHolder<EntityType<?>, EntityType<NativeRobotEntity>> KITSUNE = registerRobot(LovelyConstant.VARIANT_KITSUNE, LegacyRobotType.KITSUNE);
    public static final DeferredHolder<EntityType<?>, EntityType<NativeRobotEntity>> NEKO = registerRobot(LovelyConstant.VARIANT_NEKO, LegacyRobotType.NEKO);
    public static final DeferredHolder<EntityType<?>, EntityType<NativeRobotEntity>> VANILLA = registerRobot(LovelyConstant.VARIANT_VANILLA, LegacyRobotType.VANILLA);

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
    private static DeferredHolder<EntityType<?>, EntityType<NativeRobotEntity>> registerRobot(String name, NativeEntityType robotType) {
        return ENTITY_TYPES.register(name, () -> EntityType.Builder.of(
                        (EntityType<NativeRobotEntity> type, Level level) -> new NativeRobotEntity(type, level, robotType), MobCategory.CREATURE)
                .sized(SharedConfigs.EntityDimensions.DEFAULT_WIDTH, SharedConfigs.EntityDimensions.DEFAULT_HEIGHT)
                .build(LegacyIdentifier.getId(name).toString()));
    } // registerRobot()

    /**
     * Registers entity types to the event bus.
     *
     * @param eventBus NeoForge event bus
     */
    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
        Legacy.LOGGER.info("Registering Entities: " + Legacy.MODID);
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
        event.put(BUNNY.get(),   InternalEntity.createAttributes(LegacyRobotType.BUNNY));
        event.put(BUNNY2.get(),  InternalEntity.createAttributes(LegacyRobotType.BUNNY2));
        event.put(DRAGON.get(),  InternalEntity.createAttributes(LegacyRobotType.DRAGON));
        event.put(HONEY.get(),   InternalEntity.createAttributes(LegacyRobotType.HONEY));
        event.put(KITSUNE.get(), InternalEntity.createAttributes(LegacyRobotType.KITSUNE));
        event.put(NEKO.get(),    InternalEntity.createAttributes(LegacyRobotType.NEKO));
        event.put(VANILLA.get(), InternalEntity.createAttributes(LegacyRobotType.VANILLA));
    } // registerAttribute()

    /**
     * Registers entity renderers on client side.
     *
     * @param event entity renderers registration event
     */
    public static void registerRender(net.neoforged.neoforge.client.event.EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(BUNNY.get(), BunnyRenderer::new);
        event.registerEntityRenderer(BUNNY2.get(), NativeRobotRenderer::new);
        event.registerEntityRenderer(DRAGON.get(), NativeRobotRenderer::new);
        event.registerEntityRenderer(HONEY.get(), NativeRobotRenderer::new);
        event.registerEntityRenderer(KITSUNE.get(), KitsuneRenderer::new);
        event.registerEntityRenderer(NEKO.get(), NativeRobotRenderer::new);
        event.registerEntityRenderer(VANILLA.get(), NativeRobotRenderer::new);
    } // registerRender()

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
     * <b>Thread Safety:</b> Not thread-safe. Should only be called from main thread
     * during mod initialization.
     */
    public static void registerNativeRobotFeature() {
        // BUNNY
        LegacyRobotType.BUNNY
                .withFeature(PickupFeature.class, new PickupFeature(LegacyItems.BUNNY_SPAWN.get()))
                .withFeature(DropFeature.class, new DropFeature(LegacyItems.ROBOT_CORE.get()));

        // BUNNY2
        LegacyRobotType.BUNNY2
                .withFeature(PickupFeature.class, new PickupFeature(LegacyItems.BUNNY2_SPAWN.get()))
                .withFeature(DropFeature.class, new DropFeature(LegacyItems.ROBOT_CORE.get()));

        // DRAGON
        LegacyRobotType.DRAGON
                .withFeature(PickupFeature.class, new PickupFeature(LegacyItems.DRAGON_SPAWN.get()))
                .withFeature(DropFeature.class, new DropFeature(LegacyItems.ROBOT_CORE.get()));

        // HONEY
        LegacyRobotType.HONEY
                .withFeature(PickupFeature.class, new PickupFeature(LegacyItems.HONEY_SPAWN.get()))
                .withFeature(DropFeature.class, new DropFeature(LegacyItems.ROBOT_CORE.get()));

        // KITSUNE
        LegacyRobotType.KITSUNE
                .withFeature(PickupFeature.class, new PickupFeature(LegacyItems.KITSUNE_SPAWN.get()))
                .withFeature(DropFeature.class, new DropFeature(LegacyItems.ROBOT_CORE.get()));

        // NEKO
        LegacyRobotType.NEKO
                .withFeature(PickupFeature.class, new PickupFeature(LegacyItems.NEKO_SPAWN.get()))
                .withFeature(DropFeature.class, new DropFeature(LegacyItems.ROBOT_CORE.get()));

        // VANILLA
        LegacyRobotType.VANILLA
                .withFeature(PickupFeature.class, new PickupFeature(LegacyItems.VANILLA_SPAWN.get()))
                .withFeature(DropFeature.class, new DropFeature(LegacyItems.ROBOT_CORE.get()));
    } // registerNativeRobotFeature ()

} // Class: LegacyEntities