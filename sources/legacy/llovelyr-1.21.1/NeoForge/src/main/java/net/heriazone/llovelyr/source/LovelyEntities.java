package net.heriazone.llovelyr.source;

import net.heriazone.llovelyr.Legacy;
import net.heriazone.lovelylib.common.shared.LovelyConstant;
import net.heriazone.lovelylib.common.shared.LovelyIdentifier;
import net.heriazone.llovelyr.entity.*;
import net.heriazone.lovelylib.common.configs.SharedConfigs;
import net.heriazone.lovelylib.common.entity.NativeEntityType;
import net.heriazone.lovelylib.common.entity.common.*;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.core.registries.BuiltInRegistries;

/**
 * Registry for LovelyRobotEntity Legacy entity types (NeoForge).
 * <p>
 * <b>Architecture:</b> Manages entity type registration, attribute creation,
 * and renderer binding for all robot variants in the Legacy mod.
 * <p>
 * <b>Registered Entities:</b> All 7 robot types (Vanilla, Bunny, Bunny2, Dragon, Honey, Kitsune, Neko)
 */
public class LovelyEntities {

    // -- Entity Type Registry --

    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, Legacy.MODID);

    // -- Entity Type Definitions --

    public static final DeferredHolder<EntityType<?>, EntityType<RobotEntity>> BUNNY = registerRobot(LovelyConstant.VARIANT_BUNNY, LegacyRobotType.BUNNY);
    public static final DeferredHolder<EntityType<?>, EntityType<RobotEntity>> BUNNY2 = registerRobot(LovelyConstant.VARIANT_BUNNY2, LegacyRobotType.BUNNY2);
    public static final DeferredHolder<EntityType<?>, EntityType<RobotEntity>> DRAGON = registerRobot(LovelyConstant.VARIANT_DRAGON, LegacyRobotType.DRAGON);
    public static final DeferredHolder<EntityType<?>, EntityType<RobotEntity>> HONEY = registerRobot(LovelyConstant.VARIANT_HONEY, LegacyRobotType.HONEY);
    public static final DeferredHolder<EntityType<?>, EntityType<RobotEntity>> KITSUNE = registerRobot(LovelyConstant.VARIANT_KITSUNE, LegacyRobotType.KITSUNE);
    public static final DeferredHolder<EntityType<?>, EntityType<RobotEntity>> NEKO = registerRobot(LovelyConstant.VARIANT_NEKO, LegacyRobotType.NEKO);
    public static final DeferredHolder<EntityType<?>, EntityType<RobotEntity>> VANILLA = registerRobot(LovelyConstant.VARIANT_VANILLA, LegacyRobotType.VANILLA);

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
                .build(LovelyIdentifier.getId(name).toString()));
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
        event.put(BUNNY.get(), LovelyRobotEntity.createAttributes(LegacyRobotType.BUNNY));
        event.put(BUNNY2.get(), LovelyRobotEntity.createAttributes(LegacyRobotType.BUNNY2));
        event.put(DRAGON.get(), LovelyRobotEntity.createAttributes(LegacyRobotType.DRAGON));
        event.put(HONEY.get(), LovelyRobotEntity.createAttributes(LegacyRobotType.HONEY));
        event.put(KITSUNE.get(), LovelyRobotEntity.createAttributes(LegacyRobotType.KITSUNE));
        event.put(NEKO.get(), LovelyRobotEntity.createAttributes(LegacyRobotType.NEKO));
        event.put(VANILLA.get(), LovelyRobotEntity.createAttributes(LegacyRobotType.VANILLA));
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

} // Class: LovelyEntities